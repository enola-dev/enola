load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "bek",
    srcs = glob(["src/main/java/**/*.java"]),
    main_class = "dev.enola.k8s.Bek",
    deps = [
        "@maven//:io_kubernetes_client_java",
        "@maven//:io_kubernetes_client_java_api",
        "@maven//:org_slf4j_slf4j_simple",
    ],
)
