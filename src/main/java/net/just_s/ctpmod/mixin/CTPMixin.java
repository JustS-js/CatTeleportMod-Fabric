package net.just_s.ctpmod.mixin;

import net.just_s.ctpmod.CMDHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class CTPMixin {
	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	private void sendCustomMessage(String message, CallbackInfo ci) {
		if (message.charAt(0) == '&') {
			CMDHandler.cmdHandler(message.substring(1).split(" "));
			ci.cancel();
		}
	}
}
