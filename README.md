# Create another Apace Beam project

mvn archetype:generate -DarchetypeGroupId=org.apache.beam -DarchetypeArtifactId=beam-sdks-java-maven-archetypes-examples
-DarchetypeVersion=2.46.0 -DgroupId=com.ursolutions.dataflow.beam -DartifactId=mike-dataflow-beam
-Dversion=\"0.1\" -Dpackage=com.ursolutions.dataflow.beam -DinteractiveMode=false

## To start
For your convenience, there is a folder with static content(fedora docs site) that is wrapped in Docker image
To wrap run:
```shell
  cd website-serving/fedora  
  docker build -t apache-server-fedora-example .
```

After creating the image you can run it
```shell
    docker container run -dit --name fedora-docs-server -p 8080:80 apache-server-fedora-example
```