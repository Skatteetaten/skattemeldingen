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
    disableAllReports       : true,
    checkstyle              : true,
    jacoco                  : false,
    checkstyle              : false,
    compileProperties       : "-U",
    mavenDeploy             : false,
    deployTo: false,
    callbackSuccess: { props ->
      git.add()

      try {
        git.commit("Oppdaterer skattemelding.mapping.version og skattemeldingtekst-mapping.version")
        echo "Pusher commit til ${env.BRANCH_NAME}"
        pushCommit(props.credentialsId, env.BRANCH_NAME)
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

def pushCommit(credentialsId, branchName) {
  try {

    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD')]) {
      git.setGitConfig()
      sh("git config credential.username ${env.GIT_USERNAME}")
      sh("git config credential.helper '!f() { echo password=\$GIT_PASSWORD; }; f'")

      int times = 0
      while (times < 3) {
        if (times != 0) {
          sleep times
        }
        int pushStatus = sh(script: "GIT_ASKPASS=true git push origin HEAD:$branchName &> result", returnStatus: true)
        if (pushStatus == 0) {
          return
        } else {
          def output = readFile('result').trim()
          println "Failed pushing tag status=$pushStatus tryNumber=${times + 1} git output=$output"
        }
        times++
      }
      error("Failed to push git tag. Root cause has been printed in the console log above.")
    }
  } finally {
    sh("git config --unset credential.username")
    sh("git config --unset credential.helper")
  }
}

fileLoader.withGit(config.pipelineScript, config.scriptVersion) {
   jenkinsfile = fileLoader.load('templates/leveransepakke')
}
jenkinsfile.run(config.scriptVersion, config, { props ->
    maven.versionsRevert(props)
    maven.run("versions:update-properties")
    maven.versionsCommit(props)
})
