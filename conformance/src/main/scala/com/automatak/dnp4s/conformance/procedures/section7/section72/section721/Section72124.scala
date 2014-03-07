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

import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group12Var1, CommandStatus }
import com.automatak.dnp4s.conformance.procedures.RequiresGroup12Var1Support
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section72.CrobOptions

object Section72124 extends TestProcedure with RequiresGroup12Var1Support {

  def id = "7.2.1.2.4"
  def description = "SBO, Operate Issued After Timeout"
  override def options = Operation.selectTimeout :: CrobOptions.validIndex1 :: CrobOptions.validCrob1

  def steps(options: TestOptions) = {

    val crob = CrobOptions.getCrob1(options)
    val index = CrobOptions.getValidIndex1(options)
    val timeoutMs = options.getInteger("command.selectTimeout")
    val header = OneByteCountOneByteIndexHeader(Group12Var1, UInt8(1), UInt8(index.toShort).toBytes ::: crob.toBytes)
    val reply = header.copy(data = UInt8(index.toShort).toBytes ::: crob.copy(status = CommandStatus.TIMEOUT).toBytes)

    Operation.stepsSelectTimeout(timeoutMs, header, reply)
  }

  /*
7.2.1.2.4 SBO, Operate Issued After Timeout
1. If the DUT supports Binary Output controls:
a. Issue a SELECT request with Function Code 3 using Object Group 12 Variation 1 to an
installed point using Qualifier 0x17, a control code supported by the device, and an
appropriate on time/off time.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Wait 1 second past the configured Maximum Time between Select and Operate delay time.
d. Immediately issue a matching OPERATE request with Function Code 4 and the application
sequence number incremented by 1 (modulo 16).
e. Verify that the DUT echoes the OPERATE message, but with the status code set to 1
[Execute received after timeout].
f. Verify that no control point operates.
*/

}
