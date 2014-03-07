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
package com.automatak.dnp4s.conformance.procedures.section6

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure, TestSection }
import com.automatak.dnp4s.conformance.procedures.{ Prompts, CommonSteps, AppSteps }
import com.automatak.dnp4s.dnp3.Apdus

object Section6 extends TestSection {

  def id = "6"
  def description = "Transport Function"

  override def subProcedures = List(Section621, Section622)
}

object Section621 extends TestProcedure {
  def id = "6.2.1"
  def description = "Segmentation of an Application Layer fragment – static data"
  override def prompts = Prompts.cyclePower :: Nil
  def steps(options: TestOptions) = List(
    "2" -> List(AppSteps.RequestClass0(0)),
    "3-4" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)))
}

/*
6.2.1 Segmentation of an Application Layer fragment – static data
1. Cycle power to the DUT.
2. Issue a CLASS 0 POLL request to the DUT.
3. Verify that the device responds with a valid message.
4. If the response contains only one segment:
a. Verify that both the FIR and FIN bits in the transport header are set.
b. Verify that the segment contains an Application Layer header.
c. Verify that the segment contains a maximum of 249 Application Layer data octets. Note that
the CRC octets are not included in the count of Application Layer data octets.
5. If the response contains more than one segment:
a. Verify that the FIR bit is set, and the FIN bit is clear, in the transport header of the first
segment.
b. Verify that the FIR bit is clear, and the FIN bit is set, in the transport header of the last
segment.
c. Verify that the FIR and FIN bits are both clear in the transport headers of any intermediate
segments.
d. Verify that the transport header sequence number of all non-first segments increments by 1
(modulo 64) from the preceding segment.
e. Verify that the first segment contains an Application Layer header, and that each non-first
segment does not contain an Application Layer header.
f. Verify that the first segment contains a maximum of 249 Application Layer data octets. Note
that the CRC octets are not included in the count of Application Layer data octets.
g. Verify that each non-first segment contains at least 1, but no more than 249, Application
Layer data octets.
*/

object Section622 extends TestProcedure {
  def id = "6.2.2"
  def description = "Segmentation of an Application Layer fragment – event and static data"
  override def prompts = Prompts.cyclePower :: Nil

  def steps(options: TestOptions) = List(
    "2" -> List(CommonSteps.PromptForUserAction("Generate the maximum amount of event data (without causing the event buffers to overflow)")),
    "3" -> List(AppSteps.SendApdu(Apdus.readIntegrity(0))),
    "4-5" -> List(AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)))
}
/*
6.2.2 Segmentation of an Application Layer fragment – event and static
data
1. Cycle power to the DUT.
2. Generate the maximum amount of event data (without causing the event buffers to overflow).
3. Issue an INTEGRITY POLL request to the DUT.
4. Verify that the device responds with a valid message.
5. If the response contains only one segment:
a. Verify that both the FIR and FIN bits in the transport header are set.
b. Verify that the segment contains an Application Layer header.
c. Verify that the segment contains a maximum of 249 Application Layer data octets. Note that
the CRC octets are not included in the count of Application Layer data octets.
6. If the response contains more than one segment:
a. Verify that the FIR bit is set, and the FIN bit is clear, in the transport header of the first
segment.
b. Verify that the FIR bit is clear, and the FIN bit is set, in the transport header of the last
segment.
c. Verify that the FIR and FIN bits are both clear in the transport headers of any intermediate
segments.
d. Verify that the transport header sequence number of all non-first segments increments by 1
(modulo 64) from the preceding segment.
e. Verify that the first segment contains an Application Layer header, and that each non-first
segment does not contain an Application Layer header.
f. Verify that the first segment contains a maximum of 249 Application Layer data octets. Note
that the CRC octets are not included in the count of Application Layer data octets.
g. Verify that each non-first segment contains at least 1, but no more than 249, Application
Layer data octets.
*/ 