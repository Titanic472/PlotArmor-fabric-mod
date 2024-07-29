package com.example.plotarmor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class SpreadingBlackstone extends Block{
    private static final int SPREAD_CHANCE = 50;

    public SpreadingBlackstone(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        PlotArmorState modState = PlotArmorState.getServerState(world.getServer());
        if(modState.isCheckBlockMatched())return;
        // Проверка вероятности
        if (random.nextInt(SPREAD_CHANCE) == 0) {
            // Проверка наличия соседнего блока воздуха
            boolean hasAirNeighbor = false;
            for(int x = -1; x<=1 && !hasAirNeighbor; ++x){
                for(int y = -1; y<=1 && !hasAirNeighbor; ++y){
                    for(int z = -1; z<=1; ++z){
                        if (world.getBlockState(new BlockPos(new Vec3i(pos.getX()+x, pos.getY()+y, pos.getZ()+z))).isAir()) {
                            hasAirNeighbor = true;
                            break;
                        }
                    }
                }
            }
            for (Direction direction : Direction.values()) {
                if (world.getBlockState(pos.offset(direction)).isAir()) {
                    hasAirNeighbor = true;
                    break;
                }
            }

            // Если есть соседний блок воздуха, распространяем блок
            if (hasAirNeighbor) {
                //PlotArmorMod.LOGGER.info("checking...");
                if(canSpread(pos.offset(Direction.DOWN), world)) world.setBlockState(pos.offset(Direction.DOWN), this.getDefaultState());
                if(canSpread(pos.offset(Direction.UP), world)) world.setBlockState(pos.offset(Direction.UP), this.getDefaultState());
                if(canSpread(pos.offset(Direction.SOUTH), world)) world.setBlockState(pos.offset(Direction.SOUTH), this.getDefaultState());
                if(canSpread(pos.offset(Direction.NORTH), world)) world.setBlockState(pos.offset(Direction.NORTH), this.getDefaultState());
                if(canSpread(pos.offset(Direction.EAST), world)) world.setBlockState(pos.offset(Direction.EAST), this.getDefaultState());
                if(canSpread(pos.offset(Direction.WEST), world)) world.setBlockState(pos.offset(Direction.WEST), this.getDefaultState());
            }
        }
    }

    private boolean canSpread(BlockPos pos, ServerWorld world){
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(block == Blocks.AIR || block == Blocks.CHEST || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER || block == Blocks.WATER || block == Blocks.ENDER_CHEST || block == PlotArmorMod.SPREADING_BLACKSTONE)return false;
        else return true;
    }
}
