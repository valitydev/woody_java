## Agents Reference

## Project Overview

- Maven multi-module library delivering RPC tracing infrastructure for
  microservices.
- Java 11 baseline; core dependencies include SLF4J, Apache Commons Pool 2,
  OpenTelemetry (API/SDK/OTLP exporter), Jakarta Servlet 5, HttpClient5, Jetty
  (tests) and EasyMock.
- Modules share the `woody` version; the parent POM manages the revision via
  `${revision}`.

## Module Breakdown

### woody-api

- Thread-local tracing via `TraceContext`/`TraceData` managing client/service
  spans, auto ID generation, duration tracking, SLF4J MDC sync and OTEL span
  lifecycle.
- `MDCUtils` publishes Woody and OpenTelemetry identifiers, deadlines and RPC
  metadata (toggle via `-Dwoody.mdc.extended=false`).
- Concurrency helpers (`WFlow`, `WCallable`, `WRunnable`, `WExecutorService`)
  clone/propagate trace context across threads; `createServiceFork` prepares a
  fresh service span with OTEL context.
- Proxy/interceptor pipeline (deadline tracing, error mapping, event
  dispatch). Metadata is extensible through `MetadataExtensionKit`.

### woody-thrift

- Thrift-over-HTTP layer atop woody-api.
- Client builders (`THClientBuilder`, `THSpawnClientBuilder`,
  `THPooledClientBuilder`) wire transport/message interceptors, metadata
  extensions, traceparent propagation and logging.
- Service builder (`THServiceBuilder`) wraps `TProcessor` in a servlet,
  attaches interceptors, error mapping (`THErrorMapProcessor`) and logging.
- Extension bundles provide client/service contexts; `TraceParentUtils`
  handles W3C traceparent headers.
- `THProviderErrorMapper` aligns HTTP/transport errors with `WErrorDefinition`
  and enriches metadata.

### libthrift

- Local fork of Apache Thrift with HttpClient5 transport tweaks, servlet/TLS
  adjustments and hooks compatible with woody interceptors; packaged as a
  module dependency for `woody-thrift`.

## Build & Tooling

- Root POM inherits from `dev.vality:library-parent-pom:3.1.0`; version is
  driven by `${revision}` to keep modules aligned.
- `woody-thrift` provides the `gen_thrift_classes` profile (requires the
  `thrift` executable).

## Testing

- `woody-api/src/test`: ID generators, tracing logic, proxy behavior,
  concurrency helpers (`MdcUtilsExtendedTest`, `TestWFlow`,
  `TestWExecutorService`, `ContextInterceptorTest`).
- `woody-thrift/src/test`: Jetty quickstart + EasyMock covering HTTP
  integration, metadata propagation, error mapping and OTEL spans:
  - `TraceLifecycleIntegrationTest` – end-to-end OpenTelemetry propagation,
    restored headers, error handling, missing metadata.
  - `MetadataMdcPropagationTest` – SLF4J MDC propagation between client and
    service.
  - `THProviderErrorMapperTest` – transport/business error mapping semantics.

### Test Commands

- `mvn -pl woody-thrift -Dcheckstyle.skip=true -Dvalidation.skip=true test`
- `mvn -pl woody-api -Dcheckstyle.skip=true -Dvalidation.skip=true test`

## Operational Notes

- Logging listeners are composable; disable with `withLogEnabled(false)`.
- Deadlines propagate through spans; `DeadlineTracer` enforces timeouts.
- Transport vs business errors: `WErrorType.BUSINESS_ERROR` keeps transport
  metadata intact.
- For new metadata propagation, implement `MetadataExtensionKit` and plug it
  via the client/service builders.
- Treat `libthrift` as authoritative transport layer—review local patches
  before upgrading upstream Thrift.
