var zlib = require('zlib');
var thrift = require('thrift');

var Compression = exports.Compression = {
	None: 0,
	Gzip: 1
};

exports.write = function (struct, compression, callback) {
	switch (compression || Compression.None) {
		case Compression.None:
			//TODO implement this
			throw new Error('Uncompressed thrift message not implemented yet');
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
			// TODO implement no compression
			throw new Error('Uncompressed thrift message not implemented yet');
			break;
		case Compression.Gzip:
			unzip(buffer.slice(1), Model, callback);
			break;
		default:
			callback(new Error('Unable to understand the compression applied'));
			break;
	}
};

function zip (struct, callback) {
	try {
		var transport = new thrift.TFramedTransport(null, function (buffer) {
			// Flush puts a 4-byte header, which needs to be parsed/sliced.
  			buffer = buffer.slice(4);

			zlib.gzip(buffer, function (err, data) {
				process.nextTick(function () {
					callback(null, Buffer.concat([
						new Buffer([Compression.Gzip]),
						data
					]));
				});
			});
		});
		var protocol  = new thrift.TCompactProtocol(transport);
		struct.write(protocol);
		transport.flush();
	} catch (ex) {
		process.nextTick(function () {
			callback(ex);
		});
	}
}

function unzip (buffer, Model, callback) {
	zlib.gunzip(buffer, function (err, message) {
		if (err) {
			process.nextTick(function () {
				callback(err);
			});
		} else {
			var client = new Model();
			try {
				var transport = new thrift.TFramedTransport(message);
				var protocol  = new thrift.TCompactProtocol(transport);
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
	});
}
