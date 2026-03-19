package moriz.orangesunshine.entity.drug.hallucination;

import java.util.*;

import moriz.orangesunshine.entity.drug.DrugProperties;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;

public class ChatBot {
    private final Personality personality;
    private final Player player;

    private final List<Character> characters = new ArrayList<>();

    private final Queue<Runnable> incomingMessageQueue = new LinkedList<>();

    public ChatBot(Personality personality, Player player) {
        this.personality = personality;
        this.player = player;
    }

    public void tick() {
        if (!player.level().isClientSide()) {
            return;
        }

        if (characters.size() < 3 && player.getRandom().nextInt(200) == 0) {
            characters.add(new Character());
        }

        Runnable action;
        while ((action = incomingMessageQueue.poll()) != null) {
            action.run();
        }

        characters.removeIf(Character::tick);
    }

    private void emitMessage(String sender, Component message) {
        HallucinationManager hallucinations = DrugProperties.of(player).getHallucinations();

        if (hallucinations.getEntities().getForcedAlpha(1) > 0 || hallucinations.getHallucinationStrength(1) > 0) {
            player.displayClientMessage(message, false);
            incomingMessageQueue.add(() -> {
                getResponsiveCharacters(sender, message).forEach(character -> character.wakeUp(sender, message, false));
            });

            if (player.level().getRandom().nextFloat() < 0.3F || message.getString().contains("!")) {
                float x = player.level().getRandom().nextFloat();
                float z = player.level().getRandom().nextFloat();
                player.playSound(SoundEvents.PLAYER_HURT, 1, 1);
                player.knockback(0.2F, x, z);
            }
        }
    }

    public void onMessageReceived(String sender, Component message) {
        getResponsiveCharacters(sender, message).forEach(character -> character.wakeUp(sender, message, true));
    }

    private List<Character> getResponsiveCharacters(String sender, Component message) {
        String txt = message.getString();
        var allCharacters = characters.stream().filter(character -> !sender.contentEquals(character.name.getString())).toList();
        if (allCharacters.isEmpty()) {
            return allCharacters;
        }
        var mentionedCharacters = characters.stream().filter(character -> txt.contains(character.name.getString())).toList();
        if (mentionedCharacters.isEmpty()) {
            return List.of(allCharacters.get(player.getRandom().nextInt(allCharacters.size())));
        }
        return mentionedCharacters;
    }

    final class Character {
        private int idleTicks;
        private int sleepTicks;

        private final Queue<DelayedMessage> messageQueue = new LinkedList<>();

        private final Component name = personality.getName(player.getRandom());
        private final ChatType.Bound parameters = ChatType.bind(ChatType.CHAT, player.level().registryAccess(), name);

        public boolean tick() {
            if (sleepTicks-- > 0) {
                return false;
            }

            if (messageQueue.isEmpty()) {
                if (idleTicks++ > 300 && player.getRandom().nextInt(300) == 0) {
                    if (player.getRandom().nextInt(120) == 0) {
                        return true;
                    }

                    personality.supplyMessage(player.getRandom(), line -> {
                        messageQueue.add(new DelayedMessage(line));
                    });
                    sleepTicks = Mth.nextInt(player.getRandom(), 5, 100);
                    idleTicks = 0;
                }
            }

            DelayedMessage message = messageQueue.peek();
            if (message != null) {
                if (message.tick()) {
                    messageQueue.poll();
                }
                sleepTicks = Mth.nextInt(player.getRandom(), 2, 20);
            }

            return false;
        }

        public void wakeUp(String sender, Component message, boolean fromPlayer) {
            messageQueue.clear();
            personality.onMessageReceived(sender, message, player.getRandom(), fromPlayer, line -> {
                messageQueue.add(new DelayedMessage(line));
            });
            idleTicks = 0;
            sleepTicks = Mth.nextInt(player.getRandom(), 1, 5);
        }

        class DelayedMessage {
            Component message;
            int delay;

            public DelayedMessage(Component message) {
                this.message = message;
                this.delay = Mth.nextInt(player.getRandom(), 2, 20);
            }

            public boolean tick() {
                if (--delay <= 0) {
                    emitMessage(name.getString(), parameters.decorate(message));
                    return true;
                }
                return false;
            }
        }
    }
}
