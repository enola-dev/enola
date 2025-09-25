# Cooking AI Agent 🧑‍🍳

An intelligent culinary assistant designed to simplify meal planning, manage personal recipes, and generate shopping lists tailored to your unique tastes and dietary needs.

## ✨ Features

**Personalized Meal Planning:** Get suggestions for a single meal or a full x-day plan based on your profile.

**Preference Management:** The agent stores and recalls your dietary restrictions, allergies, likes, and dislikes from a local preferences.md file.

**Digital Cookbook:** Automatically saves confirmed meal plan recipes to a cookbook.md file, creating a personal recipe collection over time.

**Smart Shopping Lists:** Generates a consolidated shopping list for your meal plan, neatly categorized by supermarket section (Produce, Dairy, etc.).

**Local Filesystem Interaction:** Directly reads and writes to your local files (cookbook.md, preferences.md) to maintain your data.

**Interactive Workflow:** Proposes plans and waits for your confirmation before taking action, allowing you to make swaps or changes.

## 🚀 Getting Started

Prerequisites:

- [enola.dev](https://github.com/enola-dev/enola)
- [a Google Gemini API Key](https://aistudio.google.com/apikey)
- [a Brave Search API Key](https://brave.com/search/api/)

## Usage

1. Store the keys in `~/.config/enola/azkaban.yaml`

    ```bash
    mkdir -p ~/.config/enola
    echo GOOGLE_AI_API_KEY=... >>~/.config/enola/azkaban.yaml
    echo BRAVE_API_KEY=... >>~/.config/enola/azkaban.yaml
    ```

2. Install [enola.dev](https://docs.enola.dev/use/).
3. Git clone the [cooking agent repository](https://github.com/enola-dev/cooking-agent/)
4. In the cloned repository, edit [chef.enola.agent.yaml](https://github.com/enola-dev/cooking-agent/blob/main/chef.enola.agent.yaml) to provide the absolute paths to your `cookbook.md` and `preferences.md` files.
5. From the root of the `cooking-agent` repository, run the server (adjusting the path to `chef.enola.agent.yaml` if necessary):

    ```bash
    enola server --chatPort=7070 -a ./chef.enola.agent.yaml
    ```

6. Go to http://localhost:7070

## 📜 License

This project is licensed under the CC-BY-SA-4.0 license. See the LICENSE file for more details.

<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->
