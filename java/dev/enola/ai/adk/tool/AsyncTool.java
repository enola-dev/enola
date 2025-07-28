package dev.enola.ai.adk.demo;

import io.reactivex.rxjava3.core.Single;

import java.util.Map;

public interface AsyncTool extends ABC {

    // TODO Single or Maybe ?
    Single<Map<String, Object>> execute(Map<String, Object> input);
}
