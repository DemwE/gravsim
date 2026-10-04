{ pkgs, lib, config, ... }:

{
  languages.c.enable = true;

  packages = with pkgs; [
    sdl3
    libGL
  ];
}