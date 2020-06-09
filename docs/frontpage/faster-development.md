## Faster development and more efficient ops

- [PaaS](https://en.wikipedia.org/wiki/Platform_as_a_service) built upon [OpenShift](http://www.openshift.com)
- At the core of the platform is our custom declarative config format AuroraConfig and the [AuroraAPI](/documentation/openshift/#the-aurora-api).
- The AuroraAPI supports deploying applications and observing their status while running.
- Applications are built in a common Jenkins CI/CD pipeline, supported by a [CustomBuilder](https://docs.openshift.org/latest/creating_images/custom.html) called [Architect](/documentation/openshift/#the-application-image-builder-architect).
