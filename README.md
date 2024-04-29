# General Prerequisites
The following version are needed to run the server executable as well as the client apllications:
* Maven
* Java 17 or higher

---

# Errors
In case of any error while running any of the instructions, you can refer to the ``Common Problems`` section at the end of the document to find some solutions.

---

# Deploying server
## Prerequisites
Create the file named ``.env`` inside the project´s root path with the variable ``SFTP_USER``with the credentials of your ``SSH`` server.
For example:

    SFTP_USER=user@server

***Note*** : The file name is ``.env``. If you use a different name it will not work.

---
Follow these instructions to deploy and run the server:
1. Run ``chmod u+x deploy.sh``
2. Run the ``deploy.sh`` script inside the project root path.
3. An SFTP prompt for typing password of the ssh server should appear
4. Enter your password
5. Type ``put server/target/tpe1-g1-server-2024.1Q-bin.tar.gz /path-in-ssh-server``. Then you can ``exit``.
6. Enter using ``ssh`` to your server.
7. You would find the ``.tar.gz`` in the ``/path-in-ssh-server``
8. Run ``tar -xzvf tpe1-g1-server-2024.1Q-bin.tar.gz``
9. Run ``cd tpe1-g1-server-2024.1Q``
10. Run ``chmod u+x run-server.sh``
11. Run ``sh run-server.sh -Dport=[portNumber]``

Alternatively, if you simply want to run the server locally or in a different environment you can follow steps ``7`` to ``9``, previously running:
1. ``mvn clean package -DskipTests`` inside project´s root path.
2. ``cd server/target/``

Note that you have to skip tests when running mvn clean package because they do not work as a suite. If you wish to run tests you will have to do so individually for each test.

In case of any problem in the previous steps, refer to section ``common problems`` at the end of the document.

---

# Executing Clients
## Prerequisites
1. Run ```mvn clean package -DskipTests``` from project´s root path.
2. ``cd client/target/``
3. ```tar -xzvf tpe1-g1-client-2024.1Q-bin.tar.gz```
4. ```sudo chmod u+x tpe1-g1-client-2024.1Q/*```
5. ```cd tpe1-g1-client-2024.1Q```

If server is deployed in an SSH server you can also run your client locally and establish a connection remotely.

``ssh -L [localPort]:[remoteHost]:[remotePort] user@server
``

---
## Admin Client
Run ```sh adminClient.sh``` with the following options

    -DServerAddress=xx.xx.xx.xx:yyyy
    -Daction=actionName
    [ -Dsector=sectorName | -Dcounters=counterCount 
    | -DinPath = manifestPath]

Actions available:

    [addSector | addCounters | manifest]

### Add a Sector
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addSector -Dsector=[sectorName]
### Add a counter range
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addCounters -Dsector=[sectorName] -Dcounters=[counterCount]
### Add expected passengers
    sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=manifest
    -DinPath=[manifestPath]
Manifest csv should have the following headers

    booking;flight;airline
    [bookingCode;flightCode;airlineName]

---

## Counter Client
Run ``sh counterClient.sh`` with the following options

    -DServerAddress=xx.xx.xx.xx:yyyy
    -Daction=actionName
    [ -Dsector=sectorName | -DcounterFrom=fromVal |
    -DcounterTo=toVal | -Dflights=flights | -Dairline=airlineName |
    -DcounterCount=countVal ]

### Querying Sectors
    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=listSectors

### Querying Counters range
    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=listCounters -Dsector=[sectorName] 
    -DcounterFrom=[fromVal] -DcounterTo=[toVal]

### Assign Counter Range
    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=assignCounters -Dsector=[sectorName] -Dflights=[fligh1|fligh2|...]
    -Dairline=[airlineName] -DcounterCount=[countVal]

### Free Counter Range
    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=freeCounters -Dsector=[sectorName] -DcounterFrom=[fromVal] 
    -Dairline=[airlineName]

### Check in for each Counter

    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=checkinCounters -Dsector=[sectorName] 
    -DcounterFrom=[fromValue] -Dairline=[airlineName]

