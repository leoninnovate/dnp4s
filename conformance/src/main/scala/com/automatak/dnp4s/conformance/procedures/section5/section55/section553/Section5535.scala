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
import com.automatak.dnp4s.conformance.procedures.{ RequiresDataLinkConfirmation, Prompts, AppSteps }
import com.automatak.dnp4s.dnp3.TestDriver
import com.automatak.dnp4s.dnp3.link.{ InvalidCrc, LinkHeader, Lpdu, LinkCtrl }
import com.automatak.dnp4s.dsl.UInt8

object Section5535 extends TestProcedure with RequiresDataLinkConfirmation {

  def id = "5.5.3.5"
  def description = "Invalid CRC"
  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.configDataLinkRetryReasonable :: Prompts.cyclePower :: Nil

  case class AckWithInvalidCrc(offset: Short) extends TestStep {
    def description = List("Send an ACK frame modified such that the CRC is incorrect")
    def run(driver: TestDriver) = {
      val lpdu = Lpdu(LinkHeader(UInt8(5), LinkCtrl(true, false, false, false, Lpdu.ACK), driver.remote, driver.local))
      val bytes = lpdu.toBytesWithGen(InvalidCrc(offset), InvalidCrc(offset))
      driver.writePhys(bytes)
      Nil
    }
  }

  def steps(options: TestOptions) = Section5531.validTransaction ::: stepsWithCRCOffset(1) ::: stepsWithCRCOffset(2)

  def stepsWithCRCOffset(offset: Short) = List(
    "1.a" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "2.b" -> List(LinkSteps.ExpectConfirmedUserDataFCBAny),
    "3.c" -> List(AckWithInvalidCrc(offset)),
    "4.d" -> List(AppSteps.ReadAnyValidResponseWithConfirms))

  /*
5.5.3.5 Invalid CRC
1. If the DUT supports Data Link Layer Confirmations and performs retries:
a. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4.
b. Verify that the DUT responds with Application Layer data, requesting Data Link Layer
Confirmation.
c. Send a CONFIRM – ACK frame modified such that the CRC is incorrect.
d. Verify that the DUT waits for its Data Link Layer Confirmation Timeout to expire, and then
retransmits the link frame.
e. Repeat this test once with a different CRC value.
*/

}
