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

import app.Apdu
import link.Lpdu
import org.fusesource.jansi.{ Ansi, AnsiConsole }
import org.fusesource.jansi.Ansi.Color._
import java.text.SimpleDateFormat
import java.util.Date
import transport.Transport
import com.automatak.dnp4s.dsl.toHex

class ConfigurableTestReporter(showOutput: Boolean, logger: Option[TestLogger]) extends TestReporter {

  def printLine(fg: Ansi.Color, bg: Ansi.Color)(msg: => String): Unit = {
    val t = time()
    if (showOutput) {
      print(WHITE, BLACK)(t)
      AnsiConsole.out.println(Ansi.ansi().bg(bg).fg(fg).a(msg).reset())
    }
    logger.foreach(_.log(t + msg))
  }

  def print(fg: Ansi.Color, bg: Ansi.Color)(msg: => String): Unit =
    AnsiConsole.out.print(Ansi.ansi().bg(bg).fg(fg).a(msg).reset())

  private val defaulUTCDateFormat = new SimpleDateFormat("HH:mm:ss.SSS")

  def prompt(msg: String): Unit = printLine(YELLOW, BLACK)(msg)

  def time(): String = defaulUTCDateFormat.format(new Date(System.currentTimeMillis())) + " - "

  def testPassed(msg: String): Unit = printLine(GREEN, BLACK)(msg)

  def enterTestSection(msg: String): Unit = printLine(BLACK, CYAN)(msg)

  def info(msg: String) = printLine(BLACK, WHITE)(msg)

  def warn(msg: String) = printLine(BLACK, YELLOW)(msg)

  def error(msg: String) = printLine(WHITE, RED)(msg)

  def receiveLPDU(lpdu: Lpdu): Unit = printLine(CYAN, BLACK)("<- " + lpdu.toString)

  def receiveTPDU(header: Transport.Header, data: List[Byte]): Unit = printLine(CYAN, BLACK)("<~ " + header.toString)

  def receiveAPDU(apdu: Apdu): Unit = {
    printLine(CYAN, BLACK)("<= " + apdu.toString)
    apdu.headers.foreach(header => printLine(WHITE, BLACK)("<= " + header.toString))
  }

  def transmitPhys(bytes: List[Byte]): Unit = printLine(CYAN, BLACK)(">> " + toHex(bytes))

  def transmitLPDU(lpdu: Lpdu): Unit = printLine(CYAN, BLACK)("-> " + lpdu.toString)

  def transmitTPDU(header: Transport.Header, data: List[Byte]): Unit = printLine(CYAN, BLACK)("~> " + header.toString + " payload size: " + data.size)

  def transmitAPDU(apdu: Apdu): Unit = {
    printLine(CYAN, BLACK)("=> " + apdu.toString)
    apdu.headers.foreach(header => printLine(WHITE, BLACK)("=> " + header.toString))
  }

}
