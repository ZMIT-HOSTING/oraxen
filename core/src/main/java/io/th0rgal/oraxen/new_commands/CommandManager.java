package io.th0rgal.oraxen.new_commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.utils.logs.Logs;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CommandManager {
    private BukkitCommandManager<CommandSender> manager;
    private CommandConfirmationManager<CommandSender> confirmationManager;


    public CommandManager(OraxenPlugin plugin) {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();

        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            manager = new PaperCommandManager<>(plugin, executionCoordinatorFunction, mapperFunction, mapperFunction);
        } catch (final Exception e) {
            Logs.logError("Failed to initialize the command this.manager");
        }
        this.manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
        ));

        if (this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.manager.registerBrigadier();
        }

        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) this.manager).registerAsynchronousCompletions();
        }

        this.confirmationManager = new CommandConfirmationManager<>(
                30L,
                TimeUnit.SECONDS,
                context -> context.getCommandContext().getSender().sendMessage(
                "<red>Confirmation required. Confirm using /example confirm."),
                sender -> sender.sendMessage(
                "<red>You don't have any pending commands.")
        );

        this.confirmationManager.registerConfirmationProcessor(this.manager);

        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        // This will allow you to decorate commands with descriptions
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();

        /*new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(
                        component -> text()
                                .append(text("[", NamedTextColor.DARK_GRAY))
                                .append(text("Example", NamedTextColor.GOLD))
                                .append(text("] ", NamedTextColor.DARK_GRAY))
                                .append(component).build()
                ).apply(this.manager, this.bukkitAudiences::sender);*/
        //
        // Create the commands
        //
        this.constructCommands();
    }

    private void constructCommands() {
        final Command.Builder<CommandSender> builder = this.manager.commandBuilder("oraxen", "oxn", "o");
        this.manager.command(LogDumpCommand.logDumpCommand(builder));
        this.manager.command(VersionCommand.versionCommand(builder));
        this.manager.command(ItemInfoCommand.itemInfoCommand(builder));
        this.manager.command(GlyphInfoCommand.glyphInfoCommand(builder));
        this.manager.command(EmojisCommand.emojiCommand(builder));
        this.manager.command(ReloadCommand.reloadCommand(builder));
        this.manager.command(ModelDataCommand.modelDataCommand(builder));

        this.manager.command(builder.literal("confirm")
                .meta(CommandMeta.DESCRIPTION, "Confirm a pending command")
                .handler(this.confirmationManager.createConfirmationExecutionHandler()));

        this.manager.command(builder.literal("reload")
                .meta(CommandMeta.DESCRIPTION, "Reload Oraxen")
                .handler(context -> this.manager.taskRecipe().begin(context)
                        .asynchronous(commandContext -> {
                            context.getSender().sendMessage("Reloading Oraxen...");
                            context.getSender().sendMessage("Oraxen reloaded!");
                        }).execute()));
    }
}
