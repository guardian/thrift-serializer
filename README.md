Thrift Serializer
========

A library for serializing a thrift model into bytes.

### How to use

* Add the library as a depency by adding

```
"com.gu" %% "thrift-serializer" % "0.0.1"
```

to your applications libraryDepencies.

* You can then use ThriftSerializer in your application:

```
import com.gu.thrift.serializer.ThriftSerializer

object myObject extends ThriftSerializer {

  def myMethod(thriftObject: MyThriftObject): Unit = {
    val bytes = serializeToBytes(thriftObject)
    //Do something with bytes
  }
}
```

### How to publish

* The library is published to Maven Central. To publish, register
to Sonatype and get added to Guardian projects.

* Run sbt release. You will be asked about what version you want the
release to be during the release process, you do not have to update it manually.
