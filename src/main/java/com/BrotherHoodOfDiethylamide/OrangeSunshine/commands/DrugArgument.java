package com.BrotherHoodOfDiethylamide.OrangeSunshine.commands;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.DrugRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DrugArgument implements ArgumentType<Drug> {
    private static final Collection<String> EXAMPLES = Arrays.asList("orangesunshine:brown_shrooms", "orangesunshine:weed");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_DRUG = new DynamicCommandExceptionType(function -> new TranslationTextComponent("OrangeSunshine.drug.drugNotFound", function));

    public static DrugArgument drug() {
        return new DrugArgument();
    }

    @Override
    public Drug parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        Drug drug = DrugRegistry.DRUGS.getValue(resourceLocation);
        if (drug != null) {
            return drug;
        } else {
            throw ERROR_UNKNOWN_DRUG.create(resourceLocation);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(DrugRegistry.DRUGS.getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static Drug getDrug(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Drug.class);
    }
}
