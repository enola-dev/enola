package dev.enola.common.secret;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class SecretManagersTest {

    SecretManager secretManager = new InMemorySecretManager();

    @Test
    public void empty() {
        assertThat(secretManager.getOptional("foo")).isEmpty();
    }

    @Test
    public void store() {
        var bar = "bar".toCharArray();
        secretManager.store("foo", bar);
        assertThat(bar).isEqualTo(new char[] {0, 0, 0});

        try (var secret = secretManager.getOptional("foo").get()) {
            secret.process(it -> assertThat(it).isEqualTo("bar".toCharArray()));
        }

        // Intentionally similarly again, but now also test the clearing business
        var hold = new AtomicReference<char[]>();
        try (var secret = secretManager.getOptional("foo").get()) {
            secret.process(
                    newValue -> {
                        hold.set(newValue);
                        assertThat(hold.get()).isEqualTo("bar".toCharArray());
                    });
            assertThat(hold.get()).isEqualTo(new char[] {0, 0, 0});
        }

        // Only if you *REALLY* must, e.g. because you need to pass it to an existing API, then:
        String azkaban;
        try (var secret = secretManager.getOptional("foo").get()) {
            azkaban = secret.map(String::new);
        }
        assertThat(azkaban).isEqualTo("bar");
    }
}
