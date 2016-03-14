# RemoteDockerRegistry

This is consists of two shell scripts

    1.) Build a Docker Registry (newRegistry.sh)
I.   Create new certificate for the given domain name

II.  Map the container port and the Host port for the Registry

III. Run the docker registry with TLS enabled

    2.) Configure the client to loggin to the remote Registry (remoteRegistryConfig.sh)
I.   Copy the domain certificate from remote host to local host

II.  Update the ca-certificates

III. Update the Docker with insecure-registry

IV.  Add a host entry for the new domain of the registry

