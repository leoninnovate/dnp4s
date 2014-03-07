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
package com.automatak.dnp4s.dnp3.app.objects

import com.automatak.dnp4s.dnp3.app._
import com.automatak.dnp4s.dsl.{ UInt32LE, SerializableToBytes }

object Group12 extends ObjectGroup {
  def objects = List(Group12Var1, Group12Var2, Group12Var3)
  def group: Byte = 12
  def typ: ObjectType = Command

  object FuncCodes {
    val pulse = 0x01.toByte
    val latchOn = 0x03.toByte
    val latchOff = 0x04.toByte
    val pulseClose = 0x41.toByte
    val pulseTrip = 0x81.toByte

    case class Description(byte: Byte, desc: String)
    val descriptions = List(
      Description(pulse, "pulse"),
      Description(latchOn, "latchOn"),
      Description(latchOff, "latchOn"),
      Description(pulseClose, "pulseClose"),
      Description(pulseTrip, "pulseTrip"))
  }
}

case class Group12Var1(code: Byte, count: Byte, onTime: UInt32LE, offTime: UInt32LE, status: Byte) extends SerializableToBytes {
  override def toBytes = code :: count :: onTime.toBytes ::: offTime.toBytes ::: List(status)
}

object Group12Var1 extends FixedSizeGroupVariation(Group12, 1, 11) with CommandGroupVariation
object Group12Var2 extends FixedSizeGroupVariation(Group12, 2, 11) with CommandGroupVariation
object Group12Var3 extends BasicGroupVariation(Group12, 3) with SingleBitfield with CommandGroupVariation
