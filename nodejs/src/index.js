var zlib = require('zlib');
var TFramedTransport = require('thrift/lib/nodejs/lib/thrift/framed_transport');
var TCompactProtocol = require('thrift/lib/nodejs/lib/thrift/compact_protocol');

var Compression = exports.Compression = {
	None: 0,
	Gzip: 1
};

exports.write = function (struct, compression, callback) {
	switch (compression || Compression.None) {
		case Compression.None:
			serialize(struct, callback);
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
	}
};

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

function serialize (struct, callback) {
	writeBytes(struct, callback, function (buffer, cb) {
		process.nextTick(function () {
			cb(null, Buffer.concat([
				new Buffer([Compression.None]),
				buffer
			]));
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
