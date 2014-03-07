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

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class IINTestSuite extends FunSuite with ShouldMatchers {

  test("Complement of empty is all bits") {
    ~IIN.empty should equal(IIN(0xFF.toByte, 0xFF.toByte))
  }

  test("Subtract of all bits is empty") {
    (~IIN.empty) - (~IIN.empty) should equal(IIN.empty)
  }

  test("Subtract of a single bits works properly") {
    val sub = IIN(IIN.IIN10_ALL_STATIONS, IIN.IIN20_FUNC_NOT_SUPPORTED) - IIN(IIN.IIN10_ALL_STATIONS, 0)
    sub should equal(IIN(0, IIN.IIN20_FUNC_NOT_SUPPORTED))
  }

}