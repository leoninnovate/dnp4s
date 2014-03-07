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
package com.automatak.dnp4s.conformance.procedures.section7

import com.automatak.dnp4s.dnp3.app.{ IIN, AppFunctions, ObjectHeader }
import com.automatak.dnp4s.conformance.procedures.{ RequireIIN, AppSteps }

object DirectOperateSteps {

  def correctOperation(header: ObjectHeader) = List(
    "2.a" -> List(AppSteps.SendHeader(AppFunctions.directOp, 0.toByte, header)),
    "2.b" -> List(AppSteps.VerifyExactResponseWithNoErrorIIN(0.toByte, header)),
    "2.c" -> List(Operation.expectOperation))

  def uninstalledIndex(header: ObjectHeader, reply: ObjectHeader) = {

    object VerifyFailure extends AppSteps.VerifyExactResponseWithSeq(0.toByte, reply) with RequireIIN {
      def requiredMasks = List(IIN(0, IIN.IIN22_PARAM_ERROR))
    }

    List(
      "1.a" -> List(AppSteps.SendHeader(AppFunctions.directOp, 0.toByte, header)),
      "1.b" -> List(VerifyFailure),
      "1.c" -> List(Operation.expectNoOperation))
  }
}
