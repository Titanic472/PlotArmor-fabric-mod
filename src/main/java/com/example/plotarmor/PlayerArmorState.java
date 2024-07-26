package com.example.plotarmor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import net.minecraft.world.PersistentState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class PlayerArmorState extends PersistentState {
    public static final String ID = "plotarmor_state";

    public final Map<UUID, Long> playerArmorTimers = new HashMap<>();
    public final Map<UUID, Integer> playerHealthDebuffs = new HashMap<>();
    public final Map<UUID, Long> playerOfflineTimes = new HashMap<>();

    public static PlayerArmorState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PlayerArmorState state = new PlayerArmorState();
        NbtCompound armorTimers = tag.getCompound("ArmorTimers");
        armorTimers.getKeys().forEach(key -> {
            state.playerArmorTimers.put(UUID.fromString(key), armorTimers.getCompound(key).getLong("ArmorTimer"));
        });

        NbtCompound healthDebuffs = tag.getCompound("HealthDebuffs");
        healthDebuffs.getKeys().forEach(key -> {
            state.playerHealthDebuffs.put(UUID.fromString(key), healthDebuffs.getCompound(key).getInt("HealthDebuff"));
        });

        NbtCompound offlineTimers = tag.getCompound("OfflineTimers");
        offlineTimers.getKeys().forEach(key -> {
            state.playerOfflineTimes.put(UUID.fromString(key), offlineTimers.getCompound(key).getLong("OfflineTimer"));
        });

        return state;
    }

    private static Type<PlayerArmorState> type = new Type<>(
            PlayerArmorState::new, // If there's no 'PlayerArmorState' yet create one
            PlayerArmorState::createFromNbt, // If there is a 'PlayerArmorState' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static PlayerArmorState getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
 
        // The first time the following 'getOrCreate' function is called, it creates a brand new 'PlayerArmorState' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'PlayerArmorState' NBT on disk to our function 'PlayerArmorState::createFromNbt'.
        PlayerArmorState state = persistentStateManager.getOrCreate(type, "plotarmorplayer");
 
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
        
        NbtCompound armorTimers = new NbtCompound();
        playerArmorTimers.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putLong("ArmorTimer", playerData);
        
            armorTimers.put(uuid.toString(), playerNbt);
        });
        nbt.put("ArmorTimers", armorTimers);

        NbtCompound healthDebuffs = new NbtCompound();
        playerHealthDebuffs.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putLong("HealthDebuff", playerData);
        
            healthDebuffs.put(uuid.toString(), playerNbt);
        });
        nbt.put("HealthDebuffs", healthDebuffs);

        NbtCompound offlineTimers = new NbtCompound();
        playerOfflineTimes.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putLong("OfflineTimer", playerData);
        
            offlineTimers.put(uuid.toString(), playerNbt);
        });
        nbt.put("OfflineTimers", offlineTimers);
        
        return nbt;
    }
}
