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
package com.automatak.dnp4s.conformance.procedures.section5.section54

import com.automatak.dnp4s.conformance._
import procedures.{ RequiresDataLinkConfirmation, Prompts, CommonSteps, AppSteps }
import com.automatak.dnp4s.dnp3.link.{ LinkCtrl, Lpdu }

object Section54 extends TestSection {

  def id = "5.4"
  def description = "DIR and FCV Bits"
  override def subProcedures = List(Section542a, Section542b)

}

object Section542a extends TestProcedure {

  def id = "5.4.2a"
  def description = "Test Procedure 3 -> 6"
  override def prompts = Prompts.configDataLinkNoConfirmation :: Prompts.cyclePower :: Nil

  private val linkStatus = LinkCtrl(false, true, false, false, Lpdu.REQUEST_LINK_STATES)
  private val testLink = LinkCtrl(false, true, false, false, Lpdu.TEST_LINK_STATES)

  def promptForRequestLink = CommonSteps.PromptForBranch(
    "If the DUT can generate REQUEST LINK STATUS, cause it to do so",
    List("Validate LinkStatus" -> List(LinkSteps.ReadHeaderOnlyLpdu(linkStatus.toByte))),
    Nil)

  def promptForTestLink = CommonSteps.PromptForBranch(
    "If the DUT can generate TEST LINK, cause it to do so",
    List("Validate RequestLink" -> List(LinkSteps.ReadHeaderOnlyLpdu(testLink.toByte))),
    Nil)

  def steps(options: TestOptions) = List(
    "3" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "4" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
    "5" -> List(promptForRequestLink),
    "6" -> List(promptForTestLink))
}

object Section542b extends TestProcedure with RequiresDataLinkConfirmation {

  def id = "5.4.2b"
  def description = "Test Procedure 7a-f"
  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.cyclePower :: Nil

  private val resetLink = LinkCtrl(false, true, false, false, Lpdu.RESET_LINK_STATES)

  def steps(options: TestOptions) = List(
    "c" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "d" -> List(LinkSteps.ReadHeaderOnlyLpdu(resetLink.toByte)),
    "e" -> List(LinkSteps.SendHeaderOnlyLpdu(0x80.toByte)),
    "f" -> List(LinkSteps.ExpectConfirmedUserData(0x73.toByte)))
}

/*
5.4.2 Test Procedure
1. If the DUT can be configured to request Data Link Layer Confirmations, configure it to NOT
request Data Link Layer Confirmations.
2. Cycle power to the DUT.
3. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
4. Verify that the response from the DUT uses Data Link Layer Control octet 0x44 (i.e.
Unconfirmed User Data with the DIR bit not set and the FCV bit not set).
5. If the DUT can generate REQUEST LINK STATUS, cause it to do so and verify that none of
these frames have the DIR or FCV bit set.
6. If the DUT can generate TEST LINK, cause it to do so and verify that these frames have the
DIR bit clear and FCV bit set.
7. If the DUT can be configured to request Data Link Layer Confirmations:
a. Configure the DUT to request Data Link Layer Confirmations.
b. Cycle power to the DUT.
c. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
d. Verify that the DUT first sends a frame using Data Link Layer Control octet 0x40 (i.e.
RESET LINK with DIR=0 and FCV=0).
e. Issue an ACK (Data Link Layer Control octet of 0x80).
f. Verify that the DUT sends a frame using Data Link Layer Control octet 0x73 i.e.
SEND/CONFIRM USER DATA with FCV=1 and FCB = 1.
*/ 