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

import better.files.File
import nl.knaw.dans.graphql.demo.app.repository.demo_impl.DemoRepo
import nl.knaw.dans.graphql.demo.fixture.TestSupportFixture
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.{ DefaultFormats, Formats }
import org.scalatest.BeforeAndAfterEach
import org.scalatra.test.EmbeddedJettyContainer
import org.scalatra.test.scalatest.ScalatraSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class GraphQLExamplesSpec extends TestSupportFixture
  with BeforeAndAfterEach
  with EmbeddedJettyContainer
  with ScalatraSuite {

  private val servlet = new GraphQLServlet(
    repository = new DemoRepo().repository,
    profilingThreshold = 12 seconds,
  )
  implicit val jsonFormats: Formats = new DefaultFormats {}

  addServlet(servlet, "/*")

  "graphQL examples" should behave like {
    val graphqlExamplesDir = File(getClass.getResource("/graphql-examples"))

    def findJsonOutput(graphQLFile: File): File = {
      graphqlExamplesDir / graphqlExamplesDir.relativize(graphQLFile.parent).toString / s"${ graphQLFile.nameWithoutExtension }.json"
    }

    for (graphQLExample <- graphqlExamplesDir.walk()
         if graphQLExample.isRegularFile
         if graphQLExample.name.endsWith(".graphql");
         expectedJsonOutput = findJsonOutput(graphQLExample);
         relativeGraphQLPath = s"${ graphqlExamplesDir.relativize(graphQLExample.parent) }/${ graphQLExample.name }";
         relativeJsonOutputPath = s"${ graphqlExamplesDir.relativize(expectedJsonOutput.parent) }/${ expectedJsonOutput.name }") {
      it should s"check that the result of GraphQL example '$relativeGraphQLPath' is as expected in '$relativeJsonOutputPath'" in {
        assume(graphQLExample.exists, s"input file does not exist: $graphQLExample")
        assume(expectedJsonOutput.exists, s"output file does not exist: $expectedJsonOutput")

        val inputBody = compact(render("query" -> graphQLExample.contentAsString))
        val expectedOutput = writePretty(parse(expectedJsonOutput.contentAsString))

        post(uri = "/", body = inputBody.getBytes) {
          body shouldBe expectedOutput
          status shouldBe 200
        }
      }
    }
  }
}
