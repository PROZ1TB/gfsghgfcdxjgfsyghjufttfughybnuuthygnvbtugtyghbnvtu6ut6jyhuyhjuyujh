package com.trueopsec.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.trueopsec.Spoof;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

/** Every scoreboard line, tab entry and nametag passes through here, so one hook covers them all. */
@Mixin(PlayerTeam.class)
public class PlayerTeamMixin {
	@Inject(method = "formatNameForTeam", at = @At("RETURN"), cancellable = true, require = 0)
	private static void trueopsec$rewrite(Team team, Component name, CallbackInfoReturnable<MutableComponent> cir) {
		cir.setReturnValue(Spoof.rewrite(cir.getReturnValue()));
	}
}