### Query pending assignments

    sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=listPendingAssignments -Dsector=[sectorName]

---

## Passenger Client
Run ``sh passengerClient.sh`` with the following options

    -DServerAddress=xx.xx.xx.xx:yyyy
    -Daction=actionName
    [ -Dbooking=booking | -Dsector=sectorName |
    -Dcounter=counterNumber ]

### Get assigned for check-in Counter Range
    sh passengerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=fetchCounter -Dbooking=[booking]

### Join Counter Range queue
    sh passengerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=passengerCheckin -Dbooking=[booking] -Dsector=[sectorName]
    -Dcounter=[counterNumber]

### Query check-in status

    sh passengerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=passengerStatus -Dbooking=[booking]

---

## Events Client
Run ``eventsClient.sh`` with the following options

    -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=actionName -Dairline=airlineName

### Register an airline notification

    sh eventsClient.sh -DserverAddress=xx.xx.xx.xx:yyyy 
    -Daction=register -Dairline=[airlineName]

### Cancel airline registration

    sh eventsClient.sh -DserverAddress=xx.xx.xx.xx:yyyy 
    -Daction=unregister -Dairline=[airlineName]

### Query airline notifications history

    sh eventsClient.sh -DserverAddress=xx.xx.xx.xx:yyyy 
    -Daction=history -Dairline=[airlineName]

---

## Query Client
Run ``queryClient.sh`` with the following options

    -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
    [ -Dsector=sectorName | -Dairline=airlineName |
    -Dcounter=counterVal | -DoutPath=queryPath]

### Query counters state

    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=queryCounters -DoutPath=[queryPath]

Filtering by sector

    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy
    -Daction=queryCounters -DoutPath=[queryPath] -Dsector=[sectorName]


### Query completed check-ins

    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkins
    -DoutPath=[queryPath]

Filtering by sector
    
    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkins
    -DoutPath=[queryPath] -Dsector=[sectorName]

Filtering by airline
    
    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkins
    -DoutPath=[queryPath] -Dairline=[airlineName]

Filtering by airline and sector

    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkins
    -DoutPath=[queryPath] -Dairline=[airlineName] -Dsector=[sectorName]

### Query counter history

    sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=history
    -DoutPath=[airlineName] -Dsector=[sectorName] -Dcounter=[counterVal]

---

# Common Problems
In this section you can find some common problems and their possible solutions.
## Java Version
To be sure you have set the appropriate JRE and JDK version you can run
``java -version`` for JRE

``javac -version`` for JDK

Both versions should be 17 or higher.

If your version is lower, you should install the appropriate versions

Once installed you can use them by setting the ``JAVA_HOME`` and ``PATH`` environment variables.

``export JAVA_HOME=/your-jdk-path`` 

``export PATH=$JAVA_HOME/bin:$PATH``

Usually the jdk path is ``/lib/jvm/``

This solution is temporally, and you could still have the problem after restarting your console. To solve the problem permanently consider using ``~/.bashrc`` or ``~/.profile`` files to keep the value of the declared environment variables.

## Java class not found
When running the ``run-server.sh`` script or any of the ``.sh`` client scripts maybe you can find an error about a ``"Java class not found"``

This could happend if the scripts where written in a Windows system where the end of line is ``\r\n``.

To get rid of such problem you can try running ``sed -i 's/\r$//' script.sh``

## Bad Interpreter

If you see a problem related with the bash interpreter try running it with
``bash script.sh`` or ``sh script.sh``

## Syntax error 

Is the same problem as ```Java Class Not Found```

You must replace the ``\r\n`` inside the script.

Refer to the ```Java Class Not Found``` section to see a solution.

## SFTP Prompt
If the SFTP prompt does not appear when running ``deploy.sh`` maybe the ``.env`` file you create has a diferrent name. For example a ``credentials.env`` woudl be invalid.

Maybe you don´t see the prompt because SFTP ask you to configure the RSA key with the SSH server.
Type ``y`` and then the password prompt should appear.

