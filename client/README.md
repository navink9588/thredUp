## Build
```
cd thredUp/client
gradle clean distZip
```

## Properties
| Property | Description | Default |
|----------|-------------|---------|
| server.port | Port on which to connect to the server | 7000 |
| server.host | Hostname where the server is running | localhost |

### Set Properties
Export before starting server to set properties.
```
export CLIENT_OPTS="-Dserver.port=5000 -Dserver.host=thredup"
```

## Run
```
cd thredUp/client/build/distributions
unzip client-0.1-SNAPSHOT.zip
cd client-0.1-SNAPSHOT/bin
./client
```
