echo "What is your DockerHub username?"
read username
cd performancebot
sudo mvn clean install -DskipTests
docker build -t $username/perfbot:latest .
sudo docker image push $username/perfbot:latest
cd ../benchmarkworker
sudo mvn clean install -DskipTests
docker build -t $username/benchmark-worker:latest .
sudo docker image push $username/benchmark-worker:latest
