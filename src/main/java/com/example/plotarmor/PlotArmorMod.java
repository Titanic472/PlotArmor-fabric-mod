package com.example.plotarmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotArmorMod implements ModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("PlotArmor");

   public static final Item BREEZE_BOW = new BreezeBowItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof());
   public static final Item PACKED_BLUE_ICE = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
   public static final Potion ESSENCE_OF_FROST = new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 72000, 4), new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 72000, 4));// Registry.register(Registries.POTION, );
   public static final Potion ESSENCE_OF_THE_SEA =  new Potion(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 72000, 0));
   public static final Potion ESSENCE_OF_DEPTHS =  new Potion(new StatusEffectInstance(StatusEffects.DARKNESS, 72000, 0));
   public static final Potion ESSENCE_OF_WEALTH =  new Potion(new StatusEffectInstance(StatusEffects.LUCK, 72000, 2));
   public static final PotionItem MAGICAL_BREW =  new PotionItem(new Item.Settings().rarity(Rarity.RARE).fireproof());
   public static final PotionItem CHARGED_MAGICAL_BREW = new ChargedMagicalBrew(new Item.Settings().rarity(Rarity.EPIC).fireproof());

    @Override
    public void onInitialize() {
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "breeze_bow"), BREEZE_BOW);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "packed_blue_ice"), PACKED_BLUE_ICE);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "magical_brew"), MAGICAL_BREW);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "charged_magical_brew"), CHARGED_MAGICAL_BREW);

      LOGGER.info("LOADING ********\n**********************\n*************************\n**************************\n************************");
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_frost"), ESSENCE_OF_FROST);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_the_sea"), ESSENCE_OF_THE_SEA);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_depths"), ESSENCE_OF_DEPTHS);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_wealth"), ESSENCE_OF_WEALTH);
      LOGGER.info("LOADED ********\n**********************\n*************************\n**************************\n************************");
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_frost"), ESSENCE_OF_FROST);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_the_sea"), ESSENCE_OF_THE_SEA);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_depths"), ESSENCE_OF_DEPTHS);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_wealth"), ESSENCE_OF_WEALTH);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "magical_brew"), MAGICAL_BREW);

      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, PlotArmorMod.PACKED_BLUE_ICE, Registries.POTION.getEntry(ESSENCE_OF_FROST));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.CONDUIT, Registries.POTION.getEntry(ESSENCE_OF_THE_SEA));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.SCULK_CATALYST, Registries.POTION.getEntry(ESSENCE_OF_DEPTHS));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.NETHERITE_BLOCK, Registries.POTION.getEntry(ESSENCE_OF_WEALTH));});
      registerModelPredicateProviders();

    }

    public static void registerModelPredicateProviders() {
    ModelPredicateProviderRegistry.register(BREEZE_BOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
        if (entity == null) {
           return 0.0F;
         } else {
           return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
        }
     });
 
    ModelPredicateProviderRegistry.register(BREEZE_BOW, Identifier.ofVanilla("pulling"), (stack, world, entity, seed) -> {
        return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
     });
  }
}