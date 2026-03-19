package moriz.orangesunshine.client.item;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSModelPredicates {
    static void bootstrap() {
        // 1.21.11 moved custom item predicates to the new item model property pipeline.
        // Keep bootstrap wired so the client initializes cleanly until those visuals are ported.
    }
}
