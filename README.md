# RAGScribe

Kubernetes Deployment

## Commands
### Go to k8s directory
```
cd k8s
```
### Start Minikube cluster
```
minikube start
```
### Create k8s deplyoment
```
chmod +x start-commands.sh
```
OR
```
kubectl apply -f .
```
### To check pod status
```
kubectl get pods [-w] [-o wide]
```
### To check service status
```
kubectl get svc [-w] [-o wide]
```
### To get pod logs
```
kubectl logs <pod-name>
```
### To expose service on NodePort
For RAG-service and Spring-service
```
minikube service <service-name> --url
```
### To SSH into pod
```
kubectl exec --stdin --tty <pod-name> -- /bin/bash
```
