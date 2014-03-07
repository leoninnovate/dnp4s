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

import com.automatak.dnp4s.dsl.{ UInt16LE, UInt8 }
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group12Var1, CommandStatus }
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.RequiresGroup12Var1Support
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section721210 extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.2.1.2.10"
  def description = "SBO, Select/Operate Mismatch (Qualifier)"
  override def options = CrobOptions.validIndex1 :: CrobOptions.validCrob1

  def steps(options: TestOptions) = {

    val index = CrobOptions.getValidIndex1(options)
    val crob = CrobOptions.getCrob1(options)

    val headerOut1 = TwoByteCountTwoByteIndexHeader(Group12Var1, UInt16LE(1), UInt16LE(index).toBytes ::: crob.toBytes)
    val headerOut2 = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(index.toShort).toBytes ::: crob.toBytes)
    val headerFailure = headerOut2.copy(data = UInt8(index.toShort).toBytes ::: crob.copy(status = CommandStatus.NO_SELECT).toBytes)

    Operation.stepsBadSBO(headerOut1, headerOut2, headerFailure)
  }

  /*
7.2.1.2.10 SBO, Select/Operate Mismatch (Qualifier)
1. If the DUT supports Binary Output controls:
a. Issue a SELECT request with Function Code 3 using Object Group 12 Variation 1 to an
installed point using Qualifier 0x28, a control code supported by the device, and an
appropriate on time/off time.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Immediately issue an OPERATE request with Function Code 4 and the application sequence
number incremented by 1 (modulo 16), matching the SELECT message except that the
Qualifier is set to 0x17.
d. Verify that the DUT echoes the OPERATE message, but with the status code set to 2 [No
previous matching select].
e. Verify that no control point operates.
*/
}

