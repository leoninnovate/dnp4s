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

import com.automatak.dnp4s.conformance.{ TestStep, TestOptions, TestProcedure }
import com.automatak.dnp4s.conformance.procedures._
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.AppSteps.ApduValidation
import com.automatak.dnp4s.conformance.procedures.section8.StaticVerification
import java.lang.Exception
import section7.section75.Class0

object Section75122 extends TestProcedure with RequiresClass123Support {

  def id = "7.5.1.2.2"
  def description = "All Inputs Assigned to Classes 1, 2, and/or 3"
  override def prompts = Prompts.assignAllPointsToEvents ::: List(Prompts.cyclePower)

  def steps(options: TestOptions) = List(
    "1c" -> List(CommonSteps.PromptForUserAction(Prompts.generate10EventsWithBinaryAnalogCounter)),
    "1d" -> List(AppSteps.RequestClass0(0.toByte)),
    "1e-1g" -> List(Class0.VerifyClass0(0)))

  /*
7.5.1.2.2 All Inputs Assigned to Classes 1, 2, and/or 3
1. If the response to the CLASS 0 POLL in Test All Inputs Assigned to Class 0 contained more
than one Data Link layer frame, or if the DUT supports Classes 1, 2, or 3:
a. Configure the device such that all installed Binary Input, Counter, and Analog Input points
are assigned to event classes, ensuring that points are assigned to each supported event class.
If possible, configure the device such that all installed Binary Output Status, Analog Output
Status, and Frozen Counter points are assigned to event classes.
b. Cycle power to the device.
c. Generate at least ten known Level 2-supported events from all supported classes, including:
At least one Binary Input event if the DUT supports Binary Input points,
At least one Counter event if the DUT supports Binary Counter points,
At least one Analog Input event if the DUT supports Analog Input points.
d. Issue a CLASS 0 POLL request.
e. Verify that all installed Binary Input, Binary Output Status, Counter, Frozen Counter,
Analog Input, and Analog Output Status points are included in the response from the device,
using only the object group, variation and qualifier combinations specified in Table 7 .
f. Verify that no object group, variation and qualifier combinations other than those specified
in Table 7 are included in the response.
g. For at least ten of the reported points of each point type: Verify that the value/state and flags
are correct.
*/

}
