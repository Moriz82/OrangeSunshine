package moriz.orangesunshine.command;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.*;

import moriz.orangesunshine.entity.drug.hallucination.HallucinationTypeKeys;
import moriz.orangesunshine.network.Channel;
import moriz.orangesunshine.network.MsgHallucinate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author Sollace
 * @since 5 Jan 2023
 */
class HallucinateCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggestResource(HallucinationTypeKeys.REGISTRY, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registries) {
        dispatcher.register(Commands.literal("hallucinate")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.argument("type", IdentifierArgument.id()).suggests(SUGGESTIONS).executes(ctx -> {
                        sendHallucination(ctx, ctx.getSource().getPlayerOrException(), Optional.empty());
                        return 0;
                    })
                    .then(Commands.argument("target", EntityArgument.players()).executes(ctx -> {
                        EntityArgument.getPlayers(ctx, "target").forEach(player -> {
                            sendHallucination(ctx, player, Optional.empty());
                        });
                        return 0;
                    }))
                    .then(Commands.argument("position", BlockPosArgument.blockPos()).executes(ctx -> {
                        sendHallucination(ctx, ctx.getSource().getPlayerOrException(), Optional.of(BlockPosArgument.getBlockPos(ctx, "position")));
                        return 0;
                    }))
            )
        );
    }

    private static void sendHallucination(CommandContext<CommandSourceStack> ctx, ServerPlayer player, Optional<BlockPos> position) {
        Identifier type = IdentifierArgument.getId(ctx, "type");
        Channel.HALLUCINATE.sendToPlayer(new MsgHallucinate(player.getId(), type, position), player);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.hallucinate.success"), true);
    }
}
