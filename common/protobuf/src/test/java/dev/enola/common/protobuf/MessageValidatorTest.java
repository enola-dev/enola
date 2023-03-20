package dev.enola.common.protobuf;

import com.google.common.truth.Truth;import com.google.protobuf.Timestamp;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

public class MessageValidatorTest {

    MessageValidator<Timestamp> testValidator = (ts, r) -> {
        // TODO Make getting Descriptor simpler & shorter...
        if (ts.getNanos() == 0)
            r.add(Timestamp.getDescriptor().findFieldByNumber(Timestamp.NANOS_FIELD_NUMBER), ">0!");
        if (ts.getSeconds() == 0)
            r.add(Timestamp.getDescriptor().findFieldByNumber(Timestamp.SECONDS_FIELD_NUMBER), ">0!");
    };

    @Test
    public void testValidate() {
        var v = new MessageValidators();
        v.register(testValidator, Timestamp.getDescriptor());
        assertThat(v.validate(Timestamp.getDefaultInstance()).toMessage().getValidationsCount()).isEqualTo(2);
    }
}
