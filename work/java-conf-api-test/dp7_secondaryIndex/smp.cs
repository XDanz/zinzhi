<?xml version="1.0"?>
<confspec xmlns="http://tail-f.com/ns/confspec/1.0"
          xmlns:confd="http://tail-f.com/ns/confd/1.0"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://tail-f.com/test/smp/1.0"
          mount="/">

  <!--   A set of server structures -->

  <elem name="servers">
    <callpoint id="simplecp" type="external"/>
    <elem name="server" minOccurs="0" maxOccurs="64">
      <elem name="name" type="xs:string" key="true" />
      <secondaryIndex name="snmp" sortOrder="snmp" indexElems="name"/>
      <elem name="ip" type="confd:inetAddressIP" />
      <elem name="port" type="confd:inetPortNumber" />
      <elem name="macaddr" type="confd:hexList" />
      <elem name="snmpref" type="confd:oid" />
      <elem name="prefixmask" type="confd:octetList" />
    </elem>
  </elem>
</confspec>
