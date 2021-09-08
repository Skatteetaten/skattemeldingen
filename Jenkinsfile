#!/usr/bin/env groovy
def jenkinsfile

def config = [
    scriptVersion           : 'gittest',
    pipelineScript          : 'https://git.aurora.skead.no/scm/~k95087/aurora-pipeline-scripts.git',
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
    disableAllReports       : true,
    checkstyle              : true,
    jacoco                  : false,
    mavenCompile            : false,
    mavenDeploy             : false,
    github                  : [
      enabled               : true
      credentialsId         : "github",
      push                  : env.BRANCH_NAME == "master",
      repoUrl               : "https://github.com/Skatteetaten/skattemeldingen",
    ]

]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.run(config.scriptVersion, config)
