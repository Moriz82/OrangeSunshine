/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package moriz.orangesunshine.entity.drug.hallucination;

import java.util.Optional;

import net.minecraft.world.entity.player.Player;

public abstract class Hallucination {

    public static final int UNLIMITED = -1;

    protected final Player player;

    protected int age;

    protected Optional<ChatBot> chatBot = Optional.empty();

    public Hallucination(Player player) {
        this.player = player;
    }

    public void update() {
        age++;
        chatBot.ifPresent(ChatBot::tick);
    }

    public Optional<ChatBot> getChatBot() {
        return chatBot;
    }

    public abstract void render(Object matrices, Object vertices, Object camera, float tickDelta, float alpha);

    public abstract boolean isDead();

    public void setDead() {
        age = Integer.MAX_VALUE;
    }

    public abstract int getMaxHallucinations();
}
