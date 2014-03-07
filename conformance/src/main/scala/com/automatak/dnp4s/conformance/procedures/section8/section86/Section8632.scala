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
import com.automatak.dnp4s.conformance.procedures.{ RequireIIN, AllowIIN, AppSteps }
import com.automatak.dnp4s.conformance.procedures.AppSteps.{ NoHeaders, SingleFragment }

object Section8632 extends TestProcedure {

  def id = "8.6.3.2"
  def description = "Object Unknown"

  case class VerifyErrorResponse(seq: Byte, required: IIN) extends AppSteps.ReadAnyValidResponseUnconfirmed with SingleFragment with NoHeaders with AllowIIN with RequireIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN17_DEVICE_RESTART, IIN.IIN14_NEED_TIME), 0) | required
    def requiredMasks = List(required)
  }

  case class VerifyNoErrors(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with AllowIIN {
    def allowedMask = IIN(IIN.field(IIN.IIN17_DEVICE_RESTART, IIN.IIN14_NEED_TIME), 0)
  }

  object MockGroup0 extends ObjectGroup {
    def group: Byte = 0
    def objects = List(Var0)
    object Var0 extends SizelessGroupVariation(MockGroup0, 0) with InformationGroupVariation
  }

  private def unknownGroupVariation = Apdu(AppCtrl(true, true, false, false, 0), 0x01, None, List(AllObjectsHeader(MockGroup0.Var0)))

  def steps(options: TestOptions) = List(
    "1" -> List(AppSteps.SendApdu(unknownGroupVariation)),
    "2" -> List(VerifyErrorResponse(0, IIN(0, IIN.IIN21_OBJECT_UNKNOWN))),
    "3" -> List(AppSteps.RequestClass0(0)),
    "4" -> List(VerifyNoErrors(0)))
}

/*
8.6.3.2 Test Procedure
1. Issue a request for Object 0 Variation 0, Function Code 0x01.
2. Verify that the device responds with an Error Response with IIN2-1 set indicating that the device has received a message
containing an unknown Object code.
3. Issue a request for Object 60 Variation 1.
4. Verify that the device responds with none of the Error IIN bits set.
*/
