## Not first use of Openshift 4 (local)

Clean up by stopping and deleting vm, to recreate via ```crc start``` below.


```
crc stop
crc status
crc delete 
```

## OpenShift cluster and PostgreSQL Database Server

https://wildfly.org/news/2019/11/11/WildFly-s2i-openshift-Datasource-configuration/

```
$ crc start
...
INFO Starting OpenShift cluster ... [waiting 3m]
INFO
INFO To access the cluster, first set up your environment by following 'crc oc-env' instructions
INFO Then you can access it by running 'oc login -u developer -p developer https://api.crc.testing:6443'
INFO To login as an admin, username is 'kubeadmin' and password is wyozw-5ywAy-5yoap-7rj8q
INFO
INFO You can now run 'crc console' and use these credentials to access the OpenShift web console
The OpenShift cluster is running

$ oc login -u kubeadmin -p wyozw-5ywAy-5yoap-7rj8q https://api.crc.testing:6443

Login successful.

You have access to 51 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "default".

$ oc import-image wildfly --confirm \--from quay.io/wildfly/wildfly-centos7 --insecure -n openshift

imagestream.image.openshift.io/wildfly imported

$ oc get is -n openshift | grep wildfly

NAME      IMAGE REPOSITORY                                                          TAGS     UPDATED
wildfly   default-route-openshift-image-registry.apps-crc.testing/default/wildfly   latest   8 seconds ago
```

```
oc new-app --name database-server \
     --env POSTGRESQL_USER=postgre \
     --env POSTGRESQL_PASSWORD=admin \
     --env POSTGRESQL_DATABASE=demodb \
     postgresql
...   
Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose svc/database-server'
    Run 'oc status' to view your app.
```

We can get the status of the pod created with the command oc get pods:

```
$ oc get pods
NAME                       READY   STATUS      RESTARTS   AGE
database-server-1-5l6fd    1/1     Running     0          44s
database-server-1-deploy   0/1     Completed   0          57s
```

We can verify the database was created opening a remote shell connection on the running pod:

```
$ oc rsh database-server-1-5l6fd
sh-4.2$ psql demodb
psql (10.6)
Type "help" for help.

\l
                                 List of databases
   Name    |  Owner   | Encoding |  Collate   |   Ctype    |   Access privileges   
-----------+----------+----------+------------+------------+-----------------------
 demodb    | postgre  | UTF8     | en_US.utf8 | en_US.utf8 | 
 postgres  | postgres | UTF8     | en_US.utf8 | en_US.utf8 | 
 template0 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
           |          |          |            |            | postgres=CTc/postgres
 template1 | postgres | UTF8     | en_US.utf8 | en_US.utf8 | =c/postgres          +
           |          |          |            |            | postgres=CTc/postgres
(4 rows)



\dt
        List of relations
 Schema | Name  | Type  |  Owner  
--------+-------+-------+---------
 public | tasks | table | postgre
(1 row)


select * from tasks;
 id |       title        
----+--------------------
  1 | This is the task-1
  2 | This is the task-2
  3 | This is the task-3
  4 | This is the task-4
  5 | This is the task-5
(5 rows)


demodb=# \q
sh-4.2$ exit
```



## Configuring the WildFly Data Source

Now is time to create our application container image that includes the WildFly server and our demo application. The oc new-app is also used for such purposes. Unlike the previous configuration of the database server where we were building a new container from an existing image stream, now we are going to create a new image combining the WildFly image stream with an external GitHub application source code.

OpenShift takes care of the details and creates a final image containing the server and the application. Internally, it uses the Source-To-Image (S2I) tool. During this process, the WildFly server is provisioned by Galleon, and our demo JAX-RS application is built and copied into the $WILDFLY_HOME/deployments folder.

https://github.com/openshift/source-to-image

We do not need the full server to run our example, for example, we do not need the ejb3, remoting or messaging subsystems. We can specify a set of Galleon layers by using the GALLEON_PROVISION_LAYERS environment variable to reduce the server footprint. This environment variable contains a comma-separated list of layer names you want to use to provision your server during the S2I phase. It is important to understand that the server provisioning is done in OpenShift by a Build Config resource, so we need to make this variable available as a build environment variable. Notice that these details usually are hidden to you when you are using a template or an Operator.


Check this post to learn more about OpenShift and Galleon layers.
https://wildfly.org/news/2019/03/01/Galleon_Openshift/


