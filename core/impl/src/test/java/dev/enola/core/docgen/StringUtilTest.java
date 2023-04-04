package dev.enola.core.docgen;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static dev.enola.core.docgen.StringUtil.capitalize;

public class StringUtilTest {
    @Test public void testCapitalize() {
        assertThat(capitalize(null)).isNull();
        assertThat(capitalize("")).isEmpty();
        assertThat(capitalize("a")).isEqualTo("A");
        assertThat(capitalize("ab")).isEqualTo("Ab");
        assertThat(capitalize("abc")).isEqualTo("Abc");
    }
}
