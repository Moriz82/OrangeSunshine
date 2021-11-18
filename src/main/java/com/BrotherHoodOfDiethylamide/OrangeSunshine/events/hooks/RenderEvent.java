package com.BrotherHoodOfDiethylamide.OrangeSunshine.events.hooks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderEvent extends Event {
    public enum Phase {
        START, END
    }

    public final Phase phase;

    public RenderEvent(Phase phase) {
        this.phase = phase;
    }

    public static class RenderTerrainEvent extends RenderEvent {
        public final RenderType blockLayer;

        public RenderTerrainEvent(Phase phase, RenderType blockLayerIn) {
            super(phase);
            this.blockLayer = blockLayerIn;
        }
    }

    public static class RenderEntityEvent extends RenderEvent {
        @Nullable
        public final Entity entity;

        public RenderEntityEvent(Phase phase, @Nullable Entity entity) {
            super(phase);
            this.entity = entity;
        }
    }

    public static class RenderBlockEntityEvent extends RenderEvent {

        public RenderBlockEntityEvent(Phase phase) {
            super(phase);
        }
    }

    public static class RenderBlockOutlineEvent extends RenderEvent {
        public final IRenderTypeBuffer.Impl buffer;

        public RenderBlockOutlineEvent(Phase phase, IRenderTypeBuffer.Impl buffer) {
            super(phase);
            this.buffer = buffer;
        }
    }

    public static class RenderParticlesEvent extends RenderEvent {
        public RenderParticlesEvent(Phase phase) {
            super(phase);
        }
    }
}
