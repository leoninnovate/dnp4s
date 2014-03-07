/**
 * Copyright 2013 Automatak, LLC
 *
 * Licensed to Automatak, LLC under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Automatak, LLC licenses this file to you
 * under the GNU Affero General Public License Version 3.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/agpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.automatak.dnp4s.dnp3

import com.automatak.dnp4s.dnp3.phys.ReadableWritableChannel
import com.automatak.dnp4s.dnp3.app.{ AppCtrl, Apdu }
import com.automatak.dnp4s.dnp3.link._
import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3.transport.{ TpduWriter, TransportReader }
import java.nio.ByteBuffer
import annotation.tailrec
import scala.Some

object TestDriver {

  def readAllResponses(driver: TestDriver)(seq: Byte, autoConfirm: Boolean, uns: Boolean, func: Byte, linkConfirmed: Boolean = false): List[Apdu] = {

    @tailrec
    def recurse(list: List[Apdu], seq: Byte): List[Apdu] = driver.readAPDU(linkConfirmed) match {
      case None => throw new Exception("Unable to read complete Apdus")
      case Some(apdu) =>
        if (apdu.ctrl.con && autoConfirm) { // auto-confirm anything we see to keep the tests going
          driver.writeAPDU(Apdus.confirm(apdu.ctrl.seq, apdu.ctrl.uns))
        }
        if (apdu.ctrl.uns == uns) {
          if (apdu.ctrl.seq == seq) {
            if (apdu.function == func) {
              if (list.isEmpty) {
                if (apdu.ctrl.fir) {
                  if (!apdu.ctrl.fin) recurse(apdu :: list, AppCtrl.nextSeq(seq))
                  else apdu :: list
                } else throw new Exception("Expecting fir bit")
              } else {
                if (apdu.ctrl.fir) throw new Exception("Not expecting fir bit")
                else {
                  if (!apdu.ctrl.fin) recurse(apdu :: list, AppCtrl.nextSeq(seq))
                  else apdu :: list
                }
              }
            } else throw new Exception("Unexpected function code: " + apdu.function)
          } else throw new Exception("Unexpected sequence: " + apdu.ctrl.seq)
        } else {
          throw new Exception("Unexpected uns bit: " + apdu)
          recurse(list, seq)
        }
    }

    recurse(Nil, seq).reverse
  }
}

trait TestDriver {

  def setExpectedFcb(fcb: Boolean): Unit
  def getFcb(): Boolean
  def toggleFcb(): Unit

  def local: UInt16LE
  def remote: UInt16LE

  def writePhys(bytes: List[Byte]): Unit
  def writeLPDU(lpdu: Lpdu): Unit
  def writeTPDU(tpdu: List[Byte]): Unit
  def writeAPDU(apdu: Apdu, linkBlock: Option[Byte] = None): Unit

  def readLPDU(): Option[Lpdu]
  def readAPDU(confirmed: Boolean = false): Option[Apdu]

  def reporter: TestReporter
}

class DefaultTestDriver(myReporter: TestReporter, channel: ReadableWritableChannel, localAddr: UInt16LE, remoteAddr: UInt16LE, linkTimeoutMs: Int, appTimeoutMs: Int, isMaster: Boolean) extends TestDriver {

  private var fcb = true

  def toggleFcb(): Unit = fcb = !fcb

  def getFcb(): Boolean = fcb

  def setExpectedFcb(expectedFcb: Boolean): Unit = {
    fcb = expectedFcb
  }

  override def reporter = myReporter

  def writePhys(bytes: List[Byte]): Unit = writeToChannel(bytes)

  def local = localAddr
  def remote = remoteAddr

  private val parser = new LpduParser(channel)
  private val lpduReader = new ValidatingLpduReader(this, parser, local, remote)(writeLPDU)

  private def writeToChannel(bytes: List[Byte]) = {
    reporter.transmitPhys(bytes)
    channel.write(ByteBuffer.wrap(bytes.toArray))
  }

  override def writeAPDU(apdu: Apdu, linkBlock: Option[Byte]): Unit = {
    reporter.transmitAPDU(apdu)
    TpduWriter.write(apdu.toBytes, 0, reporter) { tpdu =>
      LinkWriter.write(local, remote, isMaster, linkBlock, reporter)(tpdu)(writeToChannel)
    }
  }

  override def writeTPDU(tpdu: List[Byte]): Unit = {
    TpduWriter.write(tpdu, 0, reporter) { data =>
      LinkWriter.write(local, remote, isMaster, None, reporter)(tpdu)(writeToChannel)
    }
  }

  override def writeLPDU(lpdu: Lpdu): Unit = {
    reporter.transmitLPDU(lpdu)
    writeToChannel(lpdu.toBytes)
  }

  override def readAPDU(confirmed: Boolean): Option[Apdu] = {
    try {
      val apdu = TransportReader.read(lpduReader, reporter, confirmed)(appTimeoutMs).map(Apdu.apply)
      apdu.foreach(myReporter.receiveAPDU)
      apdu
    } catch {
      case ex: Exception =>
        reporter.error("Error parsing APDU: " + ex)
        None
    }
  }

  override def readLPDU(): Option[Lpdu] = parser.read(reporter)(linkTimeoutMs)

}