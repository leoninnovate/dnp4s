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
package com.automatak.dnp4s.dnp3.transport

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import com.automatak.dnp4s.dsl._
import com.automatak.dnp4s.dnp3.NullTestReporter

@RunWith(classOf[JUnitRunner])
class TransportWriterTest extends FunSuite with ShouldMatchers {

  test("correctly writes a frame") {
    val someAppData = fromHex("BE EF")

    val queue = collection.mutable.Queue.empty[List[Byte]]
    TpduWriter.write(someAppData, 0, NullTestReporter)(bytes => queue.enqueue(bytes))

    queue.size should equal(1)
    queue.dequeue() should equal(fromHex("C0 BE EF"))
  }

}