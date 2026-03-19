package moriz.orangesunshine;

import org.apache.logging.log4j.Logger;

public final class OrangeSunshinePlatform {
    public static final String MOD_ID = "orangesunshine";
    public static final String MOD_NAME = "Orange Sunshine";
    public static final Logger LOGGER = OrangeSunshine.LOGGER;

    private OrangeSunshinePlatform() {
    }

    public static void init(String platformName) {
        LOGGER.info("Initializing {} on {}", MOD_NAME, platformName);
        OrangeSunshine.init();
    }
}
