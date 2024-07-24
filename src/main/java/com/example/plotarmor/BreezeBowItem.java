package com.example.plotarmor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreezeBowItem extends BowItem {
    public BreezeBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = BowItem.getPullProgress(i);
            if ((double)f >= 0.1) {
                boolean bl = user.isHolding(Items.SPECTRAL_ARROW);
                ItemStack itemStack = user.getProjectileType(stack);

                if (!itemStack.isEmpty() || bl) {
                    if (itemStack.isEmpty()) {
                        itemStack = new ItemStack(Items.ARROW);
                    }

                    float g = BowItem.getPullProgress(i);
                    PersistentProjectileEntity arrowEntity = ProjectileUtil.createArrowProjectile(user, stack, 1f, null);
                    arrowEntity.setDamage(g * 7.0F);
                    Vec3d direction = user.getRotationVec(1.0F);
                    arrowEntity.setVelocity(direction.x, direction.y, direction.z, g * 3.0F, 0.0F);
                    //arrowEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, g * 3.0F, 1.0F);

                    // Стрелы не подвержены гравитации
                    arrowEntity.setNoGravity(true);

                    world.spawnEntity(arrowEntity);
                    stack.damage(1,user, EquipmentSlot.MAINHAND);
                }
            }
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    // @Override
    // public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
    //     return super.canApplyAtEnchantingTable(stack, enchantment);
    // }
}
