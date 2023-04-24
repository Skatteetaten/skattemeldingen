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
    checkstyle              : false,
    compileProperties       : "-U",
    mavenDeploy             : false,
    callbackSuccess: { props ->
      git.add()

      try {
        git.commit("Oppdaterer skattemelding.mapping.version og skattemeldingtekst-mapping.version")
        echo "Pusher commit til ${env.BRANCH_NAME}"
        git.pushCommit(props.credentialsId, env.BRANCH_NAME)
      } catch(Exception ex) {
        echo ex.toString()
      }
    },
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
jenkinsfile.run(config.scriptVersion, config, { props ->
    maven.versionsRevert(props)
    maven.run("versions:update-properties")
    maven.versionsCommit(props)
})
