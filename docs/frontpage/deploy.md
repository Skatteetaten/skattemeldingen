## How we deploy

A deploy starts in the AuroraAPI, triggered from one of the user facing clients ([AO](/documentation/openshift/#ao) and [AuroraConsole](/documentation/openshift/#aurora-console)), or automatically from the build pipeline. The API extracts and merges relevant parts of the specified AuroraConfig in order to create an AuroraDeploymentSpec for the application being deployed.

From the AuroraDeploymentSpec we provision resources in our existing infrastructure and generate OpenShift objects that are applied to the cluster. The application is then rolled out either via importing a new image or triggering a new deploy. The deploy result is saved for later inspection.
