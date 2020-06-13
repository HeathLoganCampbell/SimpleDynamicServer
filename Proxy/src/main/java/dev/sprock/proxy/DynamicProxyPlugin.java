package dev.sprock.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.sprock.common.Constants;
import dev.sprock.common.monitior.MonitiorModule;
import dev.sprock.common.serverdata.ServerData;
import dev.sprock.common.serverdata.ServerType;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

@Plugin(id = "dynamicserver", name = "DynamicServer", version = "1.0",
        description = "A server scalar", authors = { "Sprock", "SprockPls" })
public class DynamicProxyPlugin
{
    private static final LegacyChannelIdentifier TOURGUIDE_CHANNEL = new LegacyChannelIdentifier(Constants.SEND_PLAYER_HEADER);


    private final ProxyServer server;
    private final Logger logger;
    private MonitiorModule monitiorModule;

    @Inject
    public DynamicProxyPlugin(ProxyServer server, Logger logger)
    {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe public void onProxyInitialization(ProxyInitializeEvent event) { this.onEnable(); }
    @Subscribe public void onProxyDisable(ProxyShutdownEvent e) { this.onDisable(); }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event)
    {
        if (!event.getIdentifier().equals(TOURGUIDE_CHANNEL)) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        ServerConnection connection = (ServerConnection) event.getSource();
        ByteArrayDataInput in = event.dataAsDataStream();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subChannel = in.readUTF();

        if (subChannel.equals("Connect"))
        {
            String serverTypeStr = in.readUTF();
            ServerType serverType = ServerType.valueOf(serverTypeStr);

            ServerData bestServerType = this.monitiorModule.getBestServerType(serverType);

            Iterator<RegisteredServer> iterator = server.getAllServers().iterator();
            while(iterator.hasNext())
            {
                RegisteredServer registeredServer = iterator.next();
                ServerInfo serverInfo = registeredServer.getServerInfo();
                if(serverInfo.getAddress().getPort() == bestServerType.getPort()
                    && serverInfo.getAddress().getAddress().getHostAddress().equalsIgnoreCase(bestServerType.getAddress()))
                {
                    connection.getPlayer().createConnectionRequest(registeredServer).fireAndForget();
                }
            }
        }
    }

    public void onEnable()
    {
        server.getChannelRegistrar().register(TOURGUIDE_CHANNEL);
        //        server.getEventManager().register(this, new PluginListener());

        String address = "localhost";
        int port = 6379;

        this.monitiorModule = new MonitiorModule(address, port)
        {
            @Override
            public void onNewServerDetected(ServerData newServerData)
            {
                String serverName = newServerData.getServerType().name() + newServerData.getPort();
                ServerInfo info = new ServerInfo(
                        serverName,
                        new InetSocketAddress(newServerData.getAddress(), newServerData.getPort()));
                server.registerServer(info);
            }
        };
    }

    public void onDisable()
    {
        this.monitiorModule.shutdown();
    }
}
