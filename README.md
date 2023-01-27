# performancebot

Bachelor thesis project in software engineering and computer science at Chalmers.

## About

performancebot is a framework meant to run benchmarks with [JMH](https://github.com/openjdk/jmh),
analyse results and compare them historically.

## Contributors

- Ali Alkhaled  [@alialk-s](https://github.com/alialk-s)
- Elin Hagman [@elinhagman](https://github.com/elinhagman)
- Samuel Kajava [@samkaj](https://github.com/samkaj)
- Jonathan Linder [@LinderJonathan](https://github.com/linderjonathan)
- Kasper Ljunggren [@Kablero](https://github.com/kablero)
- Linus Lundgren [@dubbell](https://github.com/dubbell)

## Running the bot

Run the following command in a terminal (make sure to `cd` into this directory/folder):

```zsh
./mvnw spring-boot:run
```

To run the tests, use:

```zsh
./mvnw test
```

## Setting the formatter

For consistency's sake, let's all use the same formatter following the Google Java Style Guide.
You should set the indentation to 2 spaces as well.

### IntelliJ

You can set the formatter in the IDE settings. Go to:

`Settings` -> `Editor` -> `Code Style`, then besides the `scheme` dropdown, press the configuration
wheel, and press `import`, select
the [`intellij-java-google-style.xml`](intellij-java-google-style.xml) file in the project root,
before finally applying the settings. Done!

_Optional_: It can be nice to automatically run the formatter whenever saving a file, to do so,
simply `Settings` -> `Tools`, and [x] `Format on save`.

### VSCode

You can set the formatter in `settings.json`, which can be opened with `ctrl+shift+p`, search for
`settings.json` and click the `Preferences: Open User Settings (JSON)`. Once the file is open,
simply put the following at the bottom, within the brackets:

    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml"

_Optional_: To automatically format the code upon saving the following can be added to the same
`settings.json`:

    "editor.formatOnSave": true
