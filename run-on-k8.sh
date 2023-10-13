

./bin/spark-submit \
  --master k8s://https://127.0.0.1:6443 \
  --deploy-mode cluster \
  --name amicusearch-etl \
  --class com.amicusearch.etl.Main \
  --conf spark.driver.memory=10g \
  --conf spark.driver.cores=3 \
  --conf spark.executor.instances=3 \
  --conf spark.executor.cores=3 \
  --conf spark.executor.memory=10g \
  --conf spark.kubernetes.container.image=spark:3.5.0 \
  local:///home/warren/projects/amicusearch-etl/AmicusearchETL.jar
  --mode