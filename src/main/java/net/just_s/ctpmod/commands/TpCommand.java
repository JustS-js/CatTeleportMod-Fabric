package net.just_s.ctpmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.ctpmod.CTPMod;
import net.just_s.ctpmod.config.Point;
import net.minecraft.command.CommandRegistryAccess;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TpCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(
                literal(CTPMod.MOD_CMD).then(
                        literal("tp").then(
                                argument("name", StringArgumentType.word()).
                                        suggests(TpCommand::suggest).executes(TpCommand::run)
                        )
                )
        );
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        for (Point point : CTPMod.points) {
            builder.suggest(point.getName());
        }
        return builder.buildFuture();
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        if (CTPMod.MC.isInSingleplayer()) {
            ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                    "§cCTPMod doesn't work in §4SinglePlayer§c. You can use §dNether Portals§c instead of rejoining."
            ));
            return 1;
        }
        String waypointName = ctx.getArgument("name", String.class);
        Point curPoint = null;
        for (Point point : CTPMod.points) {
            if (Objects.equals(point.getName(), waypointName)) {
                curPoint = point;
                break;
            }
        }
        if (curPoint == null) {
            ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                    "§cThere is no §fPoint §cwith name \"§f{0}§c\".",
                    waypointName
            ));
            return 0;
        }

        //if everything is okay, only then start reconnect cycle:
        CTPMod.startReconnect(curPoint);
        return 1;
    }
}
