# Executing Clients
## Prerequisites
1. ```tar -xzvf tpe-g1-client-2024.1Q-bin.tar.gz```
2. ```sudo chmod u+x tpe1-g1-client-2024-1Q/*```
3. ```cd tpe1-g1-client-2024-1Q```
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