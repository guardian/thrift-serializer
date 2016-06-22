var zlib = require('zlib');
var TFramedTransport = require('thrift/lib/nodejs/lib/thrift/framed_transport');
var TCompactProtocol = require('thrift/lib/nodejs/lib/thrift/compact_protocol');

var Compression = exports.Compression = {
	None: 0,
	Gzip: 1,
  Disable: 2
};

exports.write = function (struct, compression, callback) {

    switch (compression || Compression.None) {
        case Compression.Disable:
            serialize(struct, true, callback);
        case Compression.None:
            serialize(struct, false, callback);
            break;
        case Compression.Gzip:
            zip(struct, callback);
            break;
        default:
            callback(new Error('Unable to understand the compression applied'));
            break;
    }
};

exports.read = function (Model, rawData, callback) {

	  var buffer = Buffer.isBuffer(rawData) ? rawData : new Buffer(rawData, 'base64');

    if (arguments.length === 4) {
        callback = arguments[3];
        readWithoutCompression(Model, rawData, buffer, callback);
    } else if (arguments.length === 3) {
        readWithCompression(Model, rawData, buffer, callback);
    }

};

function readWithCompression(Model, rawData, buffer, callback) {
    switch (buffer.readInt8(0)) {
        case Compression.None:
            readBytes(buffer.slice(1), Model, callback);
            break;
        case Compression.Gzip:
            unzip(buffer.slice(1), Model, callback);
            break;
        default:
            callback(new Error('Unable to understand the compression applied'));
            break;
    };

}

function readWithoutCompression(Model, rawData, buffer, callback) {
    readBytes(buffer, Model, callback);
}


function writeBytes (struct, callback, transform) {
	try {
		var transport = new TFramedTransport(null, function (buffer) {
			// Flush puts a 4-byte header, which needs to be parsed/sliced.
			transform(buffer.slice(4), callback);
		});
		var protocol  = new TCompactProtocol(transport);
		struct.write(protocol);
		transport.flush();
	} catch (ex) {
		process.nextTick(function () {
			callback(ex);
		});
	}
}

function serialize (struct, noSettings, callback) {
	writeBytes(struct, callback, function (buffer, cb) {
		process.nextTick(function () {

      var bufferWithSettings;
      if (noSettings) {
          bufferWithSettings = buffer;
      } else {
          bufferWithSettings = Buffer.concat([new Buffer([Compression.None]), buffer]);
      }

			cb(null, bufferWithSettings);
		});
	});
}

function zip (struct, callback) {
	writeBytes(struct, callback, function (buffer, cb) {
		zlib.gzip(buffer, function (err, data) {
			process.nextTick(function () {
				if (err) {
					cb(err);
				} else {
					cb(null, Buffer.concat([
						new Buffer([Compression.Gzip]),
						data
					]));
				}
			});
		});
	});
}

function unzip (buffer, Model, callback) {
	zlib.gunzip(buffer, function (err, message) {
		if (err) {
			process.nextTick(function () {
				callback(err);
			});
		} else {
			readBytes(message, Model, callback);
		}
	});
}

function readBytes (buffer, Model, callback) {
	var client = new Model();
	try {
		var transport = new TFramedTransport(buffer);
		var protocol  = new TCompactProtocol(transport);
		client.read(protocol);
		process.nextTick(function () {
			callback(null, client);
		});
	} catch (ex) {
		process.nextTick(function () {
			callback(ex);
		});
	}
}
