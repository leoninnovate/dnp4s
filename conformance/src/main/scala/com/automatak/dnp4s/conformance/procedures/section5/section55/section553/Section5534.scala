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
package com.automatak.dnp4s.conformance.procedures.section5.section55.section553

import com.automatak.dnp4s.conformance.{ TestOptions, TestStep, LinkSteps, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresDataLinkConfirmation, AppSteps }
import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.dnp3.link.{ LinkHeader, Lpdu, LinkCtrl }
import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }

object Section5534 extends TestProcedure with RequiresDataLinkConfirmation {

  def id = "5.5.3.4"
  def description = "Invalid Destination Address"
  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.configDataLinkRetryReasonable :: Prompts.cyclePower :: Nil

  case class SendAckToWrongAddress(offset: Short) extends TestStep {
    def description = List("Send an ACK frame to a destination address that is not that of the DUT")
    def run(driver: TestDriver) = {
      val ctrl = LinkCtrl(true, false, false, false, Lpdu.ACK)
      driver.writeLPDU(Lpdu(LinkHeader(UInt8(5), ctrl, UInt16LE(driver.remote.i + offset), driver.local)))
      Nil
    }
  }

  def steps(options: TestOptions) =
    Section5531.validTransaction ::: stepsWithAddressOffset(1) ::: stepsWithAddressOffset(2)

  def stepsWithAddressOffset(offset: Short) = List(
    "1.a" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "1.b" -> List(LinkSteps.ExpectConfirmedUserDataFCBAny),
    "1.c" -> List(SendAckToWrongAddress(offset)),
    "1.d" -> List(AppSteps.ReadAnyValidResponseWithConfirms))

  /*
5.5.3.4 Invalid Destination Address
1. If the DUT supports Data Link Layer Confirmations and performs retries:
a. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4.
b. Verify that the DUT responds with Application Layer data, requesting Data Link Layer
Confirmation.
c. Send a CONFIRM – ACK frame to a destination address that is not that of the DUT.
d. Verify that the DUT waits for its Data Link Layer Confirmation Timeout to expire, and then
retransmits the link frame.
e. Repeat this test once with a different destination address.
*/

}
