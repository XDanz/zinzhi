<?xml version="1.0"?>
<confspec xmlns="http://tail-f.com/ns/confspec/1.0"
          xmlns:confd="http://tail-f.com/ns/confd/1.0"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://tail-f.com/ns/example/smp/1.0"
          mount="/">
  <!--    A set of server structures -->
  <elem name="servers">
    <elem name="server" minOccurs="0" maxOccurs="64">
      <elem name="name" type="xs:string" key="true" />
      <elem name="ip" type="confd:inetAddressIP" unique="foo"/>
      <elem name="port" type="confd:inetPortNumber" unique="foo"/>
    </elem>
  </elem>
</confspec>
