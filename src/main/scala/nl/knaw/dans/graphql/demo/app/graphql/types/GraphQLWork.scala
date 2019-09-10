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
import nl.knaw.dans.graphql.demo.app.graphql.resolvers.{ PersonResolver, WorkResolver }
import nl.knaw.dans.graphql.demo.app.model.{ PersonId, Work, WorkId }
import sangria.macros.derive.{ GraphQLDescription, GraphQLField, GraphQLName }
import sangria.schema.{ Context, DeferredValue, SequenceLeafAction }

@GraphQLName("Work")
@GraphQLDescription("The object containing data about the work.")
class GraphQLWork(@GraphQLDescription("The identifier with which this work is associated.") id: WorkId,
                  @GraphQLDescription("The work's title.") title: String,
                 ) extends Work(id, title) {

  @GraphQLField
  @GraphQLDescription("List all authors of this work.")
  def authors()(implicit ctx: Context[DataContext, GraphQLWork]): DeferredValue[DataContext, Seq[GraphQLPerson]] = {
    WorkResolver.authorIdsOfWork(ctx.value.id)
      .map(optSeqPersonIds => {
        optSeqPersonIds.map(personIds => {
          PersonResolver.personsById(personIds)
            .map(_.map(GraphQLPerson(_)))
        })
      })
    
    ???
  }
}

object GraphQLWork {
  def apply(work: Work): GraphQLWork = {
    new GraphQLWork(work.id, work.title)
  }
}
