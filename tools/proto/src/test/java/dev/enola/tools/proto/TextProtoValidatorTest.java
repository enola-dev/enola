package dev.enola.tools.proto;

import com.google.common.io.Resources;
import com.google.protobuf.Timestamp;
import org.junit.Test;

import static com.google.common.io.Resources.getResource;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

// TODO(vorburger) Upstream this!
public class TextProtoValidatorTest {
    @Test public void testValidation() {
        // OK
        // TODO(vorburger) Should not require Timestamp.newBuilder(), but proto-file + proto-message
        // TODO(vorburger) Should not return null, but Timestamp instance, which needs to be asserted for equalsTo.
        assertThat(new TextProtoValidator().validate(getResource("dev/enola/tools/proto/ok.textproto"), Timestamp.newBuilder())).isNull();

        // NOK
        assertThrows(IllegalArgumentException.class, () -> new TextProtoValidator().validate(getResource("dev/enola/tools/proto/nok.textproto"), Timestamp.newBuilder()));
    }
}