package io.th0rgal.oraxen.new_commands;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.font.Glyph;
import io.th0rgal.oraxen.utils.AdventureUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

public class GlyphInfoCommand {

    public static Command.Builder<CommandSender> glyphInfoCommand(Command.Builder<CommandSender> builder) {
        return builder.literal("glyphinfo")
                .permission("oraxen.command.glyphinfo")
                .argument(StringArgument.greedy("glyphid"))
                .handler(context -> {
                    String glyphId = context.get("glyphid");
                    Glyph glyph = OraxenPlugin.get().fontManager().getGlyphFromID(glyphId);
                    Audience audience = OraxenPlugin.get().audience().sender(context.getSender());
                    if (glyph == null) {
                        audience.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("<red>No glyph found with glyph-id <i><dark_red>" + glyphId));
                    } else {
                        audience.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("<dark_aqua>GlyphID: <aqua>" + glyphId));
                        audience.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("<dark_aqua>Texture: <aqua>" + glyph.texture()));
                        audience.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("<dark_aqua>Bitmap: <aqua>" + glyph.isBitMap()));
                        audience.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("<dark_aqua>Unicode: <white>" + glyph.character()).hoverEvent(HoverEvent.showText(AdventureUtils.MINI_MESSAGE.deserialize("<gold>Click to copy to clipboard!"))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, glyph.character())));
                    }
                });
    }
}
