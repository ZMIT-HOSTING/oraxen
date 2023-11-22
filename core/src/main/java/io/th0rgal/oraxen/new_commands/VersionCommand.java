package io.th0rgal.oraxen.new_commands;

import cloud.commandframework.Command;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.config.Message;
import io.th0rgal.oraxen.utils.AdventureUtils;
import org.bukkit.command.CommandSender;

public class VersionCommand {

    public static Command.Builder<CommandSender> versionCommand(Command.Builder<CommandSender> builder) {
        return builder.literal("version")
                .permission("oraxen.command.version")
                .handler(context -> Message.VERSION.send(context.getSender(), AdventureUtils.tagResolver("version", OraxenPlugin.get().getDescription().getVersion())));
    }
}
