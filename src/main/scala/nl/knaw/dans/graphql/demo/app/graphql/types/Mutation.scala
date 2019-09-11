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
import nl.knaw.dans.graphql.demo.app.model.{ InputPerson, InputWork, PersonId }
import sangria.macros.derive.{ GraphQLDescription, GraphQLField }
import sangria.schema.Context

class Mutation {

  @GraphQLField
  @GraphQLDescription("Add a person to the service.")
  def addPerson(@GraphQLDescription("The person to be inserted.") person: InputPerson)
               (implicit ctx: Context[DataContext, Unit]): GraphQLPerson = {
    GraphQLPerson(ctx.ctx.repo.personDao.store(person))
  }

  @GraphQLField
  @GraphQLDescription("Add a work together with it's authors.")
  def addWork(@GraphQLDescription("The work's metadata.") work: InputWork,
              @GraphQLDescription("The authors of the work.") authors: Seq[PersonId],
             )(implicit ctx: Context[DataContext, Unit]): GraphQLWork = {
    GraphQLWork(ctx.ctx.repo.workDao.store(authors, work))
  }
}
