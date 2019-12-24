package nl.knaw.dans.graphql.demo.app.graphql.relay

import sangria.relay.{ ConnectionLike, Edge, PageInfo }

import scala.annotation.implicitNotFound
import scala.language.higherKinds

@implicitNotFound("Type ${T} can't be used as a ExtendedConnection. Please consider defining implicit instance of nl.dans.knaw.easy.properties.app.relay.ExtendedConnectionLike for type ${T} or extending nl.dans.knaw.easy.properties.app.relay.ExtendedConnection trait.")
private[relay] trait ExtendedConnectionLike[T[_], Val, E <: Edge[Val]] extends ConnectionLike[T, Val, E] {
  def totalCount(conn: T[Val]): Int
}

private[relay] object ExtendedConnectionLike {
  private object ExtendedConnectionIsExtendedConnectionLike extends ExtendedConnectionLike[ExtendedConnection, Any, Edge[Any]] {
    override def pageInfo(conn: ExtendedConnection[Any]): PageInfo = conn.pageInfo

    override def edges(conn: ExtendedConnection[Any]): Seq[Edge[Any]] = conn.edges

    override def totalCount(conn: ExtendedConnection[Any]): Int = conn.totalCount
  }

  implicit def extendedConnectionIsExtendedConnectionLike[E <: Edge[Val], Val, T[_]]: ExtendedConnectionLike[T, Val, E] =
    ExtendedConnectionIsExtendedConnectionLike.asInstanceOf[ExtendedConnectionLike[T, Val, E]]
}
