package com.example.plotarmor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChargedMagicalBrew extends PotionItem {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlotArmor");

    public ChargedMagicalBrew(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemstack = super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            if(player.getCommandTags().contains("EverDrunkChargedBrew")){
                LOGGER.info("\n*\n*\nEverDrunkChargedBrew is now true\n*\n*");
            }
            player.addCommandTag("EverDrunkChargedBrew");
            if(player.getCommandTags().contains("EverDrunkChargedBrew")){
                LOGGER.info("\n*\n*\nEverDrunkChargedBrew is now true\n*\n*");
            }
            player.clearStatusEffects();
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 2.0F, 1.0F);
            player.getServerWorld().spawnParticles(ParticleTypes.SCRAPE, player.getX(), player.getY()+1, player.getZ(), 150, 0.75, 1, 0.75, 0.1);
            
            PlayerArmorState persistentState = PlayerArmorState.getServerState(PlotArmorMod.GlobalServerWorld.getServer());
            persistentState.playerArmorTimers.remove(player.getUuid());
            persistentState.playerHealthDebuffs.remove(player.getUuid());
            
        }
        return itemstack;
    }
}