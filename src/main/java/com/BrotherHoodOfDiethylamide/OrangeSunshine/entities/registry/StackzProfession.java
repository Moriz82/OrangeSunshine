package com.BrotherHoodOfDiethylamide.OrangeSunshine.entities.registry;

import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Random;

public class StackzProfession extends VillagerRegistry.VillagerProfession {

    String name;
    int careerId;

    public StackzProfession(String name, String texture, String zombie) {
        super(name, texture, zombie);
        this.name = name;
    }

    @Override
    public VillagerRegistry.VillagerCareer getCareer(int id){
        return super.getCareer(careerId);
    }

    public void getId(){
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (getCareer(i).getName().contains(name)){
                careerId = i;
                break;
            }
        }
    }

    @Override
    public int getRandomCareer(Random rand) {
        return careerId;
    }
}
