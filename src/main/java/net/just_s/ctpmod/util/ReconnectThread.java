package net.just_s.ctpmod.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.just_s.ctpmod.CTPMod;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class ReconnectThread extends Thread {
    private final int secondsToReconnect;

    public ReconnectThread(int start, int end) {
        super();
        this.secondsToReconnect = (end - start) / 2 + start;
    }

    /**
     * Tries to connect to the server using a socket as many times as is set, and returns if it could connect
     */
    @Override
    public void run() {
        try {
            int[] s = {secondsToReconnect - CTPMod.delta, 0};
            CTPMod.LOGGER.info("reconnect in " + Collections.max(Arrays.asList(ArrayUtils.toObject(s))) + " sec");

            Thread.sleep((Collections.max(Arrays.asList(ArrayUtils.toObject(s)))) * 1000L);
            synchronized (this) {
                CTPMod.LOGGER.info("Reconnecting to server.");
                MinecraftClient.getInstance().execute(CTPMod.INSTANCE::finishReconnect);
            }
            return;
        } catch (/*IOException* |*/ InterruptedException e) {
            CTPMod.LOGGER.error("Reconnection failed. Reason: " + e);
        }
        MinecraftClient.getInstance().execute(CTPMod.INSTANCE::cancelReconnect);
    }
}
