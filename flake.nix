{
  inputs = {
    # Track a specific tag on the nixpkgs repo.
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";

    # The flake format itself is very minimal, so the use of this
    # library is common.
    flake-utils.url = "github:numtide/flake-utils";
  };

  # Here we can define various kinds of "outputs": packages, tests,
  # and so on, but we will only define a development shell.

  outputs = { nixpkgs, flake-utils, ... }:

    # For every platform that Nix supports, we ...
    flake-utils.lib.eachDefaultSystem (system:

      # ... get the package set for this particular platform ...
      let pkgs = import nixpkgs { inherit system; };
      in
      {
        # ... and define a development shell for it.
        # TODO: for https://nix-bazel.build, replace with mkShellNoCC.
        devShells.default = pkgs.mkShell {
          # Pinned packages available in the environment
          packages = with pkgs; [
            python312
            curl
            git
            go
            jq
            bazelisk
            shellcheck
            nixpkgs-fmt
            unzip
            nodejs
            coursier
            jdk21
            graphviz
            docker
          ];

          # Python venv. Warning: impure! We mitigate impurity through
          # specifying exact package versions in requirements.txt
          venvDir = "./.venv";
          postVenvCreation = ''
            pip install -r requirements.txt
          '';
          buildInputs = with pkgs.python312Packages; [
            venvShellHook
          ];

          # A hook run every time you enter the environment
          postShellHook = ''
            echo Welcome!
          '';
        };
    }
  );
}
