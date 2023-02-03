package net.just_s.ctpmod.util;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.just_s.ctpmod.commands.AddCommand;
import net.just_s.ctpmod.commands.DelCommand;
import net.just_s.ctpmod.commands.RcCommand;
import net.just_s.ctpmod.commands.TpCommand;
import net.just_s.ctpmod.commands.ListCommand;

public class CommandManager {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(AddCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(DelCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(TpCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(ListCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(RcCommand::register);
    }
}