For our demo example on OpenShift, we instruct Galleon to provision our server with these two Galleon Layers: jaxrs-server and postgresql-datasource.

The jaxrs-server layer provisions the server with some features needed to run our example e.g. cdi, jaxrs, jpa, undertow, transactions, datasources. It belongs to the default Galleon pack which is used to provision the default WildFly server.

The postgresql-datasource layer comes from WildFly Datasources Galleon Pack. This layer adds to the server the PostgreSQL drivers and specific PostgreSQL data source configuration. It allows us to configure the PostgreSQL data source by using the following variables:

POSTGRESQL_DATABASE

POSTGRESQL_SERVICE_PORT

POSTGRESQL_SERVICE_HOST

POSTGRESQL_PASSWORD

POSTGRESQL_USER

Let us create our WildFly container then configuring the data source to connect to our PostgreSQL server running in a different pod:

```
$ oc new-app --name wildfly-app \
     https://github.com/yersan/jaxrs-postgresql-demo.git \
     --image-stream=wildfly \
     --env POSTGRESQL_SERVICE_HOST=database-server \
     --env POSTGRESQL_SERVICE_PORT=5432 \
     --env POSTGRESQL_USER=postgre \
     --env POSTGRESQL_PASSWORD=admin \
     --env POSTGRESQL_DATABASE=demodb \
     --env POSTGRESQL_DATASOURCE=PostgreSQLDS \
     --build-env GALLEON_PROVISION_LAYERS=jaxrs-server,postgresql-datasource
--> Found image 38b29f9 (3 weeks old) in image stream "openshift/wildfly" under tag "latest" for "wildfly"

    WildFly 18.0.0.Final
    --------------------
    Platform for building and running JEE applications on WildFly 18.0.0.Final

    Tags: builder, wildfly, wildfly18

    * The source repository appears to match: jee
    * A source build using source code from https://github.com/yersan/jaxrs-postgresql-demo.git will be created
      * The resulting image will be pushed to image stream tag "wildfly-app:latest"
      * Use 'oc start-build' to trigger a new build
    * This image will be deployed in deployment config "wildfly-app"
    * Ports 8080/tcp, 8778/tcp will be load balanced by service "wildfly-app"
      * Other containers can access this service through the hostname "wildfly-app"

--> Creating resources ...
    imagestream.image.openshift.io "wildfly-app" created
    buildconfig.build.openshift.io "wildfly-app" created
    deploymentconfig.apps.openshift.io "wildfly-app" created
    service "wildfly-app" created
--> Success
    Build scheduled, use 'oc logs -f bc/wildfly-app' to track its progress.
    Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose svc/wildfly-app'
    Run 'oc status' to view your app.

$ oc get pods
NAME                       READY   STATUS      RESTARTS   AGE
database-server-1-5l6fd    1/1     Running     0          10m
database-server-1-deploy   0/1     Completed   0          10m
wildfly-app-1-build        0/1     Completed   0          3m50s
wildfly-app-1-deploy       0/1     Completed   0          55s
wildfly-app-1-sdk2m        1/1     Running     0          46s

$ oc expose svc/wildfly-app --name wildfly-app
route.route.openshift.io/wildfly-app exposed
```

The new-app command creates three additional pods in the OpenShift cluster; one build config (-build suffix, completed), one deploy config (-deploy suffix, completed) and our running application pod.

Remember, the build config is the resource that creates the container image using the S2I tool, builds your application and provisions the server using Galleon. The deployment config is the resource that starts the new container image created by the build config.


## Review Pod Logs

You can review the pod logs issuing the following command ```oc log pod/{pod_name}```

## After build and deploy - review app working

Now we can verify our application is working. We exposed the application to the outside world using oc expose. If we want to access to our container via the web, we need to know its host name. We can get this value by inspecting the routes/wildfly-app resource. Once we know the host name, we can use curl to fetch some information from our application:

```
$ oc get routes/wildfly-app --template={{.spec.host}}
wildfly-app-wildfly-demo.apps-crc.testing

$ curl http://wildfly-app-wildfly-demo.apps-crc.testing/jaxrs-postgresql-demo/api/tasks
[{"id":1,"title":"This is the task-1"},{"id":2,"title":"This is the task-2"},{"id":3,"title":"This is the task-3"},{"id":4,"title":"This is the task-4"},{"id":5,"title":"This is the task-5"}]
```

Now, let us take a look at our current datasources subsystem configuration to see how it was configured. We can open a remote session on our WildFly running pod and examine the standalone.xml file:

