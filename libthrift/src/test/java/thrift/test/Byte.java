/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class Byte implements org.apache.thrift.TBase<Byte, Byte._Fields>, java.io.Serializable, Cloneable, Comparable<Byte> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Byte");

    private static final org.apache.thrift.protocol.TField VAL_FIELD_DESC = new org.apache.thrift.protocol.TField("val", org.apache.thrift.protocol.TType.BYTE, (short) 1);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ByteStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ByteTupleSchemeFactory();

    public byte val; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        VAL((short) 1, "val");

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
                case 1: // VAL
                    return VAL;
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
    private static final int __VAL_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VAL, new org.apache.thrift.meta_data.FieldMetaData("val", org.apache.thrift.TFieldRequirementType.DEFAULT,
                new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BYTE)));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Byte.class, metaDataMap);
    }

    public Byte() {
    }

    public Byte(
            byte val) {
        this();
        this.val = val;
        setValIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public Byte(Byte other) {
        __isset_bitfield = other.__isset_bitfield;
        this.val = other.val;
    }

    @Override
    public Byte deepCopy() {
        return new Byte(this);
    }

    @Override
    public void clear() {
        setValIsSet(false);
        this.val = 0;
    }

    public byte getVal() {
        return this.val;
    }

    public Byte setVal(byte val) {
        this.val = val;
        setValIsSet(true);
        return this;
    }

    public void unsetVal() {
        __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __VAL_ISSET_ID);
    }

    /** Returns true if field val is set (has been assigned a value) and false otherwise */
    public boolean isSetVal() {
        return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __VAL_ISSET_ID);
    }

    public void setValIsSet(boolean value) {
        __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __VAL_ISSET_ID, value);
    }

    @Override
    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
        switch (field) {
            case VAL:
                if (value == null) {
                    unsetVal();
                } else {
                    setVal((java.lang.Byte) value);
                }
                break;

        }
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case VAL:
                return getVal();

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
            case VAL:
                return isSetVal();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that instanceof Byte)
            return this.equals((Byte) that);
        return false;
    }

    public boolean equals(Byte that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_val = true;
        boolean that_present_val = true;
        if (this_present_val || that_present_val) {
            if (!(this_present_val && that_present_val))
                return false;
            if (this.val != that.val)
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + (int) (val);

        return hashCode;
    }

    @Override
    public int compareTo(Byte other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.compare(isSetVal(), other.isSetVal());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetVal()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.val, other.val);
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
        java.lang.StringBuilder sb = new java.lang.StringBuilder("Byte(");
        boolean first = true;

        sb.append("val:");
        sb.append(this.val);
        first = false;
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

    private static class ByteStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public ByteStandardScheme getScheme() {
            return new ByteStandardScheme();
        }
    }

    private static class ByteStandardScheme extends org.apache.thrift.scheme.StandardScheme<Byte> {

        @Override
        public void read(org.apache.thrift.protocol.TProtocol iprot, Byte struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // VAL
                        if (schemeField.type == org.apache.thrift.protocol.TType.BYTE) {
                            struct.val = iprot.readByte();
                            struct.setValIsSet(true);
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
        public void write(org.apache.thrift.protocol.TProtocol oprot, Byte struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            oprot.writeFieldBegin(VAL_FIELD_DESC);
            oprot.writeByte(struct.val);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class ByteTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public ByteTupleScheme getScheme() {
            return new ByteTupleScheme();
        }
    }

    private static class ByteTupleScheme extends org.apache.thrift.scheme.TupleScheme<Byte> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, Byte struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet optionals = new java.util.BitSet();
            if (struct.isSetVal()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetVal()) {
                oprot.writeByte(struct.val);
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, Byte struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.val = iprot.readByte();
                struct.setValIsSet(true);
            }
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
}

