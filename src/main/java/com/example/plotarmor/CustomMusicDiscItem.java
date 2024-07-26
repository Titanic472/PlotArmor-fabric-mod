package com.example.plotarmor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CustomMusicDiscItem extends Item {
    private final SoundEvent sound;

    public CustomMusicDiscItem(int comparatorOutput, SoundEvent sound, Item.Settings settings) {
        super(settings);
        this.sound = sound;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), this.sound, SoundCategory.RECORDS, 1.0F, 1.0F);
        }
        return super.use(world, player, hand);
    }
}
