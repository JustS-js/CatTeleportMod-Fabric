package net.just_s.ctpmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.just_s.ctpmod.config.ConfigParser;
import net.just_s.ctpmod.config.Point;
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
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static Point[] points = new Point[0];
	public static int delta = 0;
	public static final ConfigParser config = ConfigParser.INSTANCE;
	public static ServerInfo server = null;
	public static CTPMod INSTANCE = new CTPMod();
	private ReconnectThread reconnectThread;


	@Override
	public void onInitializeClient() {
	}

	public void listPoints() {
		String[] pointRepr = new String[points.length];
		for (int i = 0; i < points.length; i++) {pointRepr[i] = points[i].toString();}
		String message = "list of loaded Points:\n" + String.join("\n", pointRepr);
		printInGame(message);
	}

	public void addPoint(String[] args) {
		String message;
		try {
			if (args.length != 3) {throw new ArrayIndexOutOfBoundsException("Too much or not enough arguments.");}
			String waypointName = args[1];
			String[] numbers = args[2].split("-");
			int startPeriod = Integer.parseInt(numbers[0]);
			int endPeriod = Integer.parseInt(numbers[1]);
			message = "Point §f" + waypointName + " §aadded§2 with period: §f" + startPeriod + "-" + endPeriod + "§2.";

			config.addPoint(new Point(waypointName, startPeriod, endPeriod));
		}
		catch (NumberFormatException e) { message = "bad Period argument. §c" + e;}
		catch (ArrayIndexOutOfBoundsException  e) { message = "wrong number of arguments. §c" + e;}
		catch (ClassCastException  e) { message = "wrong type of argument. §c" + e;}
		catch (IllegalArgumentException e) { message = "bad writing. §c" + e;}


		printInGame(message);
	}

	public void delPoint(String[] args) {
		String message;
		try {
			if (args.length != 2) {throw new ArrayIndexOutOfBoundsException("Too much or not enough arguments.");}
			String waypointName = args[1];
			if (config.deletePoint(waypointName)) {
				message = "Point §f" + waypointName + " §cdeleted§2.";
			} else {
				message = "Point §f" + waypointName + " §cwas not deleted due to some issues§2.";
			}
		}
		catch (NumberFormatException e) { message = "bad Period argument. §c" + e;}
		catch (ArrayIndexOutOfBoundsException  e) { message = "wrong number of arguments. §c" + e;}
		catch (ClassCastException  e) { message = "wrong type of argument. §c" + e;}
		catch (IllegalArgumentException e) { message = "bad writing. §c" + e;}

		printInGame(message);
	}

	public void teleport(String[] args) {
		//Doesn't work in singleplayer, getCurrentServerEntry() returns null
		if (!MC.isInSingleplayer()) {
			if (args.length != 2) {throw new IllegalArgumentException("Too much or not enough arguments.");}
			Point curPoint = null;
			for (Point point : points) {
				if (Objects.equals(point.getName(), args[1])) {
					curPoint = point;
					break;
				}
			}
			if (curPoint == null) {
				printInGame("§cThere is no §fPoint §cwith name \"§f" + args[1] + "§c\".");
				return;
			}
			if (curPoint.getStartPeriod() < 0) {
				printInGame("§cCan't teleport to §fPoint §cwith negative §fstartPeriodTime§c.");
				return;
			}

			//if everything is okay, only then start reconnect cycle:
			startReconnect(curPoint);
		} else {
			printInGame("§cCTPMod doesn't work in §4SinglePlayer§c. You can use §dNether Portals§c instead of rejoining.");
		}
	}

	public void rawTp(String[] args) {
		if (!MC.isInSingleplayer()) {
			if (args.length != 2) {throw new IllegalArgumentException("Too much or not enough arguments.");}
			int time = Integer.parseInt(args[1]);

			if (time < 0) {
				printInGame("§cCan't reconnect with negative §fstartPeriodTime§c.");
				return;
			}

			//if everything is okay, only then start reconnect cycle:
			startReconnect(new Point("null", time, time));
		} else {
			printInGame("§cCTPMod doesn't work in §4SinglePlayer§c. You can use §dNether Portals§c instead of rejoining.");
		}
	}

	public void startReconnect(Point point) {
		Screen newScr = new DisconnectedScreen(
				new MultiplayerScreen(new TitleScreen()),
				Text.of("§8[§6CatTeleport§8]"),
				Text.of("startReconnect"));

		server = MC.getCurrentServerEntry();
		ClientConnection con = Objects.requireNonNull(MC.getNetworkHandler()).getConnection();
		//con.disconnect(new LiteralText("§8[§6CatTeleport§8]"));

		reconnectThread = new ReconnectThread(server, point.getStartPeriod(), point.getEndPeriod());
		reconnectThread.start();

		newScr.init(MC, 0, 0);
		MC.disconnect(newScr);
	}

	public void finishReconnect() {
		connectToServer(server);
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

	public static void printInGame(String... msgargs) {
		//Send message in chat that only user can see
		//§0  black			§8	dark_gray		§g	minecoin_gold
		//§1  dark_blue		§9	blue			§f	white
		//§2  dark_green	§a	green			§7	gray
		//§3  dark_aqua		§b	aqua			§e	yellow
		//§4  dark_red		§c	red				§6	gold
		//§5  dark_purple	§d	light_purple
		String msg = String.join(", ", msgargs);
		MC.inGameHud.getChatHud().addMessage(Text.of("§8[§6CatTeleport§8]§2 " + msg));
	}
}
