load("@rules_java//java:defs.bzl", "java_plugin")

java_plugin(
    name = "autoservice",
    processor_class = "com.google.auto.service.processor.AutoServiceProcessor",
    visibility = ["//:__subpackages__"],
    deps = ["@enola_maven//:com_google_auto_service_auto_service"],
)

java_plugin(
    name = "picocli",
    processor_class = "picocli.codegen.aot.graalvm.processor.NativeImageConfigGeneratorProcessor",
    visibility = ["//:__subpackages__"],
    deps = ["@enola_maven//:info_picocli_picocli_codegen"],
)
