package com.example.plotarmor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class SpreadingBlackstone extends Block{
    private static final int SPREAD_CHANCE = 100;
    boolean forceSpread = false;

    public SpreadingBlackstone(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        // Add this block's position to the list when it's added
        PlotArmorState modState = PlotArmorState.getServerState(world.getServer());
        modState.addBlackstone(pos);
        //PlotArmorMod.LOGGER.info("added block");
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        // Remove this block's position from the list when it's broken
        PlotArmorState modState = PlotArmorState.getServerState(world.getServer());
        modState.removeBlackstone(pos);
        //PlotArmorMod.LOGGER.info("deleted block");
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //PlotArmorState modState = PlotArmorState.getServerState(world.getServer());
        //if(modState.isCheckBlockMatched())return;//safety switch
        // Проверка вероятности
        if (forceSpread || random.nextInt(SPREAD_CHANCE) == 0) {
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
            forceSpread = false;
        }
    }

    private boolean canSpread(BlockPos pos, ServerWorld world){
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(block == Blocks.AIR || block == Blocks.CHEST || block == Blocks.BARREL || block == Blocks.SHULKER_BOX  || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER || block == Blocks.WATER || block == Blocks.ENDER_CHEST || block == PlotArmorMod.SPREADING_BLACKSTONE || block == Blocks.DEEPSLATE_TILE_STAIRS || block == Blocks.POLISHED_DEEPSLATE_STAIRS || block == Blocks.SOUL_SAND || block == Blocks.CHISELED_TUFF_BRICKS || block == Blocks.CHISELED_TUFF || block == Blocks.GOLD_BLOCK || block == Blocks.NETHERITE_BLOCK || block == Blocks.LAPIS_BLOCK || block == Blocks.POLISHED_TUFF_SLAB || block == Blocks.CHAIN || block == Blocks.CUT_COPPER_SLAB || block == Blocks.POLISHED_BLACKSTONE_STAIRS || block == Blocks.CRYING_OBSIDIAN || block == Blocks.TRIAL_SPAWNER || block == Blocks.VAULT || block == Blocks.REDSTONE_WIRE || block == Blocks.REDSTONE_BLOCK || block == Blocks.STICKY_PISTON || block == Blocks.PISTON)return false;
        else return true;
    }

    public static void spreadAll(ServerWorld world) {
        PlotArmorState modState = PlotArmorState.getServerState(world.getServer());
        List<BlockPos> blackstonePositions = modState.getBlackstonePositions(); // Get a copy of the list
        
        for (BlockPos pos : blackstonePositions) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block == PlotArmorMod.SPREADING_BLACKSTONE) {
                Random random = world.getRandom();
                ((SpreadingBlackstone) block).forceSpread = true;
                ((SpreadingBlackstone) block).randomTick(blockState, world, pos, random);  // Trigger spreading logic
            }
            else {
                modState.removeBlackstone(pos);
            }
        }
    }
}
