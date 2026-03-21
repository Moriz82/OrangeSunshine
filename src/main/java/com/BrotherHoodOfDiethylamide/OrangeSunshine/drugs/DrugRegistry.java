package com.BrotherHoodOfDiethylamide.OrangeSunshine.drugs;

import java.util.LinkedHashMap;
import java.util.Map;

public class DrugRegistry {
    public static final Map<String, Drug> DRUGS = new LinkedHashMap<>();

    public static void registerAll() {
        register("red_shrooms", new RedShrooms(new Drug.DrugProperties().adsr(2400F, 200F, 0.8F, 2400F)));
        register("brown_shrooms", new BrownShrooms(new Drug.DrugProperties().adsr(2400F, 0F, 1F, 2400F)));
        register("cocaine", new Cocaine(new Drug.DrugProperties().adsr(800F, 0F, 1F, 1200F).abuse(2)));
        register("weed", new Weed(new Drug.DrugProperties().adsr(1800F, 0F, 1F, 2400F)));
        register("morphine", new Morphine(new Drug.DrugProperties().adsr(0F, 800F, 0.8F, 200F)));
        register("lsd_bottle", new LSDEffect(new Drug.DrugProperties().adsr(2400F, 200F, 0.95F, 3000F)));
        register("lsd_blotter", new LSDEffect(new Drug.DrugProperties().adsr(2400F, 200F, 0.8F, 2400F)));
        register("orangesunshine_bottle", new LSDEffect(new Drug.DrugProperties().adsr(3100F, 300F, 0.66F, 3000F)));
        register("orangesunshine_blotter", new LSDEffect(new Drug.DrugProperties().adsr(3100F, 300F, 0.66F, 2400F)));
        register("dmt", new DMTEffect(new Drug.DrugProperties().adsr(2400F, 200F, 4.0F, 3000F)));
        register("dmt_5_meo", new DMTEffect(new Drug.DrugProperties().adsr(2400F, 200F, 6.0F, 3000F)));
        register("peyote", new CactusDrugEffect(new Drug.DrugProperties().adsr(3000F, 400F, 0.6F, 2200F)));
        register("nic", new NicEffect(new Drug.DrugProperties().adsr(150F, 10F, 0.2F, 1000F)));
        register("mdma", new PartyEffect(new Drug.DrugProperties().adsr(500F, 200F, 0.8F, 2500F)));
    }

    private static void register(String name, Drug drug) {
        DRUGS.put(name, drug);
    }
}
