# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Starlark rule to generate a Junit4 TestSuite
Assumes srcs are all .java Test files
Assumes junit4 is already added to deps by the user.

See https://github.com/bazelbuild/bazel/issues/1017 for background.
"""

load("@rules_java//java:defs.bzl", "java_test")

_OUTPUT = """import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({%s})
@SuppressWarnings("DefaultPackage")
public class %s {}
"""

_PREFIXES = ("org", "com", "edu", "dev", "io")

def _SafeIndex(j, val):
    for i, v in enumerate(j):
        if val == v:
            return i
    return -1

def _AsClassName(fname):
    fname = [x.path for x in fname.files.to_list()][0]
    toks = fname[:-5].split("/")
    findex = -1
    for s in _PREFIXES:
        findex = _SafeIndex(toks, s)
        if findex != -1:
            break
    if findex == -1:
        fail("%s does not contain any of %s" % (fname, _PREFIXES))
    return ".".join(toks[findex:]) + ".class"

def _impl(ctx):
    classes = ",".join(
        [_AsClassName(x) for x in ctx.attr.srcs],
    )
    ctx.actions.write(output = ctx.outputs.out, content = _OUTPUT % (
        classes,
        ctx.attr.outname,
    ))

_gen_suite = rule(
    attrs = {
        "deps": attr.label_list(allow_files = False),
        "outname": attr.string(),
        "srcs": attr.label_list(allow_files = True),
        "srcs_utils": attr.label_list(allow_files = True, default = []),
    },
    outputs = {"out": "%{name}.java"},
    implementation = _impl,
)

def junit_tests(name, srcs, deps, srcs_utils = [], **kwargs):
    """Implementation.

    Args:
        name: Rule Name
        srcs: All Tests to actually run
        srcs_utils: Utility classes to build but not run tests, if any
        deps: Dependencies of Test
        **kwargs: KW Args
    """
    s_name = name.replace("-", "_") + "TestSuite"
    _gen_suite(
        name = s_name,
        srcs = srcs,
        deps = deps,
        outname = s_name,
    )
    size = kwargs.get("size", "small")
    size = size
    jvm_flags = kwargs.get("jvm_flags", [])
    jvm_flags = jvm_flags
    java_test(
        name = name,
        test_class = s_name,
        srcs = [":" + s_name] + srcs + srcs_utils,
        deps = deps + [
            "@maven//:com_google_guava_guava",
            "@maven//:com_google_jimfs_jimfs",
            "@maven//:com_google_protobuf_protobuf_java",
            "@maven//:com_google_protobuf_protobuf_java_util",
            "@maven//:com_google_truth_extensions_truth_java8_extension",
            "@maven//:com_google_truth_truth",
            "@maven//:com_google_truth_extensions_truth_proto_extension",
            # TODO https://valfirst.github.io/slf4j-test/usage.html#printing-log-statements-to-system-out-and-err
            #   instead of "@maven//:org_slf4j_slf4j_simple",
            "@maven//:com_github_valfirst_slf4j_test",
            "@maven//:junit_junit",
        ],
        # Increase timeout, due to https://youtrack.jetbrains.com/issue/BAZEL-1791
        timeout = "long",
        **dict(kwargs, size = size, jvm_flags = jvm_flags)
    )
