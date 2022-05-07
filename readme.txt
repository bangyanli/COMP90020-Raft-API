# COMP90020-Raft-API

PLEASE READ README.md.

### Project Description
This is the backend for team Handshake of comp90020 Distributed Algorithm, implementing raft algorithm

### Raft Structure

```
├───main
│   ├───java
│   │   └───com
│   │       └───handshake
│   │           └───raft
│   │               ├───common
│   │               │   ├───constants
│   │               │   ├───exceptions
│   │               │   ├───response
│   │               │   └───utils
│   │               ├───config
│   │               ├───controller
│   │               ├───dao
│   │               ├───raftServer
│   │               │   ├───log
│   │               │   │   └───Impl
│   │               │   ├───proto
│   │               │   │   └───Impl
│   │               │   ├───rpc
│   │               │   ├───service
│   │               │   │   └───Impl
│   │               │   └───ThreadPool
│   │               ├───service
│   │               │   └───Impl
│   │               └───test
│   └───resources
│       ├───static
│       └───templates
└───test
└───java
└───com
└───handshake
└───raft
```
It is a Springboot application with built-in raft server.
Controller package handles the request from web.
Service package handles the actions on database.
All things about raft are included in raftServer package exclude the configuration.
Configurations are in config package.

### raft 
```
raftServer
│   AddSelf.java
│   Election.java
│   ElectionTimer.java
│   Heartbeat.java
│   LifeCycle.java
│   Node.java
│   Role.java
│   Status.java
│
├───log
│   │   LogDatabase.java
│   │   LogInfo.java
│   │   LogSystem.java
│   │
│   └───Impl
│           LogDatabaseImpl.java
│           LogSystemImpl.java
│
├───proto
│   │   AddPeerParam.java
│   │   AddPeerResult.java
│   │   AppendEntriesParam.java
│   │   AppendEntriesResult.java
│   │   Command.java
│   │   GetClusterInfoParam.java
│   │   GetClusterInfoResult.java
│   │   LogEntry.java
│   │   RemovePeerParam.java
│   │   RemovePeerResult.java
│   │   RequestVoteParam.java
│   │   RequestVoteResult.java
│   │   ServerInfo.java
│   │
│   └───Impl
│           ChangeConfigurationCommand.java
│           CreateBookCommand.java
│           UploadChapterCommand.java
│
├───rpc
│       RpcClient.java
│       RpcServiceProvider.java
│
├───service
│   │   RaftConsensusService.java
│   │
│   └───Impl
│           RaftConsensusServiceImpl.java
│
└───ThreadPool
        RaftThreadPool.java
```
#### Basic functions
ThreadPool package provides manages the threads;
service package provides the services for rpc server and clients.
rpc package is used for rpc communication between nodes.
proto package defines the variable transmitted between nodes,
log package manages the log for raft.

#### node

Node first start as follower and start ```ElectionTimer```.

When election timeout elapses, the node change to candidate and run ```Election```.

When node receive majority votes, it becomes leader. It will stop the ```ElectionTimer``` and start ```Heartbeat```.

When node is started as new server, it will try to add itself to cluster using ```AddSelf```


### Environment
download [OpenJDK 15](https://jdk.java.net/archive/)

### Configuration
if you want to set the configuration, create a file name ``` application-1.yml ```
Then add```--spring.config.location=classpath:/application-1.yaml``` to ```Environment Variables```

basic node ``` application-1.yml ```
```yml
server:
  port: 5000

# common log level
logging:
  level:
    root: INFO
  # where to save system log
  file:
    name: "log1.log"


raft:
  # raft servers
  servers:
    - "localhost:5001"
  # the spring ip and port for this raft servers
  serversSpringAddress:
    - "localhost:5000"
  # self address for raft server
  self: "localhost:5001"
  electionTimeout: 7000
  heartBeatFrequent: 5000
  clientTimeout: 10000
  # where to save raft log
  log: "logs/log1.json"
  # where try to add itself to cluster when start
  newServer: false

# where to save books
library:
  address: "libraries/library1"

spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
```


Other nodes' property file
need to include at least one node in cluster
Here we include "localhost:5001"

new node``` application-2.yml ```
```yml
server:
  port: 9000

# common log level
logging:
  level:
    root: INFO
  file:
    name: "log2.log"

raft:
  servers:
    # node already in cluster
    - "localhost:5001"
    #self
    - "localhost:9001"
  serversSpringAddress:
    # Spring address of node already in cluster
    - "localhost:5000"
    #self Spring address
    - "localhost:9000"
  self: "localhost:9001"
  electionTimeout: 15000
  heartBeatFrequent: 5000
  clientTimeout: 10000
  log: "logs/log2.json"
  # try to add itself to cluster
  newServer: true

library:
  address: "libraries/library2"

spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
```

We already provided some pre-configured property file under ```src/main/resource```.

### Basic multi-server
We also include a basic implementation for basic multi-servers which removed all functionality in raft.
It is in ```bacis``` modules
