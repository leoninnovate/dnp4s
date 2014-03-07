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

import com.automatak.dnp4s.conformance.{ LinkSteps, TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.{ Prompts, AppSteps }

object Section5521 extends TestProcedure {

  def id = "5.5.2.1"
  def description = "Valid Transaction"
  override def prompts = Prompts.configDataLinkNoConfirmation :: Prompts.cyclePower :: Nil

  def steps(options: TestOptions) = List(
    "3" -> List(LinkSteps.SendHeaderOnlyLpdu(0xC0.toByte)),
    "4" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte)),
    "5" -> List(AppSteps.RequestClass0(0, 0xF3.toByte)),
    "6" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00)),
    "7" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)))
}

/*
5.5.2.1 Valid Transaction
1. If the DUT can be configured to request Data Link Layer Confirmations, configure it to NOT
request Data Link Layer Confirmations.
2. Cycle power to the DUT.
3. Issue a RESET LINK request using Data Link Layer Control octet 0xC0.
4. Verify that the DUT responds with an ACK (Data Link Layer Control octet 0x00).
5. Issue a CLASS 0 POLL request with a Data Link Layer Control octet of 0xF3.
6. Verify that the DUT responds with an ACK (Data Link Layer Control octet of 0x00).
7. Verify that the DUT responds with Application Layer data.
*/ 