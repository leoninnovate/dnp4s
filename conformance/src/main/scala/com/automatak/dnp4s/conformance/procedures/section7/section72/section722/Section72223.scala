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
package com.automatak.dnp4s.conformance.procedures.section7.section72.section722

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app._
import objects.Group12Var1
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresDisableControl }
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section72223 extends TestProcedure with RequiresDisableControl {

  def id = "7.2.2.2.3"
  def description = "Direct Operate, All Control Points Uninstalled or Disabled"
  override def prompts = Prompts.disableControl :: Nil
  override def options = CrobOptions.validCrob1

  def steps(options: TestOptions) = {

    val crob = CrobOptions.getCrob1(options)

    val header1 = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(0).toBytes ::: crob.toBytes)
    val header2 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(0).toBytes ::: crob.toBytes)

    Operation.stepsUninstalledOrDisabled(AppFunctions.directOp, header1) ::: Operation.stepsUninstalledOrDisabled(AppFunctions.directOp, header2)
  }

}

/*
7.2.2.2.3 Direct Operate, All Control Points Uninstalled or Disabled
1. If the DUT supports Binary Output controls, and it is possible to uninstall or disable all binary
control points:
a. Configure the DUT such that all Binary Output points are uninstalled or disabled.
b. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 12 Variation
1 to point 0 using Qualifier 0x17, a control code supported by the device, and an appropriate
on time/off time.
c. Verify that the DUT responds by returning an Error Response with IIN2.1 or IIN2.2 set.
d. Verify that no control operates.
e. Repeat this test using Qualifier 0x28.
f. Configure the DUT such that at least one, and preferably ten or more, Binary Output
point(s) are installed or enabled.
*/
