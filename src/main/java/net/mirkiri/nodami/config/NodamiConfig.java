package net.mirkiri.nodami.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.mirkiri.nodami.NoDamIMod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class NodamiConfig {
    private static final ForgeConfigSpec.Builder BUILDER;
    public static final Core CORE;
    public static final Thresholds THRESHOLDS;
    public static final Exclusions EXCLUSIONS;
    public static final Misc MISC;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        CORE = new Core(BUILDER);
        THRESHOLDS = new Thresholds(BUILDER);
        EXCLUSIONS = new Exclusions(BUILDER);
        MISC = new Misc(BUILDER);
        SPEC = BUILDER.build();
    }

    public static class Core {
        public final ForgeConfigSpec.IntValue iFrameIntervalTemp;
        public final ForgeConfigSpec.BooleanValue excludePlayersTemp, excludeAllMobsTemp;

        public int iFrameInterval;
        public boolean excludePlayers, excludeAllMobs;

        Core(ForgeConfigSpec.Builder builder) {
            builder.comment("Core functionality settings").push("core");

            iFrameIntervalTemp = builder.comment(
                            "How many ticks of i-frames does an entity get when damaged, from 0 (default), to 2^31-1 (nothing can take damage)")
                    .defineInRange("iFrameInterval", 0, 0, Integer.MAX_VALUE);
            excludePlayersTemp = builder.comment(
                            "Are players excluded from this mod (if true, players will always get 10 ticks of i-frames on being damaged")
                    .define("excludePlayers", false);
            excludeAllMobsTemp = builder.comment(
                            "Are players excluded from this mod (if true, players will always get 10 ticks of i-frames on being damaged")
                    .define("excludeAllMobs", false);

            builder.pop();
        }

    }

    public static class Thresholds {
        public final ForgeConfigSpec.DoubleValue attackCancelThresholdTemp, knockbackCancelThresholdTemp;

        public double attackCancelThreshold, knockbackCancelThreshold;

        public Thresholds(ForgeConfigSpec.Builder builder) {
            builder.comment("Threshold values for certain features").push("threshold");

            attackCancelThresholdTemp = builder.comment(
                            "How weak a player's attack can be before it gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, players cannot attack), or -0.1 (disables this feature)")
                    .defineInRange("attackCancelThreshold", 0.1, -0.1, 1);
            knockbackCancelThresholdTemp = builder.comment(
                            "How weak a player's attack can be before the knockback gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, no knockback), or -0.1 (disables this feature)")
                    .defineInRange("knockbackCancelThreshold", 0.75, -0.1, 1);

            builder.pop();
        }
    }

    public static class Exclusions {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> attackExcludedEntitiesTemp, dmgReceiveExcludedEntitiesTemp,
                damageSrcWhitelistTemp;

        public HashSet<String> attackExcludedEntities, dmgReceiveExcludedEntities, damageSrcWhitelist;

        public Exclusions(ForgeConfigSpec.Builder builder) {
            List<String> defaultAEE = Arrays.asList("minecraft:slime", "minecraft:magma_cube", "tconstruct:earth_slime", "tconstruct:sky_slime", "tconstruct:ender_slime", "tconstruct:terracube", "twilightforest:maze_slime");
            List<String> defaultREEE = Arrays.asList(new String[0]);
            List<String> defaultDSW = Arrays.asList("inFire", "lava", "sweetBerryBush", "cactus", "lightningBolt", "inWall", "hotFloor",
                    "outOfWorld");
            builder.comment("Exclusion lists for certain features").push("exclusions");

            Predicate<Object> dummyPredicate = t -> true;

            attackExcludedEntitiesTemp = builder.comment("List of entities that need to give i-frames on attacking")
                    .defineList("attackExcludedEntities", defaultAEE, dummyPredicate);

            dmgReceiveExcludedEntitiesTemp = builder
                    .comment(
                            "List of entities that need to receive i-frames on receiving attacks or relies on i-frames")
                    .defineList("dmgReceiveExcludedEntities", defaultREEE, dummyPredicate);

            damageSrcWhitelistTemp = builder
                    .comment("List of damage sources that need to give i-frames on doing damage (ex: lava)")
                    .defineList("damageSrcWhitelist", defaultDSW, dummyPredicate);

            builder.pop();

        }
    }

    public static class Misc {
        public final ForgeConfigSpec.BooleanValue debugModeTemp;

        public boolean debugMode;

        public Misc(ForgeConfigSpec.Builder builder) {
            builder.comment("Miscellaneous stuff").push("misc");

            debugModeTemp = builder.comment(
                            "If true, turns on feature which sends a message when a player receives damage, containing information such as the name of the source and the quantity. Use this to find the name of the source you need to whitelist, or the id of the mob you want to exclude.")
                    .define("debugMode", false);
            builder.pop();
        }
    }

    public static void cacheValues() {
        CORE.iFrameInterval = CORE.iFrameIntervalTemp.get();
        CORE.excludePlayers = CORE.excludePlayersTemp.get();
        CORE.excludeAllMobs = CORE.excludeAllMobsTemp.get();

        THRESHOLDS.attackCancelThreshold = THRESHOLDS.attackCancelThresholdTemp.get();
        THRESHOLDS.knockbackCancelThreshold = THRESHOLDS.knockbackCancelThresholdTemp.get();

        EXCLUSIONS.attackExcludedEntities = new HashSet<>(EXCLUSIONS.attackExcludedEntitiesTemp.get());
        EXCLUSIONS.dmgReceiveExcludedEntities = new HashSet<>(EXCLUSIONS.dmgReceiveExcludedEntitiesTemp.get());
        EXCLUSIONS.damageSrcWhitelist = new HashSet<>(EXCLUSIONS.damageSrcWhitelistTemp.get());

        MISC.debugMode = MISC.debugModeTemp.get();

    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == NodamiConfig.SPEC) {
            NodamiConfig.cacheValues();
        }
    }

    @SubscribeEvent
    public void onLoad(final ModConfigEvent.Loading event) {
        NoDamIMod.LOGGER.info("Config loaded!");
        NodamiConfig.cacheValues();
    }

    @SubscribeEvent
    public void onReload(final ModConfigEvent.Reloading event) {
        NoDamIMod.LOGGER.info("Config reloaded!");
        NodamiConfig.cacheValues();
    }
}
