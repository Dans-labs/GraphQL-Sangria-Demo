package nl.knaw.dans.graphql.demo.app.graphql.relay

import sangria.relay._
import sangria.schema._

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.higherKinds
import scala.reflect.ClassTag

trait ExtendedConnection[T] extends Connection[T] {
  def totalCount: Int
}

object ExtendedConnection {

  def definition[Ctx, Conn[_], Val](name: String,
                                    nodeType: OutputType[Val],
                                    edgeFields: => List[Field[Ctx, Edge[Val]]] = Nil,
                                    connectionFields: => List[Field[Ctx, Conn[Val]]] = Nil
                                   )(implicit connEv: ExtendedConnectionLike[Conn, Val, Edge[Val]],
                                     classEv: ClassTag[Conn[Val]]): ConnectionDefinition[Ctx, Conn[Val], Val, Edge[Val]] = {
    definitionWithEdge[Ctx, Conn, Val, Edge[Val]](name, nodeType, edgeFields, connectionFields)
  }

  def definitionWithEdge[Ctx, Conn[_], Val, E <: Edge[Val]](name: String,
                                                            nodeType: OutputType[Val],
                                                            edgeFields: => List[Field[Ctx, E]] = Nil,
                                                            connectionFields: => List[Field[Ctx, Conn[Val]]] = Nil
                                                           )(implicit connEv: ExtendedConnectionLike[Conn, Val, E],
                                                             classEv: ClassTag[Conn[Val]],
                                                             classE: ClassTag[E]): ConnectionDefinition[Ctx, Conn[Val], Val, E] = {
    Connection.definitionWithEdge(
      name = name,
      nodeType = nodeType,
      edgeFields = edgeFields,
      connectionFields = connectionFields ++ fields[Ctx, Conn[Val]](
        Field(
          name = "totalCount",
          fieldType = IntType,
          description = Option("Identifies the total count of items in the connection."),
          resolve = ctx => connEv.totalCount(ctx.value),
        ),
      ),
    )
  }

  def empty[T]: ExtendedConnection[T] = {
    DefaultExtendedConnection(Connection.empty[T], 0)
  }

  def connectionFromFutureSeq[T](seq: Future[Seq[T]], args: ConnectionArgs)(implicit ec: ExecutionContext): Future[ExtendedConnection[T]] = {
    seq.map(connectionFromSeq(_, args))
  }

  def connectionFromSeq[T](seq: Seq[T], args: ConnectionArgs): ExtendedConnection[T] = {
    connectionFromSeq(seq, args, SliceInfo(0, seq.size))
  }

  def connectionFromFutureSeq[T](seq: Future[Seq[T]], args: ConnectionArgs, sliceInfo: SliceInfo)(implicit ec: ExecutionContext): Future[ExtendedConnection[T]] = {
    seq.map(connectionFromSeq(_, args, sliceInfo))
  }

  def connectionFromSeq[T](seqSlice: Seq[T], args: ConnectionArgs, sliceInfo: SliceInfo): ExtendedConnection[T] = {
    DefaultExtendedConnection(Connection.connectionFromSeq(seqSlice, args, sliceInfo), seqSlice.size)
  }
}
