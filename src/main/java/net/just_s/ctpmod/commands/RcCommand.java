package net.just_s.ctpmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.ctpmod.CTPMod;
import net.just_s.ctpmod.config.Point;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RcCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(
                literal(CTPMod.MOD_CMD).then(
                        literal("rc").then(
                                argument("seconds", IntegerArgumentType.integer(1)).
                                        executes(RcCommand::run)
                        )
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        int seconds = ctx.getArgument("seconds", int.class);
        if (CTPMod.MC.isInSingleplayer()) {
            ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                    "§cCTPMod doesn't work in §4SinglePlayer§c. You can use §dNether Portals§c instead of rejoining."
            ));
            return 1;
        }
        CTPMod.startReconnect(new Point(null, seconds, seconds));
        return 1;
    }
}
