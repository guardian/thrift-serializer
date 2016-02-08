package com.gu.thrift.serializer

import com.gu.auditing.model.v1.{App, Notification}

import org.scalatest.{FreeSpec, Matchers}

class ThirftSerializerTest extends FreeSpec with Matchers {

  "serialises and desrialises the model correctly with GzipType" - {

      val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

      val bytes = ThriftSerializer.serializeToBytes(notification, Some(GzipType), Some(128))

      "lower order bits set correctly" in {
          bytes.head should be (1)
      }
  }

  "serialises and desrialises the model correctly with NoneType" - {

      val notification = Notification(App.FaciaTool, "operation", "email", "date", Some("id"), Option("message"))

      val bytes = ThriftSerializer.serializeToBytes(notification, Some(NoneType), Some(128))
      "lower order bits set correctly" in {
          bytes.head should be (0)
      }
  }
}

