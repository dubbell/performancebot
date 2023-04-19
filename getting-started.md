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

### Installing the App

Once the Bot is created, go to the settings of it, and press `Install App` in the menu on the left and allow it to access all your repositories.

### Configuring the App

This step covers generating private keys to sign access token requests, as well as setting the right webhook url.

### The App ID

1. Go to your bot settings and copy the App ID under the About section.
2. Go to `performancebot/src/main/resources/application.properties` and replace `<your app id>` with your app ID.

### Generating a private key

When you have finished creating your app, it is time to generate a private key in order to consume the GitHub API.

1. On GitHub, go to `Settings / Developer Settings` and select `Edit` on your GitHub App.
2. Scroll down to `Private keys`, and press generate.
3. You will get a file looking like `<username.date>.private-key.pem` in your downloads folder. 
4. Move it to this to the `performancebot/src/main/resources` directory, and *do not* push or commit it, it contains sensitive information! 
   - If you happen to do it, simply remove it and go back to your account settings and press `Delete`.
5. Go to `performancebot/src/main/resources/application.properties` and replace `dummy.pem` with what your file is called.

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

### Connecting to MongoDB

For the PerformanceBot to connect to the MongoDB instance running on your computer, 
the following entries needs to be added to `performancebot/src/main/resources/application.properties`:
- `spring.data.mongodb.database=perfbot`
- `spring.data.mongodb.host=<mongodb-ip-address>`
- `spring.data.mongodb.port=27017 # default port`

### Deploying to Kubernetes
For the application to be deployed to Kubernetes, first images have to be created of the 
PerformanceBot and the BenchmarkWorker and uploaded to DockerHub. 
If you have created a DockerHub account, then the next step is to edit `k8s-perfbot.yaml` 
and change the image URLS in the `perfbot` and `benchmark-worker` deployments to `docker.io/<your-dockerhub-username>/perfbot:latest` 
and `docker.io/<your-dockerhub-username>/benchmark-worker:latest`. Also make sure that the port number in the `perfbot-ingress` is set to the same port
specified in `application.properties`.

Once this is done, you can then run the `build_images.sh` shell script which will ask for your
DockerHub username, after which it will compile the `performancebot` and 
`benchmarkworker` modules, build images, and finally push the images to 
your DockerHub account.

The final step is to create a Kubernetes cluster (you can use Minikube for testing) and install kubectl, which is a 
command-line tool used to execute this command: `kubectl apply -f k8s-perfbot.yaml`. This
command will create the pods, services, etc., specified in the `k8s-perfbot.yaml`.
To change the number of `benchmark-worker` or `perfbot` pods running in the cluster,
you can simply change the value `spec.replicas` in their deployments in `k8s-perfbot.yaml`.

### Using it

To use the Performance Bot, you first need to install the GitHub app in your 
repository. Once this is done, you need to add a file called `perfbot.yaml` in
the root of your repository. This file will tell the bot how to run the benchmarks.

The only required configuration that needs to be in `perfbot.yaml` is the 
`buildTool` of the project, which can be either Maven or Gradle. Other than that, there are 
an additional three optional configurations: `options`, `buildTasks` and `jmhJar`.
- `options`
  - Should look like this: `[regexp*] [additional JMH options]`, where the
  regular expressions specify which benchmarks to run, and the additional options can be any
  configuration allowed by the [JMH CLI](https://github.com/guozheng/jmh-tutorial/blob/master/README.md).
  Is empty by default.
- `buildTasks`
  - A list of tasks that needs to be executed to compile the project into a 
  jar file which can execute the benchmarks. Each item should contain a `path`, which is the path to the `pom.xml`file for Maven 
  projects or the `build.gradle` file for Gradle projects. It should also contain a list of
  `tasks`, where each item is a string, representing either a Maven goal or a Gradle task
  to be executed. By default this is set to the root of the repository, with either the Gradle task `jmhJar` or the Maven goal `package`. 
- `jmhJar`
  - The path to the JMH jar file which should be created after all the tasks in 
  `buildTasks` are completed. By default this is set to `target/benchmarks.jar` for Maven projects
  and `build/libs/<file-name-containing-jmh>.jar` for Gradle projects.

Example perfbot.yaml file:
```
buildTool: maven
options: "JMHTestClass.test1 AnotherJMHTestClass.test3 -i 1"
buildTasks:
- path: pom.xml
  tasks:
  - package
- path: jmh-tests/pom.xml
  tasks:
  - package
jmhJar: jmh-tests/target/benchmarks.jar
```

Once this file is added to a branch, then the benchmarks should be run as soon as a 
pull request is opened with the title `[performancebot]`. And then, once the benchmarks
have been executed, the results will be sent back to the repository as a GitHub issue.

>**Tip**: if you don't want to spam new PRs all the time, use the replay button to resend a payload in ngrok's web interface! 

