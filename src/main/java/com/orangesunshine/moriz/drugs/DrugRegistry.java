package com.orangesunshine.moriz.drugs;

import com.orangesunshine.moriz.OrangeSunshine;
import com.orangesunshine.moriz.drugs.effects.LSDEffect;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
public class DrugRegistry {

/*    public static final RegistryObject<Drug> RED_SHROOMS = register("red_shrooms", () -> new RedShrooms(new Drug.DrugProperties().adsr(2400F, 200F, 0.8F, 2400F)));
    public static final RegistryObject<Drug> BROWN_SHROOMS = register("brown_shrooms", () -> new BrownShrooms(new Drug.DrugProperties().adsr(2400F, 0F, 1F, 2400F)));
    public static final RegistryObject<Drug> COCAINE = register("cocaine", () -> new Cocaine(new Drug.DrugProperties().adsr(800F, 0F, 1F, 1200F).abuse(2)));
    public static final RegistryObject<Drug> WEED = register("weed", () -> new Weed(new Drug.DrugProperties().adsr(1800F, 0F, 1F, 2400F)));
    public static final RegistryObject<Drug> MORPHINE = register("morphine", () -> new Morphine(new Drug.DrugProperties().adsr(0F, 800F, 0.8F, 200F)));
    public static final RegistryObject<Drug> LSD_BOTTLE = register("lsd_bottle", () -> new LSDEffect(new Drug.DrugProperties().adsr(2400F, 200F, 0.95F, 3000F)));*/
    public static final RegistryObject<Drug> LSD_BLOTTER = register("lsd_blotter", () -> new LSDEffect(new Drug.DrugProperties().adsr(2400F, 200F, 0.8F, 2400F)));
/*
    public static final RegistryObject<Drug> ORANGESUNSHINE_BOTTLE = register("orangesunshine_bottle", () -> new LSDEffect(new Drug.DrugProperties().adsr(3100F, 300F, 0.66F, 3000F)));
    public static final RegistryObject<Drug> ORANGESUNSHINE_BLOTTER = register("orangesunshine_blotter", () -> new LSDEffect(new Drug.DrugProperties().adsr(3100F, 300F, 0.66F, 2400F)));
    public static final RegistryObject<Drug> DMT = register("dmt", () -> new DMTEffect(new Drug.DrugProperties().adsr(2400F, 200F, 4.0F, 3000F)));
    public static final RegistryObject<Drug> DMT_5_MEO = register("dmt_5_meo", () -> new DMTEffect(new Drug.DrugProperties().adsr(2400F, 200F, 6.0F, 3000F)));
    public static final RegistryObject<Drug> CACTUS_DRUG = register("peyote", () -> new CactusDrugEffect(new Drug.DrugProperties().adsr(3000F, 400F, 0.6F, 2200F)));
    public static final RegistryObject<Drug> NIC = register("nic", () -> new NicEffect(new Drug.DrugProperties().adsr(150F, 10F, 0.2F, 1000F)));
    public static final RegistryObject<Drug> PARTY = register("mdma", () -> new PartyEffect(new Drug.DrugProperties().adsr(500F, 200F, 0.8F, 2500F)));
*/

    public static RegistryObject<Drug> register(String name, Supplier<? extends Drug> supplier) {
        return OrangeSunshine.DRUGS.register(name, supplier);
    }
}