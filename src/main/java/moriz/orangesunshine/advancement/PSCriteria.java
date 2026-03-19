package moriz.orangesunshine.advancement;

public final class PSCriteria {
    public static final MashingTubEventCriterion SIMPLY_MASHING = new MashingTubEventCriterion();
    public static final CustomEventCriterion CUSTOM = new CustomEventCriterion();
    public static final DrugEffectsChangedCriterion DRUG_EFFECTS_CHANGED = new DrugEffectsChangedCriterion();

    public static final CustomEventCriterion.Trigger FEED_VILLAGER = CUSTOM.createTrigger("feed_villager");
    public static final CustomEventCriterion.Trigger HANGOVER = CUSTOM.createTrigger("get_hangover");

    private PSCriteria() {
    }

    public static void bootstrap() {
    }
}
