package com.gu.thrift.serializer

import java.lang.{Byte => JByte}

import com.twitter.scrooge.ThriftStruct
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.TMemoryBuffer

trait ThriftSerializer {
  import ThriftSerializer._

  def serializeToBytes(struct: ThriftStruct): Array[Byte] = {
    val buffer = new TMemoryBuffer(ThriftBufferInitialSize)
    val protocol = new TCompactProtocol(buffer)
    struct.write(protocol)
    settings +: payload(buffer.getArray)
  }

  def settings: Byte = {
    /*
       Ox00000XXX - used for compression type
       OxXXXXX000 - kept for the future
    */
    val other: Byte = 0x07.toByte
    (other & compression).toByte
  }

  val compression: Byte = {
    compressionType match {
      case NoneType => 0x00.toByte
      case GzipType => 0x01.toByte
    }
  }

  def payload(data: Array[Byte]): Array[Byte] = {
    compressionType match {
      case NoneType => data
      case GzipType => Compression.gzip(data)
    }
  }

}

object ThriftSerializer {

  private val ThriftBufferInitialSize = 128
  private val compressionType: CompressionType = GzipType

}
