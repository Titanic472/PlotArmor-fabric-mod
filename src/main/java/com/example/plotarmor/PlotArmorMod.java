package com.example.plotarmor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.registry.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.item.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotArmorMod implements ModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("PlotArmor");

   public final Map<UUID, Long> iframes = new HashMap<>();

   public static final Item BREEZE_BOW = new BreezeBowItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof().maxDamage(96));
   public static final Item PACKED_BLUE_ICE = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
   public static final Potion ESSENCE_OF_FROST = new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 72000, 4), new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 72000, 4));// Registry.register(Registries.POTION, );
   public static final Potion ESSENCE_OF_THE_SEA =  new Potion(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 72000, 0));
   public static final Potion ESSENCE_OF_DEPTHS =  new Potion(new StatusEffectInstance(StatusEffects.DARKNESS, 72000, 0));
   public static final Potion ESSENCE_OF_WEALTH =  new Potion(new StatusEffectInstance(StatusEffects.LUCK, 72000, 2));
   public static final PotionItem MAGICAL_BREW =  new PotionItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxCount(1));
   public static final PotionItem CHARGED_MAGICAL_BREW = new ChargedMagicalBrew(new Item.Settings().rarity(Rarity.EPIC).fireproof().maxCount(1));

   public static final SoundEvent GROSZA_DAJ_WIEDZMINOWI = registerSound("grosza_daj_wiedzminowi");
   public static final SoundEvent KOTLOK_DEJ_HEKSEROWI = registerSound("kotlok_dej_hekserowi");

   public static final Item GROSZA_DAJ_WIEDZMINOWI_MUSIC_DISC = new Item(new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(RegistryKey.of(RegistryKeys.JUKEBOX_SONG, GROSZA_DAJ_WIEDZMINOWI.getId())));//.jukeboxPlayable(PlotArmorMusicDiscs.GROSZA_DAJ_WIEDZMINOWI_JUKEBOX));
   public static final Item KOTLOK_DEJ_HEKSEROWI_MUSIC_DISC = new Item(new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(RegistryKey.of(RegistryKeys.JUKEBOX_SONG, KOTLOK_DEJ_HEKSEROWI.getId())));

   public static ServerWorld GlobalServerWorld = null;

   private static final long INITIAL_DEBUFF_INTERVAL = 30 * 60 * 20;//minutes to minecraft ticks
   @Override
   public void onInitialize() {
      LOGGER.info("LOADING Plot Armor");

      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "breeze_bow"), BREEZE_BOW);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "packed_blue_ice"), PACKED_BLUE_ICE);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "magical_brew"), MAGICAL_BREW);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "charged_magical_brew"), CHARGED_MAGICAL_BREW);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "grosza_daj_wiedzminowi_music_disc"), GROSZA_DAJ_WIEDZMINOWI_MUSIC_DISC);
      Registry.register(Registries.ITEM, Identifier.of("plotarmor", "kotlok_dej_hekserowi_music_disc"), KOTLOK_DEJ_HEKSEROWI_MUSIC_DISC);

      
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_frost"), ESSENCE_OF_FROST);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_the_sea"), ESSENCE_OF_THE_SEA);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_depths"), ESSENCE_OF_DEPTHS);
      Registry.register(Registries.POTION, Identifier.of("plotarmor", "essence_of_wealth"), ESSENCE_OF_WEALTH);

      
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_frost"), ESSENCE_OF_FROST);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_the_sea"), ESSENCE_OF_THE_SEA);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_depths"), ESSENCE_OF_DEPTHS);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "essence_of_wealth"), ESSENCE_OF_WEALTH);
      //   Registry.register(Registries.ITEM, Identifier.of("plotarmor", "magical_brew"), MAGICAL_BREW);

      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, PlotArmorMod.PACKED_BLUE_ICE, Registries.POTION.getEntry(ESSENCE_OF_FROST));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.CONDUIT, Registries.POTION.getEntry(ESSENCE_OF_THE_SEA));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.SCULK_CATALYST, Registries.POTION.getEntry(ESSENCE_OF_DEPTHS));});
      FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {builder.registerPotionRecipe(Potions.THICK, Items.NETHERITE_BLOCK, Registries.POTION.getEntry(ESSENCE_OF_WEALTH));});

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
      ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
      ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::onPlayerDamage);
      ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this::onPlayerDeath);

      ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
         PlayerArmorState persistentState = PlayerArmorState.getServerState(GlobalServerWorld.getServer());
         UUID playerId = handler.player.getUuid();
         //loadPlayerData(handler.player);
         LOGGER.info("\nlogin:\n" + playerId + "\n**********");
         if (persistentState.playerOfflineTimes.containsKey(playerId)) {
            LOGGER.info("last logout time:" + persistentState.playerOfflineTimes.get(playerId));
             long offlineTime = GlobalServerWorld.getTime() - persistentState.playerOfflineTimes.remove(playerId);
             //long currentTime = server.getWorld(handler.player.getWorld().getRegistryKey()).getTime();
             LOGGER.info("total offline time:" + offlineTime);
             
             if (persistentState.playerArmorTimers.containsKey(playerId)) {
                 LOGGER.info("last timer:" + persistentState.playerArmorTimers.get(playerId));
                 persistentState.playerArmorTimers.put(playerId, persistentState.playerArmorTimers.get(playerId) + offlineTime);
                 LOGGER.info("new timer:" + persistentState.playerArmorTimers.get(playerId));
             }
         }
     });
 
     ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
         PlayerArmorState persistentState = PlayerArmorState.getServerState(GlobalServerWorld.getServer());
         //savePlayerData(handler.player);
         UUID playerId = handler.player.getUuid();
         persistentState.playerOfflineTimes.put(playerId, GlobalServerWorld.getTime());
         persistentState.markDirty();
         LOGGER.info("\nlogout:\n" + playerId + "\n**********");
         LOGGER.info("\nlogged out with time:" + persistentState.playerOfflineTimes.get(playerId));
     });
     LOGGER.info("LOADED Plot Armor");
      //ServerWorldEvents.UNLOAD.register(this::onWorldUnload);
  }

  private static SoundEvent registerSound(String id) {
    Identifier identifier = Identifier.of("plotarmor", id);
    SoundEvent event = Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    //Registry.registerReference(Registries.SOUND_EVENT, event.getId(), event);
    return event;
  }

  private void onWorldLoad(MinecraftServer server, ServerWorld world) {
      GlobalServerWorld = world;
      UseBlockCallback.EVENT.register(this::onBlockPlaced);
      PlayerBlockBreakEvents.AFTER.register(this::onBlockBroken);
  }

  private void onWorldTick(ServerWorld world) {
        PlayerArmorState persistentState = PlayerArmorState.getServerState(GlobalServerWorld.getServer());
        long currentTime = world.getTime();
        for (ServerPlayerEntity player : world.getPlayers()) {
            UUID playerId = player.getUuid();
            if (player.getCommandTags().contains("EverDrunkChargedBrew")) {
                //LOGGER.info("ignoring debuff counter for: " + playerId);
                continue;
            }

            if (persistentState.playerArmorTimers.containsKey(playerId)) {
                long lastDebuffTime = persistentState.playerArmorTimers.get(playerId);
                long interval = INITIAL_DEBUFF_INTERVAL * (1L << persistentState.playerHealthDebuffs.getOrDefault(playerId, 0));

                if(!player.hasStatusEffect(StatusEffects.WEAKNESS) && persistentState.playerHealthDebuffs.getOrDefault(playerId, 0)>=3)applyDebuffs(player, false);

                if (currentTime - lastDebuffTime >= interval) {
                    applyDebuffs(player, true);
                    persistentState.playerArmorTimers.put(playerId, currentTime);
                }
            }

            if (isWearingFullChainmail(player)) {
                if (!persistentState.playerArmorTimers.containsKey(playerId)) {
                    LOGGER.info("adding timer for:" + playerId);
                    //LOGGER.info("Default timer is:" + INITIAL_DEBUFF_INTERVAL);
                    persistentState.playerArmorTimers.put(playerId, currentTime);
                }
            }
        }
    }

    private boolean onPlayerDamage(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            if (isWearingFullChainmail(player)) {
                long currentTime = GlobalServerWorld.getTime();
                if(!iframes.containsKey(player.getUuid()) || iframes.get(player.getUuid())+600 <= currentTime || (iframes.get(player.getUuid())+100  >= currentTime && !player.getCommandTags().contains("EverDrunkChargedBrew")) || (iframes.get(player.getUuid())+20  >= currentTime && player.getCommandTags().contains("EverDrunkChargedBrew"))){
                    if(!iframes.containsKey(player.getUuid()) || iframes.get(player.getUuid())+600 <= currentTime) transferDamageToArmor(player, amount);
                    return false;
                }
                else return true;
            }
        }
        return true;
    }

    private void transferDamageToArmor(ServerPlayerEntity player, float damage) {
        long currentTime = GlobalServerWorld.getTime();
        float Divider = 1F;
        if(player.getCommandTags().contains("EverDrunkChargedBrew")) Divider = 4F;
        int armorDamage = Math.max(MathHelper.ceil(damage/Divider), 0);
        player.getInventory().getArmorStack(3).damage(armorDamage, player, EquipmentSlot.HEAD);
        player.getInventory().getArmorStack(2).damage(armorDamage, player, EquipmentSlot.CHEST);
        player.getInventory().getArmorStack(1).damage(armorDamage, player, EquipmentSlot.LEGS);
        player.getInventory().getArmorStack(0).damage(armorDamage, player, EquipmentSlot.FEET);
        //LOGGER.info("damaging armor, time: " + currentTime + " last damage time: " + iframes.get(player.getUuid()));
        iframes.put(player.getUuid(), currentTime); 
    }

    private void onPlayerDeath(ServerWorld world, Entity killer, LivingEntity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity) entity;
         iframes.remove(player.getUuid()); 
         UUID playerId = player.getUuid();
         if (!player.getCommandTags().contains("EverDrunkChargedBrew")) {
            PlayerArmorState persistentState = PlayerArmorState.getServerState(GlobalServerWorld.getServer());
            if (persistentState.playerHealthDebuffs.containsKey(playerId)) {
               applyDebuffs((ServerPlayerEntity) player, false);
            }
         }
      }
  }

    private void applyDebuffs(ServerPlayerEntity player, Boolean IncreaseLevel) {
        PlayerArmorState persistentState = PlayerArmorState.getServerState(GlobalServerWorld.getServer());
        UUID playerId = player.getUuid();
        int debuffLevel = persistentState.playerHealthDebuffs.getOrDefault(playerId, 0);
        if(IncreaseLevel)debuffLevel += 1;
        //LOGGER.info("debuffs time, lvl:" + debuffLevel);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.max(2.0, 20f - debuffLevel*2f));
        if (debuffLevel >= 3) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, Integer.MAX_VALUE, (debuffLevel / 3) - 1, true, false));
        }
        if (debuffLevel >= 4) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Integer.MAX_VALUE, (debuffLevel / 4) - 1, true, false));
        }

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.PLAYERS, 2.0F, 1.0F);
        player.getServerWorld().spawnParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR, player.getX(), player.getY()+1, player.getZ(), 150, 0.75, 1, 0.75, 0.1);

        persistentState.playerHealthDebuffs.put(playerId, debuffLevel);
    }

    private boolean isWearingFullChainmail(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.CHAINMAIL_HELMET &&
               player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.CHAINMAIL_CHESTPLATE &&
               player.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.CHAINMAIL_LEGGINGS &&
               player.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.CHAINMAIL_BOOTS;
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
          //LOGGER.info("CheckBlockMatched: " + state.isCheckBlockMatched());
   }
}