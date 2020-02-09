## Wildfly example running on local openshift 4

```
oc create -f https://raw.githubusercontent.com/wildfly/wildfly-s2i/wf-18.0/imagestreams/wildfly-centos7.json


imagestream.image.openshift.io/wildfly created


odo create wildfly --git https://github.com/openshift/openshift-jee-sample.git

Warning: wildfly is not fully supported by odo, and it is not guaranteed to work
 ✓  Validating component [12ms]

Please use `odo push` command to create the component with source deployed
```

#This created a wildflower app but no route
In the gui login as admin and create a route named banana - exposes:
	http://banana-aaa.apps-crc.testing/
	which is :
	
Welcome to your JavaEE application on OpenShift
How to use this example application
For instructions on how to use this application with OpenShift, start by reading the Developer Guide.







## Export the project objects to a .yaml or .json file.

To export the project objects into a project.yaml file:

```
$ oc get -o yaml --export all > project.yaml
```

To export the project objects into a project.json file:

```
$ oc get -o json --export all > project.json
```
Export the project’s role bindings, secrets, service accounts, and persistent volume claims:

```
$ for object in rolebindings serviceaccounts secrets imagestreamtags cm egressnetworkpolicies rolebindingrestrictions limitranges resourcequotas pvc templates cronjobs statefulsets hpa deployments replicasets poddisruptionbudget endpoints
do
  oc get -o yaml --export $object > $object.yaml
done
```
To list all the namespaced objects:

```
$ oc api-resources --namespaced=true -o name
```

## Restore from above 

```
$ oc new-project <projectname>
$ oc create -f project.yaml
$ oc create -f secret.yaml
$ oc create -f serviceaccount.yaml
$ oc create -f pvc.yaml
$ oc create -f rolebindings.yaml
```

