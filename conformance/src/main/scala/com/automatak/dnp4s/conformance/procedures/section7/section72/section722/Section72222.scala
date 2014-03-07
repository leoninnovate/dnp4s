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
import objects.{ CommandStatus, Group12Var1 }
import com.automatak.dnp4s.conformance.procedures.RequiresGroup12Var1Support
import com.automatak.dnp4s.conformance.procedures.section7.DirectOperateSteps
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section72222 extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.2.2.2.2"
  def description = "Direct Operate to Uninstalled Point"
  override def options = (CrobOptions.invalidIndex1 :: CrobOptions.validCrob1) ::: (CrobOptions.invalidIndex2 :: CrobOptions.validCrob2)

  def steps(options: TestOptions) = {

    val index1 = CrobOptions.getInvalidIndex1(options)
    val crob1 = CrobOptions.getCrob1(options)
    val header1 = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(index1.toShort).toBytes ::: crob1.toBytes)
    val reply1 = header1.copy(data = UInt8(index1.toShort).toBytes ::: crob1.copy(status = CommandStatus.NOT_SUPPORTED).toBytes)

    val index2 = CrobOptions.getInvalidIndex2(options)
    val crob2 = CrobOptions.getCrob2(options)
    val header2 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(index2).toBytes ::: crob2.toBytes)
    val reply2 = header2.copy(data = UInt16LE(index2).toBytes ::: crob2.copy(status = CommandStatus.NOT_SUPPORTED).toBytes)

    DirectOperateSteps.uninstalledIndex(header1, reply1) ::: DirectOperateSteps.uninstalledIndex(header2, reply2)
  }
}

/*
7.2.2.2.2 Direct Operate to Uninstalled Point
1. If the DUT supports Binary Output controls:
a. Issue a DIRECT OPERATE request with Function Code 5 using Object Group 12 Variation
1 to an uninstalled point using Qualifier 0x17, a control code supported by the device, and
an appropriate on time/off time.
b. Verify that the DUT responds by returning an Error Response with IIN2.2 set and a status
code of 4.
c. Verify that no control operates.
d. Repeat this test using Qualifier 0x28 and a different uninstalled point.
*/
