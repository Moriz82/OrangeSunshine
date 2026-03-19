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

    static SoundEvent register(String name) {
        Identifier id = OrangeSunshine.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    static void bootstrap() {}
}
