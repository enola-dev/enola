{
  description = "Enola AI; see https://enola.dev";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";
    nixpkgs-bun.url = "github:nixos/nixpkgs/ab1f3b61279dfe63cdc938ed90660b99e9d46619"; # bun==1.2.19
    flake-utils.url = "github:numtide/flake-utils";
    deadnix.url = "github:astro/deadnix";
  };

  outputs =
    {
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
          jdk'
          graphviz
          docker

          statix
          deadnix.packages.${system}.default

          pkgs-bun.bun
        ];
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
            version = "0.0.1"; # TODO: read from file

            buildInputs = [ jdk' ];
            nativeBuildInputs = buildTools ++ [
              pkgs.cacert
              pkgs.makeWrapper
            ];
            src = ./.;

            buildPhase = ''
              export HOME=$TMPDIR
              bazelisk build //java/dev/enola/cli:enola_deploy.jar
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
      }
    );
}
