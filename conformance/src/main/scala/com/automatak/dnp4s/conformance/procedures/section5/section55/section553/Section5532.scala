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

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure, TestStep }
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, LinkHeader, Lpdu }
import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.conformance.procedures.AppSteps
import com.automatak.dnp4s.conformance.LinkSteps.ExpectConfirmedUserDataFCBAny

object Section5532 extends TestProcedure {

  override def prompts = List(
    "Configure the device to enable link layer confirms on primary transmissions and if retries are configurable set them to a reasonable value (at least 1)",
    "Cycle power to DUT")

  def id = "5.5.3.2"
  def description = "Invalid Start Octets"

  case class SendConfirmWithBadStart(first: Byte, second: Byte) extends TestStep {
    def description = List("Send a ACK frame modified so its start octet is invalid (e.g. 0x09)")
    def run(driver: TestDriver) = {
      val ctrl = LinkCtrl(true, false, false, false, Lpdu.ACK)
      val bytes = Lpdu(LinkHeader(UInt8(5), ctrl, driver.remote, driver.local)).toBytesWithStart(first, second)
      driver.writePhys(bytes)
      Nil
    }
  }

  def steps(options: TestOptions) = stepsWithInvalidOctests(0x09, 0xFF.toByte) ::: stepsWithInvalidOctests(0x07, 0xFE.toByte)

  private def stepsWithInvalidOctests(start: Byte, stop: Byte) = Section5531.validTransaction :::
    List(
      "1.a" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "1.b" -> List(ExpectConfirmedUserDataFCBAny),
      "1.c" -> List(SendConfirmWithBadStart(start, 0x64.toByte)),
      "1.d" -> List(ExpectConfirmedUserDataFCBAny),
      "1.e" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "1.f" -> List(ExpectConfirmedUserDataFCBAny),
      "1.g" -> List(SendConfirmWithBadStart(0x05.toByte, stop)),
      "1.h" -> List(ExpectConfirmedUserDataFCBAny))

  /*
5.5.3.2 Invalid Start Octets
1. If the DUT supports Data Link Layer Confirmations and performs retries:
a. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4.
b. Verify that the DUT responds with Application Layer data, requesting Data Link Layer
Confirmation.
c. Send a CONFIRM – ACK frame modified so its start octet is invalid (e.g. 0x09).
d. Verify that the DUT waits for its Data Link Layer Confirmation Timeout to expire, and then
retransmits the link frame.
e. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4 using Data Link
Layer Control octet 0xC4.
f. Verify that the DUT responds with Application Layer data, requesting Data Link Layer
Confirmation.
g. Send a CONFIRM – ACK frame modified so the start octet is correct (0x05), but the
second octet is incorrect (e.g. 0xff).
h. Verify that the DUT waits for its Data Link Layer Confirmation Timeout to expire, and then
retransmits the link frame.
i. Repeat this test once with different start octet values.
*/

}

