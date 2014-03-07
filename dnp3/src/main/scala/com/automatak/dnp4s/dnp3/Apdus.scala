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
package com.automatak.dnp4s.dnp3

import app._
import app.AllObjectsHeader
import app.OneByteStartStopHeader
import objects._
import com.automatak.dnp4s.dsl.UInt8

object Apdus {

  def readClass0(seq: Byte) = readAllObjects(seq, Group60Var1)

  def readEvents(seq: Byte) = readAllObjects(seq, Group60Var2, Group60Var3, Group60Var4)

  def readIntegrity(seq: Byte) = readAllObjects(seq, Group60Var1, Group60Var2, Group60Var3, Group60Var4)

  def clearIIN(seq: Byte) = Apdu(AppCtrl(true, true, false, false, seq), AppFunctions.write, None, List(OneByteStartStopHeader(Group80Var1, UInt8(7), UInt8(7), List(0.toByte))))

  def confirm(seq: Byte, uns: Boolean) = Apdu(AppCtrl(true, true, false, uns, seq), AppFunctions.confirm, None, Nil)

  def readAllObjects(seq: Byte, gv: GroupVariation*) = {
    val headers = gv.map(AllObjectsHeader.apply).toList
    Apdu(AppCtrl(true, true, false, false, seq), AppFunctions.read, None, headers)
  }

  def readHeaders(seq: Byte, headers: ObjectHeader*) = {
    Apdu(AppCtrl(true, true, false, false, 0), AppFunctions.read, None, headers.toList)
  }

}
