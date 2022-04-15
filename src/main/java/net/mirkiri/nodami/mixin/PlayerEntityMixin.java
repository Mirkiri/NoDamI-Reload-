package net.mirkiri.nodami.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.mirkiri.nodami.interfaces.EntityHurtCallback;
import net.mirkiri.nodami.interfaces.PlayerAttackCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
	private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo ci) {
		ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((PlayerEntity) (Object) this, source,
				amount);
		if (result == ActionResult.FAIL) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "attack", cancellable = true)
	private void onPlayerAttack(final Entity target, CallbackInfo ci) {
		ActionResult result = PlayerAttackCallback.EVENT.invoker().attackEntity((PlayerEntity)(Object)this, target);
		if (result == ActionResult.FAIL) {
			ci.cancel();
		}
	}
}
