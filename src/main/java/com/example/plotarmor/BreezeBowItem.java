package com.example.plotarmor;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BreezeBowItem extends BowItem {
    public BreezeBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
         ItemStack itemStack = playerEntity.getProjectileType(stack);
         if (!itemStack.isEmpty()) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i);
            if (!((double)f < 0.1)) {
               List<ItemStack> list = load(stack, itemStack, playerEntity);
               if (world instanceof ServerWorld) {
                  ServerWorld serverWorld = (ServerWorld)world;
                  if (!list.isEmpty()) {
                     this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 0.0F, f == 1.0F, (LivingEntity)null);
                  }
               }
               world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            }
         }
        }
        /*if (!world.isClient) {
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
                    if (user instanceof PlayerEntity playerEntity) stack.damage(this.getWeaponStackDamage(itemStack), user, LivingEntity.getSlotForHand(playerEntity.getActiveHand()));
                    //if (user instanceof PlayerEntity playerEntity) stack.damage(1, playerEntity, EquipmentSlot.MAINHAND);
                }
            }
        }*/
    }

    @Override
    protected void shootAll(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, @Nullable LivingEntity target) {
      float i = 1.0F;

      for(int j = 0; j < projectiles.size(); ++j) {
         ItemStack itemStack = (ItemStack)projectiles.get(j);
         if (!itemStack.isEmpty()) {
            i = -i;
            ProjectileEntity projectileEntity = this.createArrowEntity(world, shooter, stack, itemStack, critical);
            Vec3d direction = shooter.getRotationVec(1.0F);
            projectileEntity.setVelocity(direction.x, direction.y, direction.z, speed, 0.0F);
            projectileEntity.setNoGravity(true);

            world.spawnEntity(projectileEntity);
            stack.damage(this.getWeaponStackDamage(itemStack), shooter, LivingEntity.getSlotForHand(hand));
            if (stack.isEmpty()) {
               break;
            }
         }
      }

   }

    public int getEnchantability() {
        return 1;
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    // @Override
    // public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
    //     return super.canApplyAtEnchantingTable(stack, enchantment);
    // }
}
