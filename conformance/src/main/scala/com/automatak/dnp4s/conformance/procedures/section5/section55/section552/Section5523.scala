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

import com.automatak.dnp4s.conformance.{ TestOptions, LinkSteps, TestProcedure, TestStep }
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.conformance.procedures.CommonSteps.Description
import com.automatak.dnp4s.conformance.procedures.CommonSteps
import com.automatak.dnp4s.dsl.toHex

object Section5523 extends TestProcedure {

  def id = "5.5.2.3"
  def description = "Invalid Primary Function Code"

  case class RequestClass0WithInvalidCode(code: Byte) extends TestStep {
    def description = List("Send the request")
    def run(driver: TestDriver) = {
      val tpdu = Transport.function(true, true, 0) :: Apdus.readClass0(0).toBytes
      val lpdu = Lpdu.withData(LinkCtrl(true, true, false, false, code), driver.remote, driver.local, tpdu)
      driver.writeLPDU(lpdu)
      Nil
    }
  }

  def steps(options: TestOptions) = stepsWithInvalidCode(0xD5.toByte) ::: stepsWithInvalidCode(0xAA.toByte)

  def stepsWithInvalidCode(invalid: Byte) = List(
    "1" -> List(Description("Prepare a READ request for Class 0 data (Object 60 Variation 1) using Qualifier Code 0x06 and a link control block of 0xD3")),
    "2" -> List(Description("Modify the frame so the control field contains the correct settings of the DIR, PRM, FCB and FCV fields, but has an invalid function code: 0x" + toHex(invalid),
      "Alter the CRC of the data link layer header so the CRC is correct for the invalid control field")),
    "3" -> List(RequestClass0WithInvalidCode(invalid)),
    "4" -> List(CommonSteps.Wait(3000)),
    "5" -> List(LinkSteps.ExpectLpduTimeoutOr(0x0F.toByte)),
    "6" -> List(LinkSteps.ExpectLpduTimeout))

  /*
5.5.2.3 Invalid Primary Function Code
1. Prepare a CLASS 0 POLL request with a Data Link Layer Control octet of 0xD3.
2. Modify the frame so the Data Link Layer Control octet contains the correct settings of the DIR,
PRM, FCB and FCV fields, but has an invalid function code, e.g. 0xD5. Also, change the CRC
of the Data Link Layer header so that the CRC is correct for the invalid Data Link Layer
Control octet.
3. Send the request.
4. Wait several seconds.
5. Verify that the DUT either does not send a Data Link Layer Confirmation, or responds with a
valid LINK SERVICE NOT SUPPORTED frame (Data Link Layer Control octet 0x0F).
6. Verify that the DUT does not send an Application Layer response.
7. Repeat this test once with a different invalid function code.
*/

}

