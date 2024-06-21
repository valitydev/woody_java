/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class OptionalDefault implements org.apache.thrift.TBase<OptionalDefault, OptionalDefault._Fields>, java.io.Serializable, Cloneable, Comparable<OptionalDefault> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OptionalDefault");

    private static final org.apache.thrift.protocol.TField OPT_INT_FIELD_DESC = new org.apache.thrift.protocol.TField("opt_int", org.apache.thrift.protocol.TType.I16, (short) 1);
    private static final org.apache.thrift.protocol.TField OPT_STR_FIELD_DESC = new org.apache.thrift.protocol.TField("opt_str", org.apache.thrift.protocol.TType.STRING, (short) 2);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new OptionalDefaultStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new OptionalDefaultTupleSchemeFactory();

    public short opt_int; // optional
    public @org.apache.thrift.annotation.Nullable java.lang.String opt_str; // optional

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        OPT_INT((short) 1, "opt_int"),
        OPT_STR((short) 2, "opt_str");

        private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

        static {
            for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not found.
         */
        @org.apache.thrift.annotation.Nullable
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
                case 1: // OPT_INT
                    return OPT_INT;
                case 2: // OPT_STR
                    return OPT_STR;
                default:
                    return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not found.
         */
        @org.apache.thrift.annotation.Nullable
        public static _Fields findByName(java.lang.String name) {
            return byName.get(name);
        }

        private final short _thriftId;
        private final java.lang.String _fieldName;

        _Fields(short thriftId, java.lang.String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        @Override
        public short getThriftFieldId() {
            return _thriftId;
        }

        @Override
        public java.lang.String getFieldName() {
            return _fieldName;
        }
    }

    // isset id assignments
    private static final int __OPT_INT_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    private static final _Fields optionals[] = {_Fields.OPT_INT, _Fields.OPT_STR};
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OPT_INT, new org.apache.thrift.meta_data.FieldMetaData("opt_int", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
        tmpMap.put(_Fields.OPT_STR, new org.apache.thrift.meta_data.FieldMetaData("opt_str", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OptionalDefault.class, metaDataMap);
    }

    public OptionalDefault() {
        this.opt_int = (short) 1234;

        this.opt_str = "default";

    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public OptionalDefault(OptionalDefault other) {
        __isset_bitfield = other.__isset_bitfield;
        this.opt_int = other.opt_int;
        if (other.isSetOpt_str()) {
            this.opt_str = other.opt_str;
        }
    }

    @Override
    public OptionalDefault deepCopy() {
        return new OptionalDefault(this);
    }

    @Override
    public void clear() {
        this.opt_int = (short) 1234;

        this.opt_str = "default";

    }

    public short getOpt_int() {
        return this.opt_int;
    }

    public OptionalDefault setOpt_int(short opt_int) {
        this.opt_int = opt_int;
        setOpt_intIsSet(true);
        return this;
    }

    public void unsetOpt_int() {
        __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __OPT_INT_ISSET_ID);
    }

    /** Returns true if field opt_int is set (has been assigned a value) and false otherwise */
    public boolean isSetOpt_int() {
        return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __OPT_INT_ISSET_ID);
    }

    public void setOpt_intIsSet(boolean value) {
        __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __OPT_INT_ISSET_ID, value);
    }

    @org.apache.thrift.annotation.Nullable
    public java.lang.String getOpt_str() {
        return this.opt_str;
    }

    public OptionalDefault setOpt_str(@org.apache.thrift.annotation.Nullable java.lang.String opt_str) {
        this.opt_str = opt_str;
        return this;
    }

    public void unsetOpt_str() {
        this.opt_str = null;
    }

    /** Returns true if field opt_str is set (has been assigned a value) and false otherwise */
    public boolean isSetOpt_str() {
        return this.opt_str != null;
    }

    public void setOpt_strIsSet(boolean value) {
        if (!value) {
            this.opt_str = null;
        }
    }

    @Override
    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
        switch (field) {
            case OPT_INT:
                if (value == null) {
                    unsetOpt_int();
                } else {
                    setOpt_int((java.lang.Short) value);
                }
                break;

            case OPT_STR:
                if (value == null) {
                    unsetOpt_str();
                } else {
                    setOpt_str((java.lang.String) value);
                }
                break;

        }
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case OPT_INT:
                return getOpt_int();

            case OPT_STR:
                return getOpt_str();

        }
        throw new java.lang.IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    @Override
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new java.lang.IllegalArgumentException();
        }

        switch (field) {
            case OPT_INT:
                return isSetOpt_int();
            case OPT_STR:
                return isSetOpt_str();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that instanceof OptionalDefault)
            return this.equals((OptionalDefault) that);
        return false;
    }

    public boolean equals(OptionalDefault that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_opt_int = true && this.isSetOpt_int();
        boolean that_present_opt_int = true && that.isSetOpt_int();
        if (this_present_opt_int || that_present_opt_int) {
            if (!(this_present_opt_int && that_present_opt_int))
                return false;
            if (this.opt_int != that.opt_int)
                return false;
        }

        boolean this_present_opt_str = true && this.isSetOpt_str();
        boolean that_present_opt_str = true && that.isSetOpt_str();
        if (this_present_opt_str || that_present_opt_str) {
            if (!(this_present_opt_str && that_present_opt_str))
                return false;
            if (!this.opt_str.equals(that.opt_str))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + ((isSetOpt_int()) ? 131071 : 524287);
        if (isSetOpt_int())
            hashCode = hashCode * 8191 + opt_int;

        hashCode = hashCode * 8191 + ((isSetOpt_str()) ? 131071 : 524287);
        if (isSetOpt_str())
            hashCode = hashCode * 8191 + opt_str.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(OptionalDefault other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.compare(isSetOpt_int(), other.isSetOpt_int());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetOpt_int()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.opt_int, other.opt_int);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.compare(isSetOpt_str(), other.isSetOpt_str());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetOpt_str()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.opt_str, other.opt_str);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public _Fields[] getFields() {
        return _Fields.values();
    }

    public java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> getFieldMetaData() {
        return metaDataMap;
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
        scheme(iprot).read(iprot, this);
    }

    @Override
    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
        scheme(oprot).write(oprot, this);
    }

    @Override
    public java.lang.String toString() {
        java.lang.StringBuilder sb = new java.lang.StringBuilder("OptionalDefault(");
        boolean first = true;

        if (isSetOpt_int()) {
            sb.append("opt_int:");
            sb.append(this.opt_int);
            first = false;
        }
        if (isSetOpt_str()) {
            if (!first) sb.append(", ");
            sb.append("opt_str:");
            if (this.opt_str == null) {
                sb.append("null");
            } else {
                sb.append(this.opt_str);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
        // check for required fields
        // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        try {
            write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
        try {
            // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
            __isset_bitfield = 0;
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private static class OptionalDefaultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public OptionalDefaultStandardScheme getScheme() {
            return new OptionalDefaultStandardScheme();
        }
    }

    private static class OptionalDefaultStandardScheme extends org.apache.thrift.scheme.StandardScheme<OptionalDefault> {

        @Override
        public void read(org.apache.thrift.protocol.TProtocol iprot, OptionalDefault struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // OPT_INT
                        if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
                            struct.opt_int = iprot.readI16();
                            struct.setOpt_intIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 2: // OPT_STR
                        if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                            struct.opt_str = iprot.readString();
                            struct.setOpt_strIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    default:
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();

            // check for required fields of primitive type, which can't be checked in the validate method
            struct.validate();
        }

        @Override
        public void write(org.apache.thrift.protocol.TProtocol oprot, OptionalDefault struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.isSetOpt_int()) {
                oprot.writeFieldBegin(OPT_INT_FIELD_DESC);
                oprot.writeI16(struct.opt_int);
                oprot.writeFieldEnd();
            }
            if (struct.opt_str != null) {
                if (struct.isSetOpt_str()) {
                    oprot.writeFieldBegin(OPT_STR_FIELD_DESC);
                    oprot.writeString(struct.opt_str);
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class OptionalDefaultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public OptionalDefaultTupleScheme getScheme() {
            return new OptionalDefaultTupleScheme();
        }
    }

    private static class OptionalDefaultTupleScheme extends org.apache.thrift.scheme.TupleScheme<OptionalDefault> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, OptionalDefault struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet optionals = new java.util.BitSet();
            if (struct.isSetOpt_int()) {
                optionals.set(0);
            }
            if (struct.isSetOpt_str()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetOpt_int()) {
                oprot.writeI16(struct.opt_int);
            }
            if (struct.isSetOpt_str()) {
                oprot.writeString(struct.opt_str);
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, OptionalDefault struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.opt_int = iprot.readI16();
                struct.setOpt_intIsSet(true);
            }
            if (incoming.get(1)) {
                struct.opt_str = iprot.readString();
                struct.setOpt_strIsSet(true);
            }
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
}

