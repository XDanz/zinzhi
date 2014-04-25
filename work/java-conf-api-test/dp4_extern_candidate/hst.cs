<?xml version="1.0"?>
<confspec xmlns="http://tail-f.com/ns/confspec/1.0"
          xmlns:confd="http://tail-f.com/ns/confd/1.0"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://tail-f.com/test/hst/1.0"
          mount="/">
  <!--   A set of host structures -->

  <elem name="hosts">
    <callpoint id="hcp" type="external"/>
    <elem name="host"  minOccurs="0" maxOccurs="64">
      <elem name="name" type="xs:string" key="true" />
      <elem name="domain"  type="xs:string" />
      <elem name="defgw" type="confd:inetAddressIP" />

      <!--       Write a validation rule to check that the defgw  -->
      <!--       resides on exactly one of the networks defined below -->

      <elem name="interfaces">
        <callpoint id="icp" type="external"/>
        <elem name="interface" minOccurs="0" maxOccurs="64">
          <elem name="name" type="xs:string" key="true" />
          <elem name="ip" type="confd:inetAddressIP" />
          <elem name="mask" type="confd:inetAddressIP" />
          <elem name="enabled" type="xs:boolean"/>
        </elem>
      </elem>
    </elem>
  </elem>
</confspec>

