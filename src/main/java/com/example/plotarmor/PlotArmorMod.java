package com.example.plotarmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;

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
   public static final PotionItem MAGICAL_BREW =  new PotionItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxCount(1));
   public static final PotionItem CHARGED_MAGICAL_BREW = new ChargedMagicalBrew(new Item.Settings().rarity(Rarity.EPIC).fireproof().maxCount(1));

   public static ServerWorld GlobalServerWorld = null;

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

      CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
         dispatcher.register(CommandManager.literal("setcheck")
            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
            .then(CommandManager.argument("block", BlockStateArgumentType.blockState(registryAccess))
            .executes(context -> {
               BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
               BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "block");

               ServerWorld world = context.getSource().getWorld();
               PlotArmorState state = PlotArmorState.getServerState(world.getServer());
               state.setCheckBlockPos(pos);
               state.setCheckBlockType(blockStateArgument.getBlockState().getBlock());
               state.setCheckBlockMatched(false);

               context.getSource().sendFeedback(() -> Text.literal("Check block position set to " + state.getCheckBlockPos() + " for block type " + state.getCheckBlockType()), true);
               return 1;
            }))));

            dispatcher.register(CommandManager.literal("setchamber")
            .then(CommandManager.argument("start", BlockPosArgumentType.blockPos())
            .then(CommandManager.argument("end", BlockPosArgumentType.blockPos())
             .executes(context -> {
               BlockPos start = BlockPosArgumentType.getBlockPos(context, "start");
               BlockPos end = BlockPosArgumentType.getBlockPos(context, "end");

               ServerWorld world = context.getSource().getWorld();
               PlotArmorState state = PlotArmorState.getServerState(world.getServer());
               state.setChamberStart(start);
               state.setChamberEnd(end);

               context.getSource().sendFeedback(() -> Text.literal("Chamber coordinates set from " + state.getChamberStart() + " to " + state.getChamberEnd()), true);
               return 1;
            }))));
      });
      ServerWorldEvents.LOAD.register(this::onWorldLoad);
      ServerTickEvents.END_WORLD_TICK.register(new ItemThrowEventHandler());
      //ServerWorldEvents.UNLOAD.register(this::onWorldUnload);
  }

  private void onWorldLoad(MinecraftServer server, ServerWorld world) {
      GlobalServerWorld = world;
      UseBlockCallback.EVENT.register(this::onBlockPlaced);
      PlayerBlockBreakEvents.AFTER.register(this::onBlockBroken);
  }

   private ActionResult onBlockPlaced(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
      updateBlockCheckState(world);
      return ActionResult.PASS;
  }

  private void onBlockBroken(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) {
      updateBlockCheckState(world);
  }

   private void updateBlockCheckState(World world) {
      PlotArmorState state = PlotArmorState.getServerState(GlobalServerWorld.getServer());
      if (state.getCheckBlockPos() != null) {
         BlockState blockState = world.getBlockState(state.getCheckBlockPos());
         state.setCheckBlockMatched(blockState.getBlock().equals(state.getCheckBlockType()));
      }
          LOGGER.info("CheckBlockMatched: " + state.isCheckBlockMatched());
   }

   public static void registerModelPredicateProviders() {
      ModelPredicateProviderRegistry.register(BREEZE_BOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
         if(entity == null) {
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