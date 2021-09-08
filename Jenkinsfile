#!/usr/bin/env groovy
def jenkinsfile

def config = [
    scriptVersion           : 'v7',
    pipelineScript          : 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',
    versionStrategy         : [
      [ branch : 'master', versionHint:'1' ]
    ],
    iqOrganizationName      : "Team Sirius IO",
    iqCredentialsId         : "ioteam-iq",
    iqBreakOnUnstable       : false,
    artifactId              : 'skattemeldingen',
    groupId                 : 'no.skatteetaten.fastsetting.formueinntekt.skattemelding',
    publishToNpm            : false,
    deployToNexus           : false,
    openShiftBuild          : false,
    nodeVersion             : "12",

    github                 : [
      enabled              : true,
      push                 : env.BRANCH_NAME == "master",
      repoUrl              : "https://github.com/Skatteetaten/skattemeldingen",
    ]

]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/webleveransepakke')
}
jenkinsfile.run(config.scriptVersion, config)
