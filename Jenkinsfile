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
         	pwsh(script: """
               cd src/Game/Pokemon/
            """)
         }
      }
   }
}
