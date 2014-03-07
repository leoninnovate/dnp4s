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
import objects.{ CommandStatus, Group12Var1 }
import com.automatak.dnp4s.conformance.procedures.{ RequiresGroup12Var1Support, AppSteps }
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section721217 extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.2.1.2.17"
  def description = "Reset After Error"
  override def options = CrobOptions.validIndex1 :: CrobOptions.validIndex2 :: CrobOptions.validCrob1

  def steps(options: TestOptions) = {

    val index1 = CrobOptions.getValidIndex1(options)
    val index2 = CrobOptions.getValidIndex2(options)
    val crob = CrobOptions.getCrob1(options)

    val header1 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(index1).toBytes ::: crob.toBytes)
    val header2 = header1.copy(data = UInt16LE(index2).toBytes ::: crob.toBytes)
    val header3 = header2.copy(data = UInt16LE(index2).toBytes ::: crob.copy(status = CommandStatus.NO_SELECT))

    List(
      "1.a" -> List(AppSteps.SendHeader(AppFunctions.select, 0.toByte, header1)),
      "1.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header1)),
      "1.c" -> List(AppSteps.SendHeader(AppFunctions.operate, 1.toByte, header2)),
      "1.d" -> List(AppSteps.VerifyExactResponseWithSeq(1.toByte, header3)),
      "1.e" -> List(Operation.expectNoOperation),
      "1.f" -> List(AppSteps.SendHeader(AppFunctions.select, 1.toByte, header1)),
      "1.g" -> List(AppSteps.VerifyExactResponseWithSeq(1.toByte, header1)),
      "1.h" -> List(AppSteps.SendHeader(AppFunctions.operate, 2.toByte, header1)),
      "1.i" -> List(AppSteps.VerifyExactResponseWithSeq(2, header1)),
      "1.j" -> List(Operation.expectOperation))
  }
}

/*
7.2.1.2.17SBO, Reset After Error
1. If the DUT supports Binary Output controls:
a. Issue a SELECT request with Function Code 3 using Object Group 12 Variation 1 to an
installed point using Qualifier 0x28, a control code supported by the device, and an
appropriate on time/off time.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Immediately issue a valid OPERATE request matching the SELECT request except that the
point number is different.
d. Verify that the DUT echoes the OPERATE message, but with a status code value of 2 [No
previous matching select].
e. Verify that no control point operates.
f. Immediately issue a SELECT request with Function Code 3 using Object Group 12
Variation 1 to a different installed point using Qualifier 0x28, a control code supported by
the device, and an appropriate on time/off time.
g. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
h. Immediately issue a matching OPERATE request with Function Code 4 and the application
sequence number incremented by 1 (modulo 16).
i. Verify that the DUT responds by echoing the OPERATE message, that the status code is 0,
and that no error IINs are set.
j. Verify that the selected control operates.
*/
