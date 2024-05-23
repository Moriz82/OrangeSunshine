package moriz.orangesunshine.chemistry;

import net.minecraft.util.StringIdentifiable;

public enum MatterState implements StringIdentifiable {
    BEAKER("beaker"),
    FLASK("flask"),
    GAS("gas"),
    VIAL("vial");

    private final String state;

    MatterState(String pState) {
        this.state = pState;
    }

    @Override
    public String asString() {
        return state;
    }
}
