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
package com.automatak.dnp4s.conformance.procedures.section5.section51

import com.automatak.dnp4s.conformance.{ TestOptions, LinkSteps, TestProcedure, TestStep }
import com.automatak.dnp4s.conformance.LinkSteps.ExpectConfirmedUserData
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresDataLinkConfirmation, AppSteps }
import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, LinkHeader, Lpdu }
import com.automatak.dnp4s.dsl.UInt8

object Section5122 extends TestProcedure with RequiresDataLinkConfirmation {

  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.cyclePower :: Nil

  def id: String = "5.1.2.2"
  def description: String = "Transmits CONFIRMED_USER_DATA messages"

  def steps(options: TestOptions) = List(
    "1c" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "1d" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x40.toByte)),
    "1e" -> List(LinkSteps.SendHeaderOnlyLpdu(0x80.toByte)),
    "1f" -> List(ExpectConfirmedUserData(0x73.toByte)),
    "1g" -> List(LinkSteps.SendHeaderOnlyLpdu(0x81.toByte)),
    "1h/i" -> List(ResetLinkOrTimeout))

  object ResetLinkOrTimeout extends TestStep {

    def description = List("Determine whether the device responds with RESET_LINK or ignores the NACK")
    def run(driver: TestDriver) = {
      driver.readLPDU() match {
        case Some(lpdu) =>
          val expected = Lpdu(LinkHeader(UInt8(5), LinkCtrl(0x40), driver.local, driver.remote))
          if (lpdu != expected) throw new Exception("Unexpected lpdu: " + lpdu)
          List(
            "1.h.1" -> List(LinkSteps.SendHeaderOnlyLpdu(0x80.toByte)),
            "1.h.2" -> List(ExpectConfirmedUserData(0x73.toByte)))
        case None =>
          List(
            "1.i.1" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
            "1.i.2" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x40.toByte)),
            "1.i.3" -> List(LinkSteps.SendHeaderOnlyLpdu(0x80.toByte)),
            "1.i.4" -> List(AppSteps.ReadAnyValidResponseWithConfirms))
      }
    }
  }

  /*
  5.1.2.2 Transmits CONFIRMED_USER_DATA messages
1. If the DUT can be configured to request Data Link Layer Confirmations when transmitting
messages containing Application Layer data (Data Link Layer Control octet 0x53 or 0x73):
a. Configure the DUT to request Data Link Layer Confirmations when transmitting.
b. Cycle power to the DUT.
c. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
d. Verify that the device responds with a RESET LINK (Data Link Layer Control octet
0x40).
e. Send a Data Link Layer ACK (Data Link Layer Control octet 0x80).
f. Verify that the device responds with a valid message which contains Data Link Layer
Control octet 0x73; this message should be the response to the CLASS 0 POLL request,
including a request for Data Link Layer Confirmation.
g. Issue a Data Link Layer NACK with DFC clear (Data Link Layer Control octet
0x81).
h. If the device then issues a RESET LINK request (Data Link Layer Control octet
0x40):
1) Issue an ACK (Data Link Layer Control octet 0x80).
2) Verify that the DUT then repeats the response to the CLASS 0 POLL request,
identical to that of step 1f.
i. If the device did not respond to the NACK:
1) Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
2) Verify that the device responds with a RESET LINK (Data Link Layer Control octet
0x40).
3) Send a Data Link Layer ACK (Data Link Layer Control octet 0x80).
4) Verify that the device responds with a valid message which contains Data Link Layer
Control octet 0x73; this message should be the response to the CLASS 0 POLL request,
including a request for Data Link Layer Confirmation.
*/

}

