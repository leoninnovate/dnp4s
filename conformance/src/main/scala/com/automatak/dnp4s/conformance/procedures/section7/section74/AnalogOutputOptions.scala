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
package com.automatak.dnp4s.conformance.procedures.section7.section74

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.conformance.IntTestOption
import com.automatak.dnp4s.dnp3.app.objects.{ CommandStatus, Group41Var2 }
import com.automatak.dnp4s.dsl.SInt16LE

object AnalogOutputOptions {

  val validIndex1 = IntTestOption("ao1.validIndex1", "Primary index of a supported Analog Output", Some(0), Some(255))
  val invalidIndex1 = IntTestOption("ao1.invalidIndex1", "Primary index of an uninstalled Analog Output", Some(0), Some(255))
  val validValue1 = IntTestOption("ao1.validValue1", "Valid value to send in Analog Output primary index", Some(Short.MinValue), Some(Short.MaxValue))

  val validIndex2 = IntTestOption("ao1.validIndex2", "Secondary index of a supported Analog Output", Some(0), Some(255))
  val invalidIndex2 = IntTestOption("ao1.invalidIndex2", "Secondary index of an uninstalled Analog Output", Some(0), Some(255))
  val validValue2 = IntTestOption("ao1.validValue2", "Valid value to send in Analog Output secondary index", Some(Short.MinValue), Some(Short.MaxValue))

  def getPrimaryValidIndex(options: TestOptions): Int = options.getInteger(validIndex1.id)
  def getPrimaryInvalidIndex(options: TestOptions): Int = options.getInteger(invalidIndex1.id)
  def getPrimaryValidGroup41Var2(options: TestOptions): Group41Var2 = {
    val value = options.getShort(validValue1.id)
    Group41Var2(SInt16LE(value), CommandStatus.SUCCESS)
  }

  def getSecondaryValidIndex(options: TestOptions): Int = options.getInteger(validIndex2.id)
  def getSecondaryInvalidIndex(options: TestOptions): Int = options.getInteger(invalidIndex2.id)
  def getSecondaryValidGroup41Var2(options: TestOptions): Group41Var2 = {
    val value = options.getShort(validValue2.id)
    Group41Var2(SInt16LE(value), CommandStatus.SUCCESS)
  }
}

