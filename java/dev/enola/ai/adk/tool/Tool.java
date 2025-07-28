package dev.enola.ai.adk.tool;

import dev.enola.ai.adk.demo.ABC;

import java.util.Map;

// TODO Move from package dev.enola.ai.adk.tool to package dev.enola.ai.tool
public interface Tool extends ABC {

    Map<String, Object> execute(Map<String, Object> input);
}
