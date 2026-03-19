package moriz.orangesunshine.chemistry;

import net.minecraft.util.StringRepresentable;

public enum MatterState implements StringRepresentable {
    BEAKER("beaker"),
    FLASK("flask"),
    GAS("gas"),
    VIAL("vial");

    private final String state;

    MatterState(String pState) {
        this.state = pState;
    }

    @Override
    public String getSerializedName() {
        return state;
    }
}
