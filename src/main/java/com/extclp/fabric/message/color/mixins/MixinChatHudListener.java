package com.extclp.fabric.message.color.mixins;

import com.extclp.fabric.message.color.CustomMessageColorMod;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ChatHudListener.class)
public class MixinChatHudListener {

    @Inject(method = "onChatMessage", at = @At(value = "HEAD"))
    public void modifyMessage(MessageType messageType, Text message, UUID sender, CallbackInfo ci) {
        if(CustomMessageColorMod.color != null){
            CustomMessageColorMod.deepForEach(message, text -> {
                String i = "commands.message.display.incoming";
                String o = "commands.message.display.outgoing";
                if (text instanceof TranslatableText translatableText) {
                    if (i.equals(translatableText.getKey()) || o.equals(translatableText.getKey())) {
                        translatableText.styled(style -> style.withColor(CustomMessageColorMod.color));
                    }
                }
            });
        }
    }
}