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
import com.automatak.dnp4s.dnp3.{ TestReporter, NullTestReporter }
import com.automatak.dnp4s.dnp3.link.LpduReader

@RunWith(classOf[JUnitRunner])
class TransportReaderTest extends FunSuite with ShouldMatchers {

  class MockLinkReader extends LpduReader {

    val queue = collection.mutable.Queue.empty[List[Byte]]

    def read(reporter: TestReporter, confirmed: Boolean)(timeoutMs: Long): Option[List[Byte]] = {
      if (queue.isEmpty) None
      else Some(queue.dequeue())
    }

  }

  val data = fromHex("DE AD BE EF")
  val fullTpdu = FixedBytes.repeat(0xFF.toByte)(249)

  test("correctly parses fir-fin packet") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock, NullTestReporter)(_)
    mock.queue.enqueue(Transport.function(true, true, 0) :: data)
    readFunc(500) should equal(Some(data))
  }

  test("throws on non-fir packet") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock, NullTestReporter)(_)
    mock.queue.enqueue(Transport.function(false, true, 0) :: data)
    intercept[Exception](readFunc(500))
  }

  test("correctly parses 3 segment APDU") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock, NullTestReporter)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: fullTpdu)
    mock.queue.enqueue(Transport.function(false, false, 1) :: fullTpdu)
    mock.queue.enqueue(Transport.function(false, true, 2) :: data)
    readFunc(500) should equal(Some(fullTpdu ::: fullTpdu ::: data))
  }

  test("Excepts when a tpdu packing error occurs") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock, NullTestReporter)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: data)
    intercept[Exception](readFunc(500))
  }

  test("Excepts when out of sequence tpdu arrives skips when new fir arrives") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock, NullTestReporter)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: fullTpdu)
    mock.queue.enqueue(Transport.function(true, true, 4) :: List(0xFF.toByte))
    intercept[Exception](readFunc(500))
  }

}