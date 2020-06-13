# Dynamic Server
* Track servers
* Sends players to best minigame server
* runs on both the server and proxy
* VelocityPowered
* Pretty simple

## What does it do
The servers on start up will send updates to the proxy
server every 500ms (10 ticks), with infomation such as
player count, joinablity, ip and port. the proxy can
use this infomation to redirect players to this server
when a game has ended on a past server (Survival games / Spleef / Skywars)
Thus sending them to a server that already has players on it
ready to go.

The proxy will also dynamically add and remove servers
based on the responses sent. eg if they server hasn't replied
in 10 seconds, it's likely offline. 

## Compile
`Mvn clean install`
jar is exported to `plugins` and you will want 
to get the `AllInOne.jar` which works for both 
the server and proxy.

## Parts
### Shared
Resources used between both proxy and server versions

### Server
Spigot / Paper wrapper and interface

### Proxy
Velocity wrapper