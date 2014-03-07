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
package com.automatak.dnp4s.conformance.procedures.section7.section72.section723

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app._
import objects.Group12Var1
import com.automatak.dnp4s.conformance.procedures.RequiresGroup12Var1Support
import com.automatak.dnp4s.conformance.procedures.section7.DirectOperateNoAckSteps
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section72322 extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.2.3.2.2"
  def description = "Direct Operate No Acknowledgment, to an Uninstalled Point"
  override def options = (CrobOptions.invalidIndex1 :: CrobOptions.validCrob1) ::: (CrobOptions.invalidIndex2 :: CrobOptions.validCrob2)

  def steps(options: TestOptions) = {

    val index1 = CrobOptions.getInvalidIndex1(options)
    val crob1 = CrobOptions.getCrob1(options)
    val header1 = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(index1.toShort).toBytes ::: crob1.toBytes)

    val index2 = CrobOptions.getInvalidIndex2(options)
    val crob2 = CrobOptions.getCrob2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(index2).toBytes ::: crob2.toBytes)

    DirectOperateNoAckSteps.stepsUninstalledOrDisable(header1) ::: DirectOperateNoAckSteps.stepsUninstalledOrDisable(header2)
  }

}

/*
7.2.3.2.2 Direct Operate No Acknowledgment, to an Uninstalled Point
1. If the DUT supports Binary Output controls:
a. Issue a DIRECT OPERATE, NO ACKNOWLEDGMENT request with Function Code 6
using Object Group 12 Variation 1 to an uninstalled point using Qualifier 0x17, a control
code supported by the device, and an appropriate on time/off time.
b. Verify that the DUT does not respond.
c. Verify that no control operates.
d. Repeat this test using Qualifier 0x28 and a different uninstalled point.
7.2.3.2.3
*/
