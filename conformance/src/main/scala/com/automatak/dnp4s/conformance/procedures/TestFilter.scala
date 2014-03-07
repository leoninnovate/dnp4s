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

import com.automatak.dnp4s.dnp3.TestReporter
import com.automatak.dnp4s.conformance.procedures.CommonSteps.GetYesNo
import com.automatak.dnp4s.conformance.TestProcedure

case class TestFilter(id: String, description: String, expectation: Boolean) {
  def negate: TestFilter = this.copy(expectation = !expectation)
}

object TestFilters {

  val supportsDataLinkConfirm = TestFilter("supports.datalinkconfirm", "Can the DUT be configured to request data link layer confirmation?", true)
  val supportsSelfAddress = TestFilter("supports.selfAddress", "Does the the DUT link layer self addressing?", true)
  val supportGroup12Var1 = TestFilter("supports.crob", "Does the DUT support Group12Var1?", true)
  val controlsCanBeDisabled = TestFilter("disable.bo", "Is the device is configurable in a manner such that all Binary Output points can be uninstalled or disabled?", true)
  val supportsAnalogOutputStatus = TestFilter("supports.aoStatus", "Does the DUT support Analog Output Status?", true)
  val supportsAnalogOutput = TestFilter("supports.ao", "Does the DUT support Analog Output Controls?", true)
  val analogOutputCanBeDisabled = TestFilter("disable.ao", "Is the device is configurable in a manner such that all Analog Output points can be uninstalled or disabled?", true)
  val supportsClass123 = TestFilter("supports.class123", "Does the DUT supports Class1,2,3 OR does it return more than 1 datalink frame in response to class0?", true)
  val supportsNoClassAssignment = TestFilter("supports.noclass", "Is the DUT configurable such that points can be removed from all Classes (i.e. points belong to no Class)?", true)

  def getFilterValue(filter: TestFilter, reporter: TestReporter, map: Map[String, Boolean]): Boolean = {
    map.get(filter.id) match {
      case Some(boolean) => boolean
      case None => GetYesNo.getValue(filter.description, reporter)
    }
  }

}

trait RequiresDataLinkConfirmation extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsDataLinkConfirm)
}

trait RequiresSelfAddressSupport extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsSelfAddress)
}

trait RequiresGroup12Var1Support extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportGroup12Var1)
}

trait DisabledByGroup12Var1Support extends TestProcedure {
  abstract override def negativeFilters = super.negativeFilters ::: List(TestFilters.supportGroup12Var1)
}

trait RequiresDisableControl extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.controlsCanBeDisabled)
}

trait RequiresAnalogOutputStatusSupport extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsAnalogOutputStatus)
}

trait DisabledByAnalogOutputStatusSupport extends TestProcedure {
  abstract override def negativeFilters = super.negativeFilters ::: List(TestFilters.supportsAnalogOutputStatus)
}

trait RequiresAnalogOutputControlSupport extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsAnalogOutput)
}

trait DisabledByAnalogOutputControlSupport extends TestProcedure {
  abstract override def negativeFilters = super.negativeFilters ::: List(TestFilters.supportsAnalogOutput)
}

trait RequiresDisableAnalogOutput extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.analogOutputCanBeDisabled)
}

trait RequiresClass123Support extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsClass123)
}

trait RequiresNoClassAssignment extends TestProcedure {
  abstract override def positiveFilters = super.positiveFilters ::: List(TestFilters.supportsNoClassAssignment)
}