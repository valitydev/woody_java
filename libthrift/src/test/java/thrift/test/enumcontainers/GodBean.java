/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test.enumcontainers;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class GodBean implements org.apache.thrift.TBase<GodBean, GodBean._Fields>, java.io.Serializable, Cloneable, Comparable<GodBean> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GodBean");

    private static final org.apache.thrift.protocol.TField POWER_FIELD_DESC = new org.apache.thrift.protocol.TField("power", org.apache.thrift.protocol.TType.MAP, (short) 1);
    private static final org.apache.thrift.protocol.TField GODDESS_FIELD_DESC = new org.apache.thrift.protocol.TField("goddess", org.apache.thrift.protocol.TType.SET, (short) 2);
    private static final org.apache.thrift.protocol.TField BY_ALIAS_FIELD_DESC = new org.apache.thrift.protocol.TField("byAlias", org.apache.thrift.protocol.TType.MAP, (short) 3);
    private static final org.apache.thrift.protocol.TField IMAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("images", org.apache.thrift.protocol.TType.SET, (short) 4);

    private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new GodBeanStandardSchemeFactory();
    private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new GodBeanTupleSchemeFactory();

    public @org.apache.thrift.annotation.Nullable java.util.Map<GreekGodGoddess, java.lang.Integer> power; // optional
    public @org.apache.thrift.annotation.Nullable java.util.Set<GreekGodGoddess> goddess; // optional
    public @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String, GreekGodGoddess> byAlias; // optional
    public @org.apache.thrift.annotation.Nullable java.util.Set<java.lang.String> images; // optional

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        POWER((short) 1, "power"),
        GODDESS((short) 2, "goddess"),
        BY_ALIAS((short) 3, "byAlias"),
        IMAGES((short) 4, "images");

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
                case 1: // POWER
                    return POWER;
                case 2: // GODDESS
                    return GODDESS;
                case 3: // BY_ALIAS
                    return BY_ALIAS;
                case 4: // IMAGES
                    return IMAGES;
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
    private static final _Fields optionals[] = {_Fields.POWER, _Fields.GODDESS, _Fields.BY_ALIAS, _Fields.IMAGES};
    public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.POWER, new org.apache.thrift.meta_data.FieldMetaData("power", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
                        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, GreekGodGoddess.class),
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
        tmpMap.put(_Fields.GODDESS, new org.apache.thrift.meta_data.FieldMetaData("goddess", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET,
                        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, GreekGodGoddess.class))));
        tmpMap.put(_Fields.BY_ALIAS, new org.apache.thrift.meta_data.FieldMetaData("byAlias", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING),
                        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, GreekGodGoddess.class))));
        tmpMap.put(_Fields.IMAGES, new org.apache.thrift.meta_data.FieldMetaData("images", org.apache.thrift.TFieldRequirementType.OPTIONAL,
                new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET,
                        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
        metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GodBean.class, metaDataMap);
    }

    public GodBean() {
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public GodBean(GodBean other) {
        if (other.isSetPower()) {
            java.util.Map<GreekGodGoddess, java.lang.Integer> __this__power = new java.util.EnumMap<GreekGodGoddess, java.lang.Integer>(GreekGodGoddess.class);
            for (java.util.Map.Entry<GreekGodGoddess, java.lang.Integer> other_element : other.power.entrySet()) {

                GreekGodGoddess other_element_key = other_element.getKey();
                java.lang.Integer other_element_value = other_element.getValue();

                GreekGodGoddess __this__power_copy_key = other_element_key;

                java.lang.Integer __this__power_copy_value = other_element_value;

                __this__power.put(__this__power_copy_key, __this__power_copy_value);
            }
            this.power = __this__power;
        }
        if (other.isSetGoddess()) {
            java.util.Set<GreekGodGoddess> __this__goddess = java.util.EnumSet.noneOf(GreekGodGoddess.class);
            for (GreekGodGoddess other_element : other.goddess) {
                __this__goddess.add(other_element);
            }
            this.goddess = __this__goddess;
        }
        if (other.isSetByAlias()) {
            java.util.Map<java.lang.String, GreekGodGoddess> __this__byAlias = new java.util.HashMap<java.lang.String, GreekGodGoddess>(other.byAlias.size());
            for (java.util.Map.Entry<java.lang.String, GreekGodGoddess> other_element : other.byAlias.entrySet()) {

                java.lang.String other_element_key = other_element.getKey();
                GreekGodGoddess other_element_value = other_element.getValue();

                java.lang.String __this__byAlias_copy_key = other_element_key;

                GreekGodGoddess __this__byAlias_copy_value = other_element_value;

                __this__byAlias.put(__this__byAlias_copy_key, __this__byAlias_copy_value);
            }
            this.byAlias = __this__byAlias;
        }
        if (other.isSetImages()) {
            java.util.Set<java.lang.String> __this__images = new java.util.HashSet<java.lang.String>(other.images);
            this.images = __this__images;
        }
    }

    @Override
    public GodBean deepCopy() {
        return new GodBean(this);
    }

    @Override
    public void clear() {
        this.power = null;
        this.goddess = null;
        this.byAlias = null;
        this.images = null;
    }

    public int getPowerSize() {
        return (this.power == null) ? 0 : this.power.size();
    }

    public void putToPower(GreekGodGoddess key, int val) {
        if (this.power == null) {
            this.power = new java.util.EnumMap<GreekGodGoddess, java.lang.Integer>(GreekGodGoddess.class);
        }
        this.power.put(key, val);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Map<GreekGodGoddess, java.lang.Integer> getPower() {
        return this.power;
    }

    public GodBean setPower(@org.apache.thrift.annotation.Nullable java.util.Map<GreekGodGoddess, java.lang.Integer> power) {
        this.power = power;
        return this;
    }

    public void unsetPower() {
        this.power = null;
    }

    /** Returns true if field power is set (has been assigned a value) and false otherwise */
    public boolean isSetPower() {
        return this.power != null;
    }

    public void setPowerIsSet(boolean value) {
        if (!value) {
            this.power = null;
        }
    }

    public int getGoddessSize() {
        return (this.goddess == null) ? 0 : this.goddess.size();
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Iterator<GreekGodGoddess> getGoddessIterator() {
        return (this.goddess == null) ? null : this.goddess.iterator();
    }

    public void addToGoddess(GreekGodGoddess elem) {
        if (this.goddess == null) {
            this.goddess = java.util.EnumSet.noneOf(GreekGodGoddess.class);
        }
        this.goddess.add(elem);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Set<GreekGodGoddess> getGoddess() {
        return this.goddess;
    }

    public GodBean setGoddess(@org.apache.thrift.annotation.Nullable java.util.Set<GreekGodGoddess> goddess) {
        this.goddess = goddess;
        return this;
    }

    public void unsetGoddess() {
        this.goddess = null;
    }

    /** Returns true if field goddess is set (has been assigned a value) and false otherwise */
    public boolean isSetGoddess() {
        return this.goddess != null;
    }

    public void setGoddessIsSet(boolean value) {
        if (!value) {
            this.goddess = null;
        }
    }

    public int getByAliasSize() {
        return (this.byAlias == null) ? 0 : this.byAlias.size();
    }

    public void putToByAlias(java.lang.String key, GreekGodGoddess val) {
        if (this.byAlias == null) {
            this.byAlias = new java.util.HashMap<java.lang.String, GreekGodGoddess>();
        }
        this.byAlias.put(key, val);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Map<java.lang.String, GreekGodGoddess> getByAlias() {
        return this.byAlias;
    }

    public GodBean setByAlias(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.String, GreekGodGoddess> byAlias) {
        this.byAlias = byAlias;
        return this;
    }

    public void unsetByAlias() {
        this.byAlias = null;
    }

    /** Returns true if field byAlias is set (has been assigned a value) and false otherwise */
    public boolean isSetByAlias() {
        return this.byAlias != null;
    }

    public void setByAliasIsSet(boolean value) {
        if (!value) {
            this.byAlias = null;
        }
    }

    public int getImagesSize() {
        return (this.images == null) ? 0 : this.images.size();
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Iterator<java.lang.String> getImagesIterator() {
        return (this.images == null) ? null : this.images.iterator();
    }

    public void addToImages(java.lang.String elem) {
        if (this.images == null) {
            this.images = new java.util.HashSet<java.lang.String>();
        }
        this.images.add(elem);
    }

    @org.apache.thrift.annotation.Nullable
    public java.util.Set<java.lang.String> getImages() {
        return this.images;
    }

    public GodBean setImages(@org.apache.thrift.annotation.Nullable java.util.Set<java.lang.String> images) {
        this.images = images;
        return this;
    }

    public void unsetImages() {
        this.images = null;
    }

    /** Returns true if field images is set (has been assigned a value) and false otherwise */
    public boolean isSetImages() {
        return this.images != null;
    }

    public void setImagesIsSet(boolean value) {
        if (!value) {
            this.images = null;
        }
    }

    @Override
    public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
        switch (field) {
            case POWER:
                if (value == null) {
                    unsetPower();
                } else {
                    setPower((java.util.Map<GreekGodGoddess, java.lang.Integer>) value);
                }
                break;

            case GODDESS:
                if (value == null) {
                    unsetGoddess();
                } else {
                    setGoddess((java.util.Set<GreekGodGoddess>) value);
                }
                break;

            case BY_ALIAS:
                if (value == null) {
                    unsetByAlias();
                } else {
                    setByAlias((java.util.Map<java.lang.String, GreekGodGoddess>) value);
                }
                break;

            case IMAGES:
                if (value == null) {
                    unsetImages();
                } else {
                    setImages((java.util.Set<java.lang.String>) value);
                }
                break;

        }
    }

    @org.apache.thrift.annotation.Nullable
    @Override
    public java.lang.Object getFieldValue(_Fields field) {
        switch (field) {
            case POWER:
                return getPower();

            case GODDESS:
                return getGoddess();

            case BY_ALIAS:
                return getByAlias();

            case IMAGES:
                return getImages();

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
            case POWER:
                return isSetPower();
            case GODDESS:
                return isSetGoddess();
            case BY_ALIAS:
                return isSetByAlias();
            case IMAGES:
                return isSetImages();
        }
        throw new java.lang.IllegalStateException();
    }

    @Override
    public boolean equals(java.lang.Object that) {
        if (that instanceof GodBean)
            return this.equals((GodBean) that);
        return false;
    }

    public boolean equals(GodBean that) {
        if (that == null)
            return false;
        if (this == that)
            return true;

        boolean this_present_power = true && this.isSetPower();
        boolean that_present_power = true && that.isSetPower();
        if (this_present_power || that_present_power) {
            if (!(this_present_power && that_present_power))
                return false;
            if (!this.power.equals(that.power))
                return false;
        }

        boolean this_present_goddess = true && this.isSetGoddess();
        boolean that_present_goddess = true && that.isSetGoddess();
        if (this_present_goddess || that_present_goddess) {
            if (!(this_present_goddess && that_present_goddess))
                return false;
            if (!this.goddess.equals(that.goddess))
                return false;
        }

        boolean this_present_byAlias = true && this.isSetByAlias();
        boolean that_present_byAlias = true && that.isSetByAlias();
        if (this_present_byAlias || that_present_byAlias) {
            if (!(this_present_byAlias && that_present_byAlias))
                return false;
            if (!this.byAlias.equals(that.byAlias))
                return false;
        }

        boolean this_present_images = true && this.isSetImages();
        boolean that_present_images = true && that.isSetImages();
        if (this_present_images || that_present_images) {
            if (!(this_present_images && that_present_images))
                return false;
            if (!this.images.equals(that.images))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 8191 + ((isSetPower()) ? 131071 : 524287);
        if (isSetPower())
            hashCode = hashCode * 8191 + power.hashCode();

        hashCode = hashCode * 8191 + ((isSetGoddess()) ? 131071 : 524287);
        if (isSetGoddess())
            hashCode = hashCode * 8191 + goddess.hashCode();

        hashCode = hashCode * 8191 + ((isSetByAlias()) ? 131071 : 524287);
        if (isSetByAlias())
            hashCode = hashCode * 8191 + byAlias.hashCode();

        hashCode = hashCode * 8191 + ((isSetImages()) ? 131071 : 524287);
        if (isSetImages())
            hashCode = hashCode * 8191 + images.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(GodBean other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = java.lang.Boolean.compare(isSetPower(), other.isSetPower());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetPower()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.power, other.power);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.compare(isSetGoddess(), other.isSetGoddess());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetGoddess()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.goddess, other.goddess);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.compare(isSetByAlias(), other.isSetByAlias());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetByAlias()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.byAlias, other.byAlias);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = java.lang.Boolean.compare(isSetImages(), other.isSetImages());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetImages()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.images, other.images);
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
        java.lang.StringBuilder sb = new java.lang.StringBuilder("GodBean(");
        boolean first = true;

        if (isSetPower()) {
            sb.append("power:");
            if (this.power == null) {
                sb.append("null");
            } else {
                sb.append(this.power);
            }
            first = false;
        }
        if (isSetGoddess()) {
            if (!first) sb.append(", ");
            sb.append("goddess:");
            if (this.goddess == null) {
                sb.append("null");
            } else {
                sb.append(this.goddess);
            }
            first = false;
        }
        if (isSetByAlias()) {
            if (!first) sb.append(", ");
            sb.append("byAlias:");
            if (this.byAlias == null) {
                sb.append("null");
            } else {
                sb.append(this.byAlias);
            }
            first = false;
        }
        if (isSetImages()) {
            if (!first) sb.append(", ");
            sb.append("images:");
            if (this.images == null) {
                sb.append("null");
            } else {
                sb.append(this.images);
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

    private static class GodBeanStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public GodBeanStandardScheme getScheme() {
            return new GodBeanStandardScheme();
        }
    }

    private static class GodBeanStandardScheme extends org.apache.thrift.scheme.StandardScheme<GodBean> {

        @Override
        public void read(org.apache.thrift.protocol.TProtocol iprot, GodBean struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: // POWER
                        if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
                            {
                                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                                struct.power = new java.util.EnumMap<GreekGodGoddess, java.lang.Integer>(GreekGodGoddess.class);
                                @org.apache.thrift.annotation.Nullable GreekGodGoddess _key1;
                                int _val2;
                                for (int _i3 = 0; _i3 < _map0.size; ++_i3) {
                                    _key1 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                                    _val2 = iprot.readI32();
                                    if (_key1 != null) {
                                        struct.power.put(_key1, _val2);
                                    }
                                }
                                iprot.readMapEnd();
                            }
                            struct.setPowerIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 2: // GODDESS
                        if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
                            {
                                org.apache.thrift.protocol.TSet _set4 = iprot.readSetBegin();
                                struct.goddess = java.util.EnumSet.noneOf(GreekGodGoddess.class);
                                @org.apache.thrift.annotation.Nullable GreekGodGoddess _elem5;
                                for (int _i6 = 0; _i6 < _set4.size; ++_i6) {
                                    _elem5 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                                    if (_elem5 != null) {
                                        struct.goddess.add(_elem5);
                                    }
                                }
                                iprot.readSetEnd();
                            }
                            struct.setGoddessIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 3: // BY_ALIAS
                        if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
                            {
                                org.apache.thrift.protocol.TMap _map7 = iprot.readMapBegin();
                                struct.byAlias = new java.util.HashMap<java.lang.String, GreekGodGoddess>(2 * _map7.size);
                                @org.apache.thrift.annotation.Nullable java.lang.String _key8;
                                @org.apache.thrift.annotation.Nullable GreekGodGoddess _val9;
                                for (int _i10 = 0; _i10 < _map7.size; ++_i10) {
                                    _key8 = iprot.readString();
                                    _val9 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                                    struct.byAlias.put(_key8, _val9);
                                }
                                iprot.readMapEnd();
                            }
                            struct.setByAliasIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case 4: // IMAGES
                        if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
                            {
                                org.apache.thrift.protocol.TSet _set11 = iprot.readSetBegin();
                                struct.images = new java.util.HashSet<java.lang.String>(2 * _set11.size);
                                @org.apache.thrift.annotation.Nullable java.lang.String _elem12;
                                for (int _i13 = 0; _i13 < _set11.size; ++_i13) {
                                    _elem12 = iprot.readString();
                                    struct.images.add(_elem12);
                                }
                                iprot.readSetEnd();
                            }
                            struct.setImagesIsSet(true);
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
        public void write(org.apache.thrift.protocol.TProtocol oprot, GodBean struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            if (struct.power != null) {
                if (struct.isSetPower()) {
                    oprot.writeFieldBegin(POWER_FIELD_DESC);
                    {
                        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32, struct.power.size()));
                        for (java.util.Map.Entry<GreekGodGoddess, java.lang.Integer> _iter14 : struct.power.entrySet()) {
                            oprot.writeI32(_iter14.getKey().getValue());
                            oprot.writeI32(_iter14.getValue());
                        }
                        oprot.writeMapEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            if (struct.goddess != null) {
                if (struct.isSetGoddess()) {
                    oprot.writeFieldBegin(GODDESS_FIELD_DESC);
                    {
                        oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I32, struct.goddess.size()));
                        for (GreekGodGoddess _iter15 : struct.goddess) {
                            oprot.writeI32(_iter15.getValue());
                        }
                        oprot.writeSetEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            if (struct.byAlias != null) {
                if (struct.isSetByAlias()) {
                    oprot.writeFieldBegin(BY_ALIAS_FIELD_DESC);
                    {
                        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32, struct.byAlias.size()));
                        for (java.util.Map.Entry<java.lang.String, GreekGodGoddess> _iter16 : struct.byAlias.entrySet()) {
                            oprot.writeString(_iter16.getKey());
                            oprot.writeI32(_iter16.getValue().getValue());
                        }
                        oprot.writeMapEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            if (struct.images != null) {
                if (struct.isSetImages()) {
                    oprot.writeFieldBegin(IMAGES_FIELD_DESC);
                    {
                        oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRING, struct.images.size()));
                        for (java.lang.String _iter17 : struct.images) {
                            oprot.writeString(_iter17);
                        }
                        oprot.writeSetEnd();
                    }
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class GodBeanTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
        @Override
        public GodBeanTupleScheme getScheme() {
            return new GodBeanTupleScheme();
        }
    }

    private static class GodBeanTupleScheme extends org.apache.thrift.scheme.TupleScheme<GodBean> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, GodBean struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet optionals = new java.util.BitSet();
            if (struct.isSetPower()) {
                optionals.set(0);
            }
            if (struct.isSetGoddess()) {
                optionals.set(1);
            }
            if (struct.isSetByAlias()) {
                optionals.set(2);
            }
            if (struct.isSetImages()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetPower()) {
                {
                    oprot.writeI32(struct.power.size());
                    for (java.util.Map.Entry<GreekGodGoddess, java.lang.Integer> _iter18 : struct.power.entrySet()) {
                        oprot.writeI32(_iter18.getKey().getValue());
                        oprot.writeI32(_iter18.getValue());
                    }
                }
            }
            if (struct.isSetGoddess()) {
                {
                    oprot.writeI32(struct.goddess.size());
                    for (GreekGodGoddess _iter19 : struct.goddess) {
                        oprot.writeI32(_iter19.getValue());
                    }
                }
            }
            if (struct.isSetByAlias()) {
                {
                    oprot.writeI32(struct.byAlias.size());
                    for (java.util.Map.Entry<java.lang.String, GreekGodGoddess> _iter20 : struct.byAlias.entrySet()) {
                        oprot.writeString(_iter20.getKey());
                        oprot.writeI32(_iter20.getValue().getValue());
                    }
                }
            }
            if (struct.isSetImages()) {
                {
                    oprot.writeI32(struct.images.size());
                    for (java.lang.String _iter21 : struct.images) {
                        oprot.writeString(_iter21);
                    }
                }
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, GodBean struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            java.util.BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                {
                    org.apache.thrift.protocol.TMap _map22 = iprot.readMapBegin(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32);
                    struct.power = new java.util.EnumMap<GreekGodGoddess, java.lang.Integer>(GreekGodGoddess.class);
                    @org.apache.thrift.annotation.Nullable GreekGodGoddess _key23;
                    int _val24;
                    for (int _i25 = 0; _i25 < _map22.size; ++_i25) {
                        _key23 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                        _val24 = iprot.readI32();
                        if (_key23 != null) {
                            struct.power.put(_key23, _val24);
                        }
                    }
                }
                struct.setPowerIsSet(true);
            }
            if (incoming.get(1)) {
                {
                    org.apache.thrift.protocol.TSet _set26 = iprot.readSetBegin(org.apache.thrift.protocol.TType.I32);
                    struct.goddess = java.util.EnumSet.noneOf(GreekGodGoddess.class);
                    @org.apache.thrift.annotation.Nullable GreekGodGoddess _elem27;
                    for (int _i28 = 0; _i28 < _set26.size; ++_i28) {
                        _elem27 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                        if (_elem27 != null) {
                            struct.goddess.add(_elem27);
                        }
                    }
                }
                struct.setGoddessIsSet(true);
            }
            if (incoming.get(2)) {
                {
                    org.apache.thrift.protocol.TMap _map29 = iprot.readMapBegin(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.I32);
                    struct.byAlias = new java.util.HashMap<java.lang.String, GreekGodGoddess>(2 * _map29.size);
                    @org.apache.thrift.annotation.Nullable java.lang.String _key30;
                    @org.apache.thrift.annotation.Nullable GreekGodGoddess _val31;
                    for (int _i32 = 0; _i32 < _map29.size; ++_i32) {
                        _key30 = iprot.readString();
                        _val31 = thrift.test.enumcontainers.GreekGodGoddess.findByValue(iprot.readI32());
                        struct.byAlias.put(_key30, _val31);
                    }
                }
                struct.setByAliasIsSet(true);
            }
            if (incoming.get(3)) {
                {
                    org.apache.thrift.protocol.TSet _set33 = iprot.readSetBegin(org.apache.thrift.protocol.TType.STRING);
                    struct.images = new java.util.HashSet<java.lang.String>(2 * _set33.size);
                    @org.apache.thrift.annotation.Nullable java.lang.String _elem34;
                    for (int _i35 = 0; _i35 < _set33.size; ++_i35) {
                        _elem34 = iprot.readString();
                        struct.images.add(_elem34);
                    }
                }
                struct.setImagesIsSet(true);
            }
        }
    }

    private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
        return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
    }
}

