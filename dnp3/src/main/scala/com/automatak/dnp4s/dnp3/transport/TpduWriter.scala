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

import annotation.tailrec
import com.automatak.dnp4s.dnp3.TestReporter

object TpduWriter {

  // Chuncks up an arbitrary sized data into TPDU's, forwarding them to a receiving function
  def write(bytes: List[Byte], seq: Byte, reporter: TestReporter)(fun: List[Byte] => Unit): Unit = {

    val iterator = bytes.grouped(249)

    @tailrec
    def recurse(first: Boolean, seq: Byte): Unit = {
      val bytes = iterator.next()
      val fin = !iterator.hasNext
      val header = Transport.Header(first, fin, seq)
      reporter.transmitTPDU(header, bytes)
      fun(header.toByte :: bytes)
      if (!fin) recurse(false, ((seq + 1) % 16).toByte)
    }

    if (iterator.hasNext) recurse(true, seq)
  }

}
