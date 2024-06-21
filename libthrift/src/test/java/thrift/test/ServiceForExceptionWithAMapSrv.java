/**
 * Autogenerated by Thrift Compiler (0.20.0)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package thrift.test;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class ServiceForExceptionWithAMapSrv {

    public interface Iface {

        public void methodThatThrowsAnException() throws ExceptionWithAMap, org.apache.thrift.TException;

    }

    public interface AsyncIface {

        public void methodThatThrowsAnException(org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException;

    }

    public static class Client extends org.apache.thrift.TServiceClient implements Iface {
        public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
            public Factory() {
            }

            @Override
            public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
                return new Client(prot);
            }

            @Override
            public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
                return new Client(iprot, oprot);
            }
        }

        public Client(org.apache.thrift.protocol.TProtocol prot) {
            super(prot, prot);
        }

        public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
            super(iprot, oprot);
        }

        @Override
        public void methodThatThrowsAnException() throws ExceptionWithAMap, org.apache.thrift.TException {
            send_methodThatThrowsAnException();
            recv_methodThatThrowsAnException();
        }

        public void send_methodThatThrowsAnException() throws org.apache.thrift.TException {
            methodThatThrowsAnException_args args = new methodThatThrowsAnException_args();
            sendBase("methodThatThrowsAnException", args);
        }

        public void recv_methodThatThrowsAnException() throws ExceptionWithAMap, org.apache.thrift.TException {
            methodThatThrowsAnException_result result = new methodThatThrowsAnException_result();
            receiveBase(result, "methodThatThrowsAnException");
            if (result.xwamap != null) {
                throw result.xwamap;
            }
            return;
        }

    }

    public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
        public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
            private org.apache.thrift.async.TAsyncClientManager clientManager;
            private org.apache.thrift.protocol.TProtocolFactory protocolFactory;

            public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
                this.clientManager = clientManager;
                this.protocolFactory = protocolFactory;
            }

            @Override
            public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
                return new AsyncClient(protocolFactory, clientManager, transport);
            }
        }

        public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
            super(protocolFactory, clientManager, transport);
        }

        @Override
        public void methodThatThrowsAnException(org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException {
            checkReady();
            methodThatThrowsAnException_call method_call = new methodThatThrowsAnException_call(resultHandler, this, ___protocolFactory, ___transport);
            this.___currentMethod = method_call;
            ___manager.call(method_call);
        }

        public static class methodThatThrowsAnException_call extends org.apache.thrift.async.TAsyncMethodCall<Void> {
            public methodThatThrowsAnException_call(org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }

            @Override
            public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
                prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("methodThatThrowsAnException", org.apache.thrift.protocol.TMessageType.CALL, 0));
                methodThatThrowsAnException_args args = new methodThatThrowsAnException_args();
                args.write(prot);
                prot.writeMessageEnd();
            }

            @Override
            public Void getResult() throws ExceptionWithAMap, org.apache.thrift.TException {
                if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
                    throw new java.lang.IllegalStateException("Method call not finished!");
                }
                org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
                org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
                (new Client(prot)).recv_methodThatThrowsAnException();
                return null;
            }
        }

    }

    public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
        private static final org.slf4j.Logger _LOGGER = org.slf4j.LoggerFactory.getLogger(Processor.class.getName());

        public Processor(I iface) {
            super(iface, getProcessMap(new java.util.HashMap<java.lang.String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
        }

        protected Processor(I iface, java.util.Map<java.lang.String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
            super(iface, getProcessMap(processMap));
        }

        private static <I extends Iface> java.util.Map<java.lang.String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(java.util.Map<java.lang.String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
            processMap.put("methodThatThrowsAnException", new methodThatThrowsAnException());
            return processMap;
        }

        public static class methodThatThrowsAnException<I extends Iface> extends org.apache.thrift.ProcessFunction<I, methodThatThrowsAnException_args> {
            public methodThatThrowsAnException() {
                super("methodThatThrowsAnException");
            }

            @Override
            public methodThatThrowsAnException_args getEmptyArgsInstance() {
                return new methodThatThrowsAnException_args();
            }

            @Override
            protected boolean isOneway() {
                return false;
            }

            @Override
            protected boolean rethrowUnhandledExceptions() {
                return false;
            }

            @Override
            public methodThatThrowsAnException_result getResult(I iface, methodThatThrowsAnException_args args) throws org.apache.thrift.TException {
                methodThatThrowsAnException_result result = new methodThatThrowsAnException_result();
                try {
                    iface.methodThatThrowsAnException();
                } catch (ExceptionWithAMap xwamap) {
                    result.xwamap = xwamap;
                }
                return result;
            }
        }

    }

    public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
        private static final org.slf4j.Logger _LOGGER = org.slf4j.LoggerFactory.getLogger(AsyncProcessor.class.getName());

        public AsyncProcessor(I iface) {
            super(iface, getProcessMap(new java.util.HashMap<java.lang.String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
        }

        protected AsyncProcessor(I iface, java.util.Map<java.lang.String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
            super(iface, getProcessMap(processMap));
        }

        private static <I extends AsyncIface> java.util.Map<java.lang.String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> getProcessMap(java.util.Map<java.lang.String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
            processMap.put("methodThatThrowsAnException", new methodThatThrowsAnException());
            return processMap;
        }

        public static class methodThatThrowsAnException<I extends AsyncIface> extends org.apache.thrift.AsyncProcessFunction<I, methodThatThrowsAnException_args, Void> {
            public methodThatThrowsAnException() {
                super("methodThatThrowsAnException");
            }

            @Override
            public methodThatThrowsAnException_args getEmptyArgsInstance() {
                return new methodThatThrowsAnException_args();
            }

            @Override
            public org.apache.thrift.async.AsyncMethodCallback<Void> getResultHandler(final org.apache.thrift.server.AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final org.apache.thrift.AsyncProcessFunction fcall = this;
                return new org.apache.thrift.async.AsyncMethodCallback<Void>() {
                    @Override
                    public void onComplete(Void o) {
                        methodThatThrowsAnException_result result = new methodThatThrowsAnException_result();
                        try {
                            fcall.sendResponse(fb, result, org.apache.thrift.protocol.TMessageType.REPLY, seqid);
                        } catch (org.apache.thrift.transport.TTransportException e) {
                            _LOGGER.error("TTransportException writing to internal frame buffer", e);
                            fb.close();
                        } catch (java.lang.Exception e) {
                            _LOGGER.error("Exception writing to internal frame buffer", e);
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(java.lang.Exception e) {
                        byte msgType = org.apache.thrift.protocol.TMessageType.REPLY;
                        org.apache.thrift.TSerializable msg;
                        methodThatThrowsAnException_result result = new methodThatThrowsAnException_result();
                        if (e instanceof ExceptionWithAMap) {
                            result.xwamap = (ExceptionWithAMap) e;
                            result.setXwamapIsSet(true);
                            msg = result;
                        } else if (e instanceof org.apache.thrift.transport.TTransportException) {
                            _LOGGER.error("TTransportException inside handler", e);
                            fb.close();
                            return;
                        } else if (e instanceof org.apache.thrift.TApplicationException) {
                            _LOGGER.error("TApplicationException inside handler", e);
                            msgType = org.apache.thrift.protocol.TMessageType.EXCEPTION;
                            msg = (org.apache.thrift.TApplicationException) e;
                        } else {
                            _LOGGER.error("Exception inside handler", e);
                            msgType = org.apache.thrift.protocol.TMessageType.EXCEPTION;
                            msg = new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.INTERNAL_ERROR, e.getMessage());
                        }
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        } catch (java.lang.Exception ex) {
                            _LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }

            @Override
            protected boolean isOneway() {
                return false;
            }

            @Override
            public void start(I iface, methodThatThrowsAnException_args args, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws org.apache.thrift.TException {
                iface.methodThatThrowsAnException(resultHandler);
            }
        }

    }

    @SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
    public static class methodThatThrowsAnException_args implements org.apache.thrift.TBase<methodThatThrowsAnException_args, methodThatThrowsAnException_args._Fields>, java.io.Serializable, Cloneable, Comparable<methodThatThrowsAnException_args> {
        private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("methodThatThrowsAnException_args");


        private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new methodThatThrowsAnException_argsStandardSchemeFactory();
        private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new methodThatThrowsAnException_argsTupleSchemeFactory();


        /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
        public enum _Fields implements org.apache.thrift.TFieldIdEnum {
            ;

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
                if (fields == null)
                    throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
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

        public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

        static {
            java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
            metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
            org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(methodThatThrowsAnException_args.class, metaDataMap);
        }

        public methodThatThrowsAnException_args() {
        }

        /**
         * Performs a deep copy on <i>other</i>.
         */
        public methodThatThrowsAnException_args(methodThatThrowsAnException_args other) {
        }

        @Override
        public methodThatThrowsAnException_args deepCopy() {
            return new methodThatThrowsAnException_args(this);
        }

        @Override
        public void clear() {
        }

        @Override
        public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
            switch (field) {
            }
        }

        @org.apache.thrift.annotation.Nullable
        @Override
        public java.lang.Object getFieldValue(_Fields field) {
            switch (field) {
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
            }
            throw new java.lang.IllegalStateException();
        }

        @Override
        public boolean equals(java.lang.Object that) {
            if (that instanceof methodThatThrowsAnException_args)
                return this.equals((methodThatThrowsAnException_args) that);
            return false;
        }

        public boolean equals(methodThatThrowsAnException_args that) {
            if (that == null)
                return false;
            if (this == that)
                return true;

            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;

            return hashCode;
        }

        @Override
        public int compareTo(methodThatThrowsAnException_args other) {
            if (!getClass().equals(other.getClass())) {
                return getClass().getName().compareTo(other.getClass().getName());
            }

            int lastComparison = 0;

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
            java.lang.StringBuilder sb = new java.lang.StringBuilder("methodThatThrowsAnException_args(");
            boolean first = true;

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

        private static class methodThatThrowsAnException_argsStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
            @Override
            public methodThatThrowsAnException_argsStandardScheme getScheme() {
                return new methodThatThrowsAnException_argsStandardScheme();
            }
        }

        private static class methodThatThrowsAnException_argsStandardScheme extends org.apache.thrift.scheme.StandardScheme<methodThatThrowsAnException_args> {

            @Override
            public void read(org.apache.thrift.protocol.TProtocol iprot, methodThatThrowsAnException_args struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TField schemeField;
                iprot.readStructBegin();
                while (true) {
                    schemeField = iprot.readFieldBegin();
                    if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                        break;
                    }
                    switch (schemeField.id) {
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
            public void write(org.apache.thrift.protocol.TProtocol oprot, methodThatThrowsAnException_args struct) throws org.apache.thrift.TException {
                struct.validate();

                oprot.writeStructBegin(STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }

        }

        private static class methodThatThrowsAnException_argsTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
            @Override
            public methodThatThrowsAnException_argsTupleScheme getScheme() {
                return new methodThatThrowsAnException_argsTupleScheme();
            }
        }

        private static class methodThatThrowsAnException_argsTupleScheme extends org.apache.thrift.scheme.TupleScheme<methodThatThrowsAnException_args> {

            @Override
            public void write(org.apache.thrift.protocol.TProtocol prot, methodThatThrowsAnException_args struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            }

            @Override
            public void read(org.apache.thrift.protocol.TProtocol prot, methodThatThrowsAnException_args struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
            }
        }

        private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
            return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
        }
    }

    @SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
    public static class methodThatThrowsAnException_result implements org.apache.thrift.TBase<methodThatThrowsAnException_result, methodThatThrowsAnException_result._Fields>, java.io.Serializable, Cloneable, Comparable<methodThatThrowsAnException_result> {
        private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("methodThatThrowsAnException_result");

        private static final org.apache.thrift.protocol.TField XWAMAP_FIELD_DESC = new org.apache.thrift.protocol.TField("xwamap", org.apache.thrift.protocol.TType.STRUCT, (short) 1);

        private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new methodThatThrowsAnException_resultStandardSchemeFactory();
        private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new methodThatThrowsAnException_resultTupleSchemeFactory();

        public @org.apache.thrift.annotation.Nullable ExceptionWithAMap xwamap; // required

        /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
        public enum _Fields implements org.apache.thrift.TFieldIdEnum {
            XWAMAP((short) 1, "xwamap");

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
                    case 1: // XWAMAP
                        return XWAMAP;
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
                if (fields == null)
                    throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
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
            tmpMap.put(_Fields.XWAMAP, new org.apache.thrift.meta_data.FieldMetaData("xwamap", org.apache.thrift.TFieldRequirementType.DEFAULT,
                    new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ExceptionWithAMap.class)));
            metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
            org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(methodThatThrowsAnException_result.class, metaDataMap);
        }

        public methodThatThrowsAnException_result() {
        }

        public methodThatThrowsAnException_result(
                ExceptionWithAMap xwamap) {
            this();
            this.xwamap = xwamap;
        }

        /**
         * Performs a deep copy on <i>other</i>.
         */
        public methodThatThrowsAnException_result(methodThatThrowsAnException_result other) {
            if (other.isSetXwamap()) {
                this.xwamap = new ExceptionWithAMap(other.xwamap);
            }
        }

        @Override
        public methodThatThrowsAnException_result deepCopy() {
            return new methodThatThrowsAnException_result(this);
        }

        @Override
        public void clear() {
            this.xwamap = null;
        }

        @org.apache.thrift.annotation.Nullable
        public ExceptionWithAMap getXwamap() {
            return this.xwamap;
        }

        public methodThatThrowsAnException_result setXwamap(@org.apache.thrift.annotation.Nullable ExceptionWithAMap xwamap) {
            this.xwamap = xwamap;
            return this;
        }

        public void unsetXwamap() {
            this.xwamap = null;
        }

        /** Returns true if field xwamap is set (has been assigned a value) and false otherwise */
        public boolean isSetXwamap() {
            return this.xwamap != null;
        }

        public void setXwamapIsSet(boolean value) {
            if (!value) {
                this.xwamap = null;
            }
        }

        @Override
        public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
            switch (field) {
                case XWAMAP:
                    if (value == null) {
                        unsetXwamap();
                    } else {
                        setXwamap((ExceptionWithAMap) value);
                    }
                    break;

            }
        }

        @org.apache.thrift.annotation.Nullable
        @Override
        public java.lang.Object getFieldValue(_Fields field) {
            switch (field) {
                case XWAMAP:
                    return getXwamap();

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
                case XWAMAP:
                    return isSetXwamap();
            }
            throw new java.lang.IllegalStateException();
        }

        @Override
        public boolean equals(java.lang.Object that) {
            if (that instanceof methodThatThrowsAnException_result)
                return this.equals((methodThatThrowsAnException_result) that);
            return false;
        }

        public boolean equals(methodThatThrowsAnException_result that) {
            if (that == null)
                return false;
            if (this == that)
                return true;

            boolean this_present_xwamap = true && this.isSetXwamap();
            boolean that_present_xwamap = true && that.isSetXwamap();
            if (this_present_xwamap || that_present_xwamap) {
                if (!(this_present_xwamap && that_present_xwamap))
                    return false;
                if (!this.xwamap.equals(that.xwamap))
                    return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;

            hashCode = hashCode * 8191 + ((isSetXwamap()) ? 131071 : 524287);
            if (isSetXwamap())
                hashCode = hashCode * 8191 + xwamap.hashCode();

            return hashCode;
        }

        @Override
        public int compareTo(methodThatThrowsAnException_result other) {
            if (!getClass().equals(other.getClass())) {
                return getClass().getName().compareTo(other.getClass().getName());
            }

            int lastComparison = 0;

            lastComparison = java.lang.Boolean.compare(isSetXwamap(), other.isSetXwamap());
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (isSetXwamap()) {
                lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.xwamap, other.xwamap);
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

        public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
            scheme(oprot).write(oprot, this);
        }

        @Override
        public java.lang.String toString() {
            java.lang.StringBuilder sb = new java.lang.StringBuilder("methodThatThrowsAnException_result(");
            boolean first = true;

            sb.append("xwamap:");
            if (this.xwamap == null) {
                sb.append("null");
            } else {
                sb.append(this.xwamap);
            }
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
                read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
            } catch (org.apache.thrift.TException te) {
                throw new java.io.IOException(te);
            }
        }

        private static class methodThatThrowsAnException_resultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
            @Override
            public methodThatThrowsAnException_resultStandardScheme getScheme() {
                return new methodThatThrowsAnException_resultStandardScheme();
            }
        }

        private static class methodThatThrowsAnException_resultStandardScheme extends org.apache.thrift.scheme.StandardScheme<methodThatThrowsAnException_result> {

            @Override
            public void read(org.apache.thrift.protocol.TProtocol iprot, methodThatThrowsAnException_result struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TField schemeField;
                iprot.readStructBegin();
                while (true) {
                    schemeField = iprot.readFieldBegin();
                    if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: // XWAMAP
                            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                                struct.xwamap = new ExceptionWithAMap();
                                struct.xwamap.read(iprot);
                                struct.setXwamapIsSet(true);
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
            public void write(org.apache.thrift.protocol.TProtocol oprot, methodThatThrowsAnException_result struct) throws org.apache.thrift.TException {
                struct.validate();

                oprot.writeStructBegin(STRUCT_DESC);
                if (struct.xwamap != null) {
                    oprot.writeFieldBegin(XWAMAP_FIELD_DESC);
                    struct.xwamap.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }

        }

        private static class methodThatThrowsAnException_resultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
            @Override
            public methodThatThrowsAnException_resultTupleScheme getScheme() {
                return new methodThatThrowsAnException_resultTupleScheme();
            }
        }

        private static class methodThatThrowsAnException_resultTupleScheme extends org.apache.thrift.scheme.TupleScheme<methodThatThrowsAnException_result> {

            @Override
            public void write(org.apache.thrift.protocol.TProtocol prot, methodThatThrowsAnException_result struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
                java.util.BitSet optionals = new java.util.BitSet();
                if (struct.isSetXwamap()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetXwamap()) {
                    struct.xwamap.write(oprot);
                }
            }

            @Override
            public void read(org.apache.thrift.protocol.TProtocol prot, methodThatThrowsAnException_result struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
                java.util.BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.xwamap = new ExceptionWithAMap();
                    struct.xwamap.read(iprot);
                    struct.setXwamapIsSet(true);
                }
            }
        }

        private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
            return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
        }
    }

}
