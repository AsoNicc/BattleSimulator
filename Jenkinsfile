pipeline {
   agent any

   stages {
      stage('Verify Branch') {
         steps {
            echo "$GIT_BRANCH"
         }
      }
      stage('Docker Build') {
         steps {
            bat(script: """
            	@echo off
            	for %%i in (.) do echo a=%%i
            """)
         }
      }
   }
}
