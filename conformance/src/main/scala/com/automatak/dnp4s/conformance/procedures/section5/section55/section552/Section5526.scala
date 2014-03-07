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
package com.automatak.dnp4s.conformance.procedures.section5.section55.section552

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.dnp3.link._
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.dnp3.link.LinkHeader
import procedures.CommonSteps.{ Description, Wait }
import com.automatak.dnp4s.dsl._

object Section5526 extends TestProcedure {

  def id = "5.5.2.6"
  def description = "Invalid FCV"

  val wait2sec = Wait(2000)

  private val lpduTimeoutOrNotImplemented = LinkSteps.ExpectLpduTimeoutOr(LinkCtrl(false, false, false, false, Lpdu.NOT_SUPPORTED).toByte)

  case class SendInvalidData(ctrlByte: Byte) extends TestStep {
    def description = List("Send user data using invalid link ctrl: 0x" + toHex(ctrlByte))
    def run(driver: TestDriver) = {
      val apdu = Apdus.readClass0(0)
      val tpdu = Transport.function(true, true, 0) :: apdu.toBytes
      val ctrl = LinkCtrl(ctrlByte)
      val lpdu = Lpdu(LinkHeader(UInt8((5 + tpdu.size).toShort), ctrl, driver.remote, driver.local), tpdu)
      driver.writeLPDU(lpdu)
      Nil
    }
  }

  def steps(options: TestOptions) = {

    val steps1to4 = List(
      "1" -> List(Description("Prepare a READ request for Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 ",
        "and a link control block 0xC3, i.e. SEND/CONFIRM USER DATA but with FCV=0 (incorrect)")),
      "2" -> List(SendInvalidData(0xC3.toByte)),
      "3" -> List(wait2sec),
      "4" -> List(lpduTimeoutOrNotImplemented))

    steps1to4 ::: invalidFCVTable.map(steps5to8).flatten
  }

  private val invalidFCVTable = List(0xD4.toByte, 0xC2.toByte, 0xD0.toByte, 0xD9.toByte)

  def steps5to8(ctrl: Byte) = List(
    "5" -> List(Description("Prepare a READ request for Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 and a link control block 0xD4, i.e. SEND/NO CONFIRM USER DATA, but with FCV=1 (incorrect)")),
    "6" -> List(SendInvalidData(ctrl)),
    "7" -> List(wait2sec),
    "8" -> List(lpduTimeoutOrNotImplemented))

  /*
1. Prepare a CLASS 0 POLL request with an invalid Data Link Layer Control octet of 0xC3 (i.e.
SEND/CONFIRM USER DATA, but with FCV=0 (incorrect)).
2. Send the request.
3. Wait several seconds.
4. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
response. The DUT may respond with a link layer frame with function code LINK SERVICE
NOT SUPPORTED.
5. Prepare a CLASS 0 POLL request with an invalid Data Link Layer Control octet of 0xD4 (i.e.
SEND/NO CONFIRM USER DATA, but with FCV=1 (incorrect)).
6. Send the request.
7. Wait several seconds.
8. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
response. The DUT may respond with a link layer frame with function code LINK SERVICE
NOT SUPPORTED.
9. Repeat steps 5-8 using the Data Link Layer Control octets in Table 5 .
*/

}

