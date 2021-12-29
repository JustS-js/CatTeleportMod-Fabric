package net.just_s.ctpmod.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.just_s.ctpmod.CTPMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class ReconnectThread extends Thread {
    private final int secondsToReconnect;
    private final ServerAddress serverAddress;

    public ReconnectThread(ServerInfo serverInfo, int secondsToReconnect) {
        super();
        this.secondsToReconnect = secondsToReconnect;
        this.serverAddress = ServerAddress.parse(serverInfo.address);
    }

    /**
     * Tries to connect to the server using a socket as many times as is set, and returns if it could connect
     */
    @Override
    public void run() {
        try {
            int[] s = {secondsToReconnect - 1, 0};
            Thread.sleep(Collections.max(Arrays.asList(ArrayUtils.toObject(s))) * 1000L);
            for (int i1 = 0; i1 < 10; i1++) {
                pingServer();
            }
            synchronized (this) {
                CTPMod.LOGGER.info("Reconnecting to server.");
                MinecraftClient.getInstance().execute(() -> CTPMod.INSTANCE.finishReconnect());
            }
            return;
        } catch (IOException | InterruptedException e) {
            CTPMod.LOGGER.debug("Reconnection failed. Reason: " + e.getMessage());
        }
        MinecraftClient.getInstance().execute(() -> CTPMod.INSTANCE.cancelReconnect());
    }

    private void pingServer() throws IOException, InterruptedException {
        long startTime = System.nanoTime();
        Socket connectionSocket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
        connectionSocket.close();
        long endTime = System.nanoTime();
        if (endTime - startTime > 2 * 1e+9)
            throw new IOException("Ping was greater than five seconds, being " + (endTime - startTime) * 1e-9);
    }
}
