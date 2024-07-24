package com.example.plotarmor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.server.network.ServerPlayerEntity;
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
            player.addCommandTag("EverDrunkChargedBrew");
            if(player.getCommandTags().contains("EverDrunkChargedBrew")){
                LOGGER.info("\n*\n*\nEverDrunkChargedBrew is now true\n*\n*");
            }
            
        }
        return itemstack;
    }
}