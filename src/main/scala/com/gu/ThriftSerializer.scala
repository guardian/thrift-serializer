package com.gu.thrift.serializer

import java.lang.{Byte => JByte}

import com.twitter.scrooge.ThriftStruct
import org.apache.thrift.protocol.TCompactProtocol
import org.apache.thrift.transport.TMemoryBuffer

object ThriftSerializer {

  private val compressionDefault: CompressionType = NoneType
  private val initialBufferDefault = 128

  def serializeToBytes(struct: ThriftStruct, userCompressionType: Option[CompressionType],
    thriftBufferInitialSize: Option[Int], withSettings: Boolean=false): Array[Byte] = {

    val bufferSize = thriftBufferInitialSize.getOrElse(initialBufferDefault)
    val buffer = new TMemoryBuffer(bufferSize)
    val protocol = new TCompactProtocol(buffer)

    struct.write(protocol)

    if (!withSettings) {
      val compressionType = userCompressionType.getOrElse(compressionDefault)

      val compression: Byte = {
        compressionType match {
          case NoneType => 0x00.toByte
          case GzipType => 0x01.toByte
        }
      }

      val other: Byte = 0x07.toByte

      /*
      Ox00000XXX - used for compression type
      OxXXXXX000 - kept for the future
      */
     val settings: Byte = (other & compression).toByte

     settings +: payload(buffer.getArray, compressionType)
    } else buffer.getArray
  }

  private def payload(data: Array[Byte], compressionType: CompressionType): Array[Byte] = {
    compressionType match {
      case NoneType => data
      case GzipType => Compression.gzip(data)
    }
  }

}
