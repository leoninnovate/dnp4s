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
package com.automatak.dnp4s.conformance.procedures.section5.section56

import com.automatak.dnp4s.conformance.{ TestProcedure, TestOptions }
import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.conformance.procedures.{ Prompts, RequiresSelfAddressSupport, AppSteps }

object Section562a extends TestProcedure with RequiresSelfAddressSupport {

  def id = "5.6.2a"
  def description = "Self-Address Support (steps 1c - 1e)"

  override def prompts = Prompts.enableSelfAddressSupport :: Prompts.cyclePower :: Nil

  def steps(options: TestOptions) = List(
    "1.c" -> List(Section56.RequestClass0WithAddress(UInt16LE(0xFFFC))),
    "1.d" -> List(new AppSteps.ReadAnyValidResponseUnconfirmedWithSeq(0)))

  /*
5.6.2 Test Procedure
1. If the device supports self-address:
a. Configure the device to enable self-address.
b. Cycle the power to the DUT.
c. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4 to destination
address 0xFFFC.
d. Verify that a valid response is received; the device must respond with its own unique address
as the source address instead of 0xFFFC.
e. Configure the device to disable self-address.
f. Cycle the power to the DUT.
g. Issue a CLASS 0 POLL request with Data Link Layer Control octet 0xC4 to destination
address 0xFFFC.
h. Verify that no response is generated.
*/
}
