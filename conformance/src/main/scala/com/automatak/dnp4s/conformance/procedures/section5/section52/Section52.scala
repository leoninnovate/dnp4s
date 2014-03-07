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
package com.automatak.dnp4s.conformance.procedures.section5.section52

import com.automatak.dnp4s.conformance.{ TestSection, TestOptions, LinkSteps, TestProcedure }
import com.automatak.dnp4s.conformance.procedures.Prompts

object Section52 extends TestSection {
  def id = "5.2"
  def description = "Request Link Status"
  override def subProcedures = List(Section522)
}

object Section522 extends TestProcedure {

  override def prompts = List(Prompts.cyclePower)

  def id: String = "5.2.2"
  def description: String = "Test Procedure"

  private val verifyLinkStatus = LinkSteps.ReadHeaderOnlyLpdu(0x0B.toByte, 0x1B.toByte)

  def steps(options: TestOptions) = List(
    "2" -> List(LinkSteps.SendHeaderOnlyLpdu(0xC9.toByte)),
    "3" -> List(verifyLinkStatus),
    "4" -> List(LinkSteps.SendHeaderOnlyLpdu(0xE9.toByte)),
    "5" -> List(verifyLinkStatus))

  /*
1. Cycle power to the DUT.
2. Request a Link Status Frame using Data Link Layer Control octet 0xC9.
3. Verify that the DUT responds with a valid LINK STATUS message with Data Link Layer
Control octet 0x0B or 0x1B.
4. Request a Link Status Frame using Data Link Layer Control octet 0xE9.
5. Verify that the DUT responds with a valid LINK STATUS message with Data Link Layer
Control octet 0x0B or 0x1B.
*/

}

