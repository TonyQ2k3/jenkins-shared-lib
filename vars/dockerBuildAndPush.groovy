def call(Map config = [:]) {
    def registry = config.registry ?: 'docker.io'
    def imageName = config.imageName
    def tag = config.tag ?: 'latest'
    def credentialsId = config.credentialsId

    if (!imageName || !credentialsId) {
        error "Missing required parameters: imageName and credentialsId must be provided."
    }

    def fullImageName = "${registry}/${imageName}:${tag}"

    // Build the Docker image
    echo "Building Docker image: ${fullImageName}"
    sh "docker build -t ${fullImageName} ."

    // Log in to the Docker registry
    echo "Pushing Docker image to registry: ${registry}"
    withDockerRegistry([credentialsId: credentialsId, url: "https://${registry}"]) {
        sh "docker push ${fullImageName}"
    }

    echo "Docker image ${fullImageName} pushed successfully."
}