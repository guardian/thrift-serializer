# You can generate the node version of this file with
# thrift --gen js:node --out nodejs/test/models nodejs/test/models/sample.thrift

struct Message {
	1: required string text;

	2: required bool isError;

	3: optional i32 count;
}