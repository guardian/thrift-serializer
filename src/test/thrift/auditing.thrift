#@namespace scala com.gu.auditing.model.v1

/** This is taken from https://github.com/guardian/auditing-thrift-model/blob/master/src/main/thrift/auditing.thrift
  * as an example for testing against.
  **/

/** List of applications **/
enum App {
    FaciaTool = 1
    StoryPackages = 2
    TagManager = 3
    Targeting = 4
    MediaServices = 5
}

/** Basic message **/
struct Notification {
    /** Application sending the message **/
    1: required App app;

    /** action / operation happening in the tool **/
    2: required string operation;

    /** email of the person making the operation, pruned regularly **/
    3: required string userEmail;

    /** ISO 8601 date of when the operation occurred **/
    4: required string date;

    /** Identifies a resource in the source application **/
    5: optional string resourceId;

    /** Additional data relevant to the source application, this is pruned regularly **/
    6: optional string message;

    /** Additional data relevant to the source application, this is stored for the entire notification duration **/
    7: optional string shortMessage;

    /** ISO 8601 date of when the operation should be pruned, if empty it'll be stored forever **/
    8: optional string expiryDate;
}