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
import nl.knaw.dans.graphql.demo.app.graphql.resolvers.PersonResolver
import nl.knaw.dans.graphql.demo.app.model.PersonId
import sangria.macros.derive.{ GraphQLDescription, GraphQLField }
import sangria.schema.{ Context, DeferredValue }

class Query {

  @GraphQLField
  @GraphQLDescription("List all known persons.")
  def persons()(implicit ctx: Context[DataContext, Unit]): Seq[GraphQLPerson] = {
    ctx.ctx.repo.personDao.getAll
      .map(new GraphQLPerson(_))
  }

  @GraphQLField
  @GraphQLDescription("Find the person identified with the given identifier.")
  def person(@GraphQLDescription("The identifier of the person to be found.") id: PersonId)
            (implicit ctx: Context[DataContext, Unit]): DeferredValue[DataContext, Option[GraphQLPerson]] = {
    PersonResolver.personById(id)
      .map(_.map(new GraphQLPerson(_)))
  }
}