```
$ oc rsh wildfly-app-1-sdk2m
sh-4.2$ cat /opt/wildfly/standalone/configuration/standalone.xml
```

The datasources subsystem configuration is the following:

```
<subsystem xmlns="urn:jboss:domain:datasources:5.0">
    <datasources>
        <datasource jndi-name="java:jboss/datasources/${env.POSTGRESQL_DATASOURCE,env.OPENSHIFT_POSTGRESQL_DATASOURCE:PostgreSQLDS}" pool-name="PostgreSQLDS" enabled="true" use-java-context="true" use-ccm="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
            <connection-url>jdbc:postgresql://${env.POSTGRESQL_SERVICE_HOST, env.OPENSHIFT_POSTGRESQL_DB_HOST}:${env.POSTGRESQL_SERVICE_PORT, env.OPENSHIFT_POSTGRESQL_DB_PORT}/${env.POSTGRESQL_DATABASE, env.OPENSHIFT_POSTGRESQL_DB_NAME}</connection-url>
            <driver>postgresql</driver>
            <pool>
                <flush-strategy>IdleConnections</flush-strategy>
            </pool>
            <security>
                <user-name>${env.POSTGRESQL_USER, env.OPENSHIFT_POSTGRESQL_DB_USERNAME}</user-name>
                <password>${env.POSTGRESQL_PASSWORD, env.OPENSHIFT_POSTGRESQL_DB_PASSWORD}</password>
            </security>
            <validation>
                <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
                <background-validation>true</background-validation>
                <background-validation-millis>60000</background-validation-millis>
            </validation>
        </datasource>
        <drivers>
            <driver name="postgresql" module="org.postgresql.jdbc">
                <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
            </driver>
        </drivers>
    </datasources>
</subsystem>
```

As you can see in the configuration file, Galleon has prepared the data source subsystem to be configured by the WildFly Datasources Galleon Pack environment variables. You can also verify that a PostgreSQL driver is added as a JBoss module in the server:


```
sh-4.2$ ls /opt/wildfly/modules/org/postgresql/jdbc/main/
module.xml  postgresql-9.4.1211.jar
```

This sort of configuration done by using the WildFly Datasource Galleon Pack is simple and easy to use. However, it has some limitations; there are some attributes related to the datasource that cannot be configured, e.g. connection min/max pool size, flush-strategy, background-validation-millis. We cannot configure more than one datasource of the same type. In the following section, we explain how you can achieve this.

Before moving to the next part, let us remove all unused configurations:

```
$ oc delete all -l app=wildfly-app
pod "wildfly-app-1-84lh6" deleted
replicationcontroller "wildfly-app-1" deleted
service "wildfly-app" deleted
deploymentconfig.apps.openshift.io "wildfly-app" deleted
buildconfig.build.openshift.io "wildfly-app" deleted
build.build.openshift.io "wildfly-app-1" deleted
imagestream.image.openshift.io "wildfly-app" deleted
route.route.openshift.io "wildfly-app" deleted
```


## Configuring additional aspects of the datasource subsystem


If you need to configure more than one data source or you need to configure some attributes that are not available by the WildFly Datasources Galleon Pack, there is a generic datasources subsystem configuration by using environment variables. You can check the Datasources configuration where these variables are explained. In the next example, we make use of some environments to configure two different datasources specifying different max/min pool sizes.

One detail we need to take into account is we no longer need the PostgreSQL datasource configuration added by the WildFly Datasources Galleon Pack, since we are going to configure the data source using a different set of variables. But we still need the PostgreSQL driver added by the Galleon Pack. The solution is easy, just instead of specifying the postgresql-datasource layer, we will specify this the postgresql-driver layer which is the layer that brings in only the driver.

Again, using new-app, we configure the two data sources s specifying the different max/min pool sizes:

