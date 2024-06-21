/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class OptionalBinary implements org.apache.thrift.TBase<OptionalBinary, OptionalBinary._Fields>, java.io.Serializable, Cloneable, Comparable<OptionalBinary> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("OptionalBinary");

    private static final org.apache.thrift.protocol.TField BIN_SET_FIELD_DESC = new org.apache.thrift.protocol.TField("bin_set", org.apache.thrift.protocol.TType.SET, (short) 1);
    private static final org.apache.thrift.protocol.TField BIN_MAP_FIELD_DESC = new org.apache.thrift.protocol.TField("bin_map", org.apache.thrift.protocol.TType.MAP, (short) 2);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new OptionalBinaryStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new OptionalBinaryTupleSchemeFactory();

    public @org.apache.thrift.annotation.Nullable java.util.Set<java.nio.ByteBuffer> bin_set; // optional
    public @org.apache.thrift.annotation.Nullable java.util.Map<java.nio.ByteBuffer, java.lang.Integer> bin_map; // optional

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        BIN_SET((short) 1, "bin_set"),
        BIN_MAP((short) 2, "bin_map");

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
                case 1: // BIN_SET
                    return BIN_SET;
                case 2: // BIN_MAP
                    return BIN_MAP;
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
    private static final _Fields optionals[] = {_Fields.BIN_SET, _Fields.BIN_MAP};
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.BIN_SET, new org.apache.thrift.meta_data.FieldMetaData("bin_set", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING, true))));
        tmpMap.put(_Fields.BIN_MAP, new org.apache.thrift.meta_data.FieldMetaData("bin_map", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING, true),
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(OptionalBinary.class, metaDataMap);
    }

    public OptionalBinary() {
        this.bin_set = new java.util.HashSet<java.nio.ByteBuffer>();

        this.bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>();

    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public OptionalBinary(OptionalBinary other) {
        if (other.isSetBin_set()) {
            java.util.Set<java.nio.ByteBuffer> __this__bin_set = new java.util.HashSet<java.nio.ByteBuffer>(other.bin_set);
            this.bin_set = __this__bin_set;
        }
        if (other.isSetBin_map()) {
            java.util.Map<java.nio.ByteBuffer, java.lang.Integer> __this__bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>(other.bin_map);
            this.bin_map = __this__bin_map;
        }
    }

    @Override
    public OptionalBinary deepCopy() {
        return new OptionalBinary(this);
    }

    @Override
    public void clear() {
        this.bin_set = new java.util.HashSet<java.nio.ByteBuffer>();

        this.bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>();

    }

    public int getBin_setSize() {
        return (this.bin_set == null) ? 0 : this.bin_set.size();
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Iterator<java.nio.ByteBuffer> getBin_setIterator() {
        return (this.bin_set == null) ? null : this.bin_set.iterator();
    }

    public void addToBin_set(java.nio.ByteBuffer elem) {
        if (this.bin_set == null) {
            this.bin_set = new java.util.HashSet<java.nio.ByteBuffer>();
        }
        this.bin_set.add(elem);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Set<java.nio.ByteBuffer> getBin_set() {
        return this.bin_set;
    }

    public OptionalBinary setBin_set(@org.apache.thrift.annotation.Nullable java.util.Set<java.nio.ByteBuffer> bin_set) {
        this.bin_set = bin_set;
        return this;
    }

    public void unsetBin_set() {
        this.bin_set = null;
    }

    /** Returns true if field bin_set is set (has been assigned a value) and false otherwise */
    public boolean isSetBin_set() {
        return this.bin_set != null;
    }

    public void setBin_setIsSet(boolean value) {
        if (!value) {
            this.bin_set = null;
        }
    }

    public int getBin_mapSize() {
        return (this.bin_map == null) ? 0 : this.bin_map.size();
    }

    public void putToBin_map(java.nio.ByteBuffer key, int val) {
        if (this.bin_map == null) {
            this.bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>();
        }
        this.bin_map.put(key, val);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Map<java.nio.ByteBuffer, java.lang.Integer> getBin_map() {
        return this.bin_map;
    }

    public OptionalBinary setBin_map(@org.apache.thrift.annotation.Nullable java.util.Map<java.nio.ByteBuffer, java.lang.Integer> bin_map) {
        this.bin_map = bin_map;
        return this;
    }

    public void unsetBin_map() {
        this.bin_map = null;
    }

    /** Returns true if field bin_map is set (has been assigned a value) and false otherwise */
    public boolean isSetBin_map() {
        return this.bin_map != null;
    }

    public void setBin_mapIsSet(boolean value) {
        if (!value) {
            this.bin_map = null;
        }
    }

    @Override
    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
        switch (field) {
            case BIN_SET:
                if (value == null) {
                    unsetBin_set();
                } else {
                    setBin_set((java.util.Set<java.nio.ByteBuffer>) value);
                }
                break;

            case BIN_MAP:
                if (value == null) {
                    unsetBin_map();
                } else {
                    setBin_map((java.util.Map<java.nio.ByteBuffer, java.lang.Integer>) value);
                }
                break;

        }
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case BIN_SET:
                return getBin_set();

            case BIN_MAP:
                return getBin_map();

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
            case BIN_SET:
                return isSetBin_set();
            case BIN_MAP:
                return isSetBin_map();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that instanceof OptionalBinary)
            return this.equals((OptionalBinary) that);
        return false;
    }

    public boolean equals(OptionalBinary that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_bin_set = true && this.isSetBin_set();
        boolean that_present_bin_set = true && that.isSetBin_set();
        if (this_present_bin_set || that_present_bin_set) {
            if (!(this_present_bin_set && that_present_bin_set))
                return false;
            if (!this.bin_set.equals(that.bin_set))
                return false;
        }

        boolean this_present_bin_map = true && this.isSetBin_map();
        boolean that_present_bin_map = true && that.isSetBin_map();
        if (this_present_bin_map || that_present_bin_map) {
            if (!(this_present_bin_map && that_present_bin_map))
                return false;
            if (!this.bin_map.equals(that.bin_map))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + ((isSetBin_set()) ? 131071 : 524287);
        if (isSetBin_set())
            hashCode = hashCode * 8191 + bin_set.hashCode();

        hashCode = hashCode * 8191 + ((isSetBin_map()) ? 131071 : 524287);
        if (isSetBin_map())
            hashCode = hashCode * 8191 + bin_map.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(OptionalBinary other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.compare(isSetBin_set(), other.isSetBin_set());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetBin_set()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.bin_set, other.bin_set);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.compare(isSetBin_map(), other.isSetBin_map());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetBin_map()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.bin_map, other.bin_map);
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
        java.lang.StringBuilder sb = new java.lang.StringBuilder("OptionalBinary(");
        boolean first = true;

        if (isSetBin_set()) {
            sb.append("bin_set:");
            if (this.bin_set == null) {
                sb.append("null");
            } else {
                org.apache.thrift.TBaseHelper.toString(this.bin_set, sb);
            }
            first = false;
        }
        if (isSetBin_map()) {
            if (!first) sb.append(", ");
            sb.append("bin_map:");
            if (this.bin_map == null) {
                sb.append("null");
            } else {
                sb.append(this.bin_map);
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
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private static class OptionalBinaryStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public OptionalBinaryStandardScheme getScheme() {
            return new OptionalBinaryStandardScheme();
        }
    }

    private static class OptionalBinaryStandardScheme extends org.apache.thrift.scheme.StandardScheme<OptionalBinary> {

        @Override
        public void read(org.apache.thrift.protocol.TProtocol iprot, OptionalBinary struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // BIN_SET
                        if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
                            {
                                org.apache.thrift.protocol.TSet _set306 = iprot.readSetBegin();
                                struct.bin_set = new java.util.HashSet<java.nio.ByteBuffer>(2 * _set306.size);
                                @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _elem307;
                                for (int _i308 = 0; _i308 < _set306.size; ++_i308) {
                                    _elem307 = iprot.readBinary();
                                    struct.bin_set.add(_elem307);
                                }
                                iprot.readSetEnd();
                            }
                            struct.setBin_setIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 2: // BIN_MAP
                        if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
                            {
                                org.apache.thrift.protocol.TMap _map309 = iprot.readMapBegin();
                                struct.bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>(2 * _map309.size);
                                @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _key310;
                                int _val311;
                                for (int _i312 = 0; _i312 < _map309.size; ++_i312) {
                                    _key310 = iprot.readBinary();
                                    _val311 = iprot.readI32();
                                    struct.bin_map.put(_key310, _val311);
                                }
                                iprot.readMapEnd();
                            }
                            struct.setBin_mapIsSet(true);
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
        public void write(org.apache.thrift.protocol.TProtocol oprot, OptionalBinary struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.bin_set != null) {
                if (struct.isSetBin_set()) {
                    oprot.writeFieldBegin(BIN_SET_FIELD_DESC);
                    {
                        oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRING, struct.bin_set.size()));
                        for (java.nio.ByteBuffer _iter313 : struct.bin_set) {
                            oprot.writeBinary(_iter313);
                        }
                        oprot.writeSetEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            if (struct.bin_map != null) {
                if (struct.isSetBin_map()) {
                    oprot.writeFieldBegin(BIN_MAP_FIELD_DESC);
                    {
                        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32, struct.bin_map.size()));
                        for (java.util.Map.Entry<java.nio.ByteBuffer, java.lang.Integer> _iter314 : struct.bin_map.entrySet()) {
                            oprot.writeBinary(_iter314.getKey());
                            oprot.writeI32(_iter314.getValue());
                        }
                        oprot.writeMapEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class OptionalBinaryTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public OptionalBinaryTupleScheme getScheme() {
            return new OptionalBinaryTupleScheme();
        }
    }

    private static class OptionalBinaryTupleScheme extends org.apache.thrift.scheme.TupleScheme<OptionalBinary> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, OptionalBinary struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet optionals = new java.util.BitSet();
            if (struct.isSetBin_set()) {
                optionals.set(0);
            }
            if (struct.isSetBin_map()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetBin_set()) {
                {
                    oprot.writeI32(struct.bin_set.size());
                    for (java.nio.ByteBuffer _iter315 : struct.bin_set) {
                        oprot.writeBinary(_iter315);
                    }
                }
            }
            if (struct.isSetBin_map()) {
                {
                    oprot.writeI32(struct.bin_map.size());
                    for (java.util.Map.Entry<java.nio.ByteBuffer, java.lang.Integer> _iter316 : struct.bin_map.entrySet()) {
                        oprot.writeBinary(_iter316.getKey());
                        oprot.writeI32(_iter316.getValue());
                    }
                }
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, OptionalBinary struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                {
                    org.apache.thrift.protocol.TSet _set317 = iprot.readSetBegin(org.apache.thrift.protocol.TType.STRING);
                    struct.bin_set = new java.util.HashSet<java.nio.ByteBuffer>(2 * _set317.size);
                    @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _elem318;
                    for (int _i319 = 0; _i319 < _set317.size; ++_i319) {
                        _elem318 = iprot.readBinary();
                        struct.bin_set.add(_elem318);
                    }
                }
                struct.setBin_setIsSet(true);
            }
            if (incoming.get(1)) {
                {
                    org.apache.thrift.protocol.TMap _map320 = iprot.readMapBegin(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32);
                    struct.bin_map = new java.util.HashMap<java.nio.ByteBuffer, java.lang.Integer>(2 * _map320.size);
                    @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer _key321;
                    int _val322;
                    for (int _i323 = 0; _i323 < _map320.size; ++_i323) {
                        _key321 = iprot.readBinary();
                        _val322 = iprot.readI32();
                        struct.bin_map.put(_key321, _val322);
                    }
                }
                struct.setBin_mapIsSet(true);
            }
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
}

