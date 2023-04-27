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
    publishToNpm            : false,
    deployToNexus           : false,
    openShiftBuild          : false,
    disableAllReports       : true,
    checkstyle              : true,
    jacoco                  : false,
    checkstyle              : false,
    compileProperties       : "-U",
    mavenDeploy             : false,
    deployTo: false,
    cleanWs                 : true,
    github                  : [
      enabled               : true,
      push                  : env.BRANCH_NAME == "master",
      repoUrl               : "https://github.com/Skatteetaten/skattemeldingen",
    ]
]

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.run(config.scriptVersion, config)
