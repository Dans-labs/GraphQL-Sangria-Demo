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
package nl.knaw.dans.graphql.demo.app.graphql.middleware

import org.slf4j.Logger
import sangria.execution.{ Middleware, MiddlewareQueryContext }

class Metrics(logger: Logger) extends Middleware[Any] {

  override type QueryVal = Long

  override def beforeQuery(context: MiddlewareQueryContext[Any, _, _]): QueryVal = {
    System.currentTimeMillis()
  }

  override def afterQuery(startTimeMs: QueryVal, context: MiddlewareQueryContext[Any, _, _]): Unit = {
    val duration = System.currentTimeMillis() - startTimeMs
    logger.info(s"Query execution time ${ duration }ms")
  }
}
