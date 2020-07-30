package com.loucaskreger.pickpick.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;

public class ItemInfo {
	private Map<Enchantment, Integer> enchantments;
	private int harvestLevel;
	private ItemStack item;
	private int itemPos;
	private Set<ToolType> toolTypes;
	private int totalEnchantmentLevel;

	/**
	 * Sets the item and position of the item in the players main inventory.
	 * 
	 * @param item
	 * @param itemPos
	 */
	public ItemInfo(ItemStack item, int itemPos, int harvestLevel) {
		this.item = item;
		this.itemPos = itemPos;
		this.toolTypes = item.getToolTypes();
		this.harvestLevel = harvestLevel;
		handleEnchants(item);
	}

	/**
	 * Returns true if the passed tool type is one of the items tool types.
	 * 
	 * @param toolType
	 * @return
	 */
	public Boolean hasToolType(ToolType toolType) {
		return toolTypes.contains(toolType) ? true : false;
	}

	/**
	 * Returns true if the passed item is equal to this item.
	 * 
	 * @param item
	 * @return
	 */
	public Boolean isSameAs(ItemStack item) {
		return this.item.equals(item);
	}

	public ItemStack getItem() {
		return item;
	}

	public int getItemPos() {
		return itemPos;
	}

	public int getTotalEnchantmentLevel() {
		return totalEnchantmentLevel;
	}

	public int getHarvestLevel() {
		return harvestLevel;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	private void handleEnchants(ItemStack item) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
		this.enchantments = enchantments;
		for (Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
			this.totalEnchantmentLevel += enchant.getValue();
		}
	}

}

