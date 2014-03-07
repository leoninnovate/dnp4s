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

import com.automatak.dnp4s.conformance.{ TestProcedure, TestOptions }
import com.automatak.dnp4s.conformance.procedures.{ DisabledByAnalogOutputControlSupport, RequiresAnalogOutputControlSupport }
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions
import com.automatak.dnp4s.dnp3.app.{ TwoByteCountTwoByteIndexHeader, OneByteCountOneByteIndexHeader, AppFunctions }
import com.automatak.dnp4s.dnp3.app.objects.Group41Var2
import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }
import com.automatak.dnp4s.conformance.procedures.section7.Operation

object Section74121a extends TestProcedure with DisabledByAnalogOutputControlSupport {
  def id = "7.4.1.2.1a"
  def description = "Select Before Operate (SBO) (no support)"
  override def options = List(AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {

    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(0).toBytes ::: ao.toBytes)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(0).toBytes ::: ao.toBytes)

    Operation.stepsUnsupportedOrUninstalled(AppFunctions.select, header1) ::: Operation.stepsUnsupportedOrUninstalled(AppFunctions.select, header2)
  }
}

/*
7.4.1.2.1 Select Before Operate (SBO)
1. If the DUT does not support Analog Output operations:
a. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to point 0
using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds with an Error Response with IIN2.1 set.
c. Repeat this test using Qualifier 0x28.
*/

object Section74121b extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.1.2.1b"
  def description = "Select Before Operate (SBO) (support)"
  override def options =
    List(validIndex1, validValue1, validIndex2, validValue2)

  def steps(options: TestOptions) = {

    val index1 = getPrimaryValidIndex(options)
    val ao1 = getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index1.toShort).toBytes ::: ao1.toBytes)

    val index2 = getSecondaryValidIndex(options)
    val ao2 = getSecondaryValidGroup41Var2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index2).toBytes ::: ao2.toBytes)

    Operation.stepsValidSBO(header1) ::: Operation.stepsValidSBO(header2)
  }

}

/*
2. If the DUT supports Analog Output operations:
a. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to an
installed point using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Immediately issue a matching OPERATE request with Function Code 4 and the application
sequence number incremented by 1 (modulo 16).
d. Verify that the DUT responds by echoing the OPERATE message, that the status code is 0,
and that no error IINs are set.
e. Verify that the selected Analog Output operates.
f. Repeat this test using Qualifier 0x28 instead of Qualifier 0x17, and a different installed
point.
*/
