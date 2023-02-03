package net.just_s.ctpmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.just_s.ctpmod.config.ConfigParser;
import net.just_s.ctpmod.config.Point;
import net.just_s.ctpmod.util.CommandManager;
import net.just_s.ctpmod.util.ReconnectThread;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CTPMod implements ClientModInitializer {
	public static final String MOD_ID = "ctpmod";
	public static final String MOD_CMD = "ctp";
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static Point[] points = new Point[0];
	public static int delta = 0;
	public static final ConfigParser config = ConfigParser.INSTANCE;
	public static ServerInfo currentServer = null;
	public static CTPMod INSTANCE = new CTPMod();
	private static ReconnectThread reconnectThread;


	@Override
	public void onInitializeClient() {
		CommandManager.registerCommands();
		ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> currentServer = client.getCurrentServerEntry());
	}

	public static void startReconnect(Point point) {
		Screen newScr = new DisconnectedScreen(
				new MultiplayerScreen(new TitleScreen()),
				Text.of("§8[§6CatTeleport§8]"),
				Text.of("startReconnect"));

		Objects.requireNonNull(CTPMod.MC.getNetworkHandler()).getConnection().disconnect(Text.translatable("reconnecting"));
		MC.disconnect();

		reconnectThread = new ReconnectThread(point.getStartPeriod(), point.getEndPeriod());
		reconnectThread.start();

		MC.setScreen(newScr);
	}

	public void finishReconnect() {
		connectToServer(currentServer);
	}

	public void cancelReconnect() {
		try {
			reconnectThread.join();
		} catch (InterruptedException | NullPointerException ignored) {
		}
		LOGGER.info("Reconnecting cancelled.");
		MC.setScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), Text.of("§8[§6CatTeleport§8]"), Text.of("cancelReconnect")));
	}

	public void connectToServer(ServerInfo targetInfo) {
		ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MC, ServerAddress.parse(targetInfo.address), targetInfo);
	}

	public static Text generateFeedback(String message, Object... args) {
		//Send message in chat that only user can see
		//§0  black			§8	dark_gray		§g	minecoin_gold
		//§1  dark_blue		§9	blue			§f	white
		//§2  dark_green	§a	green			§7	gray
		//§3  dark_aqua		§b	aqua			§e	yellow
		//§4  dark_red		§c	red				§6	gold
		//§5  dark_purple	§d	light_purple
		for (int i = 0; i < args.length; i++) {
			message = message.replace("{" + i + "}", args[i].toString());
		}
		return Text.of("§8[§6CatTeleport§8]§2 " + message);
	}
}
