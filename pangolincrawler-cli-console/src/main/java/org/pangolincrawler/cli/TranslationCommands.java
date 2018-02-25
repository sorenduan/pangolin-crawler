package org.pangolincrawler.cli;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

//@ShellComponent
public class TranslationCommands {

  @ShellMethod("Translate text from one language to another.")
  public String translate() {

    return "11111";
  }

  @ShellMethod(value = "Add numbers.", key = "sum")
  public int add(int a, int b) {
    return a + b;
  }

  @ShellMethod(value = "Display stuff.", prefix = "-")
  public String echo(int a, int b, @ShellOption("--third") int c) {
    return String.format("You said a=%d, b=%d, c=%d", a, b, c);
  }

}