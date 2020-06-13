package dev.sprock.common.caller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sprock.common.Constants;
import dev.sprock.common.redis.RedisModule;
import dev.sprock.common.serverdata.ServerData;
import lombok.Getter;
import redis.clients.jedis.Jedis;

public class CallerModule extends RedisModule
{
    @Getter
    private ServerData selfData = new ServerData();
    private ObjectMapper objectMapper = new ObjectMapper();

    public CallerModule(String host, int port)
    {
        super(host, port);
    }

    public void pushServerData() throws JsonProcessingException
    {
        Jedis connection = this.getConnection();

        String serverdataJson = objectMapper.writeValueAsString(this.selfData);
        connection.publish(Constants.MSG_CHANNEL, serverdataJson);

        connection.close();
    }
}
