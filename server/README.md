## Build
```
cd thredUp/server
gradle clean distZip
```

## Properties
| Property | Description | Default |
|----------|-------------|---------|
| inactivity.timeout.sec | Max timeout in secs for allowed inactivity from client | 10 sec |
| server.port | Port on which the server will server | 7000 |
| simulated.device.count | Number of simulated devices server will keep track of. Device ID 1 to count. | 100 |

### Set Properties
Export before starting server to set properties.
```
export SERVER_OPTS="-Dinactivity.timeout.sec=1 -Dserver.port=5000 -Dsimulated.device.count=50"
```

## Run
```
cd thredUp/server/build/distributions
unzip server-0.1-SNAPSHOT.zip
cd server-0.1-SNAPSHOT/bin
./server
```
