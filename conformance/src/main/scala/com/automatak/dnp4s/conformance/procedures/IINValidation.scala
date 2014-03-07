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
package com.automatak.dnp4s.conformance.procedures

import com.automatak.dnp4s.conformance.TestStep
import com.automatak.dnp4s.dnp3.app.{ Apdu, IIN }
import com.automatak.dnp4s.conformance.procedures.AppSteps.ApduValidation

trait RequireIIN extends ApduValidation with TestStep {

  def requiredMasks: List[IIN]

  abstract override def description =
    super.description ::: List("Verify that the response contains indications " + requiredMasks.map(iin => iin.toString).mkString(" or "))

  abstract override def validate(apdu: Apdu) = {
    super.validate(apdu)
    apdu.iin match {
      case Some(iin) =>
        val noMatch = requiredMasks.forall(ex => (ex & iin) != ex)
        if (noMatch) throw new Exception("Missing some expected IIN bits from set: " + requiredMasks.toString)
      case None =>
        throw new Exception("No IIN present")
    }
  }
}

trait ProhibitIIN extends ApduValidation with TestStep {

  def prohibitedMask: IIN

  abstract override def description =
    super.description ::: List("Verify that the response does NOT contain indications " + prohibitedMask.toString)

  abstract override def validate(apdu: Apdu) = {
    super.validate(apdu)
    apdu.iin match {
      case Some(iin) =>
        val disallowed = iin & prohibitedMask
        if (disallowed.nonEmpty) throw new Exception("Prohibited indications present: " + disallowed)
      case None =>
        throw new Exception("No IIN present")
    }
  }

}

trait AllowIIN extends ApduValidation with TestStep {

  def allowedMask: IIN

  abstract override def description =
    super.description ::: List("Verify that the response does not contain indications other than " + allowedMask.toString)

  abstract override def validate(apdu: Apdu) = {
    super.validate(apdu)
    apdu.iin match {
      case Some(iin) =>
        val violations = iin - allowedMask
        if (violations.nonEmpty) throw new Exception("Prohibited indications were present: " + violations)
      case None =>
        throw new Exception("No IIN present")
    }
  }

}
