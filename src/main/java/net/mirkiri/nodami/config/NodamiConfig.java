package net.mirkiri.nodami.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.mirkiri.nodami.NodamiMod;

@Config(name = NodamiMod.MODID)
public class NodamiConfig implements ConfigData {
    @Comment("How many ticks of i-frames does an entity get when damaged, from 0 (default), to 2^31-1 (nothing can take damage)")
    public int iFrameInterval = 0;

    @Comment("Are players excluded from this mod (if true, players will always get 10 ticks of i-frames on being damaged")
    public boolean excludePlayers = false;

    @Comment("Are players excluded from this mod (if true, players will always get 10 ticks of i-frames on being damaged")
    public boolean excludeAllMobs = false;

    @Comment("If true, turns on feature which sends a message when a player receives damage, containing information such as the name of the source and the quantity. Use this to find the name of the source you need to whitelist, or the id of the mob you want to exclude.")
    public boolean debugMode = false;

    @Comment("How weak a player's attack can be before it gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, players cannot attack), or -0.1 (disables this feature)")
    public float attackCancelThreshold = 0.1f;

    @Comment("How weak a player's attack can be before the knockback gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, no knockback), or -0.1 (disables this feature)")
    public float knockbackCancelThreshold = 0.75f;

    @Comment("List of entities that need to give i-frames on attacking")
    public String[] attackExcludedEntities = new String[] {"minecraft:slime", "minecraft:magma_cube", "twilightforest:maze_slime"};;

    @Comment("List of entities that need to receive i-frames on receiving attacks or relies on i-frames")
    public String[] dmgReceiveExcludedEntities = new String[] {};

    @Comment("List of damage sources that need to give i-frames on doing damage (ex: lava)")
    public String[] damageSrcWhitelist = new String[] {"inFire", "lava", "sweetBerryBush", "cactus", "lightningBolt", "inWall", "hotFloor"};

    public static NodamiConfig getConfig() {
        return AutoConfig.getConfigHolder(NodamiConfig.class).getConfig();
    }
}
