package net.mirkiri.nodami;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.mirkiri.nodami.config.NodamiConfig;
import net.mirkiri.nodami.interfaces.EntityHurtCallback;
import net.mirkiri.nodami.interfaces.EntityKnockbackCallback;
import net.mirkiri.nodami.interfaces.PlayerAttackCallback;
import net.mirkiri.nodami.interfaces.PlayerEntityAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodamiMod implements ModInitializer {
	public static final String MODID = "nodami";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("NoDamI for Minecraft 1.18.2 Fabric Edition is starting.");
		AutoConfig.register(NodamiConfig.class, JanksonConfigSerializer::new);
		registerHandlers();
		LOGGER.info("NoDamI: Loading completed.");
	}

	private void registerHandlers() {
		EntityHurtCallback.EVENT.register((entity, source, amount) -> {
			if (entity.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}
			Entity trueSource = source.getAttacker();
			if (NodamiConfig.getConfig().debugMode && entity instanceof PlayerEntity) {
				String debugSource;
				if (trueSource == null || EntityType.getId(trueSource.getType()) == null) {
					debugSource = "null";
				} else {
					debugSource = EntityType.getId(trueSource.getType()).toString();
				}
				String message = String.format("Type of damage received: %s\nAmount: %.3f\nTrue Source (mob id): %s\n",
						source.getName(), amount, debugSource);
				((PlayerEntity) entity).sendMessage(new LiteralText(message), false);

			}
			if (NodamiConfig.getConfig().excludePlayers && entity instanceof PlayerEntity) {
				return ActionResult.PASS;
			}
			if (NodamiConfig.getConfig().excludeAllMobs && !(entity instanceof PlayerEntity)) {
				return ActionResult.PASS;
			}
				Identifier loc = EntityType.getId(entity.getType());
			for (String id : NodamiConfig.getConfig().dmgReceiveExcludedEntities) {
				if (loc == null)
					break;
				int starIndex = id.indexOf('*');
				if (starIndex != -1) {
					if (loc.toString().contains(id.substring(0, starIndex))) {
						return ActionResult.PASS;
					}
				} else if (loc.toString().equals(id)) {
					return ActionResult.PASS;
				}
			}
			for (String dmgType : NodamiConfig.getConfig().damageSrcWhitelist) {
				if (source.getName().equals(dmgType)) {
					return ActionResult.PASS;
				}
			}
			for (String id : NodamiConfig.getConfig().attackExcludedEntities) {
				Entity attacker = source.getAttacker();
				if (attacker == null)
					break;
				if (loc == null)
					break;
				int starIndex = id.indexOf('*');
				if (starIndex != -1) {
					if (loc.toString().contains(id.substring(0, starIndex))) {
						return ActionResult.PASS;
					}
				} else if (loc.toString().equals(id)) {
					return ActionResult.PASS;
				}

			}

			entity.timeUntilRegen = NodamiConfig.getConfig().iFrameInterval;
			return ActionResult.PASS;
		});
		PlayerAttackCallback.EVENT.register((player, target) -> {
			if (player.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}

			if (NodamiConfig.getConfig().debugMode) {
				String message = String.format("Entity attacked: %s",
						EntityType.getId(target.getType()));
				player.sendMessage(new LiteralText(message), false);
			}

			float str = player.getAttackCooldownProgress(0);
			if (str <= NodamiConfig.getConfig().attackCancelThreshold) {
				return ActionResult.FAIL;
			}
			if (str <= NodamiConfig.getConfig().knockbackCancelThreshold) {
				// Don't worry, it's only magic
				PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
				playerAccessor.setSwinging(true);
			}

			return ActionResult.PASS;

		});
		EntityKnockbackCallback.EVENT.register((entity, source, amp, dx, dz) -> {
			if (entity.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}
			if (source != null) {
				// IT'S ONLY MAGIC
				if (source instanceof PlayerEntity player) {
					PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
					if (playerAccessor.isSwinging()) {
						playerAccessor.setSwinging(false);
						return ActionResult.FAIL;
					}
				}
			}
			return ActionResult.PASS;
		});
	}

}
