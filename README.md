Confluent Cloud Apache Flink® Table API - Fraud Detection Demo
===

To demonstrate to bootup a [Confluent Platform](https://docs.confluent.io/platform/current/installation/docker/image-reference.html) cluster to work with [afedulov/fraud-detection-demo](https://github.com/afedulov/fraud-detection-demo)



# 📒 Pre-requisites
* [fraud-detection-demo](https://github.com/krakenninja/fraud-detection-demo)
* [Docker](https://www.docker.com/products/docker-desktop/)
* [Maven](https://maven.apache.org/download.cgi)
* [Java](https://www.java.com/en/)



# 🧭 Guides
Follow the guide below to bootup [Confluent Platform](https://docs.confluent.io/platform/current/installation/docker/image-reference.html) alongside with [fraud-detection-demo](https://github.com/krakenninja/fraud-detection-demo)

## Setup Confluent Cloud
You **must setup** [Confluent Cloud Flink Table API](https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html). Access to your [Confluent Cloud](https://confluent.cloud/) and perform the following steps

### 1️⃣ **Create [Cloud Environment](https://confluent.cloud/environments)** environment named e.g `fraud-detection-demo`

![Add Cloud Environment](resources/confluent-cloud-setup-01.png)

### 2️⃣ **Create cluster** for Cloud Environment `fraud-detection-demo` cluster named e.g `cluster_fraud_detection_demo_0`

![Create Cluster : Type](resources/confluent-cloud-setup-02.png)

![Create Cluster : Region/Zone](resources/confluent-cloud-setup-03.png)

![Create Cluster : Review & Launch](resources/confluent-cloud-setup-04.png)

![Create Cluster : Overview](resources/confluent-cloud-setup-05.png)

### 3️⃣ **Generate API Key** for Cloud Environment `fraud-detection-demo`

![Generate API Keys : Create Key](resources/confluent-cloud-setup-06.png)

![Generate API Keys : Create Key](resources/confluent-cloud-setup-07.png)

![Generate API Keys : Create Key](resources/confluent-cloud-setup-08.png)

![Generate API Keys : Create Key](resources/confluent-cloud-setup-09.png)

![Generate API Keys : Create Key](resources/confluent-cloud-setup-10.png)

### 4️⃣ **Create Flink Compute Pool** for Cloud Environment `fraud-detection-demo`

![Create Flink Compute Pool : Create Compute Pool](resources/confluent-cloud-setup-11.png)

![Create Flink Compute Pool : Region](resources/confluent-cloud-setup-12.png)

![Create Flink Compute Pool : Review & Create](resources/confluent-cloud-setup-13.png)

### 5️⃣ **Create Flink API Key** for Cloud Environment `fraud-detection-demo`

![Create Flink API Key : Create](resources/confluent-cloud-setup-14.png)

![Create Flink API Key : Account](resources/confluent-cloud-setup-15.png)

![Create Flink API Key : Region](resources/confluent-cloud-setup-16.png)

![Create Flink API Key : Details](resources/confluent-cloud-setup-17.png)

![Create Flink API Key : Download & Complete](resources/confluent-cloud-setup-18.png)

![Create Flink API Key : Finished](resources/confluent-cloud-setup-19.png)

### 6️⃣ **Create Kafka Topic** for Cloud Environment `fraud-detection-demo` cluster

![Create Kafka Topic : Cluster Selection](resources/confluent-cloud-setup-21.png)

![Create Kafka Topic : Create](resources/confluent-cloud-setup-22.png)

![Create Kafka Topic : Create with Defaults](resources/confluent-cloud-setup-23.png)

## Test Confluent Cloud Setup
Once you had done the **Setup Confluent Cloud** mentioned above, you can run the following JUnit Test to test your setup is proper

* Make sure your OS profile (i.e. `~/.zshrc`) contains the following exported environment variable(s)

```sh
export CLOUD_PROVIDER="xxx"
export CLOUD_REGION="xxx"
export FLINK_API_KEY="xxx"
export FLINK_API_SECRET="xxx"
export ORG_ID="xxx"
export ENV_ID="xxx"
export COMPUTE_POOL_ID="xxx"
```

* Next, open your terminal and run the command below : 

```sh
cd confluent-flink-fraud-detection-demo/fraud-detection-job
mvn test -Dtest=com.github.krakenninja.demo.confluent.cloud.HelloTableApiJUnitTest
```

* You should be able to see the following result in your terminal 

![JUnit Test : Confluent Cloud Setup](resources/confluent-cloud-setup-20.png)



# 📚 References
* [Mount Docker External Volumes in Confluent Platform](https://docs.confluent.io/platform/current/installation/docker/operations/external-volumes.html#external-volumes)
* [Install Confluent Platform Using Docker](https://docs.confluent.io/platform/current/installation/docker/installation.html)
* [Java Table API Quick Start on Confluent Cloud for Apache Flink](https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html)