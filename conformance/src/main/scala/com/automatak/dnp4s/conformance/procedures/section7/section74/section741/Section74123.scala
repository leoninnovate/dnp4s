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

import com.automatak.dnp4s.dsl.{ UInt8, UInt16LE }
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group41Var2, CommandStatus }
import com.automatak.dnp4s.conformance.procedures.RequiresAnalogOutputControlSupport
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions

object Section74123 extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.1.2.3"
  def description = "Analog Output, SBO, to Uninstalled Point"

  override def options = List(invalidIndex1, validValue1, invalidIndex2, validValue2)

  def steps(options: TestOptions) = {

    val index1 = getPrimaryInvalidIndex(options)
    val ao1 = getPrimaryValidGroup41Var2(options)
    val header1 = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index1.toShort).toBytes ::: ao1.toBytes)
    val header1Reply = header1.copy(data = UInt8(index1.toShort).toBytes ::: ao1.copy(status = CommandStatus.NOT_SUPPORTED).toBytes)

    val index2 = getSecondaryInvalidIndex(options)
    val ao2 = getSecondaryValidGroup41Var2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index2).toBytes ::: ao2.toBytes)
    val header2Reply = header2.copy(data = UInt16LE(index2).toBytes ::: ao2.copy(status = CommandStatus.NOT_SUPPORTED).toBytes)

    Operation.stepsSelectUninstalledControl(header1, header1Reply) ::: Operation.stepsSelectUninstalledControl(header2, header2Reply)
  }

}

/*
7.4.1.2.3 SBO, to Uninstalled Point
1. If the DUT supports Analog Output operations:
a. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to an
uninstalled point using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds by echoing the SELECT message, but with IIN2.2 set and a
status code of 4.
c. Verify that no Analog Output operates.
d. Repeat this test using Qualifier 0x28 instead of Qualifier 0x17, and a different uninstalled
point.
*/
