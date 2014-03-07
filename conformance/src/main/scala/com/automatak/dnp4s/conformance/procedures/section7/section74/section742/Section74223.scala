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
package com.automatak.dnp4s.conformance.procedures.section7.section74.section742

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.{ RequiresDisableAnalogOutput, Prompts }
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions
import objects.Group41Var2

object Section74223 extends TestProcedure with RequiresDisableAnalogOutput {

  def id = "7.4.2.2.3"
  def description = "Direct Operate, All Analog Outputs Uninstalled or Disabled"
  override def prompts = Prompts.disableAnalogOutput :: Prompts.cyclePower :: Nil
  override def options = List(AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {
    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(0).toBytes ::: ao.toBytes)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(0).toBytes ::: ao.toBytes)
    Operation.stepsUninstalledOrDisabled(AppFunctions.directOp, header1) ::: Operation.stepsUninstalledOrDisabled(AppFunctions.directOp, header2)
  }

}

/*
7.4.2.2.3 Direct Operate, All Analog Outputs Uninstalled or Disabled
1. If the DUT supports Analog Output operations, and it is possible to uninstall or disable all
Analog Output points:
a. Configure the DUT such that all Analog Output points are uninstalled or disabled.
b. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 41 Variation
2 to point 0 using Qualifier 0x17 and an appropriate value.
c. Verify that the DUT responds by returning an Error Response with IIN2.1 or IIN2.2 set.
d. Verify that no Analog Output operates.
e. Repeat this test using Qualifier 0x28.
f. Configure the DUT such that at least one, and preferably ten or more, Analog Output
point(s) are installed or enabled.
*/
