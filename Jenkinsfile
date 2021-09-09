#!/usr/bin/env groovy
def jenkinsfile

//TODO:Dette skal vi hente fra inkommende parametre

def config = [
    jobParameters           : [
      [name: 'SKATTEMELDING_MAPPING_VERSION']
     ],
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
    compilePropertiesFunction :       {
      if(params.SKATTEMELDING_MAPPING_VERSION != null){
        echo "Using compile parameters from upstream job"
        "-Dskattemelding.mapping.version=${params.SKATTEMELDING_MAPPING_VERSION}"
      }else {
        echo "No upstream version, provided i params:${params}"
        return ""
      }
    },
    mavenDeploy             : false,
    callbackBeforeDeploy    : { maven, git, props->
       echo "callbackBeforeDeploy"
       if (params.SKATTEMELDING_MAPPING_VERSION != null){
           //Vi gjør dette kun hvis vi har parametre fra oppstrøms bygg
           def result = git.status()
           echo "Before maven.versionsRevert ${result}"
           echo "Reverterer versjon fra versions:set"
           maven.versionsRevert(props)
           echo "After maven.versionsRevert ${result}"
           def commitMessage = "Oppdaterer version skattemelding.mapping.version=${params.SKATTEMELDING_MAPPING_VERSION}"
           git.add()
           git.commit(commitMessage)
           echo "Pusher commit  til ${env.BRANCH_NAME}"
           git.pushCommit(props.credentialsId, env.BRANCH_NAME)
       }else {
            echo "Det er ikke satt parametre fra oppstrøms bygg, denne byggen er trigget av egne endringer"
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
jenkinsfile.run(config.scriptVersion, config)
