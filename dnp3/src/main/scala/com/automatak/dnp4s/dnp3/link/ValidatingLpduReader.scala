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

import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }
import annotation.tailrec
import com.automatak.dnp4s.dnp3.{ TestDriver, TestReporter }
import scala.Some

class ValidatingLpduReader(driver: TestDriver, parser: LpduParser, local: UInt16LE, remote: UInt16LE)(writer: Lpdu => Unit) extends LpduReader {

  def read(reporter: TestReporter, confirmed: Boolean)(timeoutMs: Long): Option[List[Byte]] = {
    val end = System.currentTimeMillis() + timeoutMs

    def validate(lpdu: Lpdu): Option[List[Byte]] = if (confirmed) {
      if (lpdu.header.ctrl.func == Lpdu.CONFIRMED_USER_DATA) {
        if (lpdu.header.ctrl.fcb == driver.getFcb()) {
          driver.toggleFcb()
          writer(Lpdu(LinkHeader(UInt8(5), LinkCtrl(true, false, false, false, Lpdu.ACK), remote, local)))
          Some(lpdu.data)
        } else {
          reporter.error("Received unexpected fcb bit in lpdu")
          None
        }
      } else {
        reporter.error("Ignoring unexpected lpdu: " + lpdu)
        None
      }
    } else {
      if (lpdu.header.ctrl.func == Lpdu.UNCONFIRMED_USER_DATA) {
        if (lpdu.header.dest == local && lpdu.header.src == remote) {
          Some(lpdu.data)
        } else {
          reporter.error("Ignoring lpdu with bad addressing: " + lpdu)
          None
        }
      } else {
        reporter.error("Ignoring unexpected lpdu: " + lpdu)
        None
      }
    }

    @tailrec
    def recurse(): Option[List[Byte]] = {
      val now = System.currentTimeMillis()
      val remain = (end - now).toInt
      if (remain > 0) parser.read(reporter)(remain) match {
        case Some(lpdu) => validate(lpdu) match {
          case Some(bytes) => Some(bytes)
          case None => recurse()
        }
        case None =>
          None
      }
      else None
    }

    recurse()
  }

}
