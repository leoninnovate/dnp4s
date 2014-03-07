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
package com.automatak.dnp4s.conformance.procedures.section7.section74.section741

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.app.objects.{ Group41Var2, CommandStatus, Group12Var1 }
import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app.{ ObjectHeader, AppFunctions, OneByteCountOneByteIndexHeader, TwoByteCountTwoByteIndexHeader }
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.{ DisabledByAnalogOutputControlSupport, RequiresAnalogOutputControlSupport, AppSteps }
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions

object Section74122a extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.1.2.2a"
  def description = "SBO, Operate without Select (supported)"
  override def options = List(validIndex1, validValue1, validIndex2, validValue2)

  def steps(options: TestOptions) = {
    val index1 = getPrimaryValidIndex(options)
    val ao1 = getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index1.toShort).toBytes ::: ao1.toBytes)
    val header1Reply = header1.copy(data = UInt8(index1.toShort).toBytes ::: ao1.copy(status = CommandStatus.NO_SELECT).toBytes)

    val index2 = getSecondaryValidIndex(options)
    val ao2 = getSecondaryValidGroup41Var2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index2).toBytes ::: ao2.toBytes)
    val header2Reply = header2.copy(data = UInt16LE(index2).toBytes ::: ao2.copy(status = CommandStatus.NO_SELECT).toBytes)

    stepsWithErrorReply(header1, header1Reply) ::: stepsWithErrorReply(header2, header2Reply)
  }

  def stepsWithErrorReply(header: ObjectHeader, failure: ObjectHeader) = {
    List(
      "2.a" -> List(AppSteps.SendHeader(AppFunctions.operate, 0, header)),
      "2.b" -> List(AppSteps.VerifyExactResponseWithSeq(0, failure)),
      "2.c" -> List(Operation.expectNoOperation))
  }
}

/*
2. If the DUT supports control output operations:
a. Issue an OPERATE request with Function Code 4 using Object Group 41 Variation 2 to an
installed point using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds by echoing the OPERATE message, but with the status code
set to 2 [No previous matching select].
c. Verify that no Analog Output operates.
d. Repeat this test using Qualifier 0x28 instead of Qualifier 0x17, and a different installed
output point.
*/

object Section74122b extends TestProcedure with DisabledByAnalogOutputControlSupport {

  def id = "7.4.1.2.2b"
  def description = "SBO, Operate without Select (not supported)"
  override def options = List(AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {

    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)

    val header1 = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(0).toBytes ::: ao.toBytes)
    val header2 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(0).toBytes ::: ao.toBytes)

    Operation.stepsUnsupportedOrUninstalled(AppFunctions.operate, header1) ::: Operation.stepsUnsupportedOrUninstalled(AppFunctions.operate, header2)
  }

}

/*
7.4.1.2.2 SBO, Operate without Select
1. If the DUT does not support Analog Output operations:
a. Issue an OPERATE request with Function Code 4 using Object Group 41 Variation 2 to
point 0 using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds with an Error Response with IIN2.1 set.
c. Repeat this test using Qualifier 0x28.
*/ 