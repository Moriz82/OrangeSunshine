package moriz.orangesunshine.command;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.*;

import moriz.orangesunshine.OrangeSunshine;
import moriz.orangesunshine.entity.drug.*;
import moriz.orangesunshine.entity.drug.Drug;
import moriz.orangesunshine.entity.drug.DrugProperties;
import moriz.orangesunshine.entity.drug.DrugType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * @author Sollace
 * @since 5 Jan 2023
 */
class DrugCommand {
    private static final Identifier ALL = OrangeSunshine.id("all");
    public static final SimpleCommandExceptionType INVALID_DRUG_NAME = new SimpleCommandExceptionType(Component.translatable("commands.drug.nodrug"));
    private static final SuggestionProvider<CommandSourceStack> DRUG_NAME_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggestResource(DrugType.REGISTRY.keySet(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registries) {
        dispatcher.register(Commands.literal("drug")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.argument("target", EntityArgument.players())
                .then(Commands.literal("lock")
                    .then(Commands.literal("all")
                        .then(Commands.argument("locked", BoolArgumentType.bool()).executes(ctx -> lockDrugs(ctx, ALL))))
                    .then(Commands.argument("drug", IdentifierArgument.id()).suggests(DRUG_NAME_SUGGESTIONS)
                        .then(Commands.argument("locked", BoolArgumentType.bool()).executes(ctx -> lockDrugs(ctx, IdentifierArgument.getId(ctx, "drug")))
                    )
                ))
                .then(Commands.literal("get").executes(DrugCommand::getAllDrugs)
                    .then(Commands.argument("drug", IdentifierArgument.id()).suggests(DRUG_NAME_SUGGESTIONS).executes(DrugCommand::getDrugs)
                ))
                .then(Commands.literal("set")
                    .then(Commands.literal("all")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(ctx -> setDrugs(ctx, ALL))))
                    .then(Commands.argument("drug", IdentifierArgument.id()).suggests(DRUG_NAME_SUGGESTIONS)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(ctx -> setDrugs(ctx, IdentifierArgument.getId(ctx, "drug")))))
                )
                .then(Commands.literal("add")
                    .then(Commands.literal("all")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(ctx -> addToDrugs(ctx, ALL))))
                    .then(Commands.argument("drug", IdentifierArgument.id()).suggests(DRUG_NAME_SUGGESTIONS)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 1)).executes(ctx -> addToDrugs(ctx, IdentifierArgument.getId(ctx, "drug")))))
                )
            )
        );
    }

    private static int getAllDrugs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        DrugProperties properties = DrugProperties.of(player);

        List<Drug> drugs = properties.getAllDrugs().stream().filter(drug -> drug.getActiveValue() > 0).toList();

        if (drugs.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.drug.success.get.sober", player.getName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.drug.success.get", player.getName(), drugs.size()), true);
            drugs.forEach(drug -> {
                double value = drug.getActiveValue();
                source.sendSuccess(() -> Component.literal(DrugType.REGISTRY.getKey(drug.getType()).getPath() + ": " + value), true);
            });
        }

        return 0;
    }

    private static int getDrugs(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        Identifier drugName = IdentifierArgument.getId(context, "drug");
        DrugProperties properties = DrugProperties.of(player);

        DrugType.REGISTRY.getOptional(drugName).ifPresentOrElse(drugType -> {
            float value = properties.isDrugActive(drugType) ? properties.getDrugValue(drugType) : 0;
            source.sendSuccess(() -> Component.translatable("commands.drug.success.get." + (player == source.getEntity() ? "self" : "other"), player.getName(), drugName.getPath(), value), true);
        }, () -> {
            source.sendSuccess(() -> Component.translatable("commands.drug.fail.get", drugName), true);
        });

        return 0;
    }

    private static int lockDrugs(CommandContext<CommandSourceStack> context, Identifier drugName) throws CommandSyntaxException {
        boolean locked = BoolArgumentType.getBool(context, "locked");
        applyDrugChange(context, drugName, (properties, type) -> properties.getDrug(type).setLocked(locked), (player, type) -> {
            if (type == UpdateType.NONE) {
                sendFeedback(context.getSource(), player, false, "none", drugName);
            } else if (type == UpdateType.ALL) {
                sendFeedback(context.getSource(), player, true, (locked ? "lock" : "unlock") + ".all");
            } else {
                sendFeedback(context.getSource(), player, true, (locked ? "lock" : "unlock"), drugName);
            }
        });
        return 0;
    }

    private static int setDrugs(CommandContext<CommandSourceStack> context, Identifier drugName) throws CommandSyntaxException {
        double value = DoubleArgumentType.getDouble(context, "value");
        applyDrugChange(context, drugName, (properties, type) -> properties.setDrugValue(type, value), (player, type) -> {
            if (type == UpdateType.NONE) {
                sendFeedback(context.getSource(), player, false, "set", drugName);
            } else if (type == UpdateType.ALL) {
                sendFeedback(context.getSource(), player, true, "set.all", value);
            } else {
                sendFeedback(context.getSource(), player, true, "set", drugName.getPath(), value);
            }
        });

        return 0;
    }

    private static int addToDrugs(CommandContext<CommandSourceStack> context, Identifier drugName) throws CommandSyntaxException {
        double value = DoubleArgumentType.getDouble(context, "value");
        applyDrugChange(context, drugName, (properties, type) -> properties.addToDrug(type, value), (player, type) -> {
            if (type == UpdateType.NONE) {
                sendFeedback(context.getSource(), player, false, "add", drugName);
            } else if (type == UpdateType.ALL) {
                sendFeedback(context.getSource(), player, true, "add.all", value);
            } else {
                sendFeedback(context.getSource(), player, true, "add", drugName.getPath(), value);
            }
        });

        return 0;
    }

    static void applyDrugChange(CommandContext<CommandSourceStack> context, Identifier drugName, BiConsumer<DrugProperties, DrugType> change, FeedbackConsumer feedback) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        DrugProperties properties = DrugProperties.of(player);

        if ("all".equals(drugName.getPath())) {
            DrugType.REGISTRY.forEach(type -> {
                change.accept(properties, type);
            });
            feedback.accept(player, UpdateType.ALL);
        } else {
            DrugType.REGISTRY.getOptional(drugName).ifPresentOrElse(type -> {
                change.accept(properties, type);
                feedback.accept(player, UpdateType.ONE);
            }, () -> feedback.accept(player, UpdateType.NONE));
        }

        properties.markDirty();
    }

    interface FeedbackConsumer {
        void accept(ServerPlayer player, UpdateType updateType);
    }

    enum UpdateType {
        ALL,
        ONE,
        NONE
    }

    private static void sendFeedback(CommandSourceStack source, ServerPlayer player, boolean succeeded, String key, Object... arguments) {
        if (source.getEntity() == player) {
            source.sendSuccess(() -> Component.translatable("commands.drug." + (succeeded ? "success" : "fail") + "." + key + ".self", arguments), true);
        } else {
            if (succeeded && source.getLevel().getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)) {
                player.sendSystemMessage(Component.translatable("commands.drug." + key + ".changed", arguments));
            }

            source.sendSuccess(() -> Component.translatable("commands.drug." + (succeeded ? "success" : "fail") + "." + key + ".other", Streams.concat(
                    Stream.of(player.getDisplayName()),
                    Arrays.stream(arguments)).toArray()
            ), true);
        }
    }
}
