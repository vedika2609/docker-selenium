FROM maven:3.6.3-jdk-8

WORKDIR /usr/src/mymaven

COPY . .

RUN apt-get update && apt-get install jq -y
RUN chmod +x wait-for-grid.sh