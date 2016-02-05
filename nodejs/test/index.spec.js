var expect = require('chai').expect;
var Message = require('./models/sample_types').Message;
var serializer = require('../src/index');

describe('Thrift serializer', function () {
	it('should serialize with compression', function (done) {
		var message = new Message({
			text: 'banana',
			isError: false,
			count: 4
		});

		serializer.write(message, serializer.Compression.Gzip, function (err, bytes) {
			expect(err).to.be.null;

			serializer.read(Message, bytes, function (err, msg) {
				console.log(err, msg)
				expect(msg.text).to.eql('banana');
				expect(msg.isError).to.be.false;
				expect(msg.count).to.eql(4);

				done(err);
			});
		});
	});
});
