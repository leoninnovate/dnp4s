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

object CommandStatus {

  val SUCCESS = 0.toByte
  val TIMEOUT = 1.toByte
  val NO_SELECT = 2.toByte
  val FORMAT_ERROR = 3.toByte
  val NOT_SUPPORTED = 4.toByte
  val ALREADY_ACTIVE = 5.toByte
  val HARDWARE_ERROR = 6.toByte
  val LOCAL = 7.toByte
  val TOO_MANY_OPS = 8.toByte
  val NOT_AUTHORIZED = 9.toByte

}
