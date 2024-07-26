package com.example.plotarmor;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface PlotArmorMusicDiscs {
    RegistryKey<JukeboxSong> KOTLOK_DEJ_HEKSEROWI_JUKEBOX = of("kotlok_dej_hekserowi_music_disc");
    RegistryKey<JukeboxSong> GROSZA_DAJ_WIEDZMINOWI_JUKEBOX = of("grosza_daj_wiedzminowi_music_disc");

    private static RegistryKey<JukeboxSong> of(String id) {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of("plotarmor", id));
    }

    private static void register(Registerable<JukeboxSong> registry, RegistryKey<JukeboxSong> key, RegistryEntry.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        registry.register(key, new JukeboxSong(soundEvent, Text.translatable(Util.createTranslationKey("jukebox_song", key.getValue())), (float)lengthInSeconds, comparatorOutput));
    }

    static void bootstrap(Registerable<JukeboxSong> registry) {
        register(registry, KOTLOK_DEJ_HEKSEROWI_JUKEBOX, PlotArmorMod.KOTLOK_DEJ_HEKSEROWI_REF, 187, 1);
        register(registry, GROSZA_DAJ_WIEDZMINOWI_JUKEBOX, PlotArmorMod.GROSZA_DAJ_WIEDZMINOWI_REF, 181, 1);
    }
}
