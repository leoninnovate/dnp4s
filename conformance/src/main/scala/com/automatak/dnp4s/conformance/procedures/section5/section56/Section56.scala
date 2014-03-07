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
package com.automatak.dnp4s.conformance.procedures.section5.section56

import com.automatak.dnp4s.conformance.{ TestStep, TestSection }
import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }

object Section56 extends TestSection {

  import com.automatak.dnp4s.dsl.toHex

  def id = "5.6"
  def description = "Self-Address Support"

  override def subProcedures = List(Section562a, Section562b)

  case class RequestClass0WithAddress(remote: UInt16LE) extends TestStep {
    def description = List(
      "Issue a request for Class 0 data (Object group 60 Variation 1) using Qualifier Code 0x06 and link control block 0xC4",
      "to device address " + toHex(remote.i, true))
    def run(driver: TestDriver) = {
      val tpdu = Transport.function(true, true, 0) :: Apdus.readClass0(0).toBytes
      val lpdu = Lpdu.withData(LinkCtrl(0xC4.toByte), remote, driver.local, tpdu)
      driver.writeLPDU(lpdu)
      Nil
    }
  }
}
