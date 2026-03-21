package com.BrotherHoodOfDiethylamide.OrangeSunshine;

import com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs.Drug;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Drug ADSR envelope calculations without needing the game to run.
 * These verify the core drug timing logic is correct.
 */
public class DrugEnvelopeTest {

    @Test
    public void testEnvelopeAttackPhase() {
        Drug.Envelope env = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        // At t=0 within duration, should be 0 (start of attack)
        float level0 = env.getLevel(0, 1000);
        assertEquals("Level at t=0 should be 0", 0.0f, level0, 0.01f);
    }

    @Test
    public void testEnvelopePeakAfterAttack() {
        Drug.Envelope env = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        // After full attack phase, should be near 1.0
        float levelPeak = env.getLevel(100, 1000);
        assertTrue("Level at peak should be >= 0.9", levelPeak >= 0.9f);
    }

    @Test
    public void testEnvelopeSustainPhase() {
        Drug.Envelope env = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        // Well into sustain phase (after attack + decay)
        float levelSustain = env.getLevel(200, 1000);
        assertEquals("Sustain level should be ~0.8", 0.8f, levelSustain, 0.1f);
    }

    @Test
    public void testEnvelopeReleasePhase() {
        Drug.Envelope env = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        // After duration, in release phase (half of release=100 ticks in)
        float levelRelease = env.getLevel(1100, 1000); // 100 ticks into release
        assertTrue("Release level should be less than sustain", levelRelease < 0.8f);
        assertTrue("Release level should be positive", levelRelease >= 0f);
    }

    @Test
    public void testEnvelopeFullyDecayed() {
        Drug.Envelope env = new Drug.Envelope(100f, 50f, 0.8f, 200f);
        // Well past release
        float levelEnd = env.getLevel(1300, 1000); // 300 ticks into release (past 200 release time)
        assertEquals("Level after full release should be ~0", 0.0f, levelEnd, 0.05f);
    }

    @Test
    public void testShortDMTEnvelope() {
        // DMT: very fast ADSR - short burst drug
        Drug.Envelope dmt = new Drug.Envelope(2400f, 200f, 4.0f, 3000f);
        // duration=10000, timeActive=2700 (still in attack phase since attack=2400, so past it into decay)
        float levelPastAttack = dmt.getLevel(2700, 10000); // in decay phase
        // At t=2700 (300 into decay), lerp(1.0, 4.0, ...) - should be between 1.0 and 4.0
        assertTrue("DMT past-attack level should be >= 1.0 (in decay toward sustain=4.0)", levelPastAttack >= 1.0f);
        // At pure sustain (long past attack+decay)
        float levelSustain = dmt.getLevel(3000, 10000); // past attack(2400) + decay(200) = 2600, into sustain
        assertEquals("DMT sustain level should be 4.0", 4.0f, levelSustain, 0.1f);
    }

    @Test
    public void testWeedEnvelope() {
        // Weed: 1800 attack, 0 decay, 1.0 sustain, 2400 release
        Drug.Envelope weed = new Drug.Envelope(1800f, 0f, 1f, 2400f);
        // At half attack
        float levelHalfAttack = weed.getLevel(900, 10000);
        assertTrue("Weed half-attack should be between 0 and 1", levelHalfAttack > 0f && levelHalfAttack < 1f);
        // At full sustain
        float levelFullSustain = weed.getLevel(1800, 10000);
        assertEquals("Weed full sustain should be 1.0", 1.0f, levelFullSustain, 0.05f);
    }

    @Test
    public void testCocaineEnvelope() {
        // Cocaine: fast 800 attack, no decay, full sustain, 1200 release
        Drug.Envelope coke = new Drug.Envelope(800f, 0f, 1f, 1200f);
        // Should reach peak quickly
        float levelPeak = coke.getLevel(800, 3200);
        assertEquals("Cocaine should reach peak fast", 1.0f, levelPeak, 0.05f);
    }
}
