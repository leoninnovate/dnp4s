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
package com.automatak.dnp4s.conformance.procedures.section7.section74.section743

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.RequiresAnalogOutputControlSupport
import com.automatak.dnp4s.conformance.procedures.section7.DirectOperateNoAckSteps
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions
import objects.Group41Var2

object Section74322 extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.3.2.2"
  def description = "Direct Operate No Acknowledgment, to an Uninstalled Point"
  override def options = List(validIndex1, validValue1, validIndex2, validValue2)

  def steps(options: TestOptions) = {

    val index1 = getPrimaryValidIndex(options)
    val ao1 = getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index1.toShort).toBytes ::: ao1.toBytes)

    val index2 = getSecondaryValidIndex(options)
    val ao2 = getSecondaryValidGroup41Var2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index2).toBytes ::: ao2.toBytes)

    DirectOperateNoAckSteps.stepsUninstalledOrDisable(header1) ::: DirectOperateNoAckSteps.stepsUninstalledOrDisable(header2)
  }

}

/*
7.4.3.2.2 Direct Operate No Acknowledgment, to an Uninstalled Point
1. If the DUT supports Analog Output operations:
a. Issue a DIRECT OPERATE, NO ACKNOWLEDGMENT request with Function Code 6
using Object Group 41 Variation 2 to an uninstalled point using Qualifier 0x17 and an value.
b. Verify that the DUT does not respond.
c. Verify that no Analog Output operates.
d. Repeat the test using Qualifier 0x28 and a different uninstalled point.
*/
