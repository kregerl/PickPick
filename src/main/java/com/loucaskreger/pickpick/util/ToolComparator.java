package com.loucaskreger.pickpick.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

public class ToolComparator implements Comparator<ItemStack> {

	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		ToolItem i1 = ((ToolItem)o1.getItem());
		ToolItem i2 = ((ToolItem)o2.getItem());
		int compareHarvestLevel = getHarvestLevel(i2) == getHarvestLevel(i1) ? 0 : getHarvestLevel(i2) > getHarvestLevel(i1) ? 1 : -1;
		int compareEnchantmentLevel = getTotalEnchantmentLevel(o2) == getTotalEnchantmentLevel(o1) ? 0 : getTotalEnchantmentLevel(o2) > getTotalEnchantmentLevel(o1) ? 1 : -1;
		if (compareHarvestLevel == 0)
			return ((compareEnchantmentLevel == 0) ? compareHarvestLevel : compareEnchantmentLevel);
		else
			return compareHarvestLevel;
	}
	
	private int getHarvestLevel(ToolItem tool) {
		return tool.getMaterial().getMiningLevel();
	}
	
	private int getTotalEnchantmentLevel(ItemStack tool) {
		int sum = 0;
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromTag(tool.getEnchantments());
		for (Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
			sum += enchant.getValue();
		}
		return sum;
	}

}
