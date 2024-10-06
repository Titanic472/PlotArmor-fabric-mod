package com.example.plotarmor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class ItemThrowEventHandler implements ServerTickEvents.EndWorldTick{
    private static final int CHECK_INTERVAL = 20; // 1 second

    @Override
    public void onEndTick(ServerWorld world) {
        if (world.getTime() % CHECK_INTERVAL == 0) {
            PlotArmorState state = PlotArmorState.getServerState(world.getServer());
            if (state.getChamberStart() != null && state.getChamberEnd() != null && state.isCheckBlockMatched()) {
                BlockPos checkPos1 = state.getChamberStart();
                BlockPos checkPos2 = state.getChamberEnd();
                for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, new Box(checkPos1.getX(), checkPos1.getY(), checkPos1.getZ(), checkPos2.getX(), checkPos2.getY(), checkPos2.getZ()), e -> true)) {
                    ItemStack stack = itemEntity.getStack();
                    if (stack.getItem() == PlotArmorMod.MAGICAL_BREW) {
                        itemEntity.setStack(new ItemStack(PlotArmorMod.CHARGED_MAGICAL_BREW));
                        world.spawnParticles(ParticleTypes.ENCHANTED_HIT, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);

                        // Проигрывание звука
                        world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, itemEntity.getSoundCategory(), 2.0F, 1.0F);
                        for(int i = 1; i<=8;i+=1) SpreadingBlackstone.spreadAll(world);
                    }
                    if (stack.getItem() == Items.GOLD_INGOT) {
                        itemEntity.setStack(new ItemStack(Items.IRON_INGOT, stack.getCount()));
                        world.spawnParticles(ParticleTypes.ENCHANTED_HIT, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);

                        // Проигрывание звука
                        world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, itemEntity.getSoundCategory(), 2.0F, 1.0F);
                        for(int i = 1; i<=stack.getCount();i*=2) SpreadingBlackstone.spreadAll(world);
                    }
                    if (stack.getItem() == Items.BONE && stack.getCount()>=8) {
                        itemEntity.setStack(new ItemStack(Items.SKELETON_SKULL, stack.getCount()/8));
                        world.spawnParticles(ParticleTypes.ENCHANTED_HIT, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);

                        // Проигрывание звука
                        world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, itemEntity.getSoundCategory(), 2.0F, 1.0F);
                        for(int i = 8; i<=stack.getCount();i*=2) SpreadingBlackstone.spreadAll(world);
                    }
                    if (stack.getItem() == Items.BLACKSTONE) {
                        itemEntity.setStack(new ItemStack(PlotArmorMod.SPREADING_BLACKSTONE, stack.getCount()));
                        world.spawnParticles(ParticleTypes.ENCHANTED_HIT, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 10, 0.5, 0.5, 0.5, 0.1);

                        // Проигрывание звука
                        world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, itemEntity.getSoundCategory(), 2.0F, 1.0F);
                    }
                }
            }
        }
    }
}