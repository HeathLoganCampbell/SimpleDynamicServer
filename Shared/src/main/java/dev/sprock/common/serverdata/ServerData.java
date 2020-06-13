package dev.sprock.common.serverdata;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerData
{
    private String address;
    private int port;

    private int playerCount = 0;
    private boolean joinable = true;
    private ServerType serverType = ServerType.UNKNOWN;

    private long lastUpdate = System.currentTimeMillis();
}
