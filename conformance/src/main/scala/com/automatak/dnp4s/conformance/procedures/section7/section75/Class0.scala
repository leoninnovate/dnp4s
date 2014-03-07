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
package com.automatak.dnp4s.conformance.procedures.section7.section75

import com.automatak.dnp4s.conformance.procedures.AppSteps.ApduValidation
import com.automatak.dnp4s.conformance.TestStep
import com.automatak.dnp4s.dnp3.app.{ IIN, Apdu }
import com.automatak.dnp4s.conformance.procedures.section8.StaticVerification
import java.lang.Exception
import com.automatak.dnp4s.conformance.procedures.{ AllowIIN, AppSteps }

object Class0 {

  trait VerifyClass0Data extends ApduValidation with TestStep {
    abstract override def description = super.description ::: List("Verify the response only contains Class0 headers and objects")
    abstract override def validate(apdu: Apdu): Unit = {
      super.validate(apdu)
      apdu.headers.foreach { header =>
        StaticVerification.validate(header).foreach(err => throw new Exception(err))
      }
    }
  }

  case class VerifyClass0(seq: Byte) extends AppSteps.ReadAnyValidResponseUnconfirmed with AllowIIN with VerifyClass0Data {
    def allowedMask = IIN(IIN.field(IIN.IIN14_NEED_TIME, IIN.IIN17_DEVICE_RESTART), 0.toByte)
  }
}
