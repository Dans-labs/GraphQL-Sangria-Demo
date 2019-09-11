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

import nl.knaw.dans.graphql.demo.app.model.{ Person, Work }
import nl.knaw.dans.graphql.demo.app.repository.{ PersonDao, Repository, WorkDao }
import org.joda.time.LocalDate

class DemoRepo {

  private val pId1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  private val pId2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  private val pId3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  private val pId4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  private val pId5 = UUID.fromString("00000000-0000-0000-0000-000000000005")

  private val wId1 = UUID.fromString("10000000-0000-0000-0000-000000000000")
  private val wId2 = UUID.fromString("20000000-0000-0000-0000-000000000000")
  private val wId3 = UUID.fromString("30000000-0000-0000-0000-000000000000")
  private val wId4 = UUID.fromString("40000000-0000-0000-0000-000000000000")
  
  private val personDao: PersonDao = new DemoPersonDao(Map(
    pId1 -> Person(pId1, "Alice", new LocalDate(1990, 1, 1), "London"),
    pId2 -> Person(pId2, "Bob", new LocalDate(1992, 2, 2), "Berlin"),
    pId3 -> Person(pId3, "Charlie", new LocalDate(1994, 3, 3), "Paris"),
    pId4 -> Person(pId4, "Dave", new LocalDate(1996, 4, 4), "Rome"),
    pId5 -> Person(pId5, "Eve", new LocalDate(1998, 5, 5), "The Hague"),
  ))
  private val workDao: WorkDao = new DemoWorkDao(
    initialWorks = Map(
      wId1 -> Work(wId1, "title1"),
      wId2 -> Work(wId2, "title2"),
      wId3 -> Work(wId3, "title3"),
      wId4 -> Work(wId4, "title4"),
    ),
    initialLinks = Map(
      pId1 -> List(wId1, wId3),
      pId2 -> List(wId1, wId2, wId3, wId4),
      pId3 -> List(wId2, wId3),
      pId4 -> List(wId1),
      pId5 -> List(wId3, wId4),
    )
  )

  def repository: Repository = Repository(
    personDao,
    workDao,
  )
}
