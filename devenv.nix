{ pkgs, lib, config, ... }:

{
  languages.c.enable = true;

  packages = with pkgs; [
    sdl3
    libGL
  ];

  env.LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath (with pkgs; [
    sdl3
    libGL
  ]);
}