# Getting Started with PerformanceBot

This document covers the steps required for running the worker and webserver.

<!-- Add shortcuts to webhook and that stuff -->

## 1. Prerequisites

1. A local installation of [`mongodb`](https://www.mongodb.com/docs/manual/administration/install-community/). Follow the steps for your operating system.
2. Containerd, the install steps vary depending on your OS, but simply follow [`getting-started.md`](https://github.com/containerd/containerd/blob/main/docs/getting-started.md) from their GitHub repo.
3. [Docker](https://docs.docker.com/get-docker/).
4. Maven.
5. [ngrok](https://ngrok.com/download), why you need this will be discussed below.

## 2. PerformanceBot installation

To run the performancebot on GitHub repositories, you have to create a GitHub app to act on behalf of it when communicating with the GitHub API.

### Creating the App

If these steps are unclear, you can also follow this [tutorial](https://docs.github.com/en/apps/creating-github-apps/creating-github-apps/creating-a-github-app). It has more steps, and the ones below are based on the bare minimum requirements to create the app.

1. On GitHub, go to `Settings / Developer Settings` and click on [GitHub Apps](https://github.com/settings/apps), followed by pressing the `New GitHub App` button to the right.
2. Enter a `GitHub App name`, set the Homepage URL and Webhook URL to `https://github.com/icetlab/performancebot` for now.
3. Under `permissions`: check the following boxes under **repository permissions**:
     - [x] **Issues**: Access: Read and Write
     - [x] **Pull requests**: Access: Read and Write
4. Under `Where can this GitHub App be installed?`, check `Only on this account`.

### Configuring the App

This step covers generating private keys to sign access token requests, as well as setting the right webhook url.

### The App ID

1. Go to your bot settings and copy the App ID under the About section.
2. Go to `src/main/resources/application.properties` and replace `<your app id>` with your app ID.

### Generating a private key

When you have finished creating your app, it is time to generate a private key in order to consume the GitHub API.

1. On GitHub, go to `Settings / Developer Settings` and select `Edit` on your GitHub App.
2. Scroll down to `Private keys`, and press generate.
3. You will get a file looking like `<username.date>.private-key.pem` in your downloads folder. 
4. Move it to this repo, and *do not* push or commit it, it contains sensitive information! 
   - If you happen to do it, simply remove it and go back to your account settings and press `Delete`.
5. Go to `src/main/resources/application.properties` and replace `dummy.pem` with what your file is called.

### The webhook URL

Time to use ngrok. You will perform this step many times (every time you end your ngrok session), but it's easy, don't worry.

1. Open your GitHub App's settings on GitHub, and scroll down to `Webhook`.
2. In your favorite terminal emulator/powershell/command prompt, and enter the following. Don't close the window!
```cmd
ngrok http 7000
```
3. Note the output, it should look something like below. Press the Web Interface link and open it in your browser.
```cmd
user@host $ ngrok http 7000
ngrok                                (Ctl+C to quit)

ngrok does not support a dynamic, color terminal UI on solaris.
Access the web interface for connection and tunnel status.

Version                       3.1.0
Region                        Auto (lowest latency) (auto)
Web Interface                 http://127.0.0.1:4040
```
1. When in the interface, note the tunnel URL you are given, should look something like this: `https://b9b9-33-22-11-000.eu.ngrok.io`. 
2. Copy it to your clipboard.
3. Paste it in the `Webhook URL` field and **ADD** `/payload` to the end of it. It should look like this: `https://b9b9-33-22-11-000.eu.ngrok.io/payload`.
4. In `src/main/resources/application.properties`, add `server.port=7000`. It has to be the same as the one used in the ngrok command! This is because your computer is listening on that port and since ngrok is pointing the tunnel to that port, we need it to receive updates as webhooks from GitHub.

## 2. BenchmarkWorker 

Remember to start the worker by running it in your IDE or from the command line so you can run jobs. Nothing else to be done here, don't close the window once started.
