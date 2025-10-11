{
  description = "Enola AI; see https://enola.dev";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";

    deadnix.url = "github:astro/deadnix";
    deadnix.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
      deadnix,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs { inherit system; };
        jdk' = pkgs.jdk21;
        buildTools = with pkgs; [
          # https://github.com/NixOS/nixfmt/issues/335
          nix

          python312
          curl
          clang-tools # clang-format
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
          maven
          jdk'
          graphviz
          protobuf
          protoc-gen-grpc-java
          protolint
          which

          statix
          deadnix.packages.${system}.default

          bun
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
          # Testing https://github.com/enola-dev/enola/issues/1875 ...
          bazel-vendor-dir = pkgs.stdenv.mkDerivation {
            pname = "bazel-vendor-dir";
            version = gitRev;

            nativeBuildInputs = [
              pkgs.bazel_8
              pkgs.protobuf
              pkgs.protoc-gen-grpc-java
              pkgs.which
              jdk'
            ];
            src = ./.;
            buildPhase = ''
              runHook preBuild

              bash tools/protoc/protoc.bash

              # https://github.com/enola-dev/enola/issues/1876
              export HOME=.built/HOME
              mkdir -p $HOME

              export VENDOR=.built/VENDOR
              mkdir -p $VENDOR
              bazel vendor --vendor_dir=$VENDOR //...

              runHook postBuild
            '';
            installPhase = ''
              runHook preInstall

              tar czvf $out \
                  --sort=name \
                  --mtime='UTC 2080-02-01' \
                  --owner=0 \
                  --group=0 \
                  --numeric-owner $VENDOR

              runHook postInstall
            '';
            # outputHash = pkgs.lib.fakeHash;
            outputHash = "sha256-ExeaXCtvUJa27pDQPixoGQH1Vu1Nh8NzciAqPOJJwRE=";
          };
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
              # This currently only serves to force a dependency to above
              ls -al ${bazel-vendor-dir}

              # class dev.enola.common.Version reads VERSION
              echo -n "${gitRev}" >tools/version/VERSION

              # See https://github.com/NixOS/nix/issues/14024
              bash tools/protoc/protoc.bash

              # https://github.com/enola-dev/enola/issues/1876
              export HOME=.built/HOME
              mkdir -p $HOME

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
