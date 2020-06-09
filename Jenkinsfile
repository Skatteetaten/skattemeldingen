#!/usr/bin/env groovy

Map<String, Object> props = [
  credentialsId                : 'github',
  nodeVersion                  : 'node-10'
]

def git
def npm

fileLoader.withGit('https://git.aurora.skead.no/scm/ao/aurora-pipeline-scripts.git', 'v7') {
  git = fileLoader.load('git/git')
  npm = fileLoader.load('node.js/npm')
}

node {
  if (env.BRANCH_NAME != "master") {
    currentBuild.result = 'ABORTED'
    error('Branch is not master')
  }

  if (props.nodeVersion) {
    echo 'Using Node version: ' + props.nodeVersion
    npm.setVersion(props.nodeVersion)
  }

  stage('Clean Workspace') {
    deleteDir()
    sh 'ls -lah'
  }

  stage('Checkout') {
    checkout scm
  }

  stage('Install dependencies') {
    npm.run('ci')
  }

  stage('Build') {
    npm.build()
  }

  stage('Deploy to GitHub') {
    try { 
      withCredentials([usernamePassword(credentialsId: props.credentialsId, usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD')]) {
        git.setGitConfig()
        sh("git config --global credential.https://github.com.username ${env.GIT_USERNAME}")
        sh("git config --global credential.helper '!echo password=\$GIT_PASSWORD; echo'")

        sh("GIT_ASKPASS=true npm run deploy")
      }
    } finally {
      sh("git config --global --unset credential.https://github.com.username")
      sh("git config --global --unset credential.helper")
    }
  }

  stage('Clear workspace') {
    step([$class: 'WsCleanup'])
  }
}