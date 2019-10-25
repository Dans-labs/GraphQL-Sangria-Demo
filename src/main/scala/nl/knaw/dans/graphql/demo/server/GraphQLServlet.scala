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
package nl.knaw.dans.graphql.demo.server

import nl.knaw.dans.graphql.demo.app.graphql.middleware.{ Middlewares, ProfilingConfiguration }
import nl.knaw.dans.graphql.demo.app.graphql.{ DataContext, GraphQLSchema }
import nl.knaw.dans.graphql.demo.app.repository.Repository
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import nl.knaw.dans.lib.logging.servlet.{ LogResponseBodyOnError, MaskedLogFormatter, ServletLogger }
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.ext.UUIDSerializer
import org.json4s.native.Serialization
import org.json4s.{ DefaultFormats, Formats, JValue }
import org.scalatra._
import sangria.ast.Document
import sangria.execution._
import sangria.marshalling.json4s.native._
import sangria.parser.{ DeliveryScheme, ParserConfig, QueryParser, SyntaxError }

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ ExecutionContext, Future }

class GraphQLServlet(profilingThreshold: FiniteDuration,
                     repository: Repository,
                    )(implicit protected val executor: ExecutionContext)
  extends ScalatraServlet
    with CorsSupport
    with FutureSupport
    with ServletLogger
    with MaskedLogFormatter
    with LogResponseBodyOnError
    with DebugEnhancedLogging {

  private implicit val jsonFormats: Formats = new DefaultFormats {} + UUIDSerializer

  post("/") {
    contentType = "application/json"
    val profiling = if (request.queryString contains "doProfile")
                      Some(ProfilingConfiguration(profilingThreshold))
                    else None

    val GraphQLInput(query, variables, operation) = Serialization.read[GraphQLInput](request.body)
    val middlewares = new Middlewares(profiling)
    QueryParser.parse(query, ParserConfig.default.withoutComments)(DeliveryScheme.Either)
      .fold({
        case e: SyntaxError => Future.successful(BadRequest(syntaxError(e)))
        case e => Future.failed(e)
      }, execute(variables, operation, middlewares))
  }

  val defaultExceptionHandler = ExceptionHandler(
    onException = {
      case (_, e) =>
        logger.error(s"Exception: ${ e.getMessage }", e)
        HandledException(e.getMessage)
    },
    onViolation = {
      case (_, e) =>
        logger.error(s"Violation: ${ e.errorMessage }", e)
        HandledException(e.errorMessage)
    },
    onUserFacingError = {
      case (_, e) =>
        logger.error(s"User facing error: ${ e.getMessage }", e)
        HandledException(e.getMessage)
    },
  )

  private def execute(variables: Option[JValue], operation: Option[String], middlewares: Middlewares)(queryAst: Document): Future[ActionResult] = {
    Executor.execute(
      schema = GraphQLSchema.schema,
      queryAst = queryAst,
      userContext = DataContext(repository),
      operationName = operation,
      variables = variables.getOrElse(JObject(Nil)),
      deferredResolver = GraphQLSchema.deferredResolver,
      exceptionHandler = defaultExceptionHandler,
      middleware = middlewares.values,
    )
      .map(Serialization.writePretty(_))
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError => BadRequest(Serialization.write(error.resolveError))
        case error: ErrorWithResolver => InternalServerError(Serialization.write(error.resolveError))
      }
  }

  private def syntaxError(error: SyntaxError): String = {
    Serialization.write {
      ("syntaxError" -> error.getMessage) ~
        ("locations" -> List(
          ("line" -> error.originalError.position.line) ~
            ("column" -> error.originalError.position.column)
        ))
    }
  }
}
