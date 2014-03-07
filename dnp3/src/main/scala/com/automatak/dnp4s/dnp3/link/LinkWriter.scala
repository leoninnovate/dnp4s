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

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.TestReporter

object LinkWriter {

  def write(src: UInt16LE, dest: UInt16LE, isMaster: Boolean, block: Option[Byte], reporter: TestReporter)(data: List[Byte])(fun: List[Byte] => Unit): Unit = {
    val ctrl = block.map(LinkCtrl.apply).getOrElse(LinkCtrl(isMaster, true, false, false, Lpdu.UNCONFIRMED_USER_DATA))
    val header = LinkHeader(UInt8((5 + data.size).toShort), ctrl, dest, src)
    val lpdu = Lpdu(header, data)
    reporter.transmitLPDU(lpdu)
    fun(lpdu.toBytes)
  }

}
