package com.example.farmerhighlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

public class FarmerHighlightMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 1. Выделение в названии и подсказках (Tooltip)
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (FarmerItemMatcher.isFarmerSword(stack) && !lines.isEmpty()) {
                Text first = lines.get(0);
                MutableText highlightedTitle = Text.literal("✦ [ФАРМЕР] ")
                        .setStyle(Style.EMPTY.withColor(FarmerItemMatcher.ACCENT_RGB).withBold(true))
                        .append(first.copy().formatted(Formatting.GOLD, Formatting.BOLD));
                lines.set(0, highlightedTitle);
            }
        });

        // 2. Хотбар, меч в руке и выпавшие мечи на земле рядом
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null || client.options.hudHidden) {
                return;
            }

            int scaledWidth = client.getWindow().getScaledWidth();
            int scaledHeight = client.getWindow().getScaledHeight();
            int hotbarStartX = scaledWidth / 2 - 90;
            int hotbarY = scaledHeight - 22 + 3;

            // Подсветка слотов хотбара
            for (int slot = 0; slot < 9; slot++) {
                ItemStack stack = client.player.getInventory().getStack(slot);
                if (FarmerItemMatcher.isFarmerSword(stack)) {
                    int x = hotbarStartX + slot * 20;
                    drawSlotHighlight(drawContext, x, hotbarY);
                }
            }

            // Индикатор при удержании меча в руке
            ItemStack mainHand = client.player.getMainHandStack();
            ItemStack offHand = client.player.getOffHandStack();
            if (FarmerItemMatcher.isFarmerSword(mainHand) || FarmerItemMatcher.isFarmerSword(offHand)) {
                String handBanner = "✦ АКТИВЕН МЕЧ: ФАРМЕР ✦";
                int textWidth = client.textRenderer.getWidth(handBanner);
                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        Text.literal(handBanner).styled(s -> s.withColor(FarmerItemMatcher.ACCENT_RGB).withBold(true)),
                        scaledWidth / 2 - textWidth / 2,
                        scaledHeight - 58,
                        FarmerItemMatcher.ACCENT_RGB
                );
            }

            // Индикатор выпавших на землю мечей с «Фармером» в радиусе 32 блоков
            Box searchBox = client.player.getBoundingBox().expand(32.0);
            List<ItemEntity> droppedSwords = client.world.getEntitiesByClass(
                    ItemEntity.class,
                    searchBox,
                    entity -> FarmerItemMatcher.isFarmerSword(entity.getStack())
            );

            if (!droppedSwords.isEmpty()) {
                String groundInfo = "✦ Меч «Фармер» на земле рядом: " + droppedSwords.size();
                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        Text.literal(groundInfo).styled(s -> s.withColor(FarmerItemMatcher.ACCENT_RGB).withBold(true)),
                        10,
                        10,
                        FarmerItemMatcher.ACCENT_RGB
                );
            }
        });
    }

    private void drawSlotHighlight(DrawContext context, int x, int y) {
        context.fill(x, y, x + 16, y + 16, FarmerItemMatcher.SLOT_FILL_ARGB);
        context.drawBorder(x - 1, y - 1, 18, 18, FarmerItemMatcher.SLOT_BORDER_ARGB);
    }
}