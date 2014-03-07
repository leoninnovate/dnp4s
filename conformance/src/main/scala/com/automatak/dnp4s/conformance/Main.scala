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

import com.automatak.dnp4s.dnp3.phys._

import java.net.InetSocketAddress
import com.automatak.dnp4s.dsl.UInt16LE
import com.automatak.dnp4s.dnp3._
import procedures._
import org.apache.commons.cli.{ CommandLine, HelpFormatter, PosixParser, Options }
import java.io.File
import com.automatak.dnp4s.dnp3.app.GroupVariation
import annotation.tailrec
import procedures.CommonSteps.GetYesNo
import scala.Some
import com.automatak.dnp4s.conformance.procedures.TestFilter

object Main {

  def printHelp(procedures: List[TestProcedure], options: Options): Unit = {
    val formatter = new HelpFormatter
    formatter.printHelp("dnp4s", options)
    println()
    println("Valid procedure ids: ")
    println()
    procedures.foreach(p => printTestProcedure(p, 0))
  }

  def printTestProcedure(p: TestProcedure, numSpaces: Int): Unit = {
    val spaces = "".padTo(numSpaces, " ").mkString
    print((spaces + p.id).padTo(30, " ").mkString)
    println(p.description)
    p.subProcedures.foreach(sp => printTestProcedure(sp, numSpaces + 2))
  }

  def makeProceedureMap(map: Map[String, TestProcedure], procedures: List[TestProcedure]): Map[String, TestProcedure] = {
    procedures.foldLeft(map) { (sum, p) =>
      sum.get(p.id).foreach(p => throw new Exception("Duplicate procedure id: " + p.id))
      makeProceedureMap(sum + (p.id -> p), p.subProcedures)
    }
  }

  def main(args: Array[String]): Unit = {

    Logo.print()

    val gp = GroupVariation(1.toByte, 2.toByte)

    val options = new Options

    options.addOption("dest", true, "Link layer destination address (1024)")
    options.addOption("src", true, "Link layer source address (1)")
    options.addOption("fuzzmaster", false, "Configures the link layer to fuzz a master")
    options.addOption("host", true, "IP address of outstation (127.0.0.1)")
    options.addOption("port", true, "IP port of outstation (20000)")
    options.addOption("listen", true, "Port to listen on (fuzzing masters)")
    options.addOption("id", true, "Id of the test procedure to run")
    options.addOption("help", false, "Prints help information")
    options.addOption("progress", false, "prints progress information to the console instead of test output")
    options.addOption("fuzzRetryCount", true, "number of link status retries before failure when fuzzing (0)")
    options.addOption("linktimeout", true, "Timeout for the link layer in milliseconds (1000)")
    options.addOption("apptimeout", true, "Timeout for the application layer in milliseconds (2000)")
    options.addOption("config", true, "Relative path to optional XML configuration file")
    options.addOption("output", true, "Relative path to output file (open and append)")
    options.addOption("start", true, "Start test case number (most useful for resuming fuzz at a certain test failure)")
    options.addOption("count", true, "Number of test cases to run (most useful for limiting fuzzing to a specified number of cases")
    options.addOption("randomSeed", true, "Supply a seed to a random number generator for fuzzing (defaults to fixed 0xFF)")

    val config = parseOptions(options, args)

    val map = makeProceedureMap(Map.empty[String, TestProcedure], ConformanceProcedure.procedures)

    if (config.test.help) printHelp(ConformanceProcedure.procedures, options)
    else {
      config.test.procedure match {
        case Some(id) => map.get(id) match {
          case Some(procedure) =>
            val options = config.config match {
              case Some(file) => Configuration.read(file)
              case None => Configuration.empty
            }
            val logger = config.test.file.map(file => new FileLogger(file))
            val reporter = if (config.test.progress) new ConfigurableTestReporter(false, logger)
            else new ConfigurableTestReporter(true, logger)
            try {
              iterate(reporter, procedure, config.factory, options, config)
            } catch {
              case ex: Exception => throw ex /// reporter.error("Test failure: " + ex.getStackTrace)
            } finally {
              logger.foreach(_.close())
            }
          case None =>
            println("Unknown procedure id: " + id)
        }
        case None =>
          println("Missing required parameter: id")
          System.exit(-1)
      }
    }

    System.exit(0)
  }

  def evaluatePrompts(prompts: List[String])(output: String => Unit): Unit = {
    if (prompts.nonEmpty) {
      prompts.foreach(output)
      output("To continue press <enter>")
      readLine()
    }
  }

  def convertCommandLineToMap(cmd: CommandLine): Map[String, Option[String]] = {
    cmd.getOptions.foldLeft(Map.empty[String, Option[String]]) { (sum, opt) =>
      if (cmd.hasOption(opt.getOpt)) {
        sum + (opt.getOpt -> Option(cmd.getOptionValue(opt.getOpt)))
      } else sum
    }
  }

  def collectOptions(reporter: TestReporter, options: List[TestOption], optionMap: Map[String, String]): Map[String, String] = {

    @tailrec
    def getOptionFromUser(option: TestOption): (String, String) = {
      reporter.prompt("Enter a value for option '" + option.id + "' - " + option.description)
      val line = readLine()
      option.validate(line) match {
        case None => (option.id, line)
        case Some(error) =>
          reporter.warn("Provided value doesn't validate: " + error)
          getOptionFromUser(option)
      }
    }

    def getOptionValue(option: TestOption): (String, String) = {
      //first thing to do is see if there's a provided option
      optionMap.get(option.id) match {
        case Some(value) =>
          option.validate(value) match {
            case None =>
              reporter.prompt("Using option '" + option.id + "' = " + value)
              (option.id, value)
            case Some(error) =>
              reporter.warn("Configured value of " + value + " doesn't validate against requirements")
              getOptionFromUser(option)
          }
        case None => getOptionFromUser(option)
      }
    }

    options.map(getOptionValue).toMap
  }

