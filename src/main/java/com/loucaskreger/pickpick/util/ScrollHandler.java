package com.loucaskreger.pickpick.util;

import java.util.Deque;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;

public class ScrollHandler {
	private static double accumulatedScrollDelta = 0;
	private static Integer[] collumnIndicies;
	private static Deque<ItemStack> items = new LinkedList<>();
	private Integer[] indicies;

	public static boolean onMouseScrolled(double scrollDelta) {
		Minecraft mc = Minecraft.getInstance();
		PlayerController pc = mc.playerController;
		int hotbarPos = mc.player.inventory.currentItem;
		collumnIndicies =  new Integer[] {hotbarPos, hotbarPos + 9, hotbarPos + 18, hotbarPos + 27};

		accumulatedScrollDelta += scrollDelta;
		System.out.println("AccDelta: " + accumulatedScrollDelta);
		int delta = (int) accumulatedScrollDelta; // delta is the number of items to move, sign controls the direction.
		System.out.println("Signum: " + Math.signum(delta));
		System.out.println("DeltaMod3: " + delta % 3);

//		System.out.println("3: " + Math.signum(accumulatedScrollDelta));
		
		return true;
	}

}
