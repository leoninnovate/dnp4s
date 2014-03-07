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
import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3.{ Apdus, TestDriver }
import com.automatak.dnp4s.dnp3.transport.Transport
import com.automatak.dnp4s.conformance.procedures.CommonSteps.{ Description, Wait }

object Section5524 extends TestProcedure {

  def id = "5.5.2.4"
  def description = "Invalid Destination Address"

  case class RequestClass0InvalidAddress(offset: Int) extends TestStep {
    def description = List("Send the request")
    def run(driver: TestDriver) = {
      val tpdu = Transport.function(true, true, 0) :: Apdus.readClass0(0).toBytes
      val lpdu = Lpdu.withData(LinkCtrl(true, true, false, false, Lpdu.UNCONFIRMED_USER_DATA), UInt16LE(driver.remote.i + offset), driver.local, tpdu)
      driver.writeLPDU(lpdu)
      Nil
    }
  }

  def steps(options: TestOptions) = stepsWithAddressOffset(1) ::: stepsWithAddressOffset(2)

  def stepsWithAddressOffset(offset: Int) = List(
    "1" -> List(Description("Prepare a CLASS 0 POLL request with a Data Link Layer Control octet of 0xD3")),
    "2" -> List(Description("Change the destination address in the frame to a value other than the address of the DUT",
      "Note: For devices that can be configured to represent multiple logical devices (i.e. can respond to requests directed to more than one address)",
      "ensure that the selected address is not one of the\nother valid addresses configured for the device.")),
    "3" -> List(RequestClass0InvalidAddress(1)),
    "4" -> List(Wait(3000)),
    "5" -> List(LinkSteps.ExpectLpduTimeout))

  /*
5.5.2.4 Invalid Destination Address
1. Prepare a CLASS 0 POLL request with a Data Link Layer Control octet of 0xD3.
2. Change the destination address in the frame to a value other than the address of the DUT. Note:
For devices that can be configured to represent multiple logical devices (i.e. can respond to
requests directed to more than one address), ensure that the selected address is not one of the
other valid addresses configured for the device.
3. Send the request.
4. Wait several seconds.
5. Verify that the DUT does not send a Data Link Layer Confirmation or Application Layer
response.
6. Repeat this test once with a different destination address.
*/

}

