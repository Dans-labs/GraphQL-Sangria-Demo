/**
 * Copyright (C) 2019 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.graphql.demo

import java.util.concurrent.Executors

import better.files.File
import nl.knaw.dans.graphql.demo.app.repository.demo_impl.DemoRepo
import nl.knaw.dans.graphql.demo.server.{ GraphQLSangriaDemoService, GraphQLSangriaDemoServlet, GraphQLServlet, GraphiQLServlet }
import nl.knaw.dans.lib.error._
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }
import scala.util.control.NonFatal
import scala.util.{ Failure, Try }

object Command extends App with DebugEnhancedLogging {
  type FeedBackMessage = String

  val configuration = Configuration(File(System.getProperty("app.home")))
  val commandLine: CommandLineOptions = new CommandLineOptions(args, configuration) {
    verify()
  }
  val repository = new DemoRepo

  runSubcommand()
    .doIfSuccess(msg => println(s"OK: $msg"))
    .doIfFailure { case e => logger.error(e.getMessage, e) }
    .doIfFailure { case NonFatal(e) => println(s"FAILED: ${ e.getMessage }") }

  private def runSubcommand(): Try[FeedBackMessage] = {
    commandLine.subcommand
      .collect {
        case commandLine.runService => runAsService()
      }
      .getOrElse(Failure(new IllegalArgumentException(s"Unknown command: ${ commandLine.subcommand }")))
  }

  private def runAsService(): Try[FeedBackMessage] = Try {
    implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(16))

    val service = new GraphQLSangriaDemoService(configuration.serverPort, Map(
      "/" -> new GraphQLSangriaDemoServlet(configuration.version),
      "/graphql" -> new GraphQLServlet(configuration.profilingThreshold, repository.repository),
      "/graphiql" -> new GraphiQLServlet("/graphql"),
    ))
    Runtime.getRuntime.addShutdownHook(new Thread("service-shutdown") {
      override def run(): Unit = {
        service.stop().unsafeGetOrThrow
        service.destroy().unsafeGetOrThrow
      }
    })

    service.start().unsafeGetOrThrow
    Thread.currentThread.join()
    "Service terminated normally."
  }
}
