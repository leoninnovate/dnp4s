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
package com.automatak.dnp4s.dnp3.link

import java.nio.ByteBuffer
import com.automatak.dnp4s.dsl._
import java.net.SocketTimeoutException
import com.automatak.dnp4s.dnp3.phys.ReadableChannel
import annotation.tailrec
import com.automatak.dnp4s.dnp3.TestReporter

object LpduParser {

  def decode(buffer: ByteBuffer, reporter: TestReporter): Option[Lpdu] = {

    @tailrec
    def sync(): Option[Lpdu] = {
      if (buffer.remaining() >= 10) { //need at least 10 bytes to parse a frame
        if (buffer.get() == 0x05) {
          if (buffer.get() == 0x64) read()
          else sync()
        } else sync()
      } else {
        buffer.position(buffer.position() + buffer.remaining())
        buffer.limit(buffer.capacity())
        None
      }
    }

    def read(): Option[Lpdu] = {

      val len = UInt8.fromByte(buffer.get())
      val ctrl = LinkCtrl(buffer.get())

      val dest = UInt16LE.from(buffer.getShort().reverseBytes)
      val src = UInt16LE.from(buffer.getShort().reverseBytes)

      val crc = buffer.getShort().reverseBytes
      val hdr = LinkHeader(len, ctrl, dest, src)

      if (crc == hdr.crc) {
        if (len.value == 5) { //header only
          buffer.compact()
          val lpdu = Lpdu(hdr)
          reporter.receiveLPDU(lpdu)
          Some(lpdu)
        } else {
          if (len.value < 5) {
            reporter.warn("Invalid length: " + len.value)
            buffer.compact()
            None
          } else { // > 5
            val dataSize = len.value - 5
            val total = calcTotalSize(dataSize)
            if (buffer.remaining() < total) { //not enough data, we need to reset until more comes in
              buffer.position(buffer.position() + buffer.remaining())
              buffer.limit(buffer.capacity())
              None
            } else { // check body CRCs and extract
              extractData(dataSize, buffer, reporter) match {
                case Some(data) =>
                  buffer.compact()
                  val lpdu = Lpdu(hdr, data)
                  reporter.receiveLPDU(lpdu)
                  Some(lpdu)
                case None => //crc failure occurred
                  buffer.compact()
                  None
              }
            }
          }
        }
      } else {
        reporter.warn("Crc failure in header: " + hdr.toString)
        buffer.position(2) //start searching for new sync
        buffer.compact()
        buffer.position(8)
        None
      }
    }

    sync()
  }

  def calcTotalSize(dataSize: Int): Int = {
    val numFull = dataSize / 16
    val mod16 = dataSize % 16
    val hasPartial = mod16 != 0
    numFull * 18 + (if (hasPartial) (mod16 + 2) else 0)
  }

  def extractData(dataSize: Int, buffer: ByteBuffer, reporter: TestReporter): Option[List[Byte]] = {

    @tailrec
    def recurse(running: List[Byte], remaining: Int, buffer: ByteBuffer): Option[List[Byte]] = {
      if (remaining == 0) Some(running)
      else {
        val num = math.min(16, remaining)
        val array = new Array[Byte](num)
        buffer.get(array, 0, num)
        val data = running ++ array.toList
        val short = buffer.getShort().reverseBytes
        if (Crc.calc(array.toList) == short) {
          recurse(data, remaining - num, buffer)
        } else {
          reporter.warn("Crc failure in body")
          None
        }
      }
    }

    recurse(Nil, dataSize, buffer)
  }

}

class LpduParser(reader: ReadableChannel) {

  val buffer = ByteBuffer.allocateDirect(292)

  def read(reporter: TestReporter)(timeoutMs: Int): Option[Lpdu] = {

    def continue(): Option[Lpdu] = {
      try {
        val num = reader.read(buffer, timeoutMs)
        if (num > 0) {
          buffer.flip()
          LpduParser.decode(buffer, reporter)
        } else None
      } catch {
        case ex: SocketTimeoutException => None
      }
    }

    buffer.flip()
    LpduParser.decode(buffer, reporter) match {
      case Some(lpdu) =>
        Some(lpdu)
      case None =>
        continue()
    }
  }

}