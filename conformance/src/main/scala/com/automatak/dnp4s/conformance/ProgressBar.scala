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
package com.automatak.dnp4s.conformance

object ProgressBar {

  private val steps: Int = 50

  def show(progress: Int, max: Int): Unit = {
    val numHash = (steps * progress) / max
    val numSpace = steps - numHash
    val hashes = (1 to numHash).foldLeft("")((s, i) => s + "#")
    val spaces = (1 to numSpace).foldLeft("")((s, i) => s + " ")
    val string = "[" + hashes + spaces + "] " + progress + " of " + max
    println(string)
  }

}
