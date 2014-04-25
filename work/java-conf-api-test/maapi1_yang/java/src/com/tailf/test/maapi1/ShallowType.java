package com.tailf.test.maapi1;
  /**
     * Enum containing all possible node values.
     *
     */
public enum ShallowType {
        /** end marker                              */
    C_NOEXISTS(1),
        /**struct xml_tag                        */
        C_XMLTAG(2),
        /** not yet used                            */
        C_SYMBOL(3),
        /** NUL-terminated strings                  */
        C_STR(4),
        /** confd_buf_t (string ...)                */
        C_BUF(5),
        /** int8_t    (int8)                        */
        C_INT8(6),
        /** int16_t   (int16)                       */
        C_INT16 (7),
        /** int32_t   (int32)                       */
        C_INT32(8),
        /** int64_t   (int64)                       */
        C_INT64(9),
        /** u_int8_t  (uint8)                       */
        C_UINT8(10),
        /** u_int16_t (uint16)                      */
        C_UINT16(11),
        /** u_int32_t (uint32)                      */
        C_UINT32(12),
        /** u_int64_t (uint64)                      */
        C_UINT64(13),
        /** double (xs:float),xs:double)             */
        C_DOUBLE(14),
        /** struct in_addr in NBO                   */
        C_IPV4(15),
        /**  (inet:ipv4-address)                    */
        /** struct in6_addr in NBO                  */
        C_IPV6(16),
        /**  (inet:ipv6-address)                    */
        /** int       (boolean)                     */
        C_BOOL(17),
        /** struct confd_qname (xs:QName)           */
        C_QNAME(18),
        /** struct confd_datetime                   */
        C_DATETIME(19),
        /**  (yang:date-and-time)                   */
        /** struct confd_date (xs:date)             */
        C_DATE(20),
        /** struct confd_gYearMonth (xs:gYearMonth) */
        C_GYEARMONTH(21),
        /** struct confd_gYear (xs:gYear)           */
        C_GYEAR(22),
        /** struct confd_time (xs:time)             */
        C_TIME(23),
        /** struct confd_gDay (xs:gDay)             */
        C_GDAY(24),
        /** struct confd_gMonth (xs:gMonthDay)      */
        C_GMONTHDAY(25),
        /** struct confd_gMonthDay (xs:gMonth)      */
        C_GMONTH(26),
        /** struct confd_duration (xs:duration)     */
        C_DURATION(27),
        /** u_int32_t (string enumerations)         */
        C_ENUM_HASH(28),
        /** u_int32_t (bits size 32)                */
        C_BIT32(29),
        /** u_int64_t (bits size 64)                */
        C_BIT64(30),
        /** confd_list (leaf-list)                  */
        C_LIST(31),
        /** struct xml_tag), start of container      */
        C_XMLBEGIN(32),
        /** struct xml_tag), end of container        */
        C_XMLEND(33),
        /** struct confd_hkeypath*                  */
        C_OBJECTREF(34),
        /**  (instance-identifier)                  */
        /** (union) - not used in API               */
        C_UNION(35),
        /** see cdb_get_values in confd_lib_cdb(3)  */
        C_PTR(36),
        /** as C_XMLBEGIN), with CDB instance index  */
        C_CDBBEGIN(37),
        /** struct confd_snmp_oid*                  */
        C_OID(38),
        /**  (yang:object-identifier)               */
        /** confd_buf_t (binary ...)                */
        C_BINARY  (39),
        /** struct confd_ipv4_prefix                */
        C_IPV4PREFIX(40),
        /**  (inet:ipv4-prefix)                     */
        /** struct confd_ipv6_prefix                */
        C_IPV6PREFIX(41),
        /**  (inet:ipv6-prefix)                     */
        /** default value indicator                 */
        C_DEFAULT (42),
        /** struct confd_decimal64 (decimal64)      */
        C_DECIMAL64(43),
        /** struct confd_identityref (identityref)  */
        C_IDENTITYREF(44),
        /** as C_XMLBEGIN, but for a deleted list instance */
        C_XMLBEGINDEL(45);


    private int value;

    ShallowType(int value){
        this.value=value;
    }

    /**
     * @return returns the integer value of the enum.
     */
    public int getValue(){
        return value;
    }
}