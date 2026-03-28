/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public interface PSSounds {

    SoundEvent ENTITY_PLAYER_HEARTBEAT = register("entity.player.heartbeat");
    SoundEvent ENTITY_PLAYER_BREATH = register("entity.player.breath");

    SoundEvent BONG_HIT = register("entity.player.bong_hit");
    SoundEvent JOINT_INHALE = register("entity.player.joint_inhale");
    SoundEvent JOINT_EXHALE = register("entity.player.joint_exhale");

    SoundEvent BLOCK_RIFT_JAR_TOGGLE = register("block.rift_jar.toggle");
    SoundEvent BLOCK_RIFT_JAR_OPEN = register("block.rift_jar.open");
    SoundEvent BLOCK_RIFT_JAR_CLOSE = register("block.rift_jar.close");

    SoundEvent DRUG_GENERIC = register("drug.generic");

    // Drug-specific sound events (one per DrugType)
    SoundEvent DRUG_ALCOHOL = register("drug.alcohol");
    SoundEvent DRUG_CANNABIS = register("drug.cannabis");
    SoundEvent DRUG_BROWN_SHROOMS = register("drug.brown_shrooms");
    SoundEvent DRUG_RED_SHROOMS = register("drug.red_shrooms");
    SoundEvent DRUG_TOBACCO = register("drug.tobacco");
    SoundEvent DRUG_COCCAINE = register("drug.coccaine");
    SoundEvent DRUG_CAFFEINE = register("drug.caffeine");
    SoundEvent DRUG_SUGAR = register("drug.sugar");
    SoundEvent DRUG_BATH_SALTS = register("drug.bath_salts");
    SoundEvent DRUG_SLEEP_DEPRIVATION = register("drug.sleep_deprivation");
    SoundEvent DRUG_LSD = register("drug.lsd");
    SoundEvent DRUG_ATROPINE = register("drug.atropine");
    SoundEvent DRUG_KAVA = register("drug.kava");
    SoundEvent DRUG_WARMTH = register("drug.warmth");
    SoundEvent DRUG_PEYOTE = register("drug.peyote");
    SoundEvent DRUG_DMT = register("drug.dmt");
    SoundEvent DRUG_MDA = register("drug.mda");
    SoundEvent DRUG_MDMA = register("drug.mdma");
    SoundEvent DRUG_PMA = register("drug.pma");
    SoundEvent DRUG_CODEINE = register("drug.codeine");
    SoundEvent DRUG_MORPHINE = register("drug.morphine");
    SoundEvent DRUG_OPIUM = register("drug.opium");
    SoundEvent DRUG_MESCALINE = register("drug.mescaline");
    SoundEvent DRUG_SALVIA = register("drug.salvia");
    SoundEvent DRUG_KRATOM = register("drug.kratom");
    SoundEvent DRUG_KETAMINE = register("drug.ketamine");
    SoundEvent DRUG_TWO_CB = register("drug.two_cb");
    SoundEvent DRUG_ZERO = register("drug.zero");
    SoundEvent DRUG_POWER = register("drug.power");
    SoundEvent DRUG_HARMONIUM = register("drug.harmonium");

    static SoundEvent register(String name) {
        Identifier id = OrangeSunshine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    static void bootstrap() {}
}
