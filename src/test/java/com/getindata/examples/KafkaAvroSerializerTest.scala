package com.getindata.examples

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConverters._
class KafkaAvroSerializerTest extends FunSuite with Matchers {
  test("deserialize specific record without specifying reader schema") {

    val schemaRegistryClient = new MockSchemaRegistryClient()
    val props = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "mock",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true"
    ).asJava

    val ser = new KafkaAvroSerializer(schemaRegistryClient, props)
    val deser = new KafkaAvroDeserializer(schemaRegistryClient, props)

    val v : V1 = V1("f1")
    val serialized = ser.serialize(null, v)

    val deserialized = deser.deserialize(null, serialized)

    deserialized shouldBe a [V1]
    deserialized shouldEqual v
    //deser.deserialize(null, )
  }

  test("deserialize specific record with specifying reader schema") {

    val schemaRegistryClient = new MockSchemaRegistryClient()
    val props = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "mock",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true"
    ).asJava

    val ser = new KafkaAvroSerializer(schemaRegistryClient, props)
    val deser = new KafkaAvroDeserializer(schemaRegistryClient, props)

    val v : V1 = V1("f1")
    val serialized = ser.serialize(null, v)

    val deserialized = deser.deserialize(null, serialized, V2.SCHEMA$)

    deserialized shouldBe a [V2]
    deserialized shouldEqual V2(v.f1, None, Some("hello"))
    //deser.deserialize(null, )
  }
}
