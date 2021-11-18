package com.BrotherHoodOfDiethylamide.OrangeSunshine.commands;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.capabilities.PlayerDrugs;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;

public class SetDrugCommand {
    private SetDrugCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("drug").requires(source -> source.hasPermission(2))
                    .then(Commands.literal("give")
                        .then(Commands.argument("players", EntityArgument.players())
                            .then(Commands.argument("drug", DrugArgument.drug())
                                .executes(context -> setDrugValue(context.getSource(), EntityArgument.getPlayers(context, "players"), DrugArgument.getDrug(context, "drug"), 0.5F, 1200))
                                .then(Commands.argument("potency", FloatArgumentType.floatArg(0, 1))
                                    .executes(context -> setDrugValue(context.getSource(), EntityArgument.getPlayers(context, "players"), DrugArgument.getDrug(context, "drug"), FloatArgumentType.getFloat(context, "potency"), 1200))
                                    .then(Commands.argument("duration", IntegerArgumentType.integer(0))
                                        .executes(context -> setDrugValue(context.getSource(), EntityArgument.getPlayers(context, "players"), DrugArgument.getDrug(context, "drug"), FloatArgumentType.getFloat(context, "potency"), IntegerArgumentType.getInteger(context, "duration"))))))))
                    .then(Commands.literal("clear")
                        .then(Commands.argument("players", EntityArgument.players())
                            .executes(context -> clearDrug(context.getSource(), EntityArgument.getPlayers(context, "players")))))
        );
    }

    public static void registerSerializer() {
        ArgumentTypes.register("orangesunshine:drug", DrugArgument.class, new ArgumentSerializer<>(DrugArgument::drug));
    }

    private static int setDrugValue(CommandSource source, Collection<ServerPlayerEntity> players, Drug drug, float potency, int duration) {
        for (ServerPlayerEntity player : players) {
            Drug.addDrug(player, new DrugInstance(drug, 0, potency, duration));
            PlayerDrugs.sync(player);
        }
        return 1;
    }

    private static int clearDrug(CommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            Drug.clearDrugs(player);
            PlayerDrugs.sync(player);
        }
        return 1;
    }
}
