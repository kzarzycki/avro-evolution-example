package com.getindata.examples

import java.io.EOFException

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.avro.{AvroMapper, AvroSchema}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.avro.Schema
import org.scalatest.{FunSuite, Matchers}


class AvroEvolutionJacksonTest extends FunSuite with Matchers {
  val mapper = {
    val avroMapper = new AvroMapper()
    avroMapper.registerModule(DefaultScalaModule)
    avroMapper
  }

  test("test avro evolution: upgraded case class, old schema") {
    val schemaV1 = new AvroSchema(V1.SCHEMA$)
    val v1: V1 = V1("hello")
    val bytes = mapper.writer().`with`(schemaV1).writeValueAsBytes(v1)

    val schemaV2 = new AvroSchema(V2.SCHEMA$)

    // reader of upgrade case class, but with old schema : OK!
    val readerV2 = mapper.reader(schemaV2).`with`(schemaV1)

    val v2 : V2 = readerV2.readValue(bytes)

    // this is NOT OK! V2 has default f3 = "Hello" in Schema. Jackson has a bug, that you can't specify reader schema.
    v2 shouldBe  V2(f1 = v1.f1, f2 = None, f3 = None)

    // this should be OK, but it's not
    // v2 shouldBe V2(f1 = v1.f1, f2 = None, f3 = Some("Hello"))
  }

  test("test avro evolution: upgraded case class, new schema") {

    val schemaV1 = new AvroSchema(V1.SCHEMA$)
    val v1: V1 = V1("hello")
    val bytes = mapper.writer().`with`(schemaV1).writeValueAsBytes(v1)

    val schemaV2 = new AvroSchema(V2.SCHEMA$)

    // reader of upgrade case class and with new schema : will not work!
    val readerV2 = mapper.readerFor(classOf[V2]).`with`(schemaV2)

    an[EOFException] should be thrownBy {
      val v2 : V2 = readerV2.readValue(bytes)
    }

  }
}
