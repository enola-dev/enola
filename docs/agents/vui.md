# Voice User Interface (VUI) AI Agent 🗣️

## Enola 🕵🏾‍♀️

1. [Create a Google Gemini API key](https://aistudio.google.com/apikey)
1. [Store this secret](https://docs.enola.dev/use/secret/), using: `mkdir -p ~/.config/enola && echo GOOGLE_AI_API_KEY=... >~/.config/enola/azkaban.yaml`
1. [Install Enola.dev](https://docs.enola.dev/use/), and run:

       enola -vvv server --chatPort=7070 --http-scheme --agents=https://raw.githubusercontent.com/enola-dev/vui-agent/refs/heads/main/enola.agent.yaml

Open http://localhost:7070 and press the Microphone button, and start speaking to your Computer.

[![Demo Video](https://img.youtube.com/vi/1T7U5mQYO-c/0.jpg)](https://www.youtube.com/watch?v=1T7U5mQYO-c)

<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->
