{ pkgs, lib, config, ... }:

{
  languages.c.enable = true;

  packages = with pkgs; [
    sdl3             # Wymagany backend dla Jaylib 6.0 (x86_64)
    libGL
  ];

  env.LD_LIBRARY_PATH = lib.makeLibraryPath (with pkgs; [
    sdl3
    libGL
  ]);
}