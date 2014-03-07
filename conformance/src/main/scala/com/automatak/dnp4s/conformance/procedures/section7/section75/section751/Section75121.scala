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
import com.automatak.dnp4s.conformance.procedures.{ AllowIIN, AppSteps }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.AppSteps.ApduValidation
import com.automatak.dnp4s.conformance.procedures.section8.StaticVerification
import java.lang.Exception
import com.automatak.dnp4s.conformance.procedures.section7.section75.Class0

object Section75121 extends TestProcedure {

  def id = "7.5.1.2.1"
  def description = "All Inputs Assigned to Class 0"

  // TODO - discuss this procedure with TC
  def steps(options: TestOptions) = List(
    "1c" -> List(AppSteps.RequestClass0(0.toByte)),
    "remaining" -> List(Class0.VerifyClass0(0.toByte)))

  /*
7.5.1.2 Test Procedure
7.5.1.2.1 All Inputs Assigned to Class 0
1. If the DUT supports both Binary Counter and Frozen Counter points:
a. Verify that the DUT is configurable such that either the Binary Counter values or the Frozen
Counter values are reported; record this configuration functionality in the test log.
b. Configure the device to report Binary Counter values instead of Frozen Counter values.
2. If the DUT supports Binary Counter points, or if it does not support either Binary Counter or
Frozen Counter points:
a. Configure the DUT such that all installed Binary Input, Binary Output Status, Counter,
Frozen Counter, Analog Input, and Analog Output Status points are assigned to (belong to)
Class 0.
b. Cycle power to the device.
c. Issue a CLASS 0 POLL request.
d. Verify that the response from the DUT contains only the object group, variation and
qualifier combinations specified in Table 7 .
e. If the DUT supports Binary Input points, verify that the response contains appropriate
Binary Input object(s) (Object Group 1), and that all installed Binary Input points are
included in the response.
f. If the DUT supports Binary Output controls, verify that the response contains appropriate
Binary Output Status object(s) (Object Group 10), and that all installed Binary Output points
are included in the response.
g. If the DUT supports Binary Counter points, verify that the response contains appropriate
Counter object(s) (Object Group 20), that all installed Binary Counter points are included in
the response, and that the response does not contain a Frozen Counter object (Object Group
21).
h. If the DUT supports Analog Input points, verify that the response contains appropriate
Analog Input object(s) (Object Group 30), and that all installed Analog Input points are
included in the response.
i. If the DUT supports Analog Output operations, verify that the response contains appropriate
Analog Output Status object(s) (Object Group 40), and that all installed Analog Output
points are included in the response.
j. For at least ten of the reported points of each point type: Verify that the value/state and flags
are correct.
3. If the DUT supports both Binary Counter and Frozen Counter points:
a. Configure the device to report Frozen Counter values instead of Binary Counter values.
4. If the DUT supports Frozen Counter points:
a. Configure the DUT such that all installed Binary Input, Binary Output Status, Counter,
Frozen Counter, Analog Input, and Analog Output Status points are assigned to (belong to)
Class 0.
b. Cycle power to the device.
c. Issue a CLASS 0 POLL request.
d. Verify that the response from the DUT contains only the object group, variation and
qualifier combinations specified in Table 7 .
e. If the DUT supports Binary Input points, verify that the response contains appropriate
Binary Input object(s) (Object Group 1), and that all installed Binary Input points are
included in the response.
f. If the DUT supports Binary Output controls, verify that the response contains appropriate
Binary Output Status object(s) (Object Group 10), and that all installed Binary Output points
are included in the response.
g. If the DUT supports Frozen Counter points, verify that the response contains appropriate
Frozen Counter object(s) (Object Group 21), that all installed Frozen Counter points are
included in the response, and that the response does not contain a Counter object (Object
Group 20).
h. If the DUT supports Analog Input points, verify that the response contains appropriate
Analog Input object(s) (Object Group 30), and that all installed Analog Input points are
included in the response.
i. If the DUT supports Analog Output operations, verify that the response contains appropriate
Analog Output Status object(s) (Object Group 40), and that all installed Analog Output
points are included in the response.
j. For at least ten of the reported points of each point type: Verify that the value/state and flags
are correct.
k. If possible, configure the device to report Binary Counter values instead of Frozen Counter
values.
  */
}
