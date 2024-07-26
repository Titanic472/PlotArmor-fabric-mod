package com.example.plotarmor;

import net.minecraft.block.Block;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class PlotArmorState extends PersistentState {
    public static final String ID = "plotarmor_state";

    private BlockPos checkBlockPos;
    private BlockPos chamberStart;
    private BlockPos chamberEnd;
    private Block checkBlockType;
    private boolean checkBlockMatched;

    public PlotArmorState() {
        // Default values
        this.checkBlockPos = null;
        this.checkBlockType = null;
        this.chamberStart = null;
        this.chamberEnd = null;
        this.checkBlockMatched = false;
    }

    public static PlotArmorState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PlotArmorState state = new PlotArmorState();
        if (tag.contains("checkBlockPos")) {
            state.checkBlockPos = BlockPos.fromLong(tag.getLong("checkBlockPos"));
        }
        if (tag.contains("checkBlockType")) {
            state.checkBlockType = Registries.BLOCK.get(Identifier.of(tag.getString("checkBlockType")));
        }
        if (tag.contains("chamberStart")) {
            state.chamberStart = BlockPos.fromLong(tag.getLong("chamberStart"));
        }
        if (tag.contains("chamberEnd")) {
            state.chamberEnd = BlockPos.fromLong(tag.getLong("chamberEnd"));
        }
        state.checkBlockMatched = tag.getBoolean("checkBlockMatched");
        return state;
    }

    private static Type<PlotArmorState> type = new Type<>(
            PlotArmorState::new, // If there's no 'PlotArmorState' yet create one
            PlotArmorState::createFromNbt, // If there is a 'PlotArmorState' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public void fromTag(NbtCompound tag) {
        
    }

    public static PlotArmorState getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
 
        // The first time the following 'getOrCreate' function is called, it creates a brand new 'PlotArmorState' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'PlotArmorState' NBT on disk to our function 'PlotArmorState::createFromNbt'.
        PlotArmorState state = persistentStateManager.getOrCreate(type, "plotarmorstate");
 
        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();
 
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (this.checkBlockPos != null) {
            nbt.putLong("checkBlockPos", this.checkBlockPos.asLong());
        }
        if (this.checkBlockType != null) {
            nbt.putString("checkBlockType", Registries.BLOCK.getId(this.checkBlockType).toString());
        }
        if (this.chamberStart != null) {
            nbt.putLong("chamberStart", this.chamberStart.asLong());
        }
        if (this.chamberEnd != null) {
            nbt.putLong("chamberEnd", this.chamberEnd.asLong());
        }
        nbt.putBoolean("checkBlockMatched", this.checkBlockMatched);
        return nbt;
    }

    // Getters and Setters
    public BlockPos getCheckBlockPos() {
        return checkBlockPos;
    }

    public void setCheckBlockPos(BlockPos checkBlockPos) {
        this.checkBlockPos = checkBlockPos;
        markDirty(); // Ensure changes are saved
    }

    public Block getCheckBlockType() {
        return checkBlockType;
    }

    public void setCheckBlockType(Block checkBlockType) {
        this.checkBlockType = checkBlockType;
        markDirty(); // Ensure changes are saved
    }

    public BlockPos getChamberStart() {
        return chamberStart;
    }

    public void setChamberStart(BlockPos chamberStart) {
        this.chamberStart = chamberStart;
        markDirty(); // Ensure changes are saved
    }

    public BlockPos getChamberEnd() {
        return chamberEnd;
    }

    public void setChamberEnd(BlockPos chamberEnd) {
        this.chamberEnd = chamberEnd;
        markDirty(); // Ensure changes are saved
    }

    public boolean isCheckBlockMatched() {
        return checkBlockMatched;
    }

    public void setCheckBlockMatched(boolean checkBlockMatched) {
        this.checkBlockMatched = checkBlockMatched;
        markDirty(); // Ensure changes are saved
    }
}
