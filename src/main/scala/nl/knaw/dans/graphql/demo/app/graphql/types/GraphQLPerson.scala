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
package nl.knaw.dans.graphql.demo.app.graphql.types

import nl.knaw.dans.graphql.demo.app.graphql.DataContext
import nl.knaw.dans.graphql.demo.app.graphql.resolvers.WorkResolver
import nl.knaw.dans.graphql.demo.app.model.{ Person, PersonId, Work }
import org.joda.time.LocalDate
import sangria.macros.derive.{ GraphQLDescription, GraphQLField, GraphQLName }
import sangria.schema.{ Context, DeferredValue }

@GraphQLName("Person")
@GraphQLDescription("The object containing data about the person.")
class GraphQLPerson(@GraphQLDescription("The identifier with which this person is associated.") personId: PersonId,
                    @GraphQLDescription("The person's name.") name: String,
                    @GraphQLDescription("The date the person was born.") birthday: LocalDate,
                    @GraphQLDescription("The city/town where this person lives.") place: String,
                   ) extends Person(personId, name, birthday, place) {

  @GraphQLField
  @GraphQLDescription("List all works of this person.")
  def works()(implicit ctx: Context[DataContext, GraphQLPerson]): DeferredValue[DataContext, Option[Seq[Work]]] = {
    WorkResolver.worksByPersonId(ctx.value.personId)
  }
}

object GraphQLPerson {
  def apply(person: Person): GraphQLPerson = {
    new GraphQLPerson(person.personId, person.name, person.birthday, person.place)
  }
}
