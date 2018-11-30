package com.gu.thrift.serializer

import com.gu.auditing.model.v1.{App, Notification}
import org.scalatest.{FreeSpec, Matchers}
import scala.util.Success

class ThriftSerializerTest extends FreeSpec with Matchers {

  implicit val codec = Notification

  val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Some("message"))

  "serialises the model correctly with ZstdType" - {

    val bytes = ThriftSerializer.serializeToBytes(notification, Some(ZstdType), Some(128))

    "lower order bits set correctly" in {
      bytes.head should be (2)
    }

    "serialized and deserialized back to itself" in {
      ThriftDeserializer.deserialize(bytes) should be(Success(notification))
    }
  }

  "serialises the model correctly with GzipType" - {

    val bytes = ThriftSerializer.serializeToBytes(notification, Some(GzipType), Some(128))

    "lower order bits set correctly" in {
      bytes.head should be (1)
    }

    "serialized and deserialized back to itself" in {

      ThriftDeserializer.deserialize(bytes) should be(Success(notification))
    }
  }

  "serialises the model correctly with NoneType" - {

    val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

    val bytes = ThriftSerializer.serializeToBytes(notification, Some(NoneType), Some(128))
    "lower order bits set correctly" in {
      bytes.head should be (0)
    }

    "serialized and deserialized back to itself" in {
      ThriftDeserializer.deserialize(bytes) should be(Success(notification))
    }
  }

  "serialises the model correctly without settings" - {

    val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

    val bytes = ThriftSerializer.serializeToBytes(notification, None, Some(128), true)

    "serialized and deserialized back to itself" in {
      ThriftDeserializer.deserialize(bytes, true) should be(Success(notification))
    }
  }

  "deserialization throws when invalid compression bytes set" in {

    val errorMessage = "The compression type: 3 is not supported"
    val bytes = Array.fill[Byte](2)(0x03.toByte)

    ThriftDeserializer.deserialize(bytes, false).failed.map(_.getMessage) should be (Success(errorMessage))

  }
  "deserilization throws when invalid set of bytes" in {

    val errorMessage = "Required field 'app' was not found in serialized data for struct Notification"
    val bytes = Array.fill[Byte](5)(0x00.toByte)

    ThriftDeserializer.deserialize(bytes).failed.map(_.getMessage) should be (Success(errorMessage))

  }
}

