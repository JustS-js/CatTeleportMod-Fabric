package net.just_s.ctpmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.ctpmod.CTPMod;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DelCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(
                literal(CTPMod.MOD_CMD).then(
                        literal("delete").then(
                                argument("name", StringArgumentType.word()).
                                                        executes(DelCommand::run)
                        )
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        String waypointName = ctx.getArgument("name", String.class);
        if (CTPMod.config.deletePoint(waypointName)) {
            ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                    "Point §f{0} §cdeleted§2.",
                    waypointName
            ));
        } else {
            ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                    "Point §f{0} §cwas not deleted due to some issues§2.",
                    waypointName
            ));
        }
        return 1;
    }
}
