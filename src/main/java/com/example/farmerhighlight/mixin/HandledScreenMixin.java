package com.example.farmerhighlight.mixin;

import com.example.farmerhighlight.FarmerItemMatcher;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void farmerhighlight$beforeDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if (slot != null && FarmerItemMatcher.isFarmerSword(slot.getStack())) {
            int x = slot.x;
            int y = slot.y;
            context.fill(x, y, x + 16, y + 16, FarmerItemMatcher.SLOT_FILL_ARGB);
            context.drawBorder(x - 1, y - 1, 18, 18, FarmerItemMatcher.SLOT_BORDER_ARGB);
        }
    }
}