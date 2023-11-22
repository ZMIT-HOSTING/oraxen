package io.th0rgal.oraxen.new_commands;

import cloud.commandframework.Command;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class ModelDataCommand {

    public static Command.Builder<CommandSender> modelDataCommand(Command.Builder<CommandSender> builder) {
        return builder.literal("highest_modeldata", "h_md")
                .permission("oraxen.command.debug")
                .handler(context -> {
                    Map<Material, Integer> itemMap = new HashMap<>();
                    for (ItemBuilder itemBuilder : OraxenItems.getItems()) {
                        int currentModelData = itemBuilder.getOraxenMeta().customModelData();
                        Material type = itemBuilder.build().getType();

                        if (currentModelData != 0) itemMap.putIfAbsent(type, currentModelData);
                        if (itemMap.containsKey(type) && itemMap.get(type) < currentModelData) {
                            itemMap.put(type, currentModelData);
                        }
                    }
                    Component report = Component.empty();
                    for (Map.Entry<Material, Integer> entry : itemMap.entrySet()) {
                        String message = ("<dark_aqua>" + entry.getKey().name() + ": <dark_green>" + entry.getValue().toString() + "\n");
                        report = report.append(Component.text(message));
                    }
                    OraxenPlugin.get().audience().sender(context.getSender()).sendMessage(report);
                });
    }
}
