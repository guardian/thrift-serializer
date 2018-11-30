package com.gu.thrift.serializer

import java.nio.ByteBuffer

import com.twitter.scrooge.{ThriftStruct, ThriftStructCodec}
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.TIOStreamTransport
import scala.util.Try

object ThriftDeserializer {

  /** Reads a Thrift value from a byte array. By default, will try to read
    * the first byte for details on how the value is encoded. Plain
    * values can be decoded by setting the `noHeader` flag to `true`.
    */
  def deserialize[T <: ThriftStruct : ThriftStructCodec](buffer: Array[Byte], noHeader: Boolean = false): Try[T] =
    Try {
      if (!noSettings) {
        val settings = buffer.head
        val compressionType = compression(settings)
        compressionType match {
          case NoneType => payload(buffer.tail)
          case GzipType => payload(GzipCompression.uncompress(buffer.tail))
          case ZstdType => payload(ZstdCompression.uncompress(buffer.tail))
        }
      } else payload(buffer)
    }

  private def compression(settings: Byte): CompressionType = {
    val compressionMask = 0x07.toByte
    val compressionType = (settings & compressionMask).toByte
    compressionType match {
      case 0 => NoneType
      case 1 => GzipType
      case 2 => ZstdType
      case x => throw new RuntimeException(s"The compression type: ${x} is not supported")
    }
  }

  private def payload[T <: ThriftStruct](buffer: Array[Byte])(implicit codec: ThriftStructCodec[T]): T = {
    val byteBuffer: ByteBuffer = ByteBuffer.wrap(buffer)
    val bbis = new ByteBufferBackedInputStream(byteBuffer)
    val transport = new TIOStreamTransport(bbis)
    val protocol = new TCompactProtocol(transport)
    val struct = codec.decode(protocol)
    struct
  }
}

