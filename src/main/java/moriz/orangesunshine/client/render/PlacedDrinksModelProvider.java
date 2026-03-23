package moriz.orangesunshine.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class PlacedDrinksModelProvider {
    public static final PlacedDrinksModelProvider INSTANCE = new PlacedDrinksModelProvider();

    public static final Entry DEFAULT = new Entry(
            new ItemStack(Items.IRON_AXE),
            new ItemTransform(ItemTransform.Type.FIXED, new Vector(0, 0, 0), new Vector(0, 0, 0), new Vector(1, 1, 1)),
            0.8F
    );

    private final Map<Item, Entry> entries = new HashMap<>();

    public PlacedDrinksModelProvider addEntry(Item item, ItemStack stack, ItemTransform.Type type, Vector scale, Vector translation, Vector rotation) {
        entries.put(item, new Entry(stack, new ItemTransform(type, translation, rotation, scale), 1F));
        return this;
    }

    public PlacedDrinksModelProvider addEntry(Item item, ItemStack stack, ItemTransform context, float height) {
        entries.put(item, new Entry(stack, context, height));
        return this;
    }

    public Optional<Entry> get(Item item) {
        return Optional.ofNullable(entries.get(item));
    }

    public static class Entry {
        private final ItemStack stack;
        private final ItemTransform context;
        private final float height;

        public Entry(ItemStack stack, ItemTransform context, float height) {
            this.stack = stack;
            this.context = context;
            this.height = height;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemTransform getContext() {
            return context;
        }

        public float height() {
            return height;
        }
    }

    public static class ItemTransform {
        public enum Type {
            GUI(ItemDisplayContext.GUI),
            FIXED(ItemDisplayContext.FIXED),
            GROUND(ItemDisplayContext.GROUND),
            NONE(ItemDisplayContext.NONE),
            THIRD_PERSON_LEFT_HAND(ItemDisplayContext.THIRD_PERSON_LEFT_HAND),
            THIRD_PERSON_RIGHT_HAND(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND),
            FIRST_PERSON_LEFT_HAND(ItemDisplayContext.FIRST_PERSON_LEFT_HAND),
            FIRST_PERSON_RIGHT_HAND(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND),
            FIXED_ITEM_FRAME(ItemDisplayContext.FIXED),
            GUI_RARE(ItemDisplayContext.GUI);

            private final ItemDisplayContext renderContext;

            Type(ItemDisplayContext renderContext) {
                this.renderContext = renderContext;
            }

            public ItemDisplayContext getDisplayContext() {
                return renderContext;
            }
        }

        private final Type type;
        private final Vector translation;
        private final Vector rotation;
        private final Vector scale;

        public ItemTransform(Type type, Vector translation, Vector rotation, Vector scale) {
            this.type = type;
            this.translation = translation;
            this.rotation = rotation;
            this.scale = scale;
        }

        public Type getType() {
            return type;
        }

        public Vector getTranslation() {
            return translation;
        }

        public Vector getRotation() {
            return rotation;
        }

        public Vector getScale() {
            return scale;
        }
    }

    public record Vector(float x, float y, float z) {}

    public void submitDrink(ItemStack stack, PoseStack matrices, SubmitNodeCollector collector, int light, int overlay) {
        get(stack.getItem()).ifPresent(entry -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return;
            }
            ItemStackRenderState itemState = new ItemStackRenderState();
            ItemSubmitHelper.updateForBlock(mc.getItemModelResolver(), itemState, entry.getStack(), entry.getContext().getType().getDisplayContext(), mc.level, stack.hashCode());

            matrices.pushPose();
            matrices.translate(entry.getContext().getTranslation().x(), entry.getContext().getTranslation().y(), entry.getContext().getTranslation().z());
            matrices.mulPose(Axis.XP.rotationDegrees(entry.getContext().getRotation().x()));
            matrices.mulPose(Axis.YP.rotationDegrees(entry.getContext().getRotation().y()));
            matrices.mulPose(Axis.ZP.rotationDegrees(entry.getContext().getRotation().z()));
            matrices.scale(entry.getContext().getScale().x(), entry.getContext().getScale().y(), entry.getContext().getScale().z());

            itemState.submit(matrices, collector, light, overlay, 0);
            matrices.popPose();
        });
    }
}
