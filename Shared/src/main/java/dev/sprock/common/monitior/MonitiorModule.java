package dev.sprock.common.monitior;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sprock.common.Constants;
import dev.sprock.common.redis.RedisModule;
import dev.sprock.common.serverdata.ServerData;
import dev.sprock.common.serverdata.ServerType;
import lombok.SneakyThrows;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MonitiorModule extends RedisModule
{
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<ServerData> serverDatas = new LinkedList<>();

    public MonitiorModule(String host, int port)
    {
        super(host, port);

        JedisPubSub subscriber = new JedisPubSub() {
            @SneakyThrows
            @Override
            public void onMessage(String channel, String message)
            {
                // handle message
                ServerData server = objectMapper.readValue(message, ServerData.class);
                saveNewServerData(server);
                System.out.println("recieved) " + server.toString());
            }
        };

        new Thread(() -> {
            try {
                this.getConnection().subscribe(subscriber, Constants.MSG_CHANNEL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean isSimilar(ServerData a, ServerData b)
    {
        if(a.getPort() != b.getPort()) return false;
        if(!a.getAddress().equalsIgnoreCase(b.getAddress())) return false;
        return true;
    }

    private void saveNewServerData(ServerData serverData)
    {

        boolean removed = this.serverDatas.removeIf(otherServerData -> this.isSimilar(serverData, otherServerData));
        this.serverDatas.add(serverData);

        if(!removed)
        {
            System.out.println("New server found " + serverData.getServerType() + " " + serverData.getAddress() + ":" + serverData.getPort());
            onNewServerDetected(serverData);
        }
    }

    public ServerData getBestServerType(ServerType serverType)
    {
        ServerData bestServer = null;
        int playerCount = 0;
        for (ServerData serverData : this.serverDatas) {
            if(serverData.getServerType() == serverType)
            {
                if(serverData.isJoinable())
                    if(serverData.getPlayerCount() >= playerCount) {
                        bestServer = serverData;
                        playerCount = serverData.getPlayerCount();
                    }
            }
        }
        return bestServer;
    }

    public void onNewServerDetected(ServerData newServerData)
    {

    }
}
