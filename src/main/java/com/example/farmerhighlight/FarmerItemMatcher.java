package com.example.farmerhighlight;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Locale;

public final class FarmerItemMatcher {

    // Яркий золотисто-оранжевый цвет подсветки
    public static final int SLOT_FILL_ARGB   = 0xAAFFAA00;
    public static final int SLOT_BORDER_ARGB = 0xFFFFE600;
    public static final int ACCENT_RGB       = 0xFFFFAA00;

    private FarmerItemMatcher() {}

    public static boolean isFarmerSword(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!(stack.getItem() instanceof SwordItem)) {
            return false;
        }

        String name = stripFormatting(stack.getName().getString()).toLowerCase(Locale.ROOT);
        if (name.contains("фармер") || name.contains("farmer")) {
            return true;
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            String rawNbt = stripFormatting(nbt.toString()).toLowerCase(Locale.ROOT);
            if (rawNbt.contains("фармер") || rawNbt.contains("farmer")) {
                return true;
            }

            if (nbt.contains("display", NbtElement.COMPOUND_TYPE)) {
                NbtCompound display = nbt.getCompound("display");
                if (display.contains("Lore", NbtElement.LIST_TYPE)) {
                    NbtList lore = display.getList("Lore", NbtElement.STRING_TYPE);
                    for (NbtElement line : lore) {
                        String cleanLine = stripFormatting(line.asString()).toLowerCase(Locale.ROOT);
                        if (cleanLine.contains("фармер") || cleanLine.contains("farmer")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String stripFormatting(String input) {
        if (input == null) return "";
        return input.replaceAll("(?i)[§&][0-9a-fk-orx]", "");
    }
}