## Observe what is running

We augment the application status data that OpenShift already keeps by regularly inspecting the master API and the management interface (part of our runtime contract) of the applications. The extra status data collected is compiled into a separate status value called [AuroraStatus](/documentation/openshift/#application-monitoring). This allow us, among other things, to create custom wallboards, alert integrations, rate of errors and 95% percentile response times.
