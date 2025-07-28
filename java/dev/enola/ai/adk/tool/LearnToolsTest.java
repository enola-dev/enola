package dev.enola.ai.adk.tool;

import static com.google.common.truth.Truth.assertThat;

import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import org.junit.Test;

public class LearnToolsTest {

    @Schema(name = "Current Time", description = "Returns the current time in the given city")
    public static String getCurrentTime(
            @Schema(description = "The name of the city for which to retrieve the current time")
                    String city) {
        // return Map.of("time", "12:00");
        return "";
    }

    @Test
    public void test() {
        var tool = FunctionTool.create(LearnToolsTest.class, "getCurrentTime");
        assertThat(tool.name()).isEqualTo("getCurrentTime");
        assertThat(tool.description()).isEmpty();
    }
}
