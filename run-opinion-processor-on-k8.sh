#!/bin/zsh
# these files need to be in the spark classpath in order for spark-submit to be able to upload/download the fat jar from s3

declare -a jars=(
  "bcprov-jdk15on-1.70"
  "bcpkix-jdk15on-1.70"
  "hadoop-aws-3.3.4"
  "aws-java-sdk-bundle-1.11.901"
  "postgresql-42.6.0"
)

for jar in "${jars[@]}"; do
  if [ ! -f "/opt/spark/jars/$jar.jar" ]; then
    wget "https://repo1.maven.org/maven2/$(echo $jar | sed 's/-/\//g')/$jar.jar" -O "/opt/spark/jars/$jar.jar"
  else
    echo "$jar.jar already exists"
  fi
done

HOST_MOUNT=/home/warren/storage-mount/amicusearch-v2-data/

nohup spark-submit \
  --master k8s://https://127.0.0.1:6443 \
  --deploy-mode cluster \
  --name amicusearch-etl \
  --class com.amicusearch.etl.Main \
  --conf spark.driver.memory=15g \
  --conf spark.driver.cores=3 \
  --conf spark.executor.instances=3 \
  --conf spark.executor.cores=6 \
  --conf spark.executor.memory=30g \
  --conf spark.kubernetes.driver.volumes.hostPath.inputvol.mount.path=/tmp/fsmount/ \
  --conf spark.kubernetes.driver.volumes.hostPath.inputvol.options.path=$HOST_MOUNT \
  --conf spark.kubernetes.executor.volumes.hostPath.inputvol.mount.path=/tmp/fsmount/ \
  --conf spark.kubernetes.executor.volumes.hostPath.inputvol.options.path=$HOST_MOUNT \
  --conf spark.kubernetes.driver.volumes.persistentVolumeClaim.outputvol.options.claimName=spark-pvc \
  --conf spark.kubernetes.driver.volumes.persistentVolumeClaim.outputvol.mount.path=/tmp/results \
  --conf spark.kubernetes.executor.volumes.persistentVolumeClaim.outputvol.options.claimName=spark-pvc \
  --conf spark.kubernetes.executor.volumes.persistentVolumeClaim.outputvol.mount.path=/tmp/results \
  --conf spark.kubernetes.container.image=warrenronsiek/spark-aws-k8:1.0.0 \
  --conf spark.kubernetes.authenticate.serviceAccountName=default \
  --conf spark.kubernetes.file.upload.path=s3a://amicusearch/etl-k8s/ \
  --conf spark.hadoop.fs.s3a.access.key=$AWS_ACCESS_KEY_ID \
  --conf spark.hadoop.fs.s3a.secret.key=$AWS_SECRET_ACCESS_KEY \
  s3a://amicusearch/etl/AmicusearchETL.jar \
  --mode CLOpinionProcessor --env dev --states FL --includeFederal true > spark.log 2>&1 &

# to forward the port: `ssh -i ~/.ssh/id_ed25519 -N -L 4040:localhost:30440 warren@192.168.1.25`
