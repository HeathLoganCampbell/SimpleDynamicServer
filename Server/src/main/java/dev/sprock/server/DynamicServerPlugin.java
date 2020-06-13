package dev.sprock.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.sprock.common.Constants;
import dev.sprock.common.caller.CallerModule;
import dev.sprock.common.serverdata.ServerData;
import dev.sprock.common.serverdata.ServerType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*
This end, we are just constantly uploading data


 */
public class DynamicServerPlugin extends JavaPlugin
{
    @Getter
    private CallerModule callerModule;

    @Override
    public void onEnable()
    {
        String address = "localhost";
        int port = 6379;

        this.callerModule = new CallerModule(address, port);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                ServerData selfData = this.callerModule.getSelfData();
                selfData.setPlayerCount(Bukkit.getOnlinePlayers().size());
                selfData.setAddress(Bukkit.getIp());
                selfData.setLastUpdate(System.currentTimeMillis());
                selfData.setPort(Bukkit.getPort());
                this.callerModule.pushServerData();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }, 10L, 10L);
    }

    @Override
    public void onDisable()
    {
        this.callerModule.shutdown();
    }

    public void setJoinnable(boolean isJoinable)
    {
        this.getCallerModule().getSelfData().setJoinable(isJoinable);
    }

    public void setServerType(ServerType serverType)
    {
        this.getCallerModule().getSelfData().setServerType(serverType);
    }

    public void sendToBestServer(Player player, ServerType serverType)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverType.name());
        player.sendPluginMessage(this, Constants.SEND_PLAYER_HEADER, out.toByteArray());
    }
}
