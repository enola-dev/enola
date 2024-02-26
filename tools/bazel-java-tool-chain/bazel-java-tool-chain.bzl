"""This module contains a helper function to extract the location of the JDK."""

def format(target):
    """Returns current_java_runtime.

    Args:
        target: Bazel Target

    Returns:
        Path to JDK, e.g. "external/remotejdk21_linux".
    """

    # See https://github.com/enola-dev/enola/issues/546
    runtime_infos = {k: v for k, v in providers(target).items() if k.endswith("JavaRuntimeInfo")}

    if len(runtime_infos) == 1:
        java_runtime_info = runtime_infos.values()[0]

        # https://bazel.build/rules/lib/providers/JavaRuntimeInfo
        return java_runtime_info.java_home

    fail("Unable to obtain JavaRuntimeInfo.")
