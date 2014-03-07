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

import com.automatak.dnp4s.conformance.{ TestOptions, LinkSteps, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresDataLinkConfirmation, AppSteps }

object Section5531 extends TestProcedure with RequiresDataLinkConfirmation {

  override def prompts = Prompts.configDataLinkConfirmation :: Prompts.configDataLinkRetryReasonable :: Prompts.cyclePower :: Nil

  def id = "5.5.3.1"

  def description = "Valid Transaction"

  def steps(options: TestOptions) = validTransaction

  def validTransaction = List(
    "1.c" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
    "1.d" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x40.toByte)),
    "1.e" -> List(LinkSteps.SendHeaderOnlyLpdu(0x80.toByte)),
    "1.f - 1.g" -> List(AppSteps.ReadAnyValidResponseWithConfirms))

  /*
  5.5.3.1 Valid Transaction
  1. If the DUT supports Data Link Layer Confirmations and performs retries:
  a. Configure the device to enable Data Link Layer Confirmations on primary transmissions
  and if the device’s Maximum Data Link Retries are configurable, set it to a reasonable value.
  b. Cycle power to the DUT.
  c. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4.
  d. Verify that the DUT first sends a frame using Data Link Layer Control octet 0x40 (i.e.
  RESET LINK with DIR=0 and FCV=0).
  e. Issue an ACK (Data Link Layer Control octet of 0x80).
  f. Verify that the DUT sends a frame using Data Link Layer Control octet 0x73 i.e.
  SEND/CONFIRM USER DATA with FCV=1 and FCB = 1.
  g. Issue an ACK (Data Link Layer Control octet of 0x80).
  */

}

