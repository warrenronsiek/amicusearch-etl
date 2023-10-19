#!/bin/zsh
# these files need to be in the spark classpath in order for spark-submit to be able to upload/download the fat jar from s3

if [ ! -f "/opt/spark/jars/bcprov-jdk15on-1.70.jar" ]; then
  wget https://repo1.maven.org/maven2/org/bouncycastle/bcprov-jdk15on/1.70/bcprov-jdk15on-1.70.jar && mv bcprov-jdk15on-1.70.jar /opt/spark/jars/
else
  echo "bcprov-jdk15on-1.70.jar already exists"
fi
if [ ! -f "/opt/spark/jars/bcpkix-jdk15on-1.70.jar" ]; then
  wget https://repo1.maven.org/maven2/org/bouncycastle/bcpkix-jdk15on/1.70/bcpkix-jdk15on-1.70.jar && mv bcpkix-jdk15on-1.70.jar /opt/spark/jars/
else
  echo "bcpkix-jdk15on-1.70.jar already exists"
fi
if [ ! -f "/opt/spark/jars/hadoop-aws-3.3.4.jar" ]; then
  wget https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.4/hadoop-aws-3.3.4.jar && mv hadoop-aws-3.3.4.jar /opt/spark/jars/
else
  echo "hadoop-aws-3.3.4.jar already exists"
fi
if [ ! -f "/opt/spark/jars/aws-java-sdk-bundle-1.11.901.jar" ]; then
  wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.11.901/aws-java-sdk-bundle-1.11.901.jar && mv aws-java-sdk-bundle-1.11.901.jar /opt/spark/jars/
else
  echo "aws-java-sdk-bundle-1.11.901.jar already exists"
fi

HOST_MOUNT=/home/warren/storage-mount/amicusearch-v2-data/

nohup spark-submit \
  --master k8s://https://127.0.0.1:6443 \
  --deploy-mode cluster \
  --name amicusearch-etl \
  --class com.amicusearch.etl.Main \
  --conf spark.driver.memory=20g \
  --conf spark.driver.cores=3 \
  --conf spark.executor.instances=3 \
  --conf spark.executor.cores=5 \
  --conf spark.executor.memory=25g \
  --conf spark.kubernetes.driver.volumes.hostPath.inputvol.mount.path=/tmp/fsmount/ \
  --conf spark.kubernetes.driver.volumes.hostPath.inputvol.options.path=$HOST_MOUNT \
  --conf spark.kubernetes.executor.volumes.hostPath.inputvol.mount.path=/tmp/fsmount/ \
  --conf spark.kubernetes.executor.volumes.hostPath.inputvol.options.path=$HOST_MOUNT \
  --conf spark.kubernetes.driver.volumes.persistentVolumeClaim.outputvol.options.claimName=spark-pvc \
  --conf spark.kubernetes.driver.volumes.persistentVolumeClaim.outputvol.mount.path=/tmp/results \
  --conf spark.kubernetes.executor.volumes.persistentVolumeClaim.outputvol.options.claimName=spark-pvc \
  --conf spark.kubernetes.executor.volumes.persistentVolumeClaim.outputvol.mount.path=/tmp/results \
  --conf spark.kubernetes.driverEnv.OPENAI_API_KEY=$OPENAI_API_KEY \
  --conf spark.kubernetes.executorEnv.OPENAI_API_KEY=$OPENAI_API_KEY \
  --conf spark.kubernetes.container.image=warrenronsiek/spark-aws-k8:1.0.0 \
  --conf spark.kubernetes.authenticate.serviceAccountName=default \
  --conf spark.kubernetes.file.upload.path=s3a://amicusearch/etl-k8s/ \
  --conf spark.hadoop.fs.s3a.access.key=$AWS_ACCESS_KEY_ID \
  --conf spark.hadoop.fs.s3a.secret.key=$AWS_SECRET_ACCESS_KEY \
  s3a://amicusearch/etl/AmicusearchETL.jar \
  --mode courtlistener --env dev --states FL,NY --includeFederal true > spark.log 2>&1 &

# to forward the port: `ssh -i ~/.ssh/id_ed25519 -N -L 4040:localhost:30440 warren@192.168.1.25`
