# Git Commit Message 🔮 AI Agent

This will generate Git Commit messages for you using an LLM:

1. First, make sure that you [have `uvx` available](https://docs.astral.sh/uv/getting-started/installation/#standalone-installer) (until #[1632](https://github.com/enola-dev/enola/issues/1632))
1. Next, [create a Google Gemini API key](https://aistudio.google.com/apikey)
1. Now [store this secret](https://docs.enola.dev/use/secret/), using: `mkdir -p ~/.config/enola && echo GOOGLE_AI_API_KEY=... >>~/.config/enola/azkaban.yaml`
1. Finally, [install Enola.dev](https://docs.enola.dev/use/), and now run:

```sh
enola ai --agents=git-commit-message --prompt="Make it so!"
```

We recommend that you create an `alias` for this in your [dotfiles](https://github.com/vorburger/vorburger-dotfiles-bin-etc/blob/main/dotfiles/alias), e.g. `ac` for _AI Commit!_

<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->
