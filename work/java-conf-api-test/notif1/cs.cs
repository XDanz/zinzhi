<?xml version="1.0"?>
<confspec xmlns="http://tail-f.com/ns/confspec/1.0"
          xmlns:confd="http://tail-f.com/ns/confd/1.0"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://tail-f.com/test/cs/1.0"
          mount="/">

  <xs:simpleType name="math_op">
    <xs:restriction base="xs:string">
      <xs:enumeration value="add"/>
      <xs:enumeration value="sub"/>
      <xs:enumeration value="mul"/>
      <xs:enumeration value="div"/>
      <xs:enumeration value="square"/>
    </xs:restriction>
  </xs:simpleType>

  <xs:simpleType name="int16list">
    <xs:list itemType="xs:short"/>
  </xs:simpleType>

  <elem name="system">
    <elem name="computer" minOccurs="0" maxOccurs="8">
      <elem name="name" type="xs:string" key="true"/>
      <action name="math">
        <actionpoint id="math_cs" type="external"/>
        <params>
          <elem name="maybe" type="xs:int" minOccurs="0"/>
          <elem name="operation" minOccurs="1" maxOccurs="3">
            <elem name="number" type="xs:int"/>
            <elem name="type" type="math_op"/>
            <elem name="operands" type="int16list"/>
          </elem>
        </params>
        <result>
          <elem name="result" minOccurs="0" maxOccurs="unbounded">
            <elem name="number" type="xs:int"/>
            <elem name="type" type="math_op"/>
            <elem name="value" type="xs:short"/>
          </elem>
        </result>
      </action>
    </elem>
  </elem>
</confspec>
