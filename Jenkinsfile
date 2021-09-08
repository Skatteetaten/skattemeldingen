#!/usr/bin/env groovy

def config = [
    scriptVersion          : 'v7',
    iqOrganizationName: "Team Sirius IO",
    iqCredentialsId: "ioteam-iq",
    iqBreakOnUnstable: false
    pipelineScript         : 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',

    versionStrategy        : [[ branch : 'master', versionHint:'1' ]],

    github                 : [
      enabled              : true,
      push                 : env.BRANCH_NAME == "master",
      repoUrl              : "https://github.com/Skatteetaten/skattemeldingen",
    ]

]

fileLoader.withGit(overrides.pipelineScript, overrides.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/webleveransepakke')
}
jenkinsfile.run(overrides.scriptVersion, overrides)
