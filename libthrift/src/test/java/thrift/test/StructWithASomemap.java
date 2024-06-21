/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class StructWithASomemap implements org.apache.thrift.TBase<StructWithASomemap, StructWithASomemap._Fields>, java.io.Serializable, Cloneable, Comparable<StructWithASomemap> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("StructWithASomemap");

    private static final org.apache.thrift.protocol.TField SOMEMAP_FIELD_FIELD_DESC = new org.apache.thrift.protocol.TField("somemap_field", org.apache.thrift.protocol.TType.MAP, (short) 1);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new StructWithASomemapStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new StructWithASomemapTupleSchemeFactory();

    public @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer, java.lang.Integer> somemap_field; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        SOMEMAP_FIELD((short) 1, "somemap_field");

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
                case 1: // SOMEMAP_FIELD
                    return SOMEMAP_FIELD;
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
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SOMEMAP_FIELD, new org.apache.thrift.meta_data.FieldMetaData("somemap_field", org.apache.thrift.TFieldRequirementType.REQUIRED,
                new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32),
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(StructWithASomemap.class, metaDataMap);
    }

    public StructWithASomemap() {
    }

    public StructWithASomemap(
            java.util.Map<java.lang.Integer, java.lang.Integer> somemap_field) {
        this();
        this.somemap_field = somemap_field;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public StructWithASomemap(StructWithASomemap other) {
        if (other.isSetSomemap_field()) {
            java.util.Map<java.lang.Integer, java.lang.Integer> __this__somemap_field = new java.util.HashMap<java.lang.Integer, java.lang.Integer>(other.somemap_field);
            this.somemap_field = __this__somemap_field;
        }
    }

    @Override
    public StructWithASomemap deepCopy() {
        return new StructWithASomemap(this);
    }

    @Override
    public void clear() {
        this.somemap_field = null;
    }

    public int getSomemap_fieldSize() {
        return (this.somemap_field == null) ? 0 : this.somemap_field.size();
    }

    public void putToSomemap_field(int key, int val) {
        if (this.somemap_field == null) {
            this.somemap_field = new java.util.HashMap<java.lang.Integer, java.lang.Integer>();
        }
        this.somemap_field.put(key, val);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Map<java.lang.Integer, java.lang.Integer> getSomemap_field() {
        return this.somemap_field;
    }

    public StructWithASomemap setSomemap_field(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer, java.lang.Integer> somemap_field) {
        this.somemap_field = somemap_field;
        return this;
    }

    public void unsetSomemap_field() {
        this.somemap_field = null;
    }

    /** Returns true if field somemap_field is set (has been assigned a value) and false otherwise */
    public boolean isSetSomemap_field() {
        return this.somemap_field != null;
    }

    public void setSomemap_fieldIsSet(boolean value) {
        if (!value) {
            this.somemap_field = null;
        }
    }

    @Override
    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
        switch (field) {
            case SOMEMAP_FIELD:
                if (value == null) {
                    unsetSomemap_field();
                } else {
                    setSomemap_field((java.util.Map<java.lang.Integer, java.lang.Integer>) value);
                }
                break;

        }
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case SOMEMAP_FIELD:
                return getSomemap_field();

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
            case SOMEMAP_FIELD:
                return isSetSomemap_field();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that instanceof StructWithASomemap)
            return this.equals((StructWithASomemap) that);
        return false;
    }

    public boolean equals(StructWithASomemap that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_somemap_field = true && this.isSetSomemap_field();
        boolean that_present_somemap_field = true && that.isSetSomemap_field();
        if (this_present_somemap_field || that_present_somemap_field) {
            if (!(this_present_somemap_field && that_present_somemap_field))
                return false;
            if (!this.somemap_field.equals(that.somemap_field))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + ((isSetSomemap_field()) ? 131071 : 524287);
        if (isSetSomemap_field())
            hashCode = hashCode * 8191 + somemap_field.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(StructWithASomemap other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.compare(isSetSomemap_field(), other.isSetSomemap_field());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetSomemap_field()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.somemap_field, other.somemap_field);
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
        java.lang.StringBuilder sb = new java.lang.StringBuilder("StructWithASomemap(");
        boolean first = true;

        sb.append("somemap_field:");
        if (this.somemap_field == null) {
            sb.append("null");
        } else {
            sb.append(this.somemap_field);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
        // check for required fields
        if (somemap_field == null) {
            throw new org.apache.thrift.protocol.TProtocolException("Required field 'somemap_field' was not present! Struct: " + toString());
        }
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
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private static class StructWithASomemapStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public StructWithASomemapStandardScheme getScheme() {
            return new StructWithASomemapStandardScheme();
        }
    }

    private static class StructWithASomemapStandardScheme extends org.apache.thrift.scheme.StandardScheme<StructWithASomemap> {

        @Override
        public void read(org.apache.thrift.protocol.TProtocol iprot, StructWithASomemap struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // SOMEMAP_FIELD
                        if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
                            {
                                org.apache.thrift.protocol.TMap _map706 = iprot.readMapBegin();
                                struct.somemap_field = new java.util.HashMap<java.lang.Integer, java.lang.Integer>(2 * _map706.size);
                                int _key707;
                                int _val708;
                                for (int _i709 = 0; _i709 < _map706.size; ++_i709) {
                                    _key707 = iprot.readI32();
                                    _val708 = iprot.readI32();
                                    struct.somemap_field.put(_key707, _val708);
                                }
                                iprot.readMapEnd();
                            }
                            struct.setSomemap_fieldIsSet(true);
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
        public void write(org.apache.thrift.protocol.TProtocol oprot, StructWithASomemap struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.somemap_field != null) {
                oprot.writeFieldBegin(SOMEMAP_FIELD_FIELD_DESC);
                {
                    oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32, struct.somemap_field.size()));
                    for (java.util.Map.Entry<java.lang.Integer, java.lang.Integer> _iter710 : struct.somemap_field.entrySet()) {
                        oprot.writeI32(_iter710.getKey());
                        oprot.writeI32(_iter710.getValue());
                    }
                    oprot.writeMapEnd();
                }
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class StructWithASomemapTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public StructWithASomemapTupleScheme getScheme() {
            return new StructWithASomemapTupleScheme();
        }
    }

    private static class StructWithASomemapTupleScheme extends org.apache.thrift.scheme.TupleScheme<StructWithASomemap> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, StructWithASomemap struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            {
                oprot.writeI32(struct.somemap_field.size());
                for (java.util.Map.Entry<java.lang.Integer, java.lang.Integer> _iter711 : struct.somemap_field.entrySet()) {
                    oprot.writeI32(_iter711.getKey());
                    oprot.writeI32(_iter711.getValue());
                }
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, StructWithASomemap struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            {
                org.apache.thrift.protocol.TMap _map712 = iprot.readMapBegin(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32);
                struct.somemap_field = new java.util.HashMap<java.lang.Integer, java.lang.Integer>(2 * _map712.size);
                int _key713;
                int _val714;
                for (int _i715 = 0; _i715 < _map712.size; ++_i715) {
                    _key713 = iprot.readI32();
                    _val714 = iprot.readI32();
                    struct.somemap_field.put(_key713, _val714);
                }
            }
            struct.setSomemap_fieldIsSet(true);
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
}

