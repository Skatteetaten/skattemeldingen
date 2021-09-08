#!/usr/bin/env groovy

def config = [
    scriptVersion          : 'v7',
    iqOrganizationName: "Team Sirius IO",
    iqCredentialsId: "ioteam-iq",
    iqBreakOnUnstable: false,
    pipelineScript         : 'https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git',

    versionStrategy        : [[ branch : 'master', versionHint:'1' ]]

]

fileLoader.withGit(overrides.pipelineScript, overrides.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.run(overrides.scriptVersion, overrides)
