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

import org.fusesource.jansi.{ Ansi, AnsiConsole }
import org.fusesource.jansi.Ansi.Color._

object Logo {

  private val logo = List(
    """       __            __ __      """,
    """  ____/ /___  ____  / // / _____""",
    """ / __  / __ \/ __ \/ // /_/ ___/""",
    """/ /_/ / / / / /_/ /__  __(__  ) """,
    """\__,_/_/ /_/ .___/  /_/ /____/  """,
    """          /_/                   """)

  private val copyright = "Copyright 2013-2014 Automatak, LLC"

  def printLine(fg: Ansi.Color, bg: Ansi.Color)(msg: => String): Unit =
    AnsiConsole.out.println(Ansi.ansi().bg(bg).fg(fg).a(msg).reset())

  def print(): Unit = {
    logo.foreach(printLine(CYAN, BLACK)(_))
    println("")
    printLine(CYAN, BLACK)(copyright)
    println("")
  }
}