```
$ oc new-app --name wildfly-app \
           https://github.com/yersan/jaxrs-postgresql-demo.git \
           --image-stream= wildfly \
           --env DB_SERVICE_PREFIX_MAPPING="dbone-postgresql=DSONE,dbtwo-postgresql=DSTWO" \
           --env DSONE_JNDI="java:/jboss/datasources/PostgreSQLDS" \
           --env DSONE_USERNAME="postgre" \
           --env DSONE_PASSWORD="admin" \
           --env DSONE_DATABASE="demodb" \
           --env DSONE_DRIVER="postgresql" \
           --env DBONE_POSTGRESQL_SERVICE_HOST="database-server" \
           --env DBONE_POSTGRESQL_SERVICE_PORT=5432 \
           --env DSONE_MAX_POOL_SIZE=10 \
           --env DSONE_MIN_POOL_SIZE=5 \
           --env DSONE_NONXA=true \
           --env DSTWO_JNDI="java:/jboss/datasources/UnusedDS" \
           --env DSTWO_USERNAME="postgre" \
           --env DSTWO_PASSWORD="admin" \
           --env DSTWO_DATABASE="demodb" \
           --env DSTWO_DRIVER="postgresql" \
           --env DBTWO_POSTGRESQL_SERVICE_HOST="database-server" \
           --env DBTWO_POSTGRESQL_SERVICE_PORT=5432 \
           --env DSTWO_MAX_POOL_SIZE=5 \
           --env DSTWO_MIN_POOL_SIZE=2 \
           --build-env GALLEON_PROVISION_LAYERS=jaxrs-server,postgresql-driver
warning: --env no longer accepts comma-separated lists of values. "DB_SERVICE_PREFIX_MAPPING=dbone-postgresql=DSONE,dbtwo-postgresql=DSTWO" will be treated as a single key-value pair.
--> Found image 38b29f9 (3 weeks old) in image stream "openshift/wildfly" under tag "latest" for "wildfly"

    WildFly 18.0.0.Final
    --------------------
    Platform for building and running JEE applications on WildFly 18.0.0.Final

    Tags: builder, wildfly, wildfly18

    * The source repository appears to match: jee
    * A source build using source code from https://github.com/yersan/jaxrs-postgresql-demo.git will be created
      * The resulting image will be pushed to image stream tag "wildfly-app:latest"
      * Use 'oc start-build' to trigger a new build
    * This image will be deployed in deployment config "wildfly-app"
    * Ports 8080/tcp, 8778/tcp will be load balanced by service "wildfly-app"
      * Other containers can access this service through the hostname "wildfly-app"

--> Creating resources ...
    imagestream.image.openshift.io "wildfly-app" created
    buildconfig.build.openshift.io "wildfly-app" created
    deploymentconfig.apps.openshift.io "wildfly-app" created
    service "wildfly-app" created
--> Success
    Build scheduled, use 'oc logs -f bc/wildfly-app' to track its progress.
    Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
     'oc expose svc/wildfly-app'
    Run 'oc status' to view your app.
```

## TWO DBS EXPOSED

The DB_SERVICE_PREFIX_MAPPING specifies the list of data sources we are going to configure (dbone-postgresql and dbtwo-postgresql) and links them with a variable prefix (DSONE and DSTWO). This mechanism allows us to create multiple datasources by using a variable prefix name identifying the variables that configure each data source. Explore the Datasource configuration documentation to learn more on this.

Once our application pod is created, if we inspect the final server configuration file, we will see that we have added two different data sources, one xa-datasource and one non-xa-datasource, each of them with a specific max/min pool sizes:

```
$ oc get pods
NAME                       READY   STATUS      RESTARTS   AGE
database-server-1-5l6fd    1/1     Running     0          19m
database-server-1-deploy   0/1     Completed   0          19m
wildfly-app-1-build        0/1     Completed   0          3m18s
wildfly-app-1-deploy       0/1     Completed   0          33s
wildfly-app-1-lwnf8        1/1     Running     0          25s

$ oc rsh wildfly-app-1-lwnf8
sh-4.2$ cat /opt/wildfly/standalone/configuration/standalone.xml
```

