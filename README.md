# General Prerequisites
The following version are needed to run the server executable as well as the client apllications:
* Maven
* Java 17 or higher
# Deploying server
## Prerequisites
Create a ``.env`` file inside the project´s root path with the variable ``SFTP_USER``with the credentials of your ``SSH`` server.
For example:

    SFTP_USER=[user]@[server]

---
Follow these instructions to deploy and run the server:

1. Run the ``deploy.sh`` script inside the project root path.
2. An SFTP prompt for typing password of the ssh server should appear
3. Enter your password
4. Type ``put server/target/tpe1-g1-server-2024.1Q-bin.tar.gz /path-in-ssh-server``.
5. Enter using ``ssh`` to your server.
6. You would find the ``.tar.gz`` in the ``/path-in-ssh-server``
7. Run ``tar -xzvf tpe1-g1-server-2024.1Q-bin.tar.gz``
8. Run ``cd tpe1-g1-server-2024.1Q``
9. Run ``chmod u+x run-server.sh``
10. Run ``sh run_server.sh -Dport=[portNumber]``

Alternatively, if you simply want to run the server locally or in a different environment you can follow steps ``7`` to ``9``, previously running:
1. ``mvn clean package`` inside project´s root path.
2. ``cd server/target/``
# Executing Clients
## Prerequisites
1. Run ```mvn clean package``` from project´s root path.
2. ``cd client/target/``
3. ```tar -xzvf tpe-g1-client-2024.1Q-bin.tar.gz```
4. ```sudo chmod u+x tpe1-g1-client-2024-1Q/*```
5. ```cd tpe1-g1-client-2024-1Q```

If server is deployed in an SSH server you can also run your client locally and establish a connection remotely.

``ssh -L [localPort]:[remoteHost]:[remotePort] [user]@[server]
``
## Admin Client
Run ```sh adminClient.sh``` with the following options

    -DServerAddress=xx.xx.xx.xx:yyyy
    -Daction=actionName
    [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath = manifestPath]

Actions available:

    [addSector | addCounters | manifest]

### Add a Sector
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addSector
    -Dsector=[sectorName]
### Add a counter range
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addCounters
    -Dsector=[sectorName] -Dcounters=[counterCount]
### Add expected passengers
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=manifest
    -DinPath=[manifestPath]
Manifest csv should have the following headers

    booking;flight;airline
    [bookingCode;flightCode;airlineName]