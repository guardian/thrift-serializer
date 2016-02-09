package com.gu.thrift.serializer

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.TIOStreamTransport
import scala.util.Try
import java.nio.ByteBuffer
import com.gu.auditing.model.v1.Notification

object ThriftDeserializer {

  def deserializeEvent(buffer: Array[Byte]): Try[Notification] = {
    Try {
      val settings = buffer.head
      val compressionType = compression(settings)
      compressionType match {
        case NoneType =>{
          println("was nonetype")
          payload(buffer.tail)
        }
        case GzipType => payload(Compression.gunzip(buffer.tail))
      }
    }
  }

  private def compression(settings: Byte): CompressionType = {
    val compressionMask = 0x07.toByte
    val compressionType = (settings & compressionMask).toByte
    compressionType match {
      case 0 => NoneType
      case 1 => GzipType
      case x => throw new RuntimeException(s"The compression type: ${x} is not supported")
    }
  }

  private def payload(buffer: Array[Byte]): Notification = {
    val byteBuffer: ByteBuffer = ByteBuffer.wrap(buffer)
    val bbis = new ByteBufferBackedInputStream(byteBuffer)
    val transport = new TIOStreamTransport(bbis)
    val protocol = new TCompactProtocol(transport)
    val struct = Notification.decode(protocol)
    struct
  }
}