```
<subsystem xmlns="urn:jboss:domain:datasources:5.0">
    <datasources>
        <datasource jta="true" jndi-name="java:/jboss/datasources/PostgreSQLDS" pool-name="dbone_postgresql-DSONE" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
            <connection-url>jdbc:postgresql://database-server:5432/demodb</connection-url>
            <driver>postgresql</driver>
            <pool>
                <min-pool-size>5</min-pool-size>
                <max-pool-size>10</max-pool-size>
            </pool>
            <security>
                <user-name>postgre</user-name>
                <password>admin</password>
            </security>
            <validation>
                <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"/>
                <validate-on-match>true</validate-on-match>
                <background-validation>false</background-validation>
                <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"/>
            </validation>
        </datasource>
        <xa-datasource jndi-name="java:/jboss/datasources/UnusedDS" pool-name="dbtwo_postgresql-DSTWO" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
            <xa-datasource-property name="ServerName">
                database-server
            </xa-datasource-property>
            <xa-datasource-property name="DatabaseName">
                demodb
            </xa-datasource-property>
            <xa-datasource-property name="PortNumber">
                5432
            </xa-datasource-property>
            <driver>postgresql</driver>
            <xa-pool>
                <min-pool-size>2</min-pool-size>
                <max-pool-size>5</max-pool-size>
            </xa-pool>
            <security>
                <user-name>postgre</user-name>
                <password>admin</password>
            </security>
            <validation>
                <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"/>
                <validate-on-match>true</validate-on-match>
                <background-validation>false</background-validation>
                <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"/>
            </validation>
        </xa-datasource>
        <drivers>
            <driver name="postgresql" module="org.postgresql.jdbc">
                <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
            </driver>
        </drivers>
    </datasources>
</subsystem>
```

Notice this time the datasources subsystem configuration is done when the server is launched by the deployment config resource. Behind the scenes the WildFly embedded server is launched and the server is configured from the values found in the environment variables.

Since we have used the postgresql-driver layer, we still have the PostgreSQL driver installed in our server, we can see it under the modules folder:

```
sh-4.2$ ls /opt/wildfly/modules/org/postgresql/jdbc/main/
module.xml  postgresql-9.4.1211.jar
```

Now you can delete the project to remove all the resources created in this demo:

```
$ oc delete project wildfly-demo
project.project.openshift.io "wildfly-demo" deleted
```


You can configure the server using pure CLI management operations instead of using environment variables. That will give you all the flexibility you could need to configure any aspect of the WildFly S2I cloud image.

https://docs.wildfly.org/18/Admin_Guide.html#operations


## Backup to file so that you can restore setup

Export the project objects to a .yaml or .json file.
To export the project objects into a project.yaml file:
   
```
     $ oc get -o yaml --export all > project.yaml 
```
    
To export the project objects into a project.json file:
    
```
     $ oc get -o json --export all > project.json 
```

Export the project’s role bindings, secrets, service accounts, and persistent volume claims: $ for object in rolebindings serviceaccounts secrets imagestreamtags cm egressnetworkpolicies rolebindingrestrictions limitranges resourcequotas pvc templates cronjobs statefulsets hpa deployments replicasets poddisruptionbudget endpoints

```
do
 oc get -o yaml --export $object > $object.yaml
done
```

above for in a convenient one line command:

```
for object in rolebindings serviceaccounts secrets imagestreamtags cm egressnetworkpolicies rolebindingrestrictions limitranges resourcequotas pvc templates cronjobs statefulsets hpa deployments replicasets poddisruptionbudget endpoints; do  oc get -o yaml --export $object > $object.yaml ;done

```

To list all the namespaced objects: 

```
$ oc api-resources --namespaced=true -o name
```


Some exported objects can rely on specific metadata or references to unique IDs in the project. This is a limitation on the usability of the recreated objects. When using imagestreams, the image parameter of a deploymentconfig can point to a specific sha checksum of an image in the internal registry that would not exist in a restored environment. 

For instance, running the sample "ruby-ex" as oc new-app centos/ruby-22-centos7~https://github.com/sclorg/ruby-ex.git creates an imagestream ruby-ex using the internal registry to host the image:

```
  $ oc get dc ruby-ex -o jsonpath="{.spec.template.spec.containers[].image}"


10.111.255.221:5000/myproject/ruby-ex@sha256:880c720b23c8d15a53b01db52f7abdcbb2280e03f686a5c8edfef1a2a7b21cee
```

   If importing the deploymentconfig as it is exported with oc get --export it fails if the image does not exist.


## Restoring a project

To restore a project, create the new project, then restore any exported files by running oc create -f pods.json. However, restoring a project from scratch requires a specific order because some objects depend on others. For example, you must create the configmaps before you create any pods.

### PROCEDURE

If the project was exported as a single file, import it by running the following commands: 
```
$ oc new-project <projectname>

$ oc create -f project.yaml

$ oc create -f secret.yaml

$ oc create -f serviceaccount.yaml

$ oc create -f pvc.yaml

$ oc create -f rolebindings.yaml
 ```

## fork git repo and update openshift build instruction

