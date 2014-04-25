<?xml version="1.0"?>

<!--
    Copyright 2005,2006 Tail-f Systems AB
  -->

<confspec xmlns="http://tail-f.com/ns/confspec/1.0"
          xmlns:confd="http://tail-f.com/ns/confd/1.0"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://tail-f.com/test/mtest/1.0"
          mount="/">



  <xs:simpleType name="listType1">
    <xs:list itemType="xs:unsignedInt"/>
  </xs:simpleType>


  <xs:simpleType name="listType2">
    <xs:list itemType="xs:string"/>
  </xs:simpleType>

  <bitsType name="myFlags" size="64">
    <field bit="0" label="turboMode"/>
    <field bit="1" label="enableQOS"/>
    <field bit="2" label="enableMAA"/>
    <field bit="7" label="strongEncryption"/>
  </bitsType>

  <xs:simpleType name="enum_test">
    <xs:restriction base="xs:string">
      <xs:enumeration value="15-mins"/>
      <xs:enumeration value="30-mins"/>
    </xs:restriction>
  </xs:simpleType>

  <elem name="mtest">
    <elem name="ilist" type="listType1"/>
    <elem name="slist" type="listType2"/>
    <elem name="firstname" type="xs:string"  default="George"/>
    <elem name="a_number"  type="xs:integer" default="42"/>
    <elem name="b_number"  type="xs:integer" default="7"/>

    <elem name="indexes">
      <elem name="index" minOccurs="0" maxOccurs="20">
        <elem name="x" type="xs:unsignedShort" key="true">
          <indexedView/>
        </elem>
        <elem name="port" type="xs:integer" default="110"/>
      </elem>
    </elem>

    <elem name="movables">
      <elem name="movable" minOccurs="0" maxOccurs="64">
        <elem name="a" type="xs:unsignedShort" key="true"/>
        <elem name="b" type="xs:unsignedShort" key="true"/>
      </elem>
    </elem>

    <elem name="servers">
      <elem name="server" minOccurs="0" maxOccurs="64">
        <elem name="name" type="xs:string" key="true" />
        <elem name="ip"   minOccurs="1" type="confd:inetAddress" />
        <elem name="port" minOccurs="0" type="confd:inetPortNumber" default="80"/>
        <elem name="foo" minOccurs="1" maxOccurs="1">
          <elem name="bar" type="xs:integer" default="42"/>
          <elem name="baz" type="xs:integer" default="7"/>
        </elem>
        <elem name="interface" minOccurs="0" maxOccurs="8">
          <elem name="name" type="xs:string" key="true"/>
          <elem name="mtu"  type="xs:integer" default="1500"/>
        </elem>
      </elem>
    </elem>

    <elem name="dks">
      <elem name="dk" minOccurs="0" maxOccurs="888">
        <elem name="name"     type="xs:string"         key="true"/>
        <elem name="ip"       type="confd:inetAddress" key="true"/>
        <elem name="luckyday" type="xs:date"/>
      </elem>
    </elem>

    <elem name="forest">
      <elem name="tree" minOccurs="0" maxOccurs="1024">
        <elem name="name" type="xs:string" key="true"/>
        <elem name="type" type="xs:string"/>
      </elem>
      <elem name="flower" minOccurs="0" maxOccurs="1024">
        <elem name="name" type="xs:string" key="true"/>
        <elem name="type" type="xs:string"/>
        <elem name="color" type="xs:string"/>
      </elem>
    </elem>

    <elem name="types">
      <elem name="c_int8"     type="xs:byte"/>
      <elem name="c_int16"    type="xs:short">
        <validate id="validate_int" type="external"/>
      </elem>
      <elem name="c_int32"    type="xs:int"/>
      <elem name="c_int64"    type="xs:integer"/>
      <elem name="c_uint8"    type="xs:unsignedByte"/>
      <elem name="c_uint16"   type="xs:unsignedShort"/>
      <elem name="c_uint32"   type="xs:unsignedInt"/>
      <elem name="c_uint64"   type="xs:unsignedLong"/>
      <elem name="b"          type="xs:boolean"/>
      <elem name="f"          type="xs:float"/>
      <elem name="c_ipv4"     type="confd:inetAddressIPv4"/>
      <elem name="c_ipv6"     type="confd:inetAddressIPv6"/>
      <elem name="datetime"   type="xs:dateTime"/>
      <elem name="date"       type="xs:date"/>
      <elem name="gyearmonth" type="xs:gYearMonth"/>
      <elem name="gyear"      type="xs:gYear"/>
      <elem name="time"       type="xs:time" />
      <elem name="gday"       type="xs:gDay" />
      <elem name="gmonthday"  type="xs:gMonthDay" />
      <elem name="gmonth"     type="xs:gMonth" />
      <elem name="duration"   type="xs:duration" />
      <elem name="enum"       type="enum_test"/>
      <elem name="objectref"  type="confd:objectRef" />
      <elem name="bits"       type="myFlags"/>
    </elem>

    <elem name="ints">
      <elem name="int" minOccurs="0" maxOccurs="888">
        <elem name="i"     type="xs:integer"         key="true"/>
        <elem name="type" type="xs:string" default="foo"/>
        <elem name="color" type="xs:string" default="bar"/>
      </elem>
    </elem>
  </elem>
</confspec>
