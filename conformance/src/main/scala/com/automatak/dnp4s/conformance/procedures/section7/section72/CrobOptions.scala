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
package com.automatak.dnp4s.conformance.procedures.section7.section72

import com.automatak.dnp4s.conformance._
import com.automatak.dnp4s.dnp3.app.objects.Group12Var1
import com.automatak.dnp4s.dsl.UInt32LE
import com.automatak.dnp4s.conformance.IntTestOption

object CrobOptions {

  val validIndex1 = IntTestOption("crob1.validIndex", "Index of a supported Group12Var1", Some(0), None)
  val invalidIndex1 = IntTestOption("crob1.invalidIndex", "Index of an uninstalled Group12Var1", Some(0), None)
  val validIndex2 = IntTestOption("crob2.validIndex", "Index of second a supported Group12Var1", Some(0), None)
  val invalidIndex2 = IntTestOption("crob2.invalidIndex", "Index of a second uninstalled Group12Var1", Some(0), None)

  val validFunction2 = ByteSetTestOption("crob1.validFunction2", "Alternate control function in hexadecimal", Section72.validCrobCodes)

  def getValidFunction2(options: TestOptions): Byte = options.getByte(validFunction2.id)

  def validCrob1 = List(
    ByteSetTestOption("crob1.validFunction", "Control function in hexadecimal", Section72.validCrobCodes),
    IntTestOption("crob1.validCount", "Count CROB field", Some(1), Some(255)),
    IntTestOption("crob1.validOnTime", "On time in ms", Some(0), Some(65535)),
    IntTestOption("crob1.validOffTime", "Off time in ms", Some(0), Some(65535)))

  def validCrob2 = List(
    ByteSetTestOption("crob2.validFunction", "Control function in hexadecimal", Section72.validCrobCodes),
    IntTestOption("crob2.validCount", "Count CROB field", Some(1), Some(255)),
    IntTestOption("crob2.validOnTime", "On time in ms", Some(0), Some(65535)),
    IntTestOption("crob2.validOffTime", "Off time in ms", Some(0), Some(65535)))

  def getValidIndex1(options: TestOptions): Int = options.getInteger(validIndex1.id)
  def getValidIndex2(options: TestOptions): Int = options.getInteger(validIndex2.id)
  def getInvalidIndex1(options: TestOptions): Int = options.getInteger(invalidIndex1.id)
  def getInvalidIndex2(options: TestOptions): Int = options.getInteger(invalidIndex2.id)

  def getCrob1(options: TestOptions): Group12Var1 = {

    val func = options.getHexdecimalByte("crob1.validFunction")
    val count = options.getByte("crob1.validCount")
    val onTime = options.getInteger("crob1.validOnTime")
    val offTime = options.getInteger("crob1.validOffTime")

    Group12Var1(func, count, UInt32LE(onTime), UInt32LE(offTime), 0x00)
  }

  def getCrob2(options: TestOptions): Group12Var1 = {

    val func = options.getHexdecimalByte("crob2.validFunction")
    val count = options.getByte("crob2.validCount")
    val onTime = options.getInteger("crob2.validOnTime")
    val offTime = options.getInteger("crob2.validOffTime")

    Group12Var1(func, count, UInt32LE(onTime), UInt32LE(offTime), 0x00)
  }

}

