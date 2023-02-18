load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "bek",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = ["@maven//:io_kubernetes_client_java", "@maven//:io_kubernetes_client_java_api"],
    main_class = "dev.enola.k8s.Bek"
)
