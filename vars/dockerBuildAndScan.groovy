def call(Map config = [:]) {
    def imageName = config.imageName
    def tag = config.tag ?: 'test'

    if (!imageName || !credentialsId || !organization) {
        error "Missing required parameters: imageName must be provided."
    }

    def fullImageName = "${imageName}:${tag}"

    // Build the Docker image
    echo "Building Docker image: ${fullImageName}"
    sh "docker build -t ${fullImageName} ."
    echo "Docker image ${fullImageName} built successfully."

    // Scan the Docker image
    echo "Scanning Docker image: ${fullImageName}"
    sh "docker run aquasec/trivy image ${fullImageName}"
    echo "Docker image ${fullImageName} scanned completed."
}