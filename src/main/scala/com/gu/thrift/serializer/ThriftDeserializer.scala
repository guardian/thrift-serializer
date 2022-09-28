package com.gu.thrift.serializer

import java.nio.ByteBuffer

import com.twitter.scrooge.{ThriftStruct, ThriftStructCodec}
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.TIOStreamTransport
import scala.util.Try

object ThriftDeserializer {
  /** Reads a Thrift value from a byte buffer. By default, will try to read
    * the first byte for details on how the value is encoded. Plain
    * values can be decoded by setting the `noHeader` flag to `true`.
    */
  def deserialize[T <: ThriftStruct : ThriftStructCodec](buffer: ByteBuffer, noHeader: Boolean): Try[T] = Try {
    if(!noHeader) {
      val settings = buffer.get() //also increments buffer position by 1, so buffer.slice() below returns the "tail"
      val compressionType = compression(settings)
      compressionType match {
        case NoneType => payload(buffer.slice())
        case GzipType => payload(GzipCompression.uncompress(buffer.slice()))
        case ZstdType => payload(ZstdCompression.uncompress(buffer.slice()))
      }
    } else {
      payload(buffer)
    }
  }

  /** Reads a Thrift value from a byte array. By default, will try to read
    * the first byte for details on how the value is encoded. Plain
    * values can be decoded by setting the `noHeader` flag to `true`.
    */
  def deserialize[T <: ThriftStruct : ThriftStructCodec](buffer: Array[Byte], noHeader: Boolean): Try[T] = deserialize(ByteBuffer.wrap(buffer), noHeader)

  def deserialize[T <: ThriftStruct : ThriftStructCodec](buffer: Array[Byte]):Try[T] = deserialize(buffer, false) orElse deserialize(buffer, true)
  def deserialize[T <: ThriftStruct : ThriftStructCodec](buffer: ByteBuffer):Try[T] = deserialize(buffer, false) orElse deserialize(buffer, true)

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

  private def payload[T <: ThriftStruct](byteBuffer: ByteBuffer)(implicit codec: ThriftStructCodec[T]): T = {
    val bbis = new ByteBufferBackedInputStream(byteBuffer)
    val transport = new TIOStreamTransport(bbis)
    val protocol = new TCompactProtocol(transport)
    val struct = codec.decode(protocol)
    struct
  }
}

