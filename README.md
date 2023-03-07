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

Running this bot before we have one hosted requires some setup, but if you follow the steps below, you should be fine. Otherwise, just write on Discord and you will get help.

- Download and install [ngrok](https://ngrok.com/download). This creates a tunnel for proxying webhooks events sent by GitHub to your locally hosted app.
	- Specify your port in `application.properties`, (i.e. `1337`), this will later be used to start the tunnel which will redirect events to our java app which will listen on the same port.

Now, start `ngrok` with the port you specified in the properties earlier:

```zsh
ngrok http 1337
```

- In the terminal window, look at the public https address and copy the link.
- Now you are ready to create the your own GitHub Application. Follow this [tutorial](https://docs.github.com/en/apps/creating-github-apps/creating-github-apps/creating-a-github-app) and enter the link you copied in Website URL. Also add it in Webhook URL with /payload at the end. Now GitHub knows where to send webhook events that the bot is subscribed to.
- Once a bot has been created, save the App ID and create a Private Key (save the file and put it in the root of the project, do not push it). You need these for authentication.
- Remember to add the app to your account so it can listen for changes you make in your repos (I use a dummy private repo for testing that stuff works).
- You can also repeat payloads in the web interface for ngrok, so you don't need to spam your contribution graph

To run the bot, enter the required information in `src/resources/application.properties`. Then run the following command in a terminal (make sure to `cd` into this directory/folder):

```zsh
# Unix
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

To run the tests, use:

```zsh
# Unix
./mvnw test

# Windows
mvn.cmd test
```

## Using the bot

To use the bot, you first install the GitHub app on the target repository. Once this is done, 
you also need to add a `perfbot.yaml` file in the root directory, with the following structure:

```
language: java
buildTool: maven
```

At the moment, the only supported project type for this bot is Java with the build tool Maven.

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
