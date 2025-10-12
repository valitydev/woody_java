# dev.vality.woody

Java реализация [Библиотеки RPC вызовов][rpc-lib] для общения между
микросервисами.

## Описание

1. [woody-api](woody-api/woody-api.md)
1. [woody-thrift](woody-thrift/woody-thrift.md)

## Архитектура

- **woody-api** – базовая библиотека трассировки: управляет `TraceContext`,
  генерирует `trace_id/span_id`, переносит контекст между потоками (`WFlow`,
  `WCallable/WRunnable/WExecutorService`), содержит цепочку прокси и
  перехватчиков для событий, дедлайнов и маппинга ошибок.
- **woody-thrift** – интеграция с Thrift over HTTP: билдеры
  клиентов/сервисов (`THClientBuilder`, `THServiceBuilder`) добавляют
  transport/message интерсепторы, логирование, поддержку `traceparent` и
  расширения метаданных.
- **libthrift** – локальный модуль с патчами Apache Thrift (HTTP-клиент 5,
  сервлеты и TLS), используется как зависимость для `woody-thrift`.

## Ключевые возможности

- Сквозная трассировка вызовов через `TraceData`, автоматическое измерение
  длительности и интеграция со SLF4J MDC и OpenTelemetry.
- Потокобезопасная обработка фоновых задач с сохранением контекста
  (`WFlow.create`, `createServiceFork`).
- Расширяемая система перехватчиков и `MetadataExtensionKit` для
  обогащения метаданных и настройки transport/message уровней.
- HTTP Thrift клиенты и сервисы с пуллингом, логированием, маппингом ошибок и
  готовыми EventListener’ами.

## Для ознакомления

[Thrift](https://thrift.apache.org/)  
[Dapper](http://research.google.com/pubs/pub36356.html)

## Выпуск новой версии

Версии _woody-pom_ и всех его модулей должны совпадать, для этого перед
началом работы над новой версией библиотеки нужно увеличить версию
_woody-pom_ и в корневой директории проекта выполнить команду:  
`mvn versions:update-child-modules -DgenerateBackupPoms=false`  
Параметр `generateBackupPoms` можно опустить, если нужны резервные копии
изменяемых файлов.

## Общая структура

- Maven-монорепо (`pom.xml`) с тремя артефактами: базовая библиотека
  `woody-api`, интеграция `woody-thrift`, а также пропатченный `libthrift`
  (форк Apache Thrift, переиспользующий HttpClient5 и подключающийся как
  модуль).
- Основной стек: Java 11, SLF4J, Apache Commons Pool 2, OpenTelemetry
  (API/SDK/OTLP), Jakarta Servlet 5, Jetty и EasyMock в тестах.

## Woody API

- `TraceContext`/`TraceData` управляют client/service span’ами в
  `ThreadLocal`, автоматически создают `trace_id/span_id`, фиксируют
  длительность, синхронизируют SLF4J MDC и завершают OTEL-спаны.
- `WFlow` и `flow.concurrent` оборачивают `Runnable`/`Callable`/
  `ExecutorService`, сохраняя контекст при выполнении в других потоках,
  поддерживают форки с новыми root- и service-span’ами.
- Система перехватчиков (`proxy`, `interceptor`, `event`):
  - `ProxyFactory` строит динамические прокси вокруг клиентов и
    обработчиков, направляя вызовы через `MethodCallTracer`.
  - `AbstractClientBuilder`/`AbstractServiceBuilder` подключают
    `ContextTracer`, контроль дедлайнов, маппинг ошибок и event-трейсеры.
  - События (`ClientEvent`, `ServiceEvent`) обрабатываются композиционными
    слушателями; `TransportEventInterceptor` и `ProviderEventInterceptor`
    публикуют события до и после вызовов.
- Расширяемость через `interceptor.ext` и `MetadataExtensionKit`:
  расширения получают `TraceData` и транспортный контекст для обогащения
  метаданных.
- Ошибки классифицируются `WErrorType`/`WErrorDefinition`;
  `ErrorMapProcessor` и `ErrorMappingInterceptor` мэппят транспортные и
  бизнес-ошибки; `DeadlineTracer` обеспечивает контроль таймаутов.

## Woody Thrift

- Thrift over HTTP поверх Woody.
  - Клиенты (`THClientBuilder`, `THSpawnClientBuilder`,
    `THPooledClientBuilder`) создают `TServiceClient`, добавляют
    транспортные и message перехватчики (метаданные, traceparent, события),
    управляют ресурсами HttpClient5.
  - Сервисы (`THServiceBuilder`) собирают `TServlet` с обёртками над
    `TProcessor`, прокидывая `TraceContext.forService`, подключая
    транспортные перехватчики и error-mapping (`THErrorMapProcessor`);
    логирование (`THSEventLogListener`, `THCEventLogListener`) включено по
    умолчанию.
  - Транспорт и сообщения расширяются через bundles
    (`MetadataExtensionBundle` и др.), создавая `THCExtensionContext`/
    `THSExtensionContext` для клиента и сервиса.
  - Поддержка W3C traceparent (`TraceParentUtils`), заполнение
    дедлайнов/ошибок в метаданные, HTTP-логгеры.
  - Дополнительные пакеты: `error` (конвертация исключений и
    HTTP-статусов), `event` (логирование), `transport` (конфигурация HTTP
    servlet’ов и клиентов).

## Libthrift

- Локальный модуль с модифицированными классами Apache Thrift
  (HTTP-транспорт, сервлеты, TLS и т.д.) под HttpClient5 и расширения Woody;
  подключается к `woody-thrift` как зависимость той же версии.

## Тесты и утилиты

- `woody-api/src/test` покрывает генераторы идентификаторов, трассировку и
  прокси.
- `woody-thrift/src/test` (Jetty quickstart + EasyMock) проверяет
  HTTP-интеграцию, обработку исключений и метаданные, включая
  интеграционные сценарии `TraceLifecycleIntegrationTest` для проверки
  сквозной OpenTelemetry-трассировки, восстановления контекста, ошибок и
  работы с неполными заголовками.
- Профиль `gen_thrift_classes` включает `thrift-maven-plugin` для генерации
  Thrift IDL.

## Итог

Реализация обеспечивает сквозную трассировку, управление временем жизни
span’ов и доступ к событиям через единую API-обвязку; `woody-thrift` поверх
неё инкапсулирует создание HTTP-клиентов и сервисов Thrift с `traceparent`,
логированием и расширяемыми метаданными, опираясь на локально
модифицированный `libthrift`.

[rpc-lib]: http://52.29.202.218/design/ms/platform/rpc-lib/
