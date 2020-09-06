package com.loucaskreger.pickpick;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.loucaskreger.pickpick.util.ToolComparator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tag.Tag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class Events {
	private static boolean cancelNext = false;

	private static final HashMap<Tag<Item>, Float> toolBase = new HashMap<>();

	static {
		toolBase.put(FabricToolTags.PICKAXES, 2.0f);
		toolBase.put(FabricToolTags.AXES, 2.0f);
		toolBase.put(FabricToolTags.SHOVELS, 2.0f);
		toolBase.put(FabricToolTags.HOES, 2.0f);
		toolBase.put(FabricToolTags.SWORDS, 2.0f);
	}

	public static void initKeyBinds(KeyBinding key) {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (key.isPressed() && !cancelNext) {
				MinecraftClient mc = MinecraftClient.getInstance();
				HitResult result = mc.crosshairTarget;

				switch (result.getType()) {
				case MISS:
					break;
				case BLOCK:
					BlockHitResult blockHit = (BlockHitResult) result;
					BlockPos pos = blockHit.getBlockPos();
					BlockState state = client.world.getBlockState(pos);
					DefaultedList<ItemStack> inventory = mc.player.inventory.main;
					ClientPlayerInteractionManager interactionManager = mc.interactionManager;

					cancelNext = !cancelNext;
					if (!state.isAir()) {
						TreeMap<ItemStack, Integer> tools = findCorrectTools(mc, pos, state, inventory);
						if (!tools.isEmpty()) {
							boolean isInHotbar = PlayerInventory.isValidHotbarIndex(tools.firstEntry().getValue());
							int slotPos = tools.firstEntry().getValue();
							if (mc.player.inventory.selectedSlot == slotPos) {
								return;
							} else if (isInHotbar) {
								mc.player.inventory.selectedSlot = slotPos;
							} else {
								interactionManager.pickFromInventory(slotPos);
							}
						}
					}
					break;
				case ENTITY:
					break;

				}
			} else if (cancelNext) {
				cancelNext = !cancelNext;
			}
		});
	}

	private static TreeMap<ItemStack, Integer> findCorrectTools(MinecraftClient mc, BlockPos pos, BlockState state,
			DefaultedList<ItemStack> inventory) {
		TreeMap<ItemStack, Integer> tools = new TreeMap<ItemStack, Integer>(new ToolComparator());
		for (ItemStack i : inventory) {
			if (i.getItem() instanceof ToolItem) {
				ToolItem item = ((ToolItem) i.getItem());
				float hardness = state.getHardness(mc.world, pos);
				if (hardness > 0.0F) {
					for (Map.Entry<Tag<Item>, Float> t : toolBase.entrySet()) {
						if (i != null && !i.isEmpty() && t.getKey().contains(item)
								&& i.getMiningSpeedMultiplier(state) >= t.getValue()) {
							tools.put(i, inventory.indexOf(i));
							break;
						}
					}
				}
			}
		}
		return tools;

	}

}
