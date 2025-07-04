def call(Map config = [:]) {
    def registry = config.registry ?: 'docker.io'
    def organization = config.organization
    def imageName = config.imageName
    def tag = config.tag ?: 'latest'
    def credentialsId = config.credentialsId

    if (!imageName || !credentialsId || !organization) {
        error "Missing required parameters: organization, imageName and credentialsId must be provided."
    }

    def fullImageName = "${organization}/${imageName}:${tag}"

    // Build the Docker image
    echo "Building Docker image: ${fullImageName}"
    sh "docker build -t ${fullImageName} ."

    // Log in to the Docker registry
    echo "Logging in to Docker registry: ${registry}"
    withCredentials([usernamePassword(
        credentialsId: credentialsId, 
        usernameVariable: 'DOCKER_USERNAME', 
        passwordVariable: 'DOCKER_PASSWORD')
    ]) {
        sh "docker login ${registry} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
    }

    echo "Pushing Docker image to registry: ${registry}"
    sh "docker push ${fullImageName}"

    echo "Docker image ${fullImageName} pushed successfully."
}