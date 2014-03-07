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
package com.automatak.dnp4s.dnp3.app

object BinaryOutputStatusQuality {

  val ONLINE = 0x01.toByte
  val RESTART = 0x02.toByte
  val COMM_LOST = 0x04.toByte
  val REMOTE_FORCED_DATA = 0x08.toByte
  val LOCAL_FORCED_DATA = 0x10.toByte
  val RESERVED_1 = 0x20.toByte
  val RESERVED_2 = 0x40.toByte
  val STATE = 0x80.toByte

  def mask(bytes: Byte*): Byte = bytes.foldLeft(0.toByte)((sum, byte) => (sum | byte).toByte)
}