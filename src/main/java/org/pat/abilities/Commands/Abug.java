package org.pat.abilities.Commands;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Abug implements TabExecutor {

    /**
     * Sophisticated debug command
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {

        if (sender instanceof Player p) {
            UUID uuid = p.getUniqueId();
            if (p.isOp()) {
                ItemStack item = p.getInventory().getItemInMainHand();
                ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
                if (axe.hasData(DataComponentTypes.MAX_DAMAGE)) item.setData(DataComponentTypes.MAX_DAMAGE, axe.getData(DataComponentTypes.MAX_DAMAGE)); else System.out.println("Missing: MAX_DAMAGE");
                if (axe.hasData(DataComponentTypes.DAMAGE)) item.setData(DataComponentTypes.DAMAGE, axe.getData(DataComponentTypes.DAMAGE)); else System.out.println("Missing: DAMAGE");
                if (axe.hasData(DataComponentTypes.USE_EFFECTS)) item.setData(DataComponentTypes.USE_EFFECTS, axe.getData(DataComponentTypes.USE_EFFECTS)); else System.out.println("Missing: USE_EFFECTS");
                if (axe.hasData(DataComponentTypes.CUSTOM_NAME)) item.setData(DataComponentTypes.CUSTOM_NAME, axe.getData(DataComponentTypes.CUSTOM_NAME)); else System.out.println("Missing: CUSTOM_NAME");
                if (axe.hasData(DataComponentTypes.ITEM_NAME)) item.setData(DataComponentTypes.ITEM_NAME, axe.getData(DataComponentTypes.ITEM_NAME)); else System.out.println("Missing: ITEM_NAME");
                if (axe.hasData(DataComponentTypes.ITEM_MODEL)) item.setData(DataComponentTypes.ITEM_MODEL, axe.getData(DataComponentTypes.ITEM_MODEL)); else System.out.println("Missing: ITEM_MODEL");
                if (axe.hasData(DataComponentTypes.LORE)) item.setData(DataComponentTypes.LORE, axe.getData(DataComponentTypes.LORE)); else System.out.println("Missing: LORE");
                if (axe.hasData(DataComponentTypes.RARITY)) item.setData(DataComponentTypes.RARITY, axe.getData(DataComponentTypes.RARITY)); else System.out.println("Missing: RARITY");
                if (axe.hasData(DataComponentTypes.ENCHANTMENTS)) item.setData(DataComponentTypes.ENCHANTMENTS, axe.getData(DataComponentTypes.ENCHANTMENTS)); else System.out.println("Missing: ENCHANTMENTS");
                if (axe.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, axe.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS)); else System.out.println("Missing: ATTRIBUTE_MODIFIERS");
                if (axe.hasData(DataComponentTypes.TOOLTIP_DISPLAY)) item.setData(DataComponentTypes.TOOLTIP_DISPLAY, axe.getData(DataComponentTypes.TOOLTIP_DISPLAY)); else System.out.println("Missing: TOOLTIP_DISPLAY");
                if (axe.hasData(DataComponentTypes.REPAIR_COST)) item.setData(DataComponentTypes.REPAIR_COST, axe.getData(DataComponentTypes.REPAIR_COST)); else System.out.println("Missing: REPAIR_COST");
                if (axe.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, axe.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)); else System.out.println("Missing: ENCHANTMENT_GLINT_OVERRIDE");
                if (axe.hasData(DataComponentTypes.DAMAGE_RESISTANT)) item.setData(DataComponentTypes.DAMAGE_RESISTANT, axe.getData(DataComponentTypes.DAMAGE_RESISTANT)); else System.out.println("Missing: DAMAGE_RESISTANT");
                if (axe.hasData(DataComponentTypes.TOOL)) item.setData(DataComponentTypes.TOOL, axe.getData(DataComponentTypes.TOOL)); else System.out.println("Missing: TOOL");
                if (axe.hasData(DataComponentTypes.WEAPON)) item.setData(DataComponentTypes.WEAPON, axe.getData(DataComponentTypes.WEAPON)); else System.out.println("Missing: WEAPON");
                if (axe.hasData(DataComponentTypes.ENCHANTABLE)) item.setData(DataComponentTypes.ENCHANTABLE, axe.getData(DataComponentTypes.ENCHANTABLE)); else System.out.println("Missing: ENCHANTABLE");
                if (axe.hasData(DataComponentTypes.REPAIRABLE)) item.setData(DataComponentTypes.REPAIRABLE, axe.getData(DataComponentTypes.REPAIRABLE)); else System.out.println("Missing: REPAIRABLE");
                if (axe.hasData(DataComponentTypes.SWING_ANIMATION)) item.setData(DataComponentTypes.SWING_ANIMATION, axe.getData(DataComponentTypes.SWING_ANIMATION)); else System.out.println("Missing: SWING_ANIMATION");
                if (axe.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) item.setData(DataComponentTypes.STORED_ENCHANTMENTS, axe.getData(DataComponentTypes.STORED_ENCHANTMENTS)); else System.out.println("Missing: STORED_ENCHANTMENTS");
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, @NotNull String @NotNull [] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }

}
