var expect = require('chai').expect;
var Message = require('./models/sample_types').Message;
var serializer = require('../src/index');

describe('Thrift serializer', function () {

	it('should serialize when compression is not specified', function (done) {
		var message = new Message({
			text: 'apple',
			isError: true,
			count: 0
		});

		serializer.write(message, serializer.Compression.Disable, function (err, bytes) {
			expect(err).to.be.null;

			serializer.read(Message, bytes, serializer.Compression.Disable, function (err, msg) {
				expect(msg.text).to.eql('apple');
				expect(msg.isError).to.be.true;
				expect(msg.count).to.eql(0);

				done(err);
			});
		});
	});

	it('should serialize without compression', function (done) {
		var message = new Message({
			text: 'apple',
			isError: true,
			count: 0
		});

		serializer.write(message, serializer.Compression.None, function (err, bytes) {
			expect(err).to.be.null;

			serializer.read(Message, bytes, function (err, msg) {
				expect(msg.text).to.eql('apple');
				expect(msg.isError).to.be.true;
				expect(msg.count).to.eql(0);

				done(err);
			});
		});
	});

	it('should serialize with compression', function (done) {
		var message = new Message({
			text: 'banana',
			isError: false,
			count: 4
		});

		serializer.write(message, serializer.Compression.Gzip, function (err, bytes) {
			expect(err).to.be.null;

			serializer.read(Message, bytes, function (err, msg) {
				expect(msg.text).to.eql('banana');
				expect(msg.isError).to.be.false;
				expect(msg.count).to.eql(4);

				done(err);
			});
		});
	});
});
