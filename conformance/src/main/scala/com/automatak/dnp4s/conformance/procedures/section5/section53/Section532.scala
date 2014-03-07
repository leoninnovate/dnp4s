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
package com.automatak.dnp4s.conformance.procedures.section5.section53

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }
import procedures.{ Prompts, RequiresDataLinkConfirmation, AppSteps }

object Section53 extends TestSection {
  def id = "5.3"
  def description = "Retries"
  override def subProcedures = List(Section532)
}

object Section532 extends TestProcedureRequiringRetries with RequiresDataLinkConfirmation {

  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.configDataLinkRetryReasonable :: Prompts.cyclePower :: Nil

  def id: String = "5.3.2"

  def description: String = "Retries e -> t"

  private val expectLinkReset = LinkSteps.ReadHeaderOnlyLpdu(LinkCtrl(false, true, false, false, Lpdu.RESET_LINK_STATES).toByte)
  private val sendConfirm = LinkSteps.SendHeaderOnlyLpdu(LinkCtrl(true, false, false, false, Lpdu.ACK).toByte)
  private val expectConfirmedUserData = LinkSteps.ExpectConfirmedUserData(LinkCtrl(false, true, true, true, Lpdu.CONFIRMED_USER_DATA).toByte)

  def steps(retries: Int) = {

    List(
      "e" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "f -> h" -> (TestProcedure.repeat(List(expectLinkReset), retries + 1) ::: List(LinkSteps.ExpectLpduTimeout)),
      "i" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "j" -> List(expectLinkReset, sendConfirm),
      "k -> m" -> (TestProcedure.repeat(List(expectConfirmedUserData), retries + 1) ::: List(LinkSteps.ExpectLpduTimeout)),
      "o" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "p" -> List(expectLinkReset, sendConfirm),
      "q" -> List(expectConfirmedUserData),
      "r" -> List(expectConfirmedUserData),
      "s" -> List(sendConfirm),
      "t" -> List(LinkSteps.ExpectLpduTimeout))
  }

  /*
1. If the DUT supports Data Link Layer retries:
a. Configure the device to enable Data Link Layer Confirmations on primary transmissions.
b. If the device’s Maximum Data Link Retries are configurable, set it to 3.
c. If the device’s Data Link Layer Confirmation Timeout is configurable, set it to 5 seconds.
d. Cycle power to the DUT.
e. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
f. Verify that the device sends a RESET LINK frame. Do not send a Data Link Layer ACK.
g. Verify that the device waits Data Link Layer Confirmation Timeout and then retransmits the
link frame.
h. Verify that it repeats this exactly Maximum Data Link Retries times.
i. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
j. Verify that the device responds with a RESET LINK frame. Send a Data Link Layer ACK
(Data Link Control octet 0x80).
k. Verify that the device sends a valid Application Layer response using Data Link Layer
Control octet 0x73 in a SEND/CONFIRM USER DATA frame. Do not send a Data Link
Layer ACK.
l. Verify that the device waits for its Data Link Layer Confirmation Timeout to expire, and
then retransmits the link frame.
m. Verify that it repeats this exactly Maximum Data Link Retries times.
n. If it is possible to cause the device to send TEST LINK or REQUEST LINK STATUS, cause
it to do so, but do not respond in each case. Repeat steps 1k and 1l for each of these frames
that can be generated.
o. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
p. Verify that the device responds with a RESET LINK frame. Issue a Data Link Layer ACK.
q. Verify that the device sends a valid Application Layer response using Data Link Layer
Control octet 0x73 in a SEND/CONFIRM USER DATA frame. Do not send a Data Link
Layer ACK.
r. Verify that the device waits for its Data Link Layer Confirmation Timeout to expire, and
then retransmits the link frame.
s. Send a Data Link Layer ACK.
t. Verify that no further retries are sent.
u. If it is possible to cause the device to send TEST LINK or REQUEST LINK STATUS, cause
it to do so, but do not respond in each case. Repeat steps 1s and 1t for each of these frames
that can be generated noting that a LINK STATUS must be sent in step 1s if the DUT
generates a REQUEST LINK STATUS frame.
*/

}

