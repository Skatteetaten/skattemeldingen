## Why did you make things in-house?

- We have many different development teams that work on our clusters and we want them to be able to work in **isolation**. Both Kubernetes and OpenShift lack support for groups of users that can adminster subsets of cluster objects. We have created a concept called **affiliation** to support this.

- The ability to deploy applications to **several clusters** in one command is highly desired within our organization. Our network infrastructure implies that we need to have multiple clusters.

- When configuring how to deploy applications and projects we want to avoid duplication. Our **declarative** config format AuroraConfig supports **composition** with **sane defaults**.
