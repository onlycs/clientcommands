package net.earthcomputer.clientcommands.util;

import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.enchantment.Enchantments;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SeedfindingUtil {
    private SeedfindingUtil() {
    }

    @Nullable
    public static com.seedfinding.mcbiome.biome.Biome toSeedfindingBiome(Level level, Holder<Biome> biome) {
        ResourceLocation name = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome.value());
        if (name == null || !"minecraft".equals(name.getNamespace())) {
            return null;
        }
        for (var b : Biomes.REGISTRY.values()) {
            if (name.getPath().equals(b.getName())) {
                return b;
            }
        }
        return null;
    }

    public static ItemStack fromSeedfindingItem(com.seedfinding.mcfeature.loot.item.Item item) {
        return fromSeedfindingItem(new com.seedfinding.mcfeature.loot.item.ItemStack(item));
    }

    public static ItemStack fromSeedfindingItem(com.seedfinding.mcfeature.loot.item.ItemStack stack) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(stack.getItem().getName()));
        if (!stack.getItem().getEnchantments().isEmpty() && item == Items.BOOK) {
            item = Items.ENCHANTED_BOOK;
        }

        ItemStack ret = new ItemStack(item, stack.getCount());
        for (var enchAndLevel : stack.getItem().getEnchantments()) {
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(enchAndLevel.getFirst()));
            if (enchantment == null) {
                continue;
            }
            ret.enchant(enchantment, enchAndLevel.getSecond());
        }
        return ret;
    }

    public static boolean doesEnchantmentExist(Enchantment enchantment) {
        if (MultiVersionCompat.INSTANCE.getProtocolVersion() == SharedConstants.getProtocolVersion()) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (id == null || !id.getNamespace().equals("minecraft")) {
            return false;
        }
        String name = id.getPath();

        // Hack: sweeping was renamed to sweeping_edge, do an inverse rename here.
        // This should no longer be necessary once enchantments are data-driven in 1.21
        if ("sweeping_edge".equals(name)) {
            name = "sweeping";
        }

        String name_f = name;
        return Enchantments.getFor(SeedfindingUtil.getMCVersion()).stream().anyMatch(ench -> ench.getName().equals(name_f));
    }

    public static MCVersion getMCVersion() {
        return Objects.requireNonNullElseGet(MCVersion.fromString(MultiVersionCompat.INSTANCE.getProtocolName()), MCVersion::latest);
    }
}
