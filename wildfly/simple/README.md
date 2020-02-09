## EXTERNAL IP - was having problems getting some tutorials to expose an ip - here is a simple demo that works:
https://kubernetes.io/docs/tutorials/stateless-application/expose-external-ip-address/
kubectl apply -f https://k8s.io/examples/service/load-balancer-example.yaml


```
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app.kubernetes.io/name: load-balancer-example
  name: hello-world
spec:
  replicas: 5
  selector:
    matchLabels:
      app.kubernetes.io/name: load-balancer-example
  template:
    metadata:
      labels:
        app.kubernetes.io/name: load-balancer-example
    spec:
      containers:
      - image: gcr.io/google-samples/node-hello:1.0
        name: hello-world
        ports:
        - containerPort: 8080
```

kubectl get all

```
NAME                              READY   STATUS              RESTARTS   AGE
pod/hello-world-f9b447754-4jsq2   0/1     ContainerCreating   0          13s
pod/hello-world-f9b447754-fj5qx   0/1     ContainerCreating   0          13s
pod/hello-world-f9b447754-h9mxh   0/1     ContainerCreating   0          13s
pod/hello-world-f9b447754-k4p6c   0/1     ContainerCreating   0          13s
pod/hello-world-f9b447754-mx29j   0/1     ContainerCreating   0          13s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   46h

NAME                          READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/hello-world   0/5     5            0           13s

NAME                                    DESIRED   CURRENT   READY   AGE
replicaset.apps/hello-world-f9b447754   5         5         0       13s
```

## Create a Service object that exposes the deployment:
kubectl expose deployment hello-world --type=LoadBalancer --name=my-service
kubectl get service/my-service

```
NAME         TYPE           CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
my-service   LoadBalancer   10.96.79.83   <pending>     8080:32512/TCP   21m
```

http://10.96.79.83:8080/
Hello Kubernetes!

minikube tunnel      # in a tab - loops output but pending is replaced with same IP!
 get service/my-service

```
NAME         TYPE           CLUSTER-IP    EXTERNAL-IP   PORT(S)          AGE
my-service   LoadBalancer   10.96.79.83   10.96.79.83   8080:32512/TCP   24m
```
