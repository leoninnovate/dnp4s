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
package com.automatak.dnp4s.dnp3.transport

import com.automatak.dnp4s.dnp3.link.LpduReader
import com.automatak.dnp4s.dnp3.TestReporter

object TransportReader {

  private case class Previous(seq: Byte)

  // reads full APDUs as bytes
  def read(reader: LpduReader, reporter: TestReporter, confirmed: Boolean = false)(timeoutMs: Long): Option[List[Byte]] = {
    val end = System.currentTimeMillis() + timeoutMs

    def recurse(seq: Option[Byte], data: List[Byte]): Option[List[Byte]] = {

      def evaluate(bytes: List[Byte]): Option[List[Byte]] = bytes match {
        case x :: apdu =>
          val ctrl = Transport.function(x)
          reporter.receiveTPDU(ctrl, apdu)
          seq match {
            case Some(num) => //previous data
              if (ctrl.fir) throw new Exception("Received new fir frame, before segment completed")
              else {
                val expected = Transport.next(num)
                if (ctrl.seq == expected) {
                  if (ctrl.fin) Some(data ++ apdu)
                  else {
                    if (apdu.size != 249) throw new Exception("TPDU packing error. Expected full TPDU")
                    recurse(Some(ctrl.seq), data ++ apdu)
                  }
                } else throw new Exception("Unexpected tpdu sequence: " + ctrl.seq)
              }
            case None =>
              if (ctrl.fir) {
                if (ctrl.fin) Some(apdu)
                else {
                  if (apdu.size != 249) throw new Exception("TPDU packing error. Expected full TPDU")
                  recurse(Some(ctrl.seq), apdu)
                }
              } else throw new Exception("Expected fir TPDU but received " + ctrl)
          }
        case Nil =>
          throw new Exception("Received 0 length TPDU")
      }

      val now = System.currentTimeMillis()
      val remaining = end - now
      if (remaining > 0) {
        reader.read(reporter, confirmed)(remaining) match {
          case Some(data) =>
            evaluate(data)
          case None =>
            None
        }
      } else None
    }

    recurse(None, Nil)
  }

}
