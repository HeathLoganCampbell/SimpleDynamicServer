package dev.sprock.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sprock.common.serverdata.ServerData;
import dev.sprock.common.serverdata.ServerType;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisModule
{
    public static final String CHANNEL_NAME = "REDIS_EXAMPLE";
    private JedisPool pool;

    public RedisModule(String host, int port)
    {
        this.pool = new JedisPool(host, port);

    }

    public void shutdown()
    {
        if(this.pool != null && !this.pool.isClosed())
            this.pool.close();
    }

    public Jedis getConnection()
    {
        return this.pool.getResource();
    }

//    public static void main(String[] args) throws JsonProcessingException {
////        new RedisModule("127.0.0.1", 6379);
//        JedisPool pool = new JedisPool("localhost", 6379);
//        Jedis jedis = pool.getResource();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        JedisPubSub subscriber = new JedisPubSub() {
//            @SneakyThrows
//            @Override
//            public void onMessage(String channel, String message)
//            {
//                // handle message
//                ServerData server = objectMapper.readValue(message, ServerData.class);
//                System.out.println("recieved) " + server.toString());
//            }
//        };
//
//        new Thread(() -> {
//            try {
//                pool.getResource().subscribe(subscriber, CHANNEL_NAME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        ServerData serverData = new ServerData("localhost", 25565, 1, true, ServerType.SURVIVAL_GAMES, System.currentTimeMillis());
//
//        long start = System.currentTimeMillis();
//        String serverdataJson = objectMapper.writeValueAsString(serverData);//30ms
//        jedis.publish(CHANNEL_NAME, serverdataJson);//20ms
//        System.out.println((System.currentTimeMillis() - start) + "ms");
//
//
//        jedis.close();
//        pool.close();
//    }
}
