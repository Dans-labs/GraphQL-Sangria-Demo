package nl.knaw.dans.graphql.demo.app.graphql.relay

import sangria.relay.{ Connection, DefaultConnection, Edge, PageInfo }

private[relay] case class DefaultExtendedConnection[T](pageInfo: PageInfo,
                                                       edges: Seq[Edge[T]],
                                                       totalCount: Int,
                                                      ) extends ExtendedConnection[T]

private[relay] object DefaultExtendedConnection {
  def apply[T](conn: Connection[T], size: Int): DefaultExtendedConnection[T] = {
    val DefaultConnection(pageInfo, edges) = conn

    DefaultExtendedConnection(pageInfo, edges, size)
  }
}
