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
package com.automatak.dnp4s.conformance.procedures.section8.section86

import com.automatak.dnp4s.conformance.{ TestOptions, TestProcedure }
import com.automatak.dnp4s.dnp3.app._
import objects.Group60Var1
import com.automatak.dnp4s.conformance.procedures.{ RequireIIN, AllowIIN, AppSteps }
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ NoHeaders, SingleFragment }

object Section8622 extends TestProcedure {

  def id = "8.6.2.2"
  def description = "Bad Function"

  case class VerifyErrorResponse(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with AllowIIN with RequireIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN17_DEVICE_RESTART, IIN.IIN14_NEED_TIME), IIN.IIN20_FUNC_NOT_SUPPORTED)
    def requiredMasks = List(IIN(0, IIN.IIN20_FUNC_NOT_SUPPORTED))
  }

  case class VerifyNoErrors(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with AllowIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN17_DEVICE_RESTART, IIN.IIN14_NEED_TIME), 0)
  }

  def steps(options: TestOptions) = List(
    "1" -> List(AppSteps.SendApdu(Apdu(AppCtrl(true, true, false, false, 0), 0x70.toByte, None, List(AllObjectsHeader(Group60Var1))))),
    "2" -> List(VerifyErrorResponse(0)),
    "3" -> List(AppSteps.RequestClass0(0)),
    "4" -> List(VerifyNoErrors(0)))
}

/*
8.6.2.2 Test Procedure
1. Issue a request for Object 60 Variation 1, Qualifier 0x06, using a Function Code of 0x70.
2. Verify that the device responds with an Error Response with IIN2-0 set indicating that the device has received a message
containing a bad function code.
3. Issue a request for Object 60 Variation 1, Qualifier 0x06, using a Function Code of 0x01.
4. Verify that the device responds with none of the Error IIN bits set.
*/
