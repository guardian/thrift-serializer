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

* You can use the ThriftDeserializer like this:

```

MyDeserializer.deserialize(buffer).map(myEvent => {
  // process the event
}

def myMethod(thriftObject: MyThriftObject): Unit = {
  val bytes = serializeToBytes(thriftObject)
  //Do something with bytes
}
```

If there is no compression type recorded in the bytes you wish to
deserialize or if you don't want these to be included when serializing
a thrift object, you can set the last arguments of the function calls
to true. They are false by default.

```
MyDeserializer.deserialize(buffer, true).map(myEvent => {
    // process the event
}
```
```
def myMethod(thriftObject: MyThriftObject, true): Unit = {
  val bytes = serializeToBytes(thriftObject)
  //Do something with bytes
}
```

### How to publish

* The library is published to Maven Central. To publish, register
to Sonatype and get added to Guardian projects.

* Run sbt release. You will be asked about what version you want the
release to be during the release process, you do not have to update it manually.


### Node JS

* Install using npm

```
npm install --save thrift-serializer
```

in your application or lambda you can **decode** messages

```
var Message = require('your-thrift-model');
var serializer = require('thrift-serializer');

serializer.read(Message, bytes, function (err, msg) {
	console.log(msg);
});
```

or **encode** them

```
var Message = require('your-thrift-model');
var serializer = require('thrift-serializer');

var message = new Message({
	someData: ''
});

serializer.write(message, serializer.Compression.Gzip, function (err, bytes) {
	// do something with your bytes, e.g. convert the buffer into a base64 string
	console.log(bytes.toString('base64'));
});
```
