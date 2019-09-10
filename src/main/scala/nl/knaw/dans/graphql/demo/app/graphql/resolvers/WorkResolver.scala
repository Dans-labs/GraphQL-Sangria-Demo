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
import nl.knaw.dans.graphql.demo.app.model.{ PersonId, Work, WorkId }
import sangria.execution.deferred.{ Fetcher, HasId }
import sangria.schema.DeferredValue

import scala.concurrent.Future

object WorkResolver {

  private implicit val workHasId: HasId[Work, WorkId] = HasId(_.id)

  val byIdFetcher: Fetcher[DataContext, Work, Work, WorkId] = Fetcher.caching[DataContext, Work, WorkId](
    fetch = (ctx, workIds) => Future.successful { ctx.repo.workDao.getById(workIds) }
  )
  val byPersonIdFetcher: Fetcher[DataContext, (PersonId, Seq[Work]), (PersonId, Seq[Work]), PersonId] = Fetcher.caching[DataContext, (PersonId, Seq[Work]), PersonId](
    fetch = (ctx, personIds) => Future.successful { ctx.repo.workDao.getByPersonId(personIds) }
  )
  val authorWorkIdFetcher: Fetcher[DataContext, (WorkId, Seq[PersonId]), (WorkId, Seq[PersonId]), WorkId] = Fetcher.caching[DataContext, (WorkId, Seq[PersonId]), WorkId](
    fetch = (ctx, workIds) => Future.successful { ctx.repo.workDao.getPersonsByWork(workIds) }
  )

  def workById(id: WorkId)(implicit ctx: DataContext): DeferredValue[DataContext, Option[Work]] = {
    DeferredValue(byIdFetcher.deferOpt(id))
  }

  def worksByPersonId(id: PersonId)(implicit ctx: DataContext): DeferredValue[DataContext, Option[Seq[Work]]] = {
    DeferredValue(byPersonIdFetcher.deferOpt(id))
      .map(_.map { case (_, works) => works })
  }

  def authorIdsOfWork(workId: WorkId)(implicit ctx: DataContext): DeferredValue[DataContext, Option[Seq[PersonId]]] = {
    DeferredValue(authorWorkIdFetcher.deferOpt(workId))
      .map(_.map { case (_, personIds) => personIds })
  }
}
