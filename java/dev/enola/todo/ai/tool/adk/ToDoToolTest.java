/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.todo.ai.tool.adk;

import static dev.enola.ai.iri.GoogleModelProvider.FLASH;
import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import static org.junit.Assume.assumeTrue;

import com.google.adk.models.BaseLlm;
import com.google.adk.tools.BaseTool;

import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.test.AgentTester;
import dev.enola.ai.iri.ModelConfig;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;
import dev.enola.todo.ToDoRepository;
import dev.enola.todo.ToDoRepositoryInMemory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class ToDoToolTest {

    AgentTester agentTester;

    @Before
    public void setup() throws IOException {
        // TODO Move this into a re-usable test harness utility class?
        SecretManager sm = new TestSecretManager();
        assumeTrue(
                "Skipping test, GOOGLE_AI_API_KEY_SECRET_NAME is not set",
                sm.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent());

        Provider<BaseLlm> llm = new LlmProviders(sm);

        ToDoRepository toDoRepository = new ToDoRepositoryInMemory();
        Map<String, BaseTool> toDoTool = new ToDoTool(toDoRepository).createToolSet();

        var model = llm.get(ModelConfig.temperature(FLASH, 0));
        agentTester = new AgentTester(model, toDoTool);
    }

    @Test
    public void noToDos() throws IOException {
        agentTester.assertTextResponseContainsAny(
                "List all of my ToDo Task items.",
                "I don't have any ToDo items",
                "I do not have any ToDo items");
    }

    @Test
    public void createAndList() throws IOException {
        agentTester.assertTextResponseContainsAll(
                "Add a new ToDo Task item to remind me to add task completion to Enola",
                "OK. I've added",
                "to your ToDo list");

        agentTester.assertTextResponseContainsAll(
                "List all of my ToDo Task items.", "Add task completion to Enola");
    }
}
