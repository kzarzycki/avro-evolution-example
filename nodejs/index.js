"use strict";
var KafkaRest = require("kafka-rest");

var api_url = "http://kafka_rest:8082";

var kafka = new KafkaRest({"url": api_url});

var consumerGroup = "test_consumer";
var topicName = "test"; 

var consumerConfig = {
  "format" : "avro",
  "auto.offset.reset" : "smallest"
};

kafka.consumer(consumerGroup).join(consumerConfig, function(err, consumer_instance) { 
  if (err) return console.log("Failed to create instance in consumer group: " + err);
  var stream = consumer_instance.subscribe(topicName);
  stream.on('data', function(msgs) {
    for(var i = 0; i < msgs.length; i++) {
      console.log(JSON.stringify(msgs[i].value));
    }
  });
  stream.on('error', function(err) {
        console.log("Consumer instance reported an error: " + err);
        console.log("Attempting to shut down consumer instance...");
        consumer_instance.shutdown(logShutdown);
  });
  stream.on('end', function() {
      console.log("Consumer stream closed.");
  });

  // Events are also emitted by the parent consumer_instance, so you can either consume individual streams separately
  // or multiple streams with one callback. Here we'll just demonstrate the 'end' event.
  consumer_instance.on('end', function() {
      console.log("Consumer instance closed.");
  });

  // Also trigger clean shutdown on Ctrl-C
  process.on('SIGINT', function() {
      console.log("Attempting to shut down consumer instance...");
      consumer_instance.shutdown(logShutdown);
  });
});

function listBrokers(done) {
    console.log("Listing brokers:");
    kafka.brokers.list(function (err, data) {
        if (err) {
            console.log("Failed trying to list brokers: " + err);
        } else {
            for (var i = 0; i < data.length; i++)
                console.log(data[i].toString() + " (raw: " + JSON.stringify(data[i].raw) + ")");
        }
        console.log();
        done(err);
    });
}

function logShutdown(err) {
    if (err)
        console.log("Error while shutting down: " + err);
    else
        console.log("Shutdown cleanly.");
}
