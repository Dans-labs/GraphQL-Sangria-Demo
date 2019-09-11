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

import nl.knaw.dans.graphql.demo.app.model.{ InputPerson, Person, PersonId }
import nl.knaw.dans.graphql.demo.app.repository.PersonDao
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.collection.mutable

class DemoPersonDao(initalInput: Map[PersonId, Person] = Map.empty) extends PersonDao with DebugEnhancedLogging {

  private val repo: mutable.Map[PersonId, Person] = mutable.Map(initalInput.toSeq: _*)

  override def getAll: Seq[Person] = {
    trace(())
    repo.values.toSeq
  }

  override def find(id: PersonId): Option[Person] = {
    trace(id)
    repo.get(id)
  }

  override def find(ids: Seq[PersonId]): Seq[Person] = {
    trace(ids)
    ids.flatMap(repo.get)
  }

  override def store(person: InputPerson): Person = {
    trace(person)
    val personId = UUID.randomUUID()
    val p = person.toPerson(personId)

    repo += (personId -> p)

    p
  }
}
