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

import com.automatak.dnp4s.dsl.UInt8
import com.automatak.dnp4s.dnp3.app._
import objects.{ Group41Var2, CommandStatus }
import com.automatak.dnp4s.conformance.procedures.RequiresAnalogOutputControlSupport
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions

object Section74125 extends TestProcedure with RequiresAnalogOutputControlSupport {

  def id = "7.4.1.2.5"

  def description = "SBO, Select/Operate Mismatch (Interruption)"

  override def options = List(AnalogOutputOptions.validIndex1, AnalogOutputOptions.validValue1)

  def steps(options: TestOptions) = {

    val index = AnalogOutputOptions.getPrimaryValidIndex(options)
    val ao = AnalogOutputOptions.getPrimaryValidGroup41Var2(options)
    val header = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index.toShort).toBytes ::: ao.toBytes)
    val reply = header.copy(data = UInt8(index.toShort).toBytes ::: ao.copy(status = CommandStatus.NO_SELECT))

    Operation.stepsSBOInterruption(header, reply)
  }

  /*
7.4.1.2.5 SBO, Select/Operate Mismatch (Interruption)
1. If the DUT supports Analog Output controls:
a. Configure the Select-Operate delay time so that steps Issue a SELECT request with
Function Code 3 using Object Group 12 Variation 1 to an installed point using Qualifier
0x17, a control code supported by the device, and an appropriate on time/off time. to
Immediately issue an OPERATE request matching the SELECT request, with Function
Code 4 and the application sequence number identical to the application sequence number
contained in the EVENTS POLL. below can be performed without exceeding the Select-
Operate delay time.
b. Empty the device of all pending events by issuing an EVENTS POLL request (and issuing a
subsequent Application Layer Confirmation if the CON bit is set in the response from the
device). Repeat as necessary until the device responds with a Null Response.
c. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to an
installed point using Qualifier 0x17 and an appropriate value.
d. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
e. Immediately issue an EVENTS POLL, with the application sequence number incremented
by 1 (modulo 16) from the application sequence number contained in the SELECT request.
f. Verify that the DUT responds with a Null Response.
g. Immediately issue an OPERATE request matching the SELECT request, with Function
Code 4 and the application sequence number identical to the application sequence number
contained in the EVENTS POLL.
h. Verify that the device echoes the OPERATE message, but with the status code set to 2 [No
previous matching select].
i. Verify that no control point operates.
*/

}
