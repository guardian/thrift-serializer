package com.gu.thrift.serializer

import org.scalatest.{FreeSpec, Matchers}
import com.gu.crier.model.event.v1.{Event, EventPayload}
import com.twitter.scrooge.ThriftStructCodec

import java.nio.ByteBuffer
import java.util.Base64
import scala.io.Source
import scala.util.{Failure, Success}

class RealDataTest extends FreeSpec with Matchers {
  implicit val codec: ThriftStructCodec[Event] = Event

  "correctly decodes real data from bytebuffer" in {
    val content = Source.fromResource("failing_data.b64")
    val arr = Base64.getDecoder.decode(content.mkString)
    val rawContent = ByteBuffer.wrap(arr)
    val result = ThriftDeserializer.deserialize(rawContent)

    result match {
      case Failure(err)=>
        println(s"Could not deserialize: ${err.getMessage}", err)
      case Success(_)=>

    }
    result.isSuccess should be(true)
  }

  "correctly decodes real data from array" in {
    val content = Source.fromResource("failing_data.b64")
    val arr = Base64.getDecoder.decode(content.mkString)
    ThriftDeserializer.deserialize(arr).isSuccess should be(true)
  }
}
