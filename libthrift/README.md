# Тесты

правила тестов generateTestThrift.gradle из lib/java/gradle/generateTestThrift.gradle  
таска соотвествует профилю плагина thrift-maven-plugin

трифты из libthrift/src/test/thrift/generatejava/JavaAnnotationTest.thrift  
генерятся вручную локально плагином thrift-maven-plugin и переносятся из target/ в src/  
все профили сразу выбирать нельзя, генерить один за одним, если все правильно, то в target/ будут добавляться новые
файлы

или  
thrift -r -out ~ --gen java:beans,nocamel,future_iface,jakarta_annotations JavaBeansTest.thrift


