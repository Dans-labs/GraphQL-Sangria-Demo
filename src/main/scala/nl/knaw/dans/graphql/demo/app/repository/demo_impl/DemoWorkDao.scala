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
package nl.knaw.dans.graphql.demo.app.repository.demo_impl

import java.util.UUID

import nl.knaw.dans.graphql.demo.app.model.{ InputWork, PersonId, Work, WorkId }
import nl.knaw.dans.graphql.demo.app.repository.WorkDao

import scala.collection.mutable

class DemoWorkDao(initialWorks: Map[WorkId, Work] = Map.empty,
                  initialLinks: Map[PersonId, List[WorkId]] = Map.empty,
                 ) extends WorkDao {

  private val workRepo: mutable.Map[WorkId, Work] = mutable.Map(initialWorks.toSeq: _*)
  private val personWorkRepo: mutable.Map[PersonId, List[WorkId]] = mutable.Map(initialLinks.toSeq: _*)

  override def getById(id: WorkId): Option[Work] = {
    workRepo.get(id)
  }

  override def getById(ids: Seq[WorkId]): Seq[Work] = {
    ids.flatMap(workRepo.get)
  }

  override def getByPersonId(id: PersonId): Option[Seq[Work]] = {
    personWorkRepo.get(id).map(_.flatMap(workRepo.get))
  }

  override def getByPersonId(ids: Seq[PersonId]): Seq[(PersonId, Seq[Work])] = {
    ids.flatMap(personId => getByPersonId(personId).map(personId -> _))
  }

  override def store(personIds: Seq[PersonId], work: InputWork): Work = {
    val workId = UUID.randomUUID()
    val w = work.toWork(workId)

    workRepo += (workId -> w)
    for (personId <- personIds) {
      val presentIds = personWorkRepo.getOrElseUpdate(personId, List.empty)
      val ids = workId :: presentIds
      personWorkRepo.update(personId, ids)
    }

    w
  }

  override def getPersonsByWork(id: WorkId): Option[Seq[PersonId]] = {
    if (workRepo.contains(id)) Some {
      personWorkRepo.collect { case (personId, workIds) if workIds.contains(id) => personId }.toSeq
    }
    else None
  }

  override def getPersonsByWork(ids: Seq[WorkId]): Seq[(WorkId, Seq[PersonId])] = {
    ids.flatMap(workId => getPersonsByWork(workId).map(workId -> _))
  }
}
