# Woody Java – Reference Context

## Project Overview

- Maven multi-module library delivering RPC tracing infrastructure for
  microservices.
- Java 11 baseline; core dependencies include SLF4J, Apache Commons Pool 2,
  OpenTelemetry (API/SDK/OTLP exporter), Jakarta Servlet 5, HttpClient5, Jetty
  (tests), EasyMock.
- Modules share version `woody` (root POM); `dependencyManagement` keeps `woody-
  api` version-aligned.

## Module Breakdown

### woody-api

- Thread-local tracing via `TraceContext`/`TraceData` managing client/service
  spans, auto ID generation, duration tracking, SLF4J MDC sync, OTEL span
  lifecycle.
- `MDCUtils` публикует trace/span идентификаторы Woody и OpenTelemetry,
  дедлайны и RPC-метаданные (отключаемо через системное свойство
  `woody.mdc.extended`).
- Concurrency helpers (`WFlow`, `WCallable`, `WRunnable`, `WExecutorService`)
  clone/propagate trace context across threads, including service/client forks.
- Proxy/interceptor pipeline:
  - `ProxyFactory` wraps interfaces with dynamic proxies and
    `MethodCallTracer`.
  - `AbstractClientBuilder`/`AbstractServiceBuilder` assemble tracing,
    deadline
    enforcement (`DeadlineTracer`), error
      mapping, and event dispatch.
  - Event system (`ClientEvent`, `ServiceEvent`, composite listeners) plus
    transport/provider interceptors for
      lifecycle hooks.
- Error framework (`WErrorType`, `WErrorDefinition`, `ErrorMapProcessor`,
  `ErrorMappingInterceptor`) translating transport/business outcomes.
- Metadata extensibility via `interceptor.ext`, `ExtensionBundle`,
  `MetadataExtensionKit`.

### woody-thrift

- Thrift-over-HTTP implementation layered on woody-api.
- Client builders (`THClientBuilder`, `THSpawnClientBuilder`,
  `THPooledClientBuilder`) construct `TServiceClient`, inject message/transport
  interceptors, traceparent propagation, metadata extensions, logging
  (`THCEventLogListener`); support custom or pooled HttpClient5.
- Service builder (`THServiceBuilder`) wraps `TProcessor` into `TServlet`,
  applies transport interceptors, `THErrorMapProcessor`, logging
  (`THSEventLogListener`), and ensures `TraceContext.forService`.
- Extension bundles produce `THCExtensionContext`/`THSExtensionContext`;
  `TraceParentUtils` handles W3C traceparent parsing/serialization.
- Supplemental packages: `error` (exception ↔ response mapping), `event` (HTTP
  logging), `transport` (servlet/client wiring).
- Обновлённый `THProviderErrorMapper` синхронизирует статус, источники ошибок,
  метаданные и обеспечивает трассировку при транспортных исключениях.

### libthrift

- Local fork of Apache Thrift with HttpClient5 transport adjustments,
  servlet/TLS tweaks, and hooks compatible with woody interceptors.
- Packaged as module dependency for `woody-thrift` (same version).

## Build & Tooling

- Root `pom.xml` наследуется от `dev.vality:library-parent-pom:3.1.0` и
  управляет версией через `${revision}`.
- `woody-thrift` offers `gen_thrift_classes` profile running `thrift-maven-
  plugin` (`thrift` executable required).
- Target Java version 11; uses Checkstyle suppressions and Renovate config.

## Testing

- `woody-api/src/test`: ID generators, tracing logic, proxy behavior.
- `woody-thrift/src/test`: Jetty quickstart servers + EasyMock cover HTTP
  integration, metadata propagation, error mapping, а также свежие
  интеграционные сценарии `TraceLifecycleIntegrationTest`, проверяющие
  сквозную OpenTelemetry-трассировку (новый/восстановленный контекст,
  обработку ошибок, отсутствие обязательных метаданных).
- Дополнительно `THProviderErrorMapperTest` и `MetadataMdcPropagationTest`
  контролируют обработку ошибок и перенос MDC/OTel данных.

## Key Concepts for Agents

- Always maintain root/service/client span consistency; `TraceContext`
  orchestrates init/destroy hooks and ensures MDC/Otel sync.
- Cross-thread execution must wrap tasks with
  `WFlow.create`/`createServiceFork`.
- Interceptors are composable; metadata extensions rely on extension bundles
  (client/service contexts differ).
- `libthrift` should be treated as authoritative transport layer—do not upgrade
  Apache Thrift without reconciling local changes.

## Ready-to-Use Snippets

- Create forked service task: `WFlow.createServiceFork(runnable)` or callables
  with custom ID generators.
- Client build pattern:
  ```java
  ThriftServiceSrv.Iface client = new THClientBuilder()
      .withAddress(URI.create("https://example"))
      .withHttpClient(HttpClientBuilder.create().build())
      .withEventListener(listener)
      .build(ThriftServiceSrv.Iface.class);
  ```
- Service servlet:
  ```java
  Servlet servlet = new THServiceBuilder()
      .withEventListener(listener)
      .build(ThriftServiceSrv.Iface.class, handlerImpl);
  ```

## Operational Notes

- Logging depends on composite listeners; disable via `withLogEnabled(false)`.
- Deadlines propagate through spans; ensure upstream services respect
  `DeadlineTracer`.
- Error mapping distinguishes transport errors vs business
  (`WErrorType.BUSINESS_ERROR` leaves transport metadata intact).
- For new metadata, implement `MetadataExtensionKit` and include via builder
  `withMetaExtensions`.
- Для фоновых задач используйте `WFlow.createServiceFork(...)` — он создаёт
  новый service-span и корректно инициализирует OpenTelemetry контекст.