  def findBlockingFilter(filters: List[TestFilter], reporter: TestReporter, cfg: Map[String, Boolean]): Option[TestFilter] = {
    filters.find { f =>
      cfg.get(f.id) match {
        case Some(value) => f.expectation != value
        case None =>
          reporter.prompt("Filter not found in configuration: " + f.id)
          GetYesNo.getValue(f.description, reporter) != f.expectation
      }
    }
  }

  def iterate(reporter: TestReporter, procedure: TestProcedure, factory: ChannelFactory, cfg: Configuration, config: RunOptions): Unit = {
    reporter.enterTestSection("Entering test section: " + procedure.id + " - " + procedure.description)
    findBlockingFilter(procedure.filters, reporter, cfg.filters) match {
      case Some(filter) =>
        reporter.info("Test procedure skipped via filter: " + filter.id)
      case None =>
        evaluatePrompts(procedure.prompts)(reporter.prompt)
        val options = collectOptions(reporter, procedure.options, cfg.options)
        val steps = {
          val s1 = procedure.steps(MapTestOption(options)).toIterator.drop(config.test.start.getOrElse(0))
          config.test.count.map(s1.take).getOrElse(s1)
        }
        if (steps.nonEmpty) { // run all steps in this section
          val channel = factory.open()
          val driver = new DefaultTestDriver(reporter, channel, config.link.src, config.link.dest, config.link.timeoutMs, config.app.timeoutMs, config.link.isMaster)
          runSteps(driver, procedure.id, steps)
          channel.close()
        }
    }

    procedure.subProcedures.foreach(sp => iterate(reporter, sp, factory, cfg, config))
  }

  def runSteps(driver: TestDriver, procedureId: String, steps: Iterator[(String, List[TestStep])]): Unit = {

    var i = 1
    for ((id, tests) <- steps) {
      tests.zipWithIndex.foreach {
        case (testCase, substep) =>
          testCase.description.foreach(desc => driver.reporter.info(procedureId + " - " + id.toString + " - substep: " + (substep + 1) + " - " + desc))
          val branches = testCase.run(driver)
          driver.reporter.testPassed("Test passed: " + procedureId + " - " + id.toString + " substep: " + (substep + 1))
          i += 1
          runSteps(driver, procedureId, branches.toIterator)
      }
    }

  }

  case class LinkOptions(src: UInt16LE, dest: UInt16LE, timeoutMs: Int, isMaster: Boolean)
  case class AppOptions(timeoutMs: Int)
  case class TestCaseOptions(procedure: Option[String], start: Option[Int], count: Option[Int], file: Option[File], help: Boolean, progress: Boolean, fuzzRetries: Int)
  case class RunOptions(factory: ChannelFactory, config: Option[File], test: TestCaseOptions, link: LinkOptions, app: AppOptions)

  def getChannelFactory(cmd: CommandLine): ChannelFactory = {
    if (cmd.hasOption("listen")) {
      val port = Integer.parseInt(cmd.getOptionValue("listen"))
      new TcpServerChannelFactory(port)
    } else {
      val host = if (cmd.hasOption("host")) cmd.getOptionValue("host") else "127.0.0.1"
      val port = if (cmd.hasOption("port")) Integer.parseInt(cmd.getOptionValue("port")) else 20000
      new TcpClientChannelFactory(new InetSocketAddress(host, port))
    }
  }

  def parseOptions(options: Options, args: Array[String]): RunOptions = {
    val parser = new PosixParser
    val cmd = parser.parse(options, args)
    val dest = if (cmd.hasOption("dest")) {
      UInt16LE(Integer.parseInt(cmd.getOptionValue("dest")))
    } else UInt16LE(1024)
    val src = if (cmd.hasOption("src")) {
      UInt16LE(Integer.parseInt(cmd.getOptionValue("src")))
    } else UInt16LE(1)
    options.addOption("config", true, "Path to optional XML configuration file")
    val procedure = if (cmd.hasOption("id")) Some(cmd.getOptionValue("id")) else None
    val fuzzRetries = if (cmd.hasOption("fuzzRetryCount")) Integer.parseInt(cmd.getOptionValue("fuzzRetryCount")) else 0
    val linkTimeout = if (cmd.hasOption("linktimeout")) Integer.parseInt(cmd.getOptionValue("linktimeout")) else 1000
    val isMaster = !cmd.hasOption("fuzzmaster")
    val appTimeout = if (cmd.hasOption("apptimeout")) Integer.parseInt(cmd.getOptionValue("apptimeout")) else 2000
    val help = cmd.hasOption("help")
    val progress = cmd.hasOption("progress")
    val outputFile = if (cmd.hasOption("output")) Some(new File(cmd.getOptionValue("output"))) else None
    val start = if (cmd.hasOption("start")) Some(Integer.parseInt(cmd.getOptionValue("start"))) else None
    val count = if (cmd.hasOption("count")) Some(Integer.parseInt(cmd.getOptionValue("count"))) else None
    val testOptions = TestCaseOptions(procedure, start, count, outputFile, help, progress, fuzzRetries)
    val appOptions = AppOptions(appTimeout)
    val linkOptions = LinkOptions(src, dest, linkTimeout, isMaster)
    val config = if (cmd.hasOption("config")) Some(cmd.getOptionValue("config")).map(path => new File(path)) else None
    val factory = getChannelFactory(cmd)
    RunOptions(factory, config, testOptions, linkOptions, appOptions)
  }

}
