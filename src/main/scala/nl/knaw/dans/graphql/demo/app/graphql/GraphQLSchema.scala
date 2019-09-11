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
package nl.knaw.dans.graphql.demo.app.graphql

import java.util.UUID

import nl.knaw.dans.graphql.demo.app.graphql.resolvers.{ PersonResolver, WorkResolver }
import nl.knaw.dans.graphql.demo.app.graphql.types.{ GraphQLPerson, GraphQLWork, Mutation, Query }
import nl.knaw.dans.graphql.demo.app.model.{ InputPerson, InputWork }
import org.joda.time.LocalDate
import sangria.ast.StringValue
import sangria.execution.deferred.DeferredResolver
import sangria.macros.derive.{ DocumentInputField, _ }
import sangria.marshalling.FromInput
import sangria.schema.{ InputObjectType, ObjectType, ScalarType, Schema }
import sangria.validation.{ StringCoercionViolation, ValueCoercionViolation, Violation }

import scala.util.Try

object GraphQLSchema {

  case object UUIDCoercionViolation extends ValueCoercionViolation("UUID value expected")
  case object DateCoercionViolation extends ValueCoercionViolation("Date value expected")

  implicit val UUIDType: ScalarType[UUID] = {
    def parseUUID(s: String): Either[Violation, UUID] = {
      Try { UUID.fromString(s) }
        .fold(_ => Left(UUIDCoercionViolation), Right(_))
    }

    ScalarType("UUID",
      description = Some("The UUID scalar type represents textual data, " +
        "formatted as a universally unique identifier."),
      coerceOutput = (value, _) => value.toString,
      coerceUserInput = {
        case s: String => parseUUID(s)
        case _ => Left(StringCoercionViolation)
      },
      coerceInput = {
        case StringValue(s, _, _, _, _) => parseUUID(s)
        case _ => Left(StringCoercionViolation)
      }
    )
  }

  implicit val LocalDateType: ScalarType[LocalDate] = {
    def parseDate(s: String): Either[Violation, LocalDate] = {
      Try { LocalDate.parse(s) }
        .fold(_ => Left(DateCoercionViolation), Right(_))
    }

    ScalarType("LocalDate",
      description = Some("A LocalDate scalar type represents textual data."),
      coerceOutput = (value, _) => value.toString("yyyy-MM-dd"),
      coerceUserInput = {
        case s: String => parseDate(s)
        case _ => Left(DateCoercionViolation)
      },
      coerceInput = {
        case StringValue(s, _, _, _, _) => parseDate(s)
        case _ => Left(DateCoercionViolation)
      }
    )
  }

  implicit val GraphQLPersonType: ObjectType[DataContext, GraphQLPerson] = deriveObjectType[DataContext, GraphQLPerson]()
  implicit val InputPersonType: InputObjectType[InputPerson] = deriveInputObjectType[InputPerson](
    InputObjectTypeDescription("The person to be inserted."),
    DocumentInputField("name", "The person's name."),
    DocumentInputField("birthday", "The date the person was born."),
    DocumentInputField("place", "The city/town where this person lives."),
  )
  implicit val InputPersonFromInput: FromInput[InputPerson] = fromInput(ad => InputPerson(
    name = ad("name").asInstanceOf[String],
    birthday = ad("birthday").asInstanceOf[LocalDate],
    place = ad("place").asInstanceOf[String],
  ))

  implicit val GraphQLWorkType: ObjectType[DataContext, GraphQLWork] = deriveObjectType[DataContext, GraphQLWork]()
  implicit val InputWorkType: InputObjectType[InputWork] = deriveInputObjectType[InputWork](
    InputObjectTypeDescription("The work to be inserted."),
    DocumentInputField("title", "The work's title."),
  )
  implicit val InputWorkFromInput: FromInput[InputWork] = fromInput(ad => InputWork(
    title = ad("title").asInstanceOf[String],
  ))

  implicit val QueryType: ObjectType[DataContext, Unit] = deriveContextObjectType[DataContext, Query, Unit](_.query)
  implicit val MutationType: ObjectType[DataContext, Unit] = deriveContextObjectType[DataContext, Mutation, Unit](_.mutation)

  val schema: Schema[DataContext, Unit] = Schema[DataContext, Unit](QueryType, mutation = Option(MutationType))
  val deferredResolver: DeferredResolver[DataContext] = DeferredResolver.fetchers(
    PersonResolver.byIdFetcher,
    WorkResolver.byIdFetcher,
    WorkResolver.byPersonIdFetcher,
  )
}
