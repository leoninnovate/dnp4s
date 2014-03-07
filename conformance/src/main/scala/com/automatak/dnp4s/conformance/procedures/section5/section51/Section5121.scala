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
import com.automatak.dnp4s.conformance.procedures.{ Prompts, AppSteps }

object Section5121 extends TestProcedure {

  override def prompts = List(Prompts.cyclePower)

  def id = "5.1.2.1"
  def description = "Passive Confirms and RESET LINKs"

  private val ackFollowedByValidResponse = List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte), new AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0.toByte))

  private def step12(count: Int): List[TestStep] = {

    val seq1 = AppSteps.RequestClass0(0, 0xD3.toByte) :: ackFollowedByValidResponse
    val seq2 = AppSteps.RequestClass0(0, 0xF3.toByte) :: ackFollowedByValidResponse

    TestProcedure.repeat(seq1 ::: seq2, count)

  }

  def steps(options: TestOptions) = {

    List(
      "2" -> List(AppSteps.RequestClass0(0, 0xC4.toByte)),
      "3" -> List(new AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
      "4" -> List(AppSteps.RequestClass0(0, 0xF3.toByte)),
      "5" -> List(LinkSteps.ReadNackOrIgnore),
      "6" -> List(AppSteps.RequestClass0(0, 0xD3.toByte)),
      "7" -> List(LinkSteps.ReadNackOrIgnore),
      "8" -> List(LinkSteps.SendHeaderOnlyLpdu(0xC0.toByte)),
      "9" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte)),
      "10" -> List(AppSteps.RequestClass0(0, 0xF3.toByte)),
      "11" -> ackFollowedByValidResponse,
      "12" -> step12(3),
      "14" -> List(LinkSteps.SendHeaderOnlyLpdu(0xC0.toByte)),
      "15" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte)),
      "16" -> List(AppSteps.RequestClass0(0, 0xD3.toByte)),
      "17" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte), LinkSteps.ExpectLpduTimeout),
      "18" -> List(AppSteps.RequestClass0(0, 0xF3.toByte)),
      "19" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte), new AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)),
      "20" -> List(AppSteps.RequestClass0(0, 0xF3.toByte)),
      "21" -> List(LinkSteps.ReadHeaderOnlyLpdu(0x00.toByte), LinkSteps.ExpectLpduTimeout))
  }

  /*
  1. Cycle power to the DUT.
  2. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xC4.
  3. Verify that the DUT responds with a valid message.
  4. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xF3.
  5. Verify that the DUT either sends a NACK with the DFC bit clear (Data Link Layer Control
  octet 0x01) or does not respond.
  6. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xD3.
  7. Verify that the DUT either sends a NACK with the DFC bit clear (Data Link Layer Control
  octet 0x01) or does not respond.
  8. Issue a RESET LINK request using Data Link Layer Control octet 0xC0.
  9. Verify that the DUT responds with a Data Link Layer ACK (Data Link Layer Control octet
  0x00).
  10. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xF3.
  11. Verify that the DUT responds with a Data Link Layer Confirmation and a valid Application
  Layer response.
  12. Issue repeated CLASS 0 POLL requests using alternating Data Link Layer Control octets 0xD3
  and 0xF3.
  13. Verify that the DUT responds with a Data Link Layer Confirmation and a valid Application
  Layer response for each request.
  14. Issue a RESET LINK request using Data Link Layer Control octet 0xC0.
  15. Verify that the DUT responds with a Data Link Layer ACK (Data Link Layer Control octet
  0x00).
  16. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xD3.
  17. Verify that the DUT responds with a Data Link Layer ACK (Data Link Layer Control octet
  0x00) and no application response.
  18. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xF3.
  19. Verify that the DUT responds with a Data Link Layer Confirmation and a valid Application
  Layer response.
  20. Issue a CLASS 0 POLL request using Data Link Layer Control octet 0xF3.
  21. Verify that the DUT responds with a Data Link Layer ACK (Data Link Layer Control octet
  0x00) and no application response.
   */

}

