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

import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }
import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.conformance.procedures.{ DisabledByAnalogOutputControlSupport, RequiresAnalogOutputControlSupport }
import com.automatak.dnp4s.conformance.procedures.section7.{ DirectOperateSteps, Operation }
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions
import objects.Group41Var2

object Section74221a extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.2.2.1a"
  def description = "Direct Operate (supported)"

  override def options = List(validIndex1, validValue1, validIndex2, validValue2)

  def steps(options: TestOptions) = {

    val index1 = getPrimaryValidIndex(options)
    val ao1 = getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index1.toShort).toBytes ::: ao1.toBytes)

    val index2 = getSecondaryValidIndex(options)
    val ao2 = getSecondaryValidGroup41Var2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index2).toBytes ::: ao2.toBytes)

    DirectOperateSteps.correctOperation(header1) ::: DirectOperateSteps.correctOperation(header2)
  }
}

object Section74221b extends TestProcedure with DisabledByAnalogOutputControlSupport {

  def id = "7.4.2.2.1b"
  def description = "Direct Operate (not supported)"
  override def options = List(AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {
    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(0).toBytes ::: ao.toBytes)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(0).toBytes ::: ao.toBytes)
    Operation.stepsUnsupportedOrUninstalled(AppFunctions.directOp, header1) ::: Operation.stepsUnsupportedOrUninstalled(AppFunctions.directOp, header2)
  }
}

/*
7.4.2.2.1 Direct Operate
1. If the DUT does not support Analog Output operations:
a. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 41 Variation
2 to point 0 using Qualifier 0x17 and an appropriate value.
b. Verify that the device responds with an Error Response with IIN2.1 set.
c. Repeat this test using Qualifier 0x28.
2. If the DUT supports Analog Output operations:
a. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 41 Variation
2 to an installed point using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds by echoing the DIRECT OPERATE message, that the status
code is 0, and that no error IINs are set.
c. Verify that the specified Analog Output operates.
d. Repeat this test using Qualifier 0x28 and a different installed control point.
*/
