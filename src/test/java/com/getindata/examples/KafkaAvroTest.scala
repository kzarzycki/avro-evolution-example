package com.getindata.examples

import java.io.Serializable
import java.util
import java.util.Properties

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException
import io.confluent.kafka.serializers
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer, KafkaAvroSerializerConfig}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.scalatest.{FunSuite, Matchers}

class KafkaAvroTest extends FunSuite with Matchers {

  val keyClass = classOf[StringSerializer]
  val valueClass = classOf[KafkaAvroSerializer]
  val topic: String = "test"

  val config = Map (
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "broker:9092",
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> keyClass.getName,
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> valueClass.getName,
    AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "http://schema_registry:8081"
  ).foldLeft(new Properties()) { case (acc, (k, v)) => acc.setProperty(k, v); acc }



  test("can produce avro message V1 to Kafka") {
    val producer = new KafkaProducer[String, V1](config)
    producer.send(new ProducerRecord(topic, V1("f1"))).get()
    producer.close()
  }

  test("can produce avro messages V2 (compatible) to Kafka") {
    val producer = new KafkaProducer[String, V2](config)
    producer.send(new ProducerRecord(topic, V2("f1"))).get()
    producer.close()
  }

  test("cannot produce avro messages IncompatibleV3 to Kafka") {
    val producer = new KafkaProducer[String, IncombatibleV3](config)

    val exception = thrownBy(producer.send(new ProducerRecord(topic, IncombatibleV3("f1"))).get())

    exception.
  }
}
