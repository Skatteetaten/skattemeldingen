---
icon: "bookmark"
title: "Aurora Config"
description: "Opinionated way of configuring cloud applications"
---

## What is Aurora Config?

TLDR; [Take me to the Configuration Reference!](#configuration-reference)

Aurora Config is a custom file based configuration format developed by the Norwegian Tax Administration designed to be a concise
representation of how applications are configured and deployed across environments and clusters in an OpenShift
based infrastructure. Parts of the configuration format is generally reusable by anybody deploying to OpenShift, while
other parts of it are designed to simplify integrations with specific third party components in our infrastructure.

The conciseness of the format is derived from a highly opinionated way of deploying applications to OpenShift,
providing override options only when necessary.

Config files are written either as Json or Yaml.

Aurora Config is structured around a four level file structure with the top level being the most general and the bottom
level, representing an application in a given environment, being the most specific - potentially overriding options set
at higher levels. Each environment has its own folder with a separate file for each application in that environment,
in addition to an about.yaml file describing the environment itself. The following table describes the different files;

| File             | Name in AC | Description                                                                                                                                                                                                                                        |
| ---------------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| about.yaml       | global     | The _global_ file is the most general file in an Aurora Config. All applications will inherit options set in this file.                                                                                                                            |
| {app}.yaml       | base       | The _base_ file contains general configuration for all instances of application {app} across all environments. All instances will inherit options set in this file and will potentially override options set in the _global_ file.                 |
| {env}/about.yaml | env        | The _env_ file contains general configuration for all applications in environment {env}. All applications in the environment will inherit options set in this file and potentially override options set in both the _base_ file and _global_ file. |
| {env}/{app}.yaml | app        | The _app_ file contains specific configuration for application {app} in environment {env}. All options set in this file will potentially override options set in other files.                                                                      |

For the applications App1 and App2, and the environments test and prod, a typical Aurora Config could then look like;

    ├── about.yaml     (Configuration for all applications in all environments)
    ├── App1.yaml      (General configuration for App1)
    ├── App2.yaml      (General configuration for App2)
    ├── prod           (A folder named prod, representing the environment prod)
    │  ├── about.yaml  (Configuration for all applications in environment prod)
    │  ├── App1.yaml   (Configuration for App1 in environment prod)
    │  └── App2.yaml   (Configuration for App2 in environment prod)
    └── test           (A folder named test, representing the environment test)
       ├── about.yaml  (Configuration for all applications in environment test)
       ├── about-alternative.yaml  (Alternative Configuration for all applications in environment test)
       ├── App1.yaml   (Configuration for App1 in environment test)
       └── App2.yaml   (Configuration for App2 in environment test)

For a given _app_ file, it is possible to change the _base_ and _env_ file if you want to compose your configuration
differently than the default. For instance, you may need to deploy the same application in the same environment with
different name and configuration;

File named "test/App1Beta.yaml"

```yaml
baseFile: App1.yaml
envFile: about-alternative.yaml
```

In this scenario 'App1.yaml' would be used instead of 'App1Beta.yaml' (which does not exist) as the base file for the
App1Beta in the environment test. The env file about-alternative will be used instead of the standard about file.
Note that env files must start with the prefix `about`

For a given env file, it is possible to include another env file that is read right before you using the configuration.

In prod/about.yaml

```yaml
includeEnvFile: test/about.yaml
```

In this scenario 'test/about.yaml' will be read right before 'prod/about.yaml'. This will make it possible to have an
environment that is a template for other environments.

## DeploymentSpec and ApplicationId

When the Aurora Config is processed a new object is generated for each _app_ file, representing the configuration
collected from the _global_ file, the _base_ file for that application, the _env_ file for the environment, and finally
the _app_ file itself. This object is called the DeploymentSpec for the given application. The identifier for a
DeploymentSpec, called ApplicationId, is the combination of environment name and application name. From the example
above we would get four DeploymentSpecs with the following ApplicationIds;

- prod/App1
- prod/App2
- test/App1
- test/App2

## Configuration Reference

The following sections will describe the different configuration options that are available in each of the files. The
examples will use the YAML format for the config files since it is terser and easier on the eyes than JSON.

### Header

Some options are considered header options and are read in a separate step during the configuration parsing process.
This allows us to set defaults and make available values in the header for variable substitution in the other
configuration options. In order to include these into a field surround them with '@', for instance.

```
config/cluster : "@cluster@"
```

Which options are available for substitution is indicated in the following tables.

Substitutions should be used with care especially if they occur in a file that applies to multiple application instances, e.g. env files and base files.

Some configuration options can only be set in the _global_ about file and the _env_ file. These are typically options that
are only relevant for configuring the environment, for instance environment name, permissions and env.ttl (time to live).
Since environments have their own folder and the environment is configured in an own about-file, it is not allowed for an
_app_-file to override any of the environment specific options. Options that can only be set in the _global_ file or in
an _env_ file will be described in a section called "About files" and options that can also be set in the _base_ files
and _app_ files will be describe in a section called "Application files".

#### About files

| path                            | required | default      | substitution | description                                                                                                                                                                                            |
| ------------------------------- | -------- | ------------ | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| affiliation                     | Yes      |              | affiliation  | Used to group the project for resource monitoring. All projects start with affiliation. lower case letters max length 10. Required.                                                                    |
| envName                         |          | \$folderName | env          | Change the name of the project. Note that the default value here is the actual name of the folder where the app file is. This option must be specified in either global or env file.                   |
| env/name                        |          |              | env          | An alias for envName                                                                                                                                                                                   |
| env/ttl                         |          |              | No           | Set a time duration in format 1d, 12h that indicate how long until this namespace should be deleted                                                                                                    |
| permissions/admin               | Yes      |              | No           | The groups in OpenShift that will have the admin role for the given project. Can either be an array or a space delimited string. This option must be specified in either global or env file. Required. |
| permissions/view                |          |              | No           | The groups in OpenShift that will have the view role for the given project. Can either be an array or a space delimited string. This option must be specified in either global or env file.            |
| permissions/adminServiceAccount |          |              | No           | The service accounts in OpenShift that will have the admin role for the given project. Can either be an array or a space delimited string. This option must be specified in either global or env file. |

At least one of the groups in permissions/admin must have a user in it.

#### Application files

| path                | required | default        | substitution | description                                                                                                                                                                                                                                       |
| ------------------- | -------- | -------------- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| schemaVersion       | Yes      |                | No           | All files in a given AuroraConfig must share a schemaVersion. For now only v1 is supported, it is here in case we need to break compatibility. Required.                                                                                          |
| type                | Yes      |                | No           | [See Deployment Types](#deployment_types)                                                                                                                                                                                                         |
| applicationPlatform |          | java           | No           | Specify application platform. java or web are valid platforms. Is only used if type is deploy/development.                                                                                                                                        |
| name                |          | \$baseFileName | name         | The name of the application. All objects created in the cluster will get an app label with this name. Cannot be longer then 40 (alphanumeric -). Note that the default value here is the actual name of the base file.                            |
| cluster             | Yes      |                | cluster      | What cluster should the application be deployed to. Must be a valid cluster name.                                                                                                                                                                 |
| ttl                 |          |                | No           | Set a time duration in format 1d, 12h that indicate how long until this application should be deleted                                                                                                                                             |
| version             | Yes      |                | No           | Version of the application to run. Can be set to any of the [valid version strategies](https://skatteetaten.github.io/aurora/documentation/openshift/#deployment-and-patching-strategy). Version is not required for template/localTemplate files |
| segment             |          |                | segment      | The segment the application exist in.                                                                                                                                                                                                             |
| message             |          |                | message      | An message that will be added to the ApplicationDeployment CRD.                                                                                                                                                                                   |

### Deployment Types

The configuration option `type` indicates the deployment type the application has. The value of this field affects
what other configuration options are available for that application. The deployment type determines primarily how
the objects that supports the application on OpenShift are generated, but it also affects the different types of
integrations that are supported.

#### deploy

The deploy deployment type is used for deploying applications using the conventions from the Aurora Platform. You can
read more about these conventions here: [How we Develop and Build our Applications](https://skatteetaten.github.io/aurora/documentation/openshift/#how-we-develop-and-build-our-applications).
This is the deployment type that will be most commonly used when deploying internally built applications. This will
provide integrations with the rest of the NTAs infrastructure and generate the necessary objects to OpenShift to support
the application.

#### development

The development deployment type is similar to the release deployment type but it will not deploy a prebuilt image and
instead create an OpenShift BuildConfig that can be used to build ad hoc images from DeliveryBundles from your local
development machine.

This will usually significantly reduce the time needed to get code from a development machine running on OpenShift
compared to, for instance, a CI/CD pipeline.

#### template

Supports deploying an application from a template available on the cluster. See [Guidelines for developing templates](#template_dev_guidelines).

#### localTemplate

Supports deploying an application from a template available in the AuroraConfig folder. See [Guidelines for developing templates](#template_dev_guidelines).

#### cronjob

Supports running a scheduled job as a CronJob resource on Kubernetes

#### job

Supports running a job as a Job resource on Kubernetes

### Configuration for Deployment Types "deploy" and "development"

| path                           | default     | description                                                                                                                                                                                                                                                                                                                   |
| ------------------------------ | ----------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| releaseTo                      |             | Used to release a given version as a shared tag in the docker registry. Other env can then use it in 'version'. NB! Must be manually updated with AO/Aurora Konsoll                                                                                                                                                           |
| debug                          | false       | Toggle to enable remote debugging on port 5005. Port forward this port locally and setup remote debugging in your Java IDE.                                                                                                                                                                                                   |
| deployStrategy/type            | rolling     | Specify type of deployment, either rolling or recreate                                                                                                                                                                                                                                                                        |
| deployStrategy/timeout         | 180         | Set timeout value in seconds for deployment process                                                                                                                                                                                                                                                                           |
| resources/cpu/min              | 10m         | Specify minimum/request cpu. See [kubernetes_docs](https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/#meaning-of-cpu) for potential values                                                                                                                                                 |
| resources/cpu/max              | 2000m       | Specify maximum/limit cpu.                                                                                                                                                                                                                                                                                                    |
| resources/memory/min           | 128Mi       | Specify minimum/request memory. See [kubernetes docs](https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/#meaning-of-memory) for potential values                                                                                                                                           |
| resources/memory/max           | 512Mi       | Specify maximum/limit memory. By default 25% of this will be set to heap in java8 and 75% in java11.                                                                                                                                                                                                                          |
| config/JAVA_MAX_MEM_RATIO      | 25          | Specify heap percentage for Java 8 applications                                                                                                                                                                                                                                                                               |
| config/JAVA_MAX_RAM_PERCENTAGE | 75.0        | Specify heap percentage for Java 11 applications                                                                                                                                                                                                                                                                              |
| groupId                        |             | groupId for your application. Max 200 length. Required if deploy/development                                                                                                                                                                                                                                                  |
| artifactId                     | \$fileName  | artifactId for your application. Max 50 length                                                                                                                                                                                                                                                                                |
| version                        |             | The version of the image you want to run.                                                                                                                                                                                                                                                                                     |
| splunkIndex                    |             | Set to a valid splunk-index to log to splunk. Only valid if splunk is enabled in the Aurora API                                                                                                                                                                                                                               |
| serviceAccount                 |             | Set to an existing serviceAccount if you need special privileges                                                                                                                                                                                                                                                              |
| prometheus                     | true        | Toggle to false if application do not have Prometheus metrics                                                                                                                                                                                                                                                                 |
| prometheus/path                | /prometheus | Change the path of where prometheus is exposed                                                                                                                                                                                                                                                                                |
| prometheus/port                | 8081        | Change the port of where prometheus is exposed                                                                                                                                                                                                                                                                                |
| management                     | true        | Toggle of if your application does not expose an management interface                                                                                                                                                                                                                                                         |
| management/path                | /actuator   | Change the path of where the management interface is exposed                                                                                                                                                                                                                                                                  |
| management/port                | 8081        | Change the port of where the management interface is exposed                                                                                                                                                                                                                                                                  |
| readiness                      | true        | Toggle to false to turn off default readiness check                                                                                                                                                                                                                                                                           |
| readiness/path                 |             | Set to a path to do a GET request to that path as a readiness check                                                                                                                                                                                                                                                           |
| readiness/port                 | 8080        | If no path present readiness will check if this port is open                                                                                                                                                                                                                                                                  |
| readiness/delay                | 10          | Number of seconds to wait before running readiness check                                                                                                                                                                                                                                                                      |
| readiness/timeout              | 1           | Number of seconds timeout before giving up readiness                                                                                                                                                                                                                                                                          |
| liveness                       | false       | Toggle to true to enable liveness check                                                                                                                                                                                                                                                                                       |
| liveness/path                  |             | Set to a path to do a GET request to that path as a liveness check                                                                                                                                                                                                                                                            |
| liveness/port                  | 8080        | If no path present liveness will check if this port is open                                                                                                                                                                                                                                                                   |
| liveness/delay                 | 10          | Number of seconds to wait before running liveness check                                                                                                                                                                                                                                                                       |
| liveness/timeout               | 1           | Number of seconds timeout before giving up liveness                                                                                                                                                                                                                                                                           |
| replicas                       | 1           | Number of replicas of this application to run.                                                                                                                                                                                                                                                                                |
| pause                          | false       | Toggle to pause an application. This will scale it down to 0 and add a label showing it is paused.                                                                                                                                                                                                                            |
| toxiproxy                      | false       | Toxiproxy feature toggle using default version                                                                                                                                                                                                                                                                                |
| toxiproxy/version              | 2.1.3       | Toxiproxy version                                                                                                                                                                                                                                                                                                             |
| config                         |             | Contains a collection of application configuration variables. The variables are passed on as environment variables to the container. Otherwise, they are ignored by the platform, and it is up to the application to interpret them. Note: If you are using JSON, then both key and value should be enclosed in double quotes |

For development flow the following configuration properties are available to specify how to build the image locally

| path              | default   | description                                                                              |
| ----------------- | --------- | ---------------------------------------------------------------------------------------- |
| builder/name      | architect | Name of the builder image that is used to run the build                                  |
| builder/version   | 1         | Version of the builder image to use. NB! This must be a tag in the architect imagestream |
| baseImage/name    |           | Name of the baseImage to use,                                                            |
| baseImage/version |           | Version of the baseImage to use.NB! This must be a tag in the baseImage imagestream      |

The following baseImage are in use at NTA

| name      | version | description      |
| --------- | ------- | ---------------- |
| wrench8   | 1       | Nodejs8 & Nginx  |
| wrench10  | 1       | Nodejs10 & Nginx |
| wingnut8  | 1       | OpenJdk 8        |
| wingnut11 | 1       | OpenJDK 11       |
| yeaster   | 1       | Oracle Jdk8      |

### Configuration for Deployment Types "template" and "localTemplate"

| path                 | default | description                                                                                                                                                                                                                                                                     |
| -------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| template             |         | Name of template in default namespace to use. This is required if type is template                                                                                                                                                                                              |
| templateFile         |         | Set the location of a local template file. It should be in the templates subfolder. This is required if type is localTemplate                                                                                                                                                   |
| `parameters/<KEY>`   |         | The parameters option is used to set values for parameters in the template. If the template has either of the parameters VERSION, NAME, SPLUNK_INDEX or REPLICAS, the values of these parameters will be set from the standard version, name and replicas AuroraConfig options. |
| replicas             |         | If set will override replicas in template                                                                                                                                                                                                                                       |
| resources/cpu/min    |         | Specify minimum/request cpu. 1000m is 1 core. see [kubernetes_docs](https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/#meaning-of-cpu)                                                                                                       |
| resources/cpu/max    |         | Specify maximum/limit cpu.                                                                                                                                                                                                                                                      |
| resources/memory/min |         | Specify minimum/request memory. See [kubernetes docs](https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/#meaning-of-memory)                                                                                                                  |
| resources/memory/max |         | Specify maximum/limit memory. By default 25% of this will be set to XMX in java.                                                                                                                                                                                                |

Note that resources and replicas have no default values for templates. If they are set they will be applied if not the value
in the template will be used.

### Configuration for job and cronjobs

For jobs and cronjobs you have to create an application that terminates when it is done and point to it using the normal groupId/artifactId:version semantics

| path       | default    | description                                                                  |
| ---------- | ---------- | ---------------------------------------------------------------------------- |
| groupId    |            | groupId for your application. Max 200 length. Required if deploy/development |
| artifactId | \$fileName | artifactId for your application. Max 50 length                               |
| version    |            | The version of the image you want to run.                                    |

#### Aditional configuration for cronjobs

| path              | default | description                                                                                                                            |
| ----------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| schedule          |         | Cron scheduel validated against http://cron-parser.com/                                                                                |
| failureCount      | 1       | Number of failed jobs to keep                                                                                                          |
| successCount      | 3       | Number of successfull jobs to keep                                                                                                     |
| concurrencyPolicy | Forbid  | Any of [concurrencyPolicy](https://kubernetes.io/docs/tasks/job/automated-tasks-with-cron-jobs/#concurrency-policy)                    |
| startingDeadline  | 60      | Override the starting deadline for the cronjob, see suspend below                                                                      |
| suspend           | false   | Suspend/stop the job. Nb! See [suspend](https://kubernetes.io/docs/tasks/job/automated-tasks-with-cron-jobs/#suspend) docs for caveats |

#### Supported integrations

Jobs and Cronjobs can have

- secrets
- databases
- STS tokens
- mounts

#### Limitations

Jobs and cronjobs do not support log aggregations and prometheus metrics at the moment. Use the script directive and do a
http call to a service alongside your job if you need this.

### Exposing an application via HTTP

The default behavior is that the application is only visible to other application in the same namespace using
its service name.

In order to control routes into the application the following fields can be used.

| path                                   | default                           | description                                                                                                                                                                                                                                                                                                                                                             |
| -------------------------------------- | --------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| route                                  | false                             | Toggle to expose application via HTTP. Routes can also be configured with expanded syntax. And routeDefault can be set for all routes. See below.                                                                                                                                                                                                                       |
| `route/<routename>/enabled`            | true                              | Set to false to turn off route                                                                                                                                                                                                                                                                                                                                          |
| `route/<routename>/host`               |                                   | Set the host of a route according to the given pattern. If not specified the default will be routeDefault/host                                                                                                                                                                                                                                                          |
| `route/<routename>/path`               |                                   | Set to create a path based route. You should use the same name/affiliation/env/separator combination for all path based routes to get the same URL                                                                                                                                                                                                                      |
| `route/<routename>/annotations/<key>`  |                                   | Set annotations for a given route. Note that you should use &#124; instead of / in annotation keys. so 'haproxy.router.openshift.io &#124; balance'. See [route annotations](https://docs.openshift.com/container-platform/3.10/architecture/networking/routes.html#route-specific-annotations) for some options. If the value is empty the annotation will be ignored. |
| `route/<routename>/tls/enabled`        |                                   | Turn on/off tls for this route                                                                                                                                                                                                                                                                                                                                          |
| `route/<routename>/tls/insecuryPolicy` |                                   | When TLS is enabled how do you handle insecure traffic. Allow/Redirect/None. If not set for a route routeDefaults/tls/insecurePolicy will be used.                                                                                                                                                                                                                      |
| `route/<routename>/tls/termination`    |                                   | Where to terminate TLS for this route. Edge/Passthrough. If not set use the default value from routeDefaults/tls/termination.                                                                                                                                                                                                                                           |
| `route/<routename>/annotations/<key>`  |                                   | Set annotations for a given route. Note that you should use &#124; instead of / in annotation keys. so 'haproxy.router.openshift.io &#124; balance'. See [route annotations](https://docs.openshift.com/container-platform/3.10/architecture/networking/routes.html#route-specific-annotations) for some options. If the value is empty the annotation will be ignored. |
| routeDefaults/host                     | @name@-@affiliation@-@env@        | Set the host of a route according to the given pattern.                                                                                                                                                                                                                                                                                                                 |
| routeDefaults/annotations/<key>        |                                   | Set annotations for a given route. Note that you should use &#124; instead of / in annotation keys. so 'haproxy.router.openshift.io &#124; balance'. See [route annotations](https://docs.openshift.com/container-platform/3.10/architecture/networking/routes.html#route-specific-annotations) for some options.                                                       |
| routeDefaults/tls/enabled              | false                             | Enable/disable tls for all routes                                                                                                                                                                                                                                                                                                                                       |
| routeDefaults/tls/insecurePolicy       | <varies for applicationPlattform> | For Java the default is None for Web the default is Redirect                                                                                                                                                                                                                                                                                                            |
| routeDefaults/tls/termination          | edge                              | Where do you terminate TLS? Edge or Passthrough. Reencrypt is not supported for now.                                                                                                                                                                                                                                                                                    |

If tls is used the host of the route cannot include the '.' key, since we do not support wildcard TLS cert.

Route annotations are usable for template types but you need to create a Service with name after the NAME parameter yourself.

### Managing Secrets

In order to provide sensitive data to an application (i.e. passwords that cannot be stored directly in the configuration block of the AuroraConfig) it is possible to
access Vaults that has been created with the `ao vault` command (see internal link
https://wiki.sits.no/pages/viewpage.action?pageId=143517331#AO(AuroraOpenShiftCLI)-AOVault). You can access the vaults in two different ways; as a
_mount_ or via the _secretVault_ option.

If a Vault is accessed via the secretVault option and the vault contains a properties file the contents of that file will be made available as
environment variables for the application. Example;

```
PASSWORD=s3cr3t
ENCRYPTION_KEY=8cdca234-9a3b-11e8-9eb6-529269fb1459
```

If you want to mount additional Vaults or access vault files directly this can be done with mounting it as a volume. See the next section for more details.

| path                                | default           | description                                                                            |
| ----------------------------------- | ----------------- | -------------------------------------------------------------------------------------- |
| `secretVaults/<svName>/name`        | \$svName          | Specify full secret vault that will be mounted under default secret location.          |
| `secretVaults/<svName>/enabled`     | true              | Set this to false to disable.                                                          |
| `secretVaults/<svName>/file`        | latest.properties | File in vault that will be used for fetching properties.                               |
| `secretVaults/<svName>/keys`        |                   | An array of keys from the latest.properties file in the vault you want to include.     |
| `secretVaults/<svName>/keyMappings` |                   | An map of key -> value that will rewrite the key in the secret to another ENV var name |

Note that it is possible to fetch multiple files from the same vault, the `svName` must be different for each one and you must set name to the same.

The old way of specifying secretVaults (detailed below is deprecated). There will be a migration feature soon. This configuration pattern only suppored
a single vault/file.

| path                    | default | description                                                                            |
| ----------------------- | ------- | -------------------------------------------------------------------------------------- |
| secretVault             |         | Specify full secret vault that will be mounted under default secret location.          |
| secretVault/name        |         | Used instead of secretVault if you want advanced configuration                         |
| secretVault/keys        |         | An array of keys from the latest.properties file in the vault you want to include.     |
| secretVault/keyMappings |         | An map of key -> value that will rewrite the key in the secret to another ENV var name |

It is possible to use substitutions in keys/keyMappings but it should be used with care and doublechecked.

### Mounting volumes

| path                             | default       | description                                                                                                                                         |
| -------------------------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| `mounts/<mountName>/type`        |               | One of Secret, PVC. Required for each mount.                                                                                                        |
| `mounts/<mountName>/enabled`     | true          | Set this to false to disable this mount                                                                                                             |
| `mounts/<mountName>/path`        |               | Path to the volume in the container. Required for each mount.                                                                                       |
| `mounts/<mountName>/mountName`   | `<mountName>` | Override the name of the mount in the container.                                                                                                    |
| `mounts/<mountName>/volumeName`  | `<mountName>` | Override the name of the volume in the DeploymentConfig.                                                                                            |
| `mounts/<mountName>/exist`       | false         | If this is set to true the existing resource must exist already.                                                                                    |
| `mounts/<mountName>/secretVault` |               | The name of the Vault to mount. This will mount the entire contents of the specified vault at the specified path. Type must be Secret, Exist false. |

The combination of type=PVC and exist=true is not supported by policy. We do not want normal java/web applications to use PVC mounts unnless strictly neccesary.

### NTA webseal integration

Webseal is used for client traffic from within NTA to reach an application. Internal tax workers have roles that can be added to limit who can access the application

| path           | default | description                                                                                                                                                                                                             |
| -------------- | ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| webseal        | false   | Toggle to expose application through WebSeal.                                                                                                                                                                           |
| webseal/host   |         | Set this to change the default prefix in WebSeal                                                                                                                                                                        |
| webseal/roles  |         | Set roles required to access this route. This can either be set as CSV or as an array of strings                                                                                                                        |
| webseal/strict | true    | If the application relies on WebSEAL security it should not have an OpenShift Route, this can be harmful. Strict will only generate warnings when both routes will be created. Set strict to false to disable warnings. |

If you want to use webseal with a template type you need to create a Service with default ports named after the name parameter

### NTA STS integration

STS certificate: An SSL certificate with a given commonName is used to identify applications to secure traffic between them

For v1 of the STS service use:

| path                   | default | description                                                 |
| ---------------------- | ------- | ----------------------------------------------------------- |
| certificate            | false   | Toggle to add a certificate with CommonName $groupId.$name. |
| certificate/commonName |         | Generate an STS certificate with the given commonName.      |

For v2 use:

| path   | default | description                                                 |
| ------ | ------- | ----------------------------------------------------------- |
| sts    | false   | Toggle to add a certificate with CommonName $groupId.$name. |
| sts/cn |         | Generate an STS certificate with the given commonName.      |

### NTA Dbh integration

[dbh](https://github.com/skatteetaten/dbh) is a service that enables an application to ask for credentials to a database schema.

If there is no schema the default behavior is to create one.

It is possible to change the default values for this process so that each application that wants a database can just use the `database=true` instruction

| path                                   | default        | description                                                                                                                                                                                                |
| -------------------------------------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| databaseDefaults/flavor                | ORACLE_MANAGED | One of `ORACLE_MANAGED`, `POSTGRES_MANAGED`.                                                                                                                                                               |
| databaseDefaults/generate              | true           | Set this to false to avoid generating a new schema if your lables does not match an existing one                                                                                                           |
| databaseDefaults/name                  | @name@         | The default name to given a database when using database=true                                                                                                                                              |
| databaseDefaults/instance/name         |                | The name of the instance you want to use for yor db schemas                                                                                                                                                |
| databaseDefaults/instance/fallback     | true           | If your instance does not match by labels, a fallback instance will be used if available. Default is true for ORACLE_MANAGED and false for POSTGRES_MANAGED                                                |
| databaseDefaults/instance/labels/<key> |                | Set key=value pair that will be sent when matching database instances. Default is affiliation=@affiliation@                                                                                                |
| database                               | false          | Toggle this to add a database with \$name to your application.                                                                                                                                             |
| `database/<name>`                      |                | Simplified config for multiple databases.If you want to add multiple databases specify a name for each. Set to 'auto' for auto generation or a given ID to pin it. Set to false to turn off this database. |

If you want to change the default configuration for one application you need to use the expanded syntax

| path                                    | default                              | description                                       |
| --------------------------------------- | ------------------------------------ | ------------------------------------------------- |
| `database/<name>/enabled`               | true                                 | Set to false to disable database                  |
| `database/<name>/flavor`                | \$databaseDefaults/flavor            | Override default flavor.                          |
| `database/<name>/name`                  | <name>                               | Override the name of the database.                |
| `database/<name>/id`                    |                                      | Set the id of the database to get an exact match. |
| `database/<name>/generate`              | \$databaseDefaults/generate          | Override default generate.                        |
| `database/<name>/instance/name`         | \$databaseDefaults/instance/name     | Override default instance/name.                   |
| `database/<name>/instance/fallback`     | \$databaseDefaults/instance/fallback | Override default instance/fallback.               |
| `database/<name>/instnace/labels/<key>` |                                      | Add/override labels for instance.                 |

## Example configuration

### Simple reference-application

Below is an example of how you could configure an instance of the [reference application](https://github.com/skatteetaten/openshift-reference-springboot-server)

about.yaml

```yaml
schemaVersion: v1
affiliation: paas
permissions:
  group: [PAAS_OPS, PAAS_DEV]
splunkIndex: paas-test
```

reference.yaml

```yaml
groupId: no.skatteetaten.aurora.openshift
artifactId: openshift-reference-springboot-server
version: 1
type: deploy
replicas: 3
certificate: true
route: true
database: true
config:
  FOO: BAR
```

dev/about.yaml

```yaml
cluster: dev
```

dev/reference.yaml

```yaml
config:
  FOO: BAZ
```

The complete config is then evaluated as

```yaml
schemaVersion: v1
affiliation: paas
permissions:
  group: [PAAS_OPS, PAAS_DEV]
splunkIndex: paas-test
groupId: no.skatteetaten.aurora.openshift
artifactId: openshift-reference-springboot-server
version: 1
type: deploy
replicas: 3
certificate: true
route: true
database: true
config:
  FOO: BAZ
cluster: dev
```

### Applying template with NTA integrations

about.yaml

```yaml
schemaVersion: v1
affiliation: paas
permissions:
  group: [PAAS_OPS, PAAS_DEV]
splunkIndex: paas-test
```

sample-atomhopper.yaml

```yaml
type: template
template: aurora-atomhopper-1.0.0
databaseDefaults:
  flavor: POSTGRES_MANAGED
database: true
route: true
parameters:
  FEED_NAME: feed
  DB_NAME: atomhopper
  DOMAIN_NAME: localhost
```

dev/about.yaml

```yaml
cluster: dev
```

dev/sample-atomhopper.yaml

empty file

The complete config is then evaluated as

```yaml
schemaVersion: v1
affiliation: paas
permissions:
  group: [PAAS_OPS, PAAS_DEV]
splunkIndex: paas-test
type: template
template: aurora-atomhopper-1.0.0
databaseDefaults:
  flavor: POSTGRES_MANAGED
database: true
route: true
parameters:
  FEED_NAME: feed
  DB_NAME: atomhopper
  DOMAIN_NAME: localhost
```

## Guidelines for developing templates

When creating templates the following guidelines should be followed:

- include the following parameters VERSION, NAME and if appropriate REPLICAS. They will be populated from relevant AuroraConfig fields
- the following labels will be added to the template: app, affiliation, updatedBy
- if the template does not have a VERSION parameter it will not be upgradable from internal web tools
- Each container in the template will get additional ENV variables applied if NTA specific integrations are applied.
