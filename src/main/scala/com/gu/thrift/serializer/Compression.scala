package com.gu.thrift.serializer

import com.github.luben.zstd.{ZstdInputStream, ZstdOutputStream}

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.annotation.tailrec

sealed trait CompressionType
case object NoneType extends CompressionType
case object GzipType extends CompressionType
case object ZstdType extends CompressionType

object GzipCompression {
  def compress(data: Array[Byte]): Array[Byte] =
    try {
      val bos = new ByteArrayOutputStream()
      val out = new GZIPOutputStream(bos)
      out.write(data)
      out.close()
      bos.toByteArray
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  def uncompress(data: Array[Byte]): Array[Byte] =
    try {
      val bos = new ByteArrayOutputStream()
      val bis = new ByteArrayInputStream(data)
      val in = new GZIPInputStream(bis)
      IOUtils.copy(in, bos)
      in.close()
      bos.close()
      bos.toByteArray()
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
}

object ZstdCompression {
  def compress(data: Array[Byte]): Array[Byte] = 
    try {
      val bos = new ByteArrayOutputStream()
      val out = new ZstdOutputStream(bos)
      out.write(data)
      out.close()
      bos.toByteArray
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
  
  def uncompress(data: Array[Byte]): Array[Byte] = 
    try {
      val bos = new ByteArrayOutputStream()
      val in = new ZstdInputStream(new ByteArrayInputStream(data))
      IOUtils.copy(in, bos)
      in.close()
      bos.close()
      bos.toByteArray
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
      
}

private[serializer] object IOUtils {
  @tailrec
  def copy(is: InputStream, os: OutputStream, bufferSize: Int = 1024) {
    val buffer = new Array[Byte](bufferSize)
    is.read(buffer, 0, buffer.length) match {
      case -1 => ()
      case n =>
        os.write(buffer, 0, n)
        copy(is, os, bufferSize)
    }
  }
}