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

import com.automatak.dnp4s.conformance.{ TestOptions, LinkSteps, TestProcedure, TestStep }
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, LinkHeader, Lpdu }
import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.dnp3.app.AppFunctions
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresDataLinkConfirmation, AppSteps }

object Section5533 extends TestProcedure with RequiresDataLinkConfirmation {

  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.configDataLinkRetryReasonable :: Prompts.cyclePower :: Nil

  def id = "5.5.3.3"

  def description = "Invalid Secondary Function Code"

  private val otherCode = LinkCtrl(true, false, false, false, Lpdu.RESET_LINK_STATES)

  def steps(options: TestOptions) = stepsWithInvalidAckCode(0x83.toByte) // ::: stepsWithInvalidAckCode(otherCode.toByte)

  case class SendInvalidACK(byte: Byte) extends TestStep {
    def description = List("Send a ACK frame modified so the DIR, PRM and DFC bits of the control field are correct, but the function code is invalid (e.g. 0x83). Ensure that the CRC is correct for the invalid function code value")
    def run(driver: TestDriver) = {
      val ctrl = LinkCtrl(true, false, false, false, byte)
      val lpdu = Lpdu(LinkHeader(UInt8(5), ctrl, driver.remote, driver.local))
      driver.writeLPDU(lpdu)
      Nil
    }
  }

  object ReadApduConfirmed extends TestStep {
    def description = List("Verify that the DUT waits LINK TIMEOUT and then retransmits the link frame")
    def run(driver: TestDriver) = {
      val apdus = TestDriver.readAllResponses(driver)(0, true, false, AppFunctions.rsp, true)
      if (apdus.isEmpty) throw new Exception("Unable to read response")
      Nil
    }
  }

  def stepsWithInvalidAckCode(byte: Byte) = Section5531.validTransaction ::: List(
    "1.a" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "1.b" -> List(LinkSteps.ExpectConfirmedUserDataFCBAny),
    "1.c" -> List(SendInvalidACK(byte)),
    "1.d" -> List(ReadApduConfirmed))

  /*
5.5.3.3 Invalid Secondary Function Code
1. If the DUT supports Data Link Layer Confirmations and performs retries:
a. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4.
b. Verify that the DUT responds with Application Layer data, requesting Data Link Layer
Confirmation.
c. Send a CONFIRM – ACK frame modified so the DIR, PRM and DFC bits of the Data
Link Layer Control octet are correct, but the Function Code is invalid (e.g. 0x83).
Ensure that the CRC is correct for the invalid Function Code value.
d. Verify that the DUT waits for its Data Link Layer Confirmation Timeout to expire, and then
retransmits the link frame.
e. Repeat this test once with a different Function Code value.
*/

}

