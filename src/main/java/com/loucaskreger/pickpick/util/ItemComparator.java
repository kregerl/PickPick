package com.loucaskreger.pickpick.util;

import java.util.Comparator;
/**
 * Compares two items by harvest level, then total enchantment level.
 * @author Loucas
 *
 */
public class ItemComparator implements Comparator<ItemInfo> {

	@Override
	public int compare(ItemInfo o1, ItemInfo o2) {
		int compareHarvestLevel = o2.getHarvestLevel() == o1.getHarvestLevel() ? 0 : o2.getHarvestLevel() > o1.getHarvestLevel() ? 1 : -1;
		int compareEnchantmentLevel = o2.getTotalEnchantmentLevel() == o1.getTotalEnchantmentLevel() ? 0 : o2.getTotalEnchantmentLevel() > o1.getTotalEnchantmentLevel() ? 1 : -1;
		if (compareHarvestLevel == 0)
			return ((compareEnchantmentLevel == 0) ? compareHarvestLevel : compareEnchantmentLevel);
		else
			return compareHarvestLevel;
	}

}
