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
package nl.knaw.dans.graphql.demo.app.graphql.resolvers

import nl.knaw.dans.graphql.demo.app.graphql.DataContext
import nl.knaw.dans.graphql.demo.app.model.{ Person, PersonId }
import sangria.execution.deferred.{ Fetcher, HasId }
import sangria.schema.DeferredValue

import scala.concurrent.Future

object PersonResolver {

  private implicit val personHasId: HasId[Person, PersonId] = HasId(_.personId)

  val byIdFetcher: Fetcher[DataContext, Person, Person, PersonId] = Fetcher.caching[DataContext, Person, PersonId](
    fetch = (ctx, personIds) => Future.successful { ctx.repo.personDao.find(personIds) }
  )

  def personById(id: PersonId)(implicit ctx: DataContext): DeferredValue[DataContext, Option[Person]] = {
    DeferredValue(byIdFetcher.deferOpt(id))
  }

  def personsById(ids: Seq[PersonId])(implicit ctx: DataContext): DeferredValue[DataContext, Seq[Person]] = {
    DeferredValue(byIdFetcher.deferSeqOpt(ids))
  }
}
