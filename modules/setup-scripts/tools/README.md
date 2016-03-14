# Docker Registry Setup

This is consists of two shell scripts

    1.) Configure the client to loggin to the remote Registry (docker_setup.sh)
  
   This shell script is for configuring the client to connect with the Docker remote Registry. When the script is running, it asks for the domain name and the port of the remote registry. Then script would automatically adds the certificates and updates the client machine.Then client can docker pull, push with that remote registry. Followings are the main steps of the script.
I.   Copy the domain certificate from remote host to local host

II.  Update the ca-certificates

III. Update the Docker with insecure-registry

IV.  Add a host entry for the new domain of the registry

    2.) Build a Docker Registry (newRegistry.sh)
    
   When the script is run, it would create a docker registry with a self signed certificate.
   
I.   Create new certificate for the given domain name

II.  Map the container port and the Host port for the Registry

III. Run the docker registry with TLS enabled