```
oc get all
NAME                           READY   STATUS       RESTARTS   AGE
pod/database-server-1-deploy   0/1     Completed    0          3h20m
pod/database-server-1-q946z    1/1     Running      0          3h20m
pod/wildfly-app-1-deploy       0/1     Completed    0          3h14m
pod/wildfly-app-2-build        0/1     Completed    0          35m
pod/wildfly-app-2-deploy       0/1     Completed    0          31m
pod/wildfly-app-3-build        0/1     Completed    0          24m
pod/wildfly-app-3-deploy       0/1     Completed    0          20m
pod/wildfly-app-4-build        0/1     Completed    0          9m4s
pod/wildfly-app-4-deploy       0/1     Completed    0          4m39s
pod/wildfly-app-4-qnxgz        1/1     Running      0          4m27s
pod/wildfly-app-5-build        0/1     Init:Error   0          3m26s

NAME                                      DESIRED   CURRENT   READY   AGE
replicationcontroller/database-server-1   1         1         1       3h20m
replicationcontroller/wildfly-app-1       0         0         0       3h14m
replicationcontroller/wildfly-app-2       0         0         0       31m
replicationcontroller/wildfly-app-3       0         0         0       20m
replicationcontroller/wildfly-app-4       1         1         1       4m39s

NAME                      TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)             AGE
service/database-server   ClusterIP   172.30.6.170    <none>        5432/TCP            3h20m
service/wildfly-app       ClusterIP   172.30.95.164   <none>        8080/TCP,8778/TCP   3h18m

NAME                                                 REVISION   DESIRED   CURRENT   TRIGGERED BY
deploymentconfig.apps.openshift.io/database-server   1          1         1         config,image(postgresql:10)
deploymentconfig.apps.openshift.io/wildfly-app       4          1         1         config,image(wildfly-app:latest)

NAME                                         TYPE     FROM   LATEST
buildconfig.build.openshift.io/wildfly-app   Source   Git    5

NAME                                     TYPE     FROM          STATUS                       STARTED          DURATION
build.build.openshift.io/wildfly-app-1   Source   Git           Complete                     3 hours ago      4m18s
build.build.openshift.io/wildfly-app-2   Source   Git@24d98fc   Complete                     35 minutes ago   4m39s
build.build.openshift.io/wildfly-app-3   Source   Git@24d98fc   Complete                     24 minutes ago   3m41s
build.build.openshift.io/wildfly-app-4   Source   Git@24d98fc   Complete                     9 minutes ago    4m25s
build.build.openshift.io/wildfly-app-5   Source   Git           Failed (FetchSourceFailed)   3 minutes ago    11s

NAME                                             IMAGE REPOSITORY                                                                       TAGS     UPDATED
imagestream.image.openshift.io/database-server   default-route-openshift-image-registry.apps-crc.testing/wildfly-demo/database-server   10       
imagestream.image.openshift.io/wildfly-app       default-route-openshift-image-registry.apps-crc.testing/wildfly-demo/wildfly-app       latest   4 minutes ago

NAME                                       HOST/PORT                                       PATH   SERVICES          PORT       TERMINATION   WILDCARD
route.route.openshift.io/database-server   database-server-wildfly-demo.apps-crc.testing          database-server   5432-tcp                 None
route.route.openshift.io/wildfly-app       wildfly-app-wildfly-demo.apps-crc.testing              wildfly-app       8080-tcp                 None
```

```
oc get buildconfig.build.openshift.io/wildfly-app -o json >> build.json
```


```
vim build1.json 

        "source": {
            "git": {
                "uri": "https://github.com/robinjohnhopkins/jaxrs-postgresql-demo.git"
            },
```

```
oc replace  buildconfig.build.openshift.io/wildfly-app -f  build.json
```

Then use gui to rebuild - it auto deploys after build.
Check url using curl or browser

http://wildfly-app-wildfly-demo.apps-crc.testing/jaxrs-postgresql-demo/api/tasks

```
[{"id":1,"title":"This is the task-1"},{"id":2,"title":"This is the task-2"},{"id":3,"title":"This is the task-3"},{"id":4,"title":"This is the task-4"},{"id":5,"title":"This is the task-5"}]
```

http://wildfly-app-wildfly-demo.apps-crc.testing/jaxrs-postgresql-demo/api/rest
http://wildfly-app-wildfly-demo.apps-crc.testing/jaxrs-postgresql-demo/api/tasks/books
```
boom
```

http://wildfly-app-wildfly-demo.apps-crc.testing/jaxrs-postgresql-demo/api/rest/task
```
{"id":11,"title":"my tasky wask"}
```

