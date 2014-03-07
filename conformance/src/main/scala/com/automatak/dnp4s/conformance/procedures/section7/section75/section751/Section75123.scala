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
package com.automatak.dnp4s.conformance.procedures.section7.section75.section751

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures._
import section7.section75.Class0

object Section75123 extends TestProcedure with RequiresNoClassAssignment {

  def id = "7.5.1.2.3"
  def description = "Some Inputs Not Assigned to Any Class"
  override def prompts = List(Prompts.assignNoClass, Prompts.cyclePower)

  def steps(options: TestOptions) = List(
    "1c" -> List(AppSteps.RequestClass0(0.toByte)),
    "1d" -> List(Class0.VerifyClass0(0.toByte)),
    "1e" -> List(CommonSteps.PromptForUserYesNo("Does the response include data for those points that have been removed from all Classes?", false)),
    "1f" -> List(CommonSteps.PromptForUserYesNo(Prompts.verify10ValueStateFlags, true)))

  /*
7.5.1.2.3 Some Inputs Not Assigned to Any Class
1. If the DUT is configurable such that points can be removed from all Classes (i.e. points belong
to no Class):
a. Configure the device such that some inputs are removed from all Classes (i.e. assign them to
no Class).
b. Cycle power to the device.
c. Issue a CLASS 0 POLL request.
d. Verify that the device responds with the current states/values of all input points assigned to
Classes 0, 1, 2, and 3, using only the object group, variation and qualifier combinations
specified in Table 7 .
e. Verify that the response does not include data for those points that have been removed from
all Classes.
f. For at least ten of the reported points of each point type: Verify that the value/state and flags
are correct.
*/

}
