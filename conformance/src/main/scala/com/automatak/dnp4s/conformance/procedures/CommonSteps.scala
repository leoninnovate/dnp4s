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
package com.automatak.dnp4s.conformance.procedures

import com.automatak.dnp4s.conformance.TestStep
import com.automatak.dnp4s.dnp3.{ TestReporter, TestDriver }
import annotation.tailrec

object CommonSteps {

  abstract class NullDescription extends TestStep {
    def description: List[String] = Nil
  }

  case class Description(desc: String*) extends TestStep {
    def description = desc.toList
    def run(driver: TestDriver) = {
      Nil
    }
  }

  case class Wait(ms: Int) extends TestStep {
    def description = List("Wait for " + ms + " milliseconds")
    def run(driver: TestDriver) = {
      Thread.sleep(ms)
      Nil
    }
  }

  object PromptForUserAction {
    def apply(desc: String): PromptForUserAction = {
      PromptForUserAction(List(desc))
    }
  }

  case class PromptForUserAction(desc: List[String]) extends TestStep {

    def description = List("Ask user to perform action")

    def run(driver: TestDriver) = {
      desc.foreach(driver.reporter.prompt)
      driver.reporter.prompt("Press <enter> when ready")
      readLine()
      Nil
    }
  }

  object GetYesNo {

    @tailrec
    def getValue(desc: String, reporter: TestReporter): Boolean = {
      reporter.prompt(desc + " (y/n)")
      readLine match {
        case "y" => true
        case "n" => false
        case _ => getValue(desc, reporter)
      }
    }

  }

  case class PromptForBranch(desc: String, yes: List[(String, List[TestStep])], no: List[(String, List[TestStep])]) extends TestStep {
    def description = List("Collect manual user input")
    def run(driver: TestDriver) = {
      if (GetYesNo.getValue(desc, driver.reporter)) yes
      else no
    }
  }

  case class PromptForUserYesNo(desc: String, correct: Boolean) extends TestStep {

    def negate = this.copy(correct = !correct)

    def description = List("Collect manual user input")

    def run(driver: TestDriver) = {

      val value = GetYesNo.getValue(desc, driver.reporter)
      if (value != correct) throw new Exception(desc + ": Required answer is " + correct)
      Nil
    }
  }

}
