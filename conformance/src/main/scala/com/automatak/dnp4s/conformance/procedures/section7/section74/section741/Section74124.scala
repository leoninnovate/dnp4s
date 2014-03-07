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

import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group41Var2, CommandStatus }
import com.automatak.dnp4s.conformance.procedures.RequiresAnalogOutputControlSupport
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions

object Section74124 extends TestProcedure with RequiresAnalogOutputControlSupport {

  def id = "7.4.1.2.4"
  def description = "SBO, Operate Issued After Timeout"
  override def options = List(Operation.selectTimeout, AnalogOutputOptions.validIndex1, AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {

    val index = AnalogOutputOptions.getPrimaryValidIndex(options)
    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)
    val timeoutMs = Operation.getSelectTimeout(options)

    val header = TwoByteCountTwoByteIndexHeader(Group41Var2, UInt16LE(1), UInt16LE(index).toBytes ::: ao.toBytes)
    val reply = header.copy(data = UInt16LE(index).toBytes ::: ao.copy(status = CommandStatus.TIMEOUT))

    Operation.stepsSelectTimeout(timeoutMs, header, reply)
  }

  /*
7.4.1.2.4 SBO, Execute Issued After Timeout
1. If the DUT supports Analog Output operations:
a. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to an
installed point using Qualifier 0x28 and an appropriate value.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Wait 1 second past the configured maximum Maximum Time between Select and Operate
delay time.
d. Immediately issue a matching OPERATE request with Function Code 4 and the application
sequence number incremented by 1 (modulo 16).
e. Verify that the DUT echoes the OPERATE message, but with the status code set to 1
[Execute received after timeout].
e. Verify that no Analog Output operates.
 */

}
