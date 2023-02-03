package net.just_s.ctpmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.ctpmod.CTPMod;
import net.just_s.ctpmod.config.Point;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AddCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register(
                literal(CTPMod.MOD_CMD).then(
                        literal("add").then(
                                argument("name", StringArgumentType.word()).then(
                                        argument("startPeriod", IntegerArgumentType.integer(0)).then(
                                                argument("endPeriod", IntegerArgumentType.integer(1)).
                                                        executes(AddCommand::run)
                                        )
                                )
                        )
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {
        String waypointName = ctx.getArgument("name", String.class);
        int startPeriod = ctx.getArgument("startPeriod", int.class);
        int endPeriod = ctx.getArgument("endPeriod", int.class);
        CTPMod.config.addPoint(new Point(waypointName, startPeriod, endPeriod));
        ctx.getSource().sendFeedback(CTPMod.generateFeedback(
                "Point §f{0} §aadded§2 with period: §f{1}-{2}§2.",
                waypointName, startPeriod, endPeriod
        ));
        return 1;
    }
}
