kubectl apply -f chroma-pv.yml
kubectl apply -f chroma-pv.yml
kubectl apply -f chromadb.yml
kubectl apply -f redis-pvc.yml
kubectl apply -f redis-pvc.yml
kubectl apply -f redis.yml
kubectl apply -f rabbitmq-pvc.yml
kubectl apply -f rabbitmq-pv.yml
kubectl apply -f rabbitmq.yml
kubectl apply -f postgres-pvc.yml
kubectl apply -f postgres-pv.yml
kubectl apply -f postgres.yml
kubectl apply -f rag.yml
kubectl apply -f indexing.yml
kubectl apply -f auth-configMap.yml
kubectl apply -f auth-secret.yml
kubectl apply -f auth.yml
kubectl apply -f spring.yml
kubectl get pods -w