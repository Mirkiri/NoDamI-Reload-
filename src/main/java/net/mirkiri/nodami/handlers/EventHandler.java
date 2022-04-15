package net.mirkiri.nodami.handlers;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mirkiri.nodami.config.NodamiConfig;

import java.util.UUID;

@Mod.EventBusSubscriber
public class EventHandler {
    private static UUID debugUUID = new UUID(0L, 0L);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityHurt(LivingHurtEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.getLevel().isClientSide()) {
                return;
            }
            DamageSource source = event.getSource();
            Entity trueSource = source.getDirectEntity();
            ResourceLocation trueSourceloc = trueSource != null ? EntityType.getKey(trueSource.getType()) : null;
            if (NodamiConfig.MISC.debugMode && entity instanceof Player) {
                String trueSourceName;
                if (trueSource != null && trueSourceloc != null) {
                    trueSourceName = trueSourceloc.toString();
                } else {
                    trueSourceName = "null";
                }
                String message = String.format("Type of damage received: %s\nAmount: %.3f\nTrue Source (mob id): %s\n",
                        source.getMsgId(), event.getAmount(), trueSourceName);
                entity.sendMessage(new TextComponent(message), debugUUID);
            }
            if (NodamiConfig.CORE.excludePlayers && entity instanceof Player) {
                return;
            }

            if (NodamiConfig.CORE.excludeAllMobs && !(entity instanceof Player)) {
                return;
            }

            ResourceLocation loc = EntityType.getKey(entity.getType());
            if (loc != null && NodamiConfig.EXCLUSIONS.dmgReceiveExcludedEntities.contains(loc.toString())) {
                return;
            }

            if (NodamiConfig.EXCLUSIONS.damageSrcWhitelist.contains(source.getMsgId())) {
                return;
            }

            if (trueSource != null) {
                if (trueSourceloc != null && NodamiConfig.EXCLUSIONS.attackExcludedEntities.contains(trueSourceloc.toString())) {
                    return;
                }
            }
            entity.invulnerableTime = NodamiConfig.CORE.iFrameInterval;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (!event.isCanceled()) {
            Player player = event.getPlayer();
            if (player.getLevel().isClientSide()) {
                return;
            }
            float str = player.getAttackStrengthScale(0);
            if (str <= NodamiConfig.THRESHOLDS.attackCancelThreshold) {
                event.setCanceled(true);
                return;
            }
            if (str <= NodamiConfig.THRESHOLDS.knockbackCancelThreshold) {
                Entity target = event.getTarget();
                // Don't worry, it's only magic
                if (target != null && target instanceof LivingEntity) {
                    ((LivingEntity)target).swinging = true;
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onKnockback(LivingKnockBackEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.swinging) {
                event.setCanceled(true);
                entity.swinging = false;
            }
        }

    }
}
