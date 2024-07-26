package com.example;

import com.example.plotarmor.PlotArmorMod;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelPredicateProviderRegistry.register(PlotArmorMod.BREEZE_BOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
         if(entity == null) {
            return 0.0F;
            } else {
            return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
         }
      });
   
      ModelPredicateProviderRegistry.register(PlotArmorMod.BREEZE_BOW, Identifier.ofVanilla("pulling"), (stack, world, entity, seed) -> {
         return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
      });
	}
}