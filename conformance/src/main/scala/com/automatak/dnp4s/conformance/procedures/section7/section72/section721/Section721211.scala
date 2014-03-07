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
package com.automatak.dnp4s.conformance.procedures.section7.section72.section721

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }

import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3.app._
import objects.Group12Var1
import com.automatak.dnp4s.conformance.procedures._
import section7.section72.CrobOptions
import section7.Operation
import com.automatak.dnp4s.dnp3.app.TwoByteCountTwoByteIndexHeader

object Section721211 extends TestProcedure with RequiresDisableControl {

  def id = "7.2.1.2.11"
  def description = "SBO, All Control Points Uninstalled or Disabled"
  override def prompts = Prompts.disableControl :: Prompts.cyclePower :: Nil
  override def options = CrobOptions.validCrob1

  def steps(options: TestOptions) = {
    val crob = CrobOptions.getCrob1(options)
    val header = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(0).toBytes ::: crob.toBytes)
    Operation.stepsUninstalledOrDisabled(AppFunctions.select, header)
  }

  /*
7.2.1.2.11 SBO, All Control Points Uninstalled or Disabled
1. If the DUT supports Binary Output controls, and it is possible to uninstall or disable all binary
control points:
a. Configure the DUT such that all Binary Output points are uninstalled or disabled.
b. Issue a SELECT request with Function Code 3 using Object Group 12 Variation 1 to point 0
using Qualifier 0x28, a control code supported by the device, and an appropriate on time/off
time.
c. Verify that the DUT responds by returning an Error Response with IIN2.1 or IIN2.2 set.
d. Verify that no control point operates.
e. Configure the DUT such that at least one, and preferably ten or more, Binary Output
point(s) are installed or enabled.
*/
}
