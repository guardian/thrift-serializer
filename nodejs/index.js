var zlib = require('zlib');
var thrift = require('thrift');

exports.read = function (Model, rawData, callback) {
	var buffer = Buffer.isBuffer(rawData) ? rawData : new Buffer(rawData, 'base64');

	switch (buffer.readInt8(0)) {
		case 0:
			// TODO implement no compression
			throw new Error('Uncompressed thrift message not implemented');
			break;
		case 1:
			unzip(buffer.slice(1), Model, callback);
			break;
		default:
			callback(new Error('Unable to understand the compression applied'));
			break;
	}
};

function unzip (buffer, Model, callback) {
	zlib.gunzip(buffer, function (err, message) {
		if (err) {
			callback(err);
		} else {
			var client = new Model();
			try {
				var transport = new thrift.TFramedTransport(message);
				var protocol  = new thrift.TCompactProtocol(transport);
				client.read(protocol);
				callback(null, client);
			} catch (ex) {
				callback(ex);
			}
		}
	});
}
