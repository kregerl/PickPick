package com.loucaskreger.pickpick.util;

import java.util.HashSet;
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
	private ItemStack stack;
	private int itemPos;
	private Set<ToolType> toolTypes;
	private int totalEnchantmentLevel;

	/**
	 * Sets the item, harvest level and position of the item in the players main
	 * inventory.
	 * 
	 * @param ItemStack
	 * @param itemPos
	 * @param harvestLevel
	 */
	public ItemInfo(ItemStack stack, int itemPos, int harvestLevel) {
		this.stack = stack;
		this.itemPos = itemPos;
		this.toolTypes = stack.getToolTypes();
		this.harvestLevel = harvestLevel;
		handleEnchants(stack);
	}

	/**
	 * Returns true if the passed tool type is the items tool type.
	 * 
	 * @param ToolType
	 * @return
	 */
	public Boolean hasToolType(ToolType toolType) {
		return this.toolTypes.contains(toolType);
	}
	/**
	 * Returns true if the item is enchanted with the passed enchantment.
	 * 
	 * @param Enchantment
	 * @return
	 */
	public Boolean hasEnchantment(Enchantment e) {
		return enchantments.containsKey(e);
	}

	/**
	 * Returns true if the passed item is the same as this item.
	 * 
	 * @param ItemStack
	 * @return
	 */
	public Boolean isSameAs(ItemStack item) {
		return this.stack.equals(item);
	}

	/**
	 * Returns the Item Stack
	 * 
	 * @return
	 */
	public ItemStack getItemStack() {
		return stack;
	}

	/**
	 * Returns the index of the item in the players main inventory
	 * 
	 * @return
	 */
	public int getItemPos() {
		return itemPos;
	}

	/**
	 * Returns the sum of all levels of enchantments on the item.
	 * @return
	 */
	public int getTotalEnchantmentLevel() {
		return totalEnchantmentLevel;
	}
	/**
	 * Returns the tools harvest level
	 * @return
	 */
	public int getHarvestLevel() {
		return harvestLevel;
	}
	public void setToolType(ToolType toolType) {
		Set<ToolType> s =  new HashSet<ToolType>();
		s.add(toolType);
		this.toolTypes = s;
	}
	
	/**
	 * Returns a map of the items enchantments
	 * @return
	 */
	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}
	
	public Set<ToolType> getItemToolTypes() {
		return toolTypes;
	}

	private void handleEnchants(ItemStack item) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
		this.enchantments = enchantments;
		for (Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
			this.totalEnchantmentLevel += enchant.getValue();
		}
	}

}
