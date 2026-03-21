package com.BrotherHoodOfDiethylamide.OrangeSunshine.commands;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugInstance;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SetDrugCommand extends CommandBase {

    @Override
    public String getName() {
        return "setdrug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setdrug <drug|clear|all|list> [potency] [duration] [delay]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        if (!(sender.getCommandSenderEntity() instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("This command can only be used by players."));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        String drugName = args[0].toLowerCase();

        if (drugName.equals("clear")) {
            Drug.clearDrugs(player);
            sender.sendMessage(new TextComponentString("Cleared all drug effects."));
            return;
        }

        if (drugName.equals("list")) {
            sender.sendMessage(new TextComponentString("Available drugs: " + DrugRegistry.DRUGS.keySet().toString()));
            return;
        }

        float potency = args.length >= 2 ? (float) parseDouble(args[1], 0.0, 10.0) : 1.0f;
        int duration = args.length >= 3 ? parseInt(args[2], 0, 100000) : 600;
        int delay = args.length >= 4 ? parseInt(args[3], 0, 100000) : 0;

        if (drugName.equals("all")) {
            for (Drug drug : DrugRegistry.DRUGS.values()) {
                Drug.addDrug(player, new DrugInstance(drug, delay, potency, duration));
            }
            sender.sendMessage(new TextComponentString("Applied all " + DrugRegistry.DRUGS.size() + " drugs (potency=" + potency + ", duration=" + duration + "t)."));
            return;
        }

        Drug drug = Drug.byName(drugName);
        if (drug == null) {
            sender.sendMessage(new TextComponentString("Unknown drug: " + drugName + ". Use /setdrug list to see valid drugs."));
            return;
        }

        Drug.addDrug(player, new DrugInstance(drug, delay, potency, duration));
        sender.sendMessage(new TextComponentString("Applied " + drugName + " (potency=" + potency + ", duration=" + duration + "t, delay=" + delay + "t). Watch the F7 HUD!"));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            List<String> opts = new ArrayList<>(DrugRegistry.DRUGS.keySet());
            opts.add("clear");
            opts.add("all");
            opts.add("list");
            return getListOfStringsMatchingLastWord(args, opts);
        }
        return new ArrayList<>();
    }
}
