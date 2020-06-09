## How we build

Applications are built, tested and verified in our custom Jenkins CI/CD pipeline. They are then passed on to the proprietary CustomBuilder, Architect, as zip files called DeliveryBundles. A DeliveryBundle contains the application files and metadata.

Builds are triggered in one of several ways;

- via the CI/CD pipeline from commits tagged as [semanic releases](/documentation/openshift/#deployment-and-patching-strategy) or as feature branch SNAPSHOTS.
- as a binary-build directly from a development machine. This will buypass Jenkins and read the DeliveryBundle from stdin.
- from ImageChange triggers when either the CustomBuilder or the Base Image changes. See our [patching strategy](/documentation/openshift/#deployment-and-patching-strategy).
