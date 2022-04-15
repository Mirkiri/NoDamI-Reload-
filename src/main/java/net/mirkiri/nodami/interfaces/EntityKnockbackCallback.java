package net.mirkiri.nodami.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface EntityKnockbackCallback {
    Event<EntityKnockbackCallback> EVENT = EventFactory.createArrayBacked(EntityKnockbackCallback.class,
            (listeners) -> (entity, source, strength, x, z) -> {
                for (EntityKnockbackCallback event : listeners) {
                    ActionResult result = event.takeKnockback(entity, source, strength, x, z);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult takeKnockback(LivingEntity entity, Entity source, double strength, double x, double z);
}