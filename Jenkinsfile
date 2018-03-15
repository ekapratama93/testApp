pipeline {
  agent {
    label {
      label ""
      customWorkspace "~/test/testApp"
    }
  }

  parameters {
    string(description: 'Deployment target environment\n- staging\n- production', name: 'STAGE', defaultValue: 'staging')
  }

  stages {

    stage('Set variables') {
      steps {
        script {
          def j = "${env.JOB_NAME}".split('/')
          PROJECT = j[1]
          BRANCH = j[2]

          if (env.TAG_NAME != null) params.STAGE == 'production'
        }

        echo "job_name = ${env.JOB_NAME}"
        echo "build_number = ${env.BUILD_NUMBER}"
        echo "tag = ${env.TAG_NAME}"
      }
    }

    stage('Build') {
      steps {
        sh "mvn package"
      }
    }

    stage('Validation') {
      steps {
        sh "java -jar target/test-0.0.1-SNAPSHOT.jar &"
        sleep(10)
        script {
          timeout(time: 3, unit: 'SECONDS') {
            def r = 0
            waitUntil {
              try{
                r = httpRequest(url: "http://127.0.0.1:8080", timeout: 3, validResponseCodes: "200")
              }catch(error){
                return false
              }
              return (r.status == 200)
            }
          }
        }
      }

      post {
        success {
           script {
             try {
               if ($params.STAGE == 'staging') {
                sh "docker build -t asia.gcr.io/kurio-dev/test:${BRANCH}.${params.STAGE}.${env.BUILD_NUMBER} ."  
                sh "gcloud docker -- push asia.gcr.io/kurio-dev/test:${BRANCH}.${params.STAGE}.${env.BUILD_NUMBER}"
               } else {
                sh "docker build -t asia.gcr.io/kurio-dev/test:${BRANCH} ."
                sh "docker tag asia.gcr.io/kurio-dev/test:${BRANCH} asia.gcr.io/kurio-dev/test:latest"
                sh "gcloud docker -- push asia.gcr.io/kurio-dev/test:${BRANCH}"
                sh "gcloud docker -- push asia.gcr.io/kurio-dev/test:latest"
               }
             } catch(error) {
               echo "Docker ops fail!"
               return false
             }
           }
        }
       }
    }

    stage('STAGING - deployment') {

      when {
        expression {
          return BRANCH == 'master'
          return params.STAGE == 'staging'
        }
      }

      steps {
        sh "echo 'STAGING'"
      }
    }

    stage('PRODUCTION - deployment') {

      when {
        expression {
          return params.STAGE == 'production'
        }
      }

      steps {
        sh "echo 'PRODUCTION'"
      }

    }
  }
}
