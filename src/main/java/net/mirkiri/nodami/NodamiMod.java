package net.mirkiri.nodami;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodamiMod implements ModInitializer {
	public static final String MODID = "nodami";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("NoDamI for Minecraft 1.18.2 Fabric Edition is starting.");
		NodamiConfig.preInit();
		registerHandlers();
		LOGGER.info("NoDamI: Loading completed. This mod is powered by FabricMC and SnakeYAML");
	}

	private void registerHandlers() {
		EntityHurtCallback.EVENT.register((entity, source, amount) -> {
			if (entity.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}
			if (NodamiConfig.debugMode && entity instanceof PlayerEntity) {
				String debugSource;
				Entity trueSource = source.getAttacker();
				if (trueSource == null || EntityType.getId(trueSource.getType()) == null) {
					debugSource = "null";
				} else {
					debugSource = EntityType.getId(trueSource.getType()).toString();
				}
				String message = String.format("Type of damage received: %s\nAmount: %.3f\nTrue Source (mob id): %s\n",
						source.getName(), amount, debugSource);
				((PlayerEntity) entity).sendMessage(new LiteralText(message), false);

			}
			if (NodamiConfig.excludePlayers && entity instanceof PlayerEntity) {
				return ActionResult.PASS;
			}
			if (NodamiConfig.excludeAllMobs && !(entity instanceof PlayerEntity)) {
				return ActionResult.PASS;
			}
			for (String id : NodamiConfig.dmgReceiveExcludedEntities) {
				Identifier loc = EntityType.getId(entity.getType());
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

			for (String dmgType : NodamiConfig.damageSrcWhitelist) {
				if (source.getName().equals(dmgType)) {
					return ActionResult.PASS;
				}
			}

			for (String id : NodamiConfig.attackExcludedEntities) {
				Entity attacker = source.getAttacker();
				if (attacker == null)
					break;
				Identifier loc = EntityType.getId(attacker.getType());
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

			entity.timeUntilRegen = NodamiConfig.iFrameInterval;
			return ActionResult.PASS;
		});
		EntityKnockbackCallback.EVENT.register((entity, source, amp, dx, dz) -> {
			if (entity.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}
			if (source != null) {
				// IT'S ONLY MAGIC
				if (source instanceof PlayerEntity && ((PlayerEntity) source).handSwinging) {
					((PlayerEntity) source).handSwinging = false;
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});

		PlayerAttackCallback.EVENT.register((player, target) -> {
			if (player.getEntityWorld().isClient) {
				return ActionResult.PASS;
			}

			if (NodamiConfig.debugMode) {
				String message = String.format("Entity attacked: %s",
						EntityType.getId(target.getType()));
				player.sendMessage(new LiteralText(message), false);
			}

			float str = player.getAttackCooldownProgress(0);
			if (str <= NodamiConfig.attackCancelThreshold) {
				return ActionResult.FAIL;
			}
			if (str <= NodamiConfig.knockbackCancelThreshold) {
				// Don't worry, it's only magic
				player.handSwinging = true;
			}

			return ActionResult.PASS;

		});
	}

}
