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
import com.automatak.dnp4s.conformance.procedures.RequiresAnalogOutputControlSupport
import com.automatak.dnp4s.conformance.procedures.section7.Operation
import com.automatak.dnp4s.conformance.procedures.section7.section74.AnalogOutputOptions
import objects.Group41Var2

object Section741212 extends TestProcedure with RequiresAnalogOutputControlSupport {

  import AnalogOutputOptions._

  def id = "7.4.1.2.12"
  def description = "SBO, Operate Retry (Same Sequence Number)"
  override def options = List(validIndex1, validValue1)

  def steps(options: TestOptions) = {

    val index = getPrimaryValidIndex(options)
    val ao = getPrimaryValidGroup41Var2(options)
    val header = OneByteCountOneByteIndexHeader(Group41Var2, UInt8(1), UInt8(index.toShort).toBytes ::: ao.toBytes)
    Operation.stepsOperateRetrySameSequenceNumber(header)
  }
}

/*
7.4.1.2.12 SBO, Operate Retry (Same Sequence Number)
1. If the DUT supports Analog Output operations:
a. Issue a SELECT request with Function Code 3 using Object Group 41 Variation 2 to an
installed point using Qualifier 0x17 and an appropriate value.
b. Verify that the DUT responds by echoing the SELECT message, that the status code is 0,
and that no error IINs are set.
c. Immediately issue a matching OPERATE request with Function Code 4 and the application
sequence number incremented by 1 (modulo 16).
d. Verify that the DUT responds by echoing the OPERATE message, that the status code is 0,
and that no error IINs are set.
e. Verify that the selected Analog Output operates.
f. Immediately issue the OPERATE message again using the same Application Layer sequence
number.
g. Verify that the DUT responds by echoing the OPERATE message, that the status code is 0,
and that no error IINs are set.
h. Verify that no Analog Output operates.s
*/
