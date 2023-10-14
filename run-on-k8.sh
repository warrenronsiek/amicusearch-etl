
# these files need to be in the spark classpath in order for spark-submit to be able to upload/download the fat jar from s3
wget https://repo1.maven.org/maven2/org/bouncycastle/bcprov-jdk15on/1.70/bcprov-jdk15on-1.70.jar && mv bcprov-jdk15on-1.70.jar /opt/spark/jars/
wget https://repo1.maven.org/maven2/org/bouncycastle/bcpkix-jdk15on/1.70/bcpkix-jdk15on-1.70.jar && mv bcpkix-jdk15on-1.70.jar /opt/spark/jars/
wget https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.4/hadoop-aws-3.3.4.jar && mv hadoop-aws-3.3.4.jar /opt/spark/jars/
wget https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.11.901/aws-java-sdk-bundle-1.11.901.jar && mv aws-java-sdk-bundle-1.11.901.jar /opt/spark/jars/

nohup spark-submit \
  --master k8s://https://127.0.0.1:6443 \
  --deploy-mode cluster \
  --name amicusearch-etl \
  --jars ~/projects/amicusearch-etl/AmicusearchETL.jar,/opt/spark/jars/bcprov-jdk15on-1.70.jar,/opt/spark/jars/bcpkix-jdk15on-1.70.jar,/opt/spark/jars/hadoop-aws-3.3.4.jar,/opt/spark/jars/aws-java-sdk-bundle-1.11.901.jar \
  --class com.amicusearch.etl.Main \
  --conf spark.driver.memory=10g \
  --conf spark.driver.cores=3 \
  --conf spark.executor.instances=3 \
  --conf spark.executor.cores=3 \
  --conf spark.executor.memory=10g \
  --conf spark.kubernetes.container.image=warrenronsiek/spark-aws-k8:1.0.0 \
  --conf spark.kubernetes.authenticate.serviceAccountName=default \
  --conf spark.kubernetes.file.upload.path=s3a://amicusearch/etl-k8s/ \
  --conf spark.hadoop.fs.s3a.access.key=$AWS_ACCESS_KEY_ID \
  --conf spark.hadoop.fs.s3a.secret.key=$AWS_SECRET_ACCESS_KEY \
  local:///opt/spark/work-dir/AmicusearchETL.jar \
  --mode courtlistener --env local --states FL,NY --includeFederal true > spark.log 2>&1 &
