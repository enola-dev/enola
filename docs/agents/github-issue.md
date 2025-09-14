# GitHub Issues 🐛 AI Agent

This AI Agent will find a relevant GitHub issue e.g. for an error message in context, or can create one.

## Gemini CLI

Create a [GitHub Personal Access Token](https://github.com/settings/personal-access-tokens/new) (PAT),
just for _Public Repositories_ and simply do NOT _Add Permission_ for anything - just a (basic) one;
then copy/paste it into your `.gemini/.env` with `GITHUB_PAT=github_pat_...`. Then install this:

    gemini extensions install https://github.com/enola-dev/github-issue-agent

and now start `gemini` as usual, and use the new `/issue` command for this.

## Enola

**TODO** _Update, similarly to https://github.com/enola-dev/git-commit-message-agent ..._ (Maybe using a template?)

<!-- DO NOT MODIFY here; @see tools/agents/update-docs.bash -->
