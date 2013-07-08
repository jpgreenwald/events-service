events-service
==============

Event Capture/Processing stored in Cassandra with Hadoop Map/Reduce

```

CREATE KEYSPACE Demo WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

CREATE TABLE events_raw (
  event_id timeuuid PRIMARY KEY,
  data text
)

CREATE TABLE events (
  event_type text,
  event_id timeuuid,
  data text,
  PRIMARY KEY (event_type, event_id)
)

java -cp capture/target/capture-1.0-jar-with-dependencies.jar com.swsandbox.CaptureServer

java -cp capture/target/capture-1.0-jar-with-dependencies.jar com.swsandbox.WorkerServer

while [ true ]; do curl -i -X POST -d '{"event":"login", "data":{ "account":123424,"character":12224422,"server":"test_realm"}}' -H 'application/json' http://localhost:5050; sleep 1s; done;

hadoop jar hadoop-events/target/hadoop-events-1.0-job.jar

```
