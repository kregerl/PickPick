package com.loucaskreger.pickpick.util;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;

public class MiniBar {
	private static final int MAX_SIZE = 4;
	private Deque<Pair<ItemStack, Integer>> inventory;
	private NavigableMap<Integer, ItemStack> collumn;
//	private Deque<ItemStack> items;
//	private ItemStack[] items;
//	private Integer[] indicies;

	public MiniBar(PlayerInventory inventory) {
		this.inventory = new LinkedList<>();
		this.collumn = new TreeMap<Integer, ItemStack>();
//		this.items = new ItemStack[MAX_SIZE];
//		this.indicies = new Integer[MAX_SIZE];
		this.fillArray(inventory);
	}

	private void fillArray(PlayerInventory inventory) {
		int index = 0;
		for (int i = inventory.currentItem; i < 36; i += 9) {
			this.inventory.add(new Pair<ItemStack, Integer>(inventory.getStackInSlot(i), i));
			this.collumn.put(i, inventory.getStackInSlot(i));
//			this.items.add(inventory.getStackInSlot(i));
//			this.indicies[index] = i;
			++index;
		}
	}

	/**
	 * Integer for direction and magnitude
	 * 
	 * @param int direction
	 * @return
	 */
	public void getNextItem(ClientPlayerEntity player, PlayerController pc, int scrollDelta) {
		System.out.println("Got here");
		if (scrollDelta < 0) {
//			this.shiftDown();
			updatePositions(player, pc, scrollDelta);
		} else if (scrollDelta > 0) {
//			this.shiftUp();
			updatePositions(player, pc, scrollDelta);
		}
	}

//	private void shiftUp() {
//		//Do shifts with map now
//		Pair<ItemStack, Integer> temp = this.inventory.pollLast();
//		this.inventory.addFirst(temp);
//	}
//
//	private void shiftDown() {
//		Pair<ItemStack, Integer> temp = this.inventory.pollFirst();
//		this.inventory.addLast(temp);
//	}

	private void updatePositions(ClientPlayerEntity player, PlayerController pc, int scrollDelta) {
		for (Map.Entry<Integer, ItemStack> entry : this.collumn.entrySet()) {
			System.out.println( entry.getKey());
			System.out.println( entry.getKey() + 8);
//			if (Math.signum(scrollDelta) < 0) {
			int first = entry.getKey();
			int second = entry.getKey() + 3;
			pc.windowClick(player.openContainer.windowId, first, second, ClickType.SWAP, player);
//			}
		}

//		for (int i = 0; i < this.indicies.length; ++i) {
//			System.out.println("UpdatePos Index: " + indicies[i]);
//			player.inventory.removeStackFromSlot(index) returns the itemstack... maybe use this?
//			pc.windowClick(player.openContainer.windowId, indicies[i], 0, ClickType.SWAP ,player);
//		}
//		items.pop();
	}
}
