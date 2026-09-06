{
  description = "Enola AI; see https://enola.dev";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
        jdk' = pkgs.jdk21;
        pythonEnv = pkgs.python3.withPackages (ps: [
          ps."mkdocs-material"
          ps."mkdocs-git-revision-date-localized-plugin"
          ps."mkdocs-include-markdown-plugin"
        ]);
        buildTools = with pkgs; [
          # https://github.com/NixOS/nixfmt/issues/335
          nix

          coreutils
          gnused
          gnugrep
          findutils
          gawk

          pythonEnv
          asciinema
          curl
          clang-tools # clang-format
          git
          go
          jq
          shellcheck
          twilio-cli
          nixpkgs-fmt
          unzip
          nodejs
          maven
          coursier
          jdk'
          jbang
          graphviz
          protobuf_32
          protoc-gen-grpc-java
          protolint
          which

          statix

          bun

          deadnix
          bazel_8
          buildifier
          buildozer
          uv
          kubo
          pre-commit
        ];
        # NB: This doesn't actually use tools/version/version-out.bash (like the non-Nix build does)
        gitRev = toString (self.shortRev or self.dirtyShortRev or self.lastModified or "DEVELOPMENT");

      in
      {
        # TODO: for https://nix-bazel.build, replace with mkShellNoCC.
        devShells.default = pkgs.mkShell {
          packages = buildTools;

          # A hook run every time you enter the environment
          postShellHook = ''
            # TODO Huh, why is this ugly hack required!?
            export PATH="${pkgs.protoc-gen-grpc-java}/bin:$PATH"

            echo Welcome to contributing to Enola.dev! You can now run e.g. ./enola or ./test.bash etc. here.
          '';
        };

        packages = rec {
          # $ nix run
          # $ nix build .#enola
          # $ result/bin/enola --help
          default = enola;
          enola = pkgs.stdenv.mkDerivation {
            pname = "enola";
            version = gitRev;
            dontUsePytestCheck = true;
            dontCheck = true;

            buildInputs = [ jdk' ];
            nativeBuildInputs = buildTools ++ [
              pkgs.cacert
              pkgs.makeWrapper
              pkgs.which
            ];
            src = ./.;

            buildPhase = ''
              runHook preBuild

              # class dev.enola.common.Version reads VERSION
              echo -n "${gitRev}" >tools/version/VERSION

              # See https://github.com/NixOS/nix/issues/14024
              bash tools/protoc/protoc.bash

              # https://github.com/enola-dev/enola/issues/1876
              export HOME="$PWD/.built/HOME"
              mkdir -p "$HOME"

              bazel build //java/dev/enola/cli:enola_deploy.jar

              runHook postBuild
            '';

            installPhase = ''
              runHook preInstall

              mkdir -p "$out/share/java"
              cp bazel-bin/java/dev/enola/cli/enola_deploy.jar "$out/share/java"
              makeWrapper ${jdk'}/bin/java $out/bin/enola \
                --add-flags "-jar $out/share/java/enola_deploy.jar"

              runHook postInstall
            '';
          };
        };

        apps = {
          test = {
            type = "app";
            program = "${
              pkgs.writeShellApplication {
                name = "test";
                runtimeInputs = buildTools;
                text = builtins.readFile ./test.bash;
              }
            }/bin/test";
          };
        };

        formatter = pkgs.nixfmt-tree;
      }
    );
}
