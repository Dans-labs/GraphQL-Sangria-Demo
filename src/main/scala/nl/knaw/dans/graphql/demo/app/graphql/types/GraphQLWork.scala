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
import nl.knaw.dans.graphql.demo.app.graphql.relay.ExtendedConnection
import nl.knaw.dans.graphql.demo.app.graphql.resolvers.PersonResolver
import nl.knaw.dans.graphql.demo.app.model.{ Work, WorkId }
import sangria.macros.derive.{ GraphQLDescription, GraphQLField, GraphQLName }
import sangria.relay.ConnectionArgs
import sangria.schema.{ Context, DeferredValue }

@GraphQLName("Work")
@GraphQLDescription("The object containing data about the work.")
class GraphQLWork(private val work: Work) {

  @GraphQLField
  @GraphQLDescription("The identifier with which this work is associated.")
  val id: WorkId = work.id

  @GraphQLField
  @GraphQLDescription("The work's title.")
  val title: String = work.title

  // NOTE: toggle between these 2 implementations and see the difference
  //  in the number of interactions with the DAO
//  @GraphQLField
//  @GraphQLDescription("List all authors of this work.")
//  def authors(implicit ctx: Context[DataContext, GraphQLWork]): Seq[GraphQLPerson] = {
//    // TODO note that this implementation is not optimized for deferred resolution
//    //  `getPersonsByWork` would ideally also be wrapped in a `Fetcher`.
//    //  However, Sangria is currently not able to compose instances of `DeferredValue`.
//    val personIds = ctx.ctx.repo.workDao.getPersonsByWork(id).getOrElse(Seq.empty)
//
//    ctx.ctx.repo.personDao.find(personIds)
//      .map(new GraphQLPerson(_))
//  }

  @GraphQLField
  @GraphQLDescription("List all authors of this work.")
  def authors(before: Option[String] = None,
              after: Option[String] = None,
              first: Option[Int] = None,
              last: Option[Int] = None,
             )(implicit ctx: Context[DataContext, GraphQLWork]): DeferredValue[DataContext, ExtendedConnection[GraphQLPerson]] = {
    // TODO note that this implementation is not optimized for deferred resolution
    //  `getPersonsByWork` would ideally also be wrapped in a `Fetcher`.
    //  However, Sangria is currently not able to compose instances of `DeferredValue`.
    val personIds = ctx.ctx.repo.workDao.getPersonsByWork(id).getOrElse(Seq.empty)

    PersonResolver.personsById(personIds)
      .map(persons => ExtendedConnection.connectionFromSeq(
        persons.map(new GraphQLPerson(_)),
        ConnectionArgs(before, after, first, last),
      ))
  }
}
