package com.gu.thrift.serializer

import com.gu.auditing.model.v1.{App, Notification}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers}
import scala.concurrent.Future

class ThirftSerializerTest extends FreeSpec with Matchers {

  object NotificationDeserializer extends ThriftDeserializer[Notification] {
    val codec = Notification
  }

  val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Some("message"))

  "serialises the model correctly with GzipType" - {

    val bytes = ThriftSerializer.serializeToBytes(notification, Some(GzipType), Some(128))

    "lower order bits set correctly" in {
      bytes.head should be (1)
    }

    "serialized and deserialized back to itself" in {

      ScalaFutures.whenReady(NotificationDeserializer.deserialize(bytes)) { result =>
        result should be(notification)
      }
    }
  }

  "serialises the model correctly with NoneType" - {

    val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

    val bytes = ThriftSerializer.serializeToBytes(notification, Some(NoneType), Some(128))
    "lower order bits set correctly" in {
      bytes.head should be (0)
    }

    "serialized and deserialized back to itself" in {
      ScalaFutures.whenReady(NotificationDeserializer.deserialize(bytes)) { result =>
        result should be(notification)
      }
    }
  }

  "serialises the model correctly without settings" - {

    val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

    val bytes = ThriftSerializer.serializeToBytes(notification, None, Some(128), true)

    "serialized and deserialized back to itself" in {
      ScalaFutures.whenReady(NotificationDeserializer.deserialize(bytes, true)) { result =>
        result should be(notification)
      }
    }
  }

  "deserialization throws when invalid compression bytes set" - {

    val errorMessage = "The compression type: 2 is not supported"
    val bytes = Array.fill[Byte](2)(0x02.toByte)
    val future = NotificationDeserializer.deserialize(bytes)

    ScalaFutures.whenReady(NotificationDeserializer.deserialize(bytes).failed) { error =>
      error.getMessage should be (errorMessage)
    }

  }
  "deserilization throws when invalid set of bytes" - {

    val errorMessage = "Required field 'app' was not found in serialized data for struct Notification"
    val bytes = Array.fill[Byte](5)(0x00.toByte)
    val future = NotificationDeserializer.deserialize(bytes)

    ScalaFutures.whenReady(NotificationDeserializer.deserialize(bytes).failed) { error =>
      error.getMessage() should be (errorMessage)
    }

  }
}

