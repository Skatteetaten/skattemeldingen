---
icon: "cloud"
title: "API"
description: "Api-beskrivelser"
---

# Intro

Intro

## Validerings-API

Aurora Config is structured around a four level file structure with the top level being the most general and the bottom
level, representing an application in a given environment, being the most specific - potentially overriding options set
at higher levels. Each environment has its own folder with a separate file for each application in that environment,
in addition to an about.yaml file describing the environment itself. The following table describes the different files;

## Hent skattemeldings-API

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

## Altinn-app API

For a given _app_ file, it is possible to change the _base_ and _env_ file if you want to compose your configuration
differently than the default. For instance, you may need to deploy the same application in the same environment with
different name and configuration;

File named "test/App1Beta.yaml"

```yaml
baseFile: App1.yaml
envFile: about-alternative.yaml
```
