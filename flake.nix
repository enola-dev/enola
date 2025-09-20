{
  description = "Enola AI; see https://enola.dev";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";

    nixpkgs-bun.url = "github:nixos/nixpkgs/ab1f3b61279dfe63cdc938ed90660b99e9d46619"; # bun==1.2.19
    # TODO How-to do this? Or is this not possible?!
    # nix develop: warning: input 'nixpkgs-bun' has an override for a non-existent input 'nixpkgs'
    # nix flake metadata shows that it does not work
    #   nixpkgs-bun.inputs.nixpkgs.follows = "nixpkgs";

    deadnix.url = "github:astro/deadnix";
    deadnix.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs =
    {
      self,
      nixpkgs,
      nixpkgs-bun,
      flake-utils,
      deadnix,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
        pkgs-bun = import nixpkgs-bun { inherit system; };
        jdk' = pkgs.jdk21;
        buildTools = with pkgs; [
          # https://github.com/NixOS/nixfmt/issues/335
          nix

          python312
          curl
          git
          go
          jq
          bazel_8
          # TODO Finish switch from Bazelisk to Bazel package
          #   by cleaning up all scripts etc. which still use
          #   bazelisk, and then rm this, and .bazelversion
          bazelisk
          shellcheck
          nixpkgs-fmt
          unzip
          nodejs
          coursier
          jdk'
          graphviz
          protobuf
          protoc-gen-grpc-java
          docker
          which

          statix
          deadnix.packages.${system}.default

          pkgs-bun.bun
        ];
        # NB: This doesn't actually use tools/version/version-out.bash (like the non-Nix build does)
        gitRev = toString (self.shortRev or self.dirtyShortRev or self.lastModified or "DEVELOPMENT");

      in
      {
        # TODO: for https://nix-bazel.build, replace with mkShellNoCC.
        devShells.default = pkgs.mkShell {
          packages = buildTools;

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

            buildInputs = [ jdk' ];
            nativeBuildInputs = buildTools ++ [
              pkgs.cacert
              pkgs.makeWrapper
              pkgs.which
            ];
            src = ./.;

            buildPhase = ''
              # class dev.enola.common.Version reads VERSION
              echo -n "${gitRev}" >tools/version/VERSION

              # See https://github.com/NixOS/nix/issues/14024
              bash tools/protoc/protoc.bash

              export HOME=$TMPDIR
              bazel build //java/dev/enola/cli:enola_deploy.jar
            '';

            installPhase = ''
              mkdir -p "$out/share/java"
              cp bazel-bin/java/dev/enola/cli/enola_deploy.jar "$out/share/java"
              makeWrapper ${jdk'}/bin/java $out/bin/enola \
                --add-flags "-jar $out/share/java/enola_deploy.jar"
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
