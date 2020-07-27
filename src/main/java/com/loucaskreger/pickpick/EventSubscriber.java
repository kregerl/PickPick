package com.loucaskreger.pickpick;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.loucaskreger.pickpick.config.ClientConfig;
import com.loucaskreger.pickpick.config.Config;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.TieredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = PickPick.MODID)
public class EventSubscriber {

	private static final KeyBinding pickTool = new KeyBinding(PickPick.MODID + ".key.pickTool", GLFW_KEY_P,
			PickPick.MODID + ".key.categories");

	static {
		ClientRegistry.registerKeyBinding(pickTool);
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == Config.CLIENT_SPEC) {
			Config.bakeConfig();
		}
	}

	@SubscribeEvent
	public static void onClientTick(final ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (pickTool.isPressed()) {
			RayTraceResult result = ((RayTraceResult) mc.objectMouseOver);
			if (result.getType() == RayTraceResult.Type.BLOCK) {
				PlayerController pc = mc.playerController;
				PlayerInventory inventory = mc.player.inventory;
				if (getBlockInfo(mc, result) == null) {
					return;
				}
				Pair<BlockPos, BlockState> blockInfo = getBlockInfo(mc, result);
				ToolType effectiveToolType = getToolTypeOfBlock(mc.world, blockInfo);
				if (effectiveToolType == null) {

					if (ClientConfig.shearWool.get() && (blockInfo.getSecond().getBlock().isIn(BlockTags.WOOL)
							|| blockInfo.getSecond().getBlock().isIn(BlockTags.LEAVES))) {
						pick(inventory, pc, Items.SHEARS);
					} else if (ClientConfig.bucketFluids.get()
							&& (blockInfo.getSecond().getBlock() instanceof FlowingFluidBlock)) {
						pick(inventory, pc, Items.BUCKET);
					}

				} else if (effectiveToolType != null) {
					pick(inventory, pc, mc, blockInfo, effectiveToolType);

				}
			} else if (result.getType() == RayTraceResult.Type.ENTITY) {
				System.out.println("Entity");
			}
		}
	}

	private static Pair<BlockPos, BlockState> getBlockInfo(Minecraft mc, RayTraceResult result) {
		BlockPos pos = ((BlockRayTraceResult) result).getPos();
		BlockState state = mc.world.getBlockState(pos);
		if (state.isAir(mc.world, pos))
			return null;

		return new Pair<BlockPos, BlockState>(pos, state);
	}

	private static void pick(PlayerInventory inventory, PlayerController playerController, Item tool) {
		NonNullList<ItemStack> mainInventory = inventory.mainInventory;
		for (ItemStack slot : mainInventory) {
			if (slot.getItem().equals(tool)) {
				int slotPos = mainInventory.indexOf(slot);
				if (PlayerInventory.isHotbar(slotPos))
					inventory.currentItem = slotPos;
				else
					playerController.pickItem(slotPos);
				break;
			}
		}
	}

	/**
	 * Gets all tools in players inventory sorted by highest harvest level and
	 * switches to highest item.
	 * 
	 * @param inventory
	 * @param playerController
	 * @param mc
	 * @param blockInfo
	 * @param toolType
	 */
	private static void pick(PlayerInventory inventory, PlayerController playerController, Minecraft mc,
			Pair<BlockPos, BlockState> blockInfo, ToolType toolType) {
		NonNullList<ItemStack> mainInventory = inventory.mainInventory;
		TreeMap<Integer, ItemStack> possibleTools = new TreeMap<Integer, ItemStack>(new Comparator<Integer>() {

			@Override
			public int compare(Integer i, Integer j) {
				return j.compareTo(i);
			}
		});

		for (ItemStack slot : mainInventory) {
			Set<ToolType> itemToolTypes = slot.getToolTypes();
			if (itemToolTypes.contains(toolType)) {
				possibleTools.put(slot.getHarvestLevel(toolType, mc.player, blockInfo.getSecond()), slot);
			}
		}
		if (possibleTools.isEmpty())
			return;
		int slotPos = mainInventory.indexOf(possibleTools.get(possibleTools.firstKey()));
		if (PlayerInventory.isHotbar(slotPos))
			inventory.currentItem = slotPos;
		else
			playerController.pickItem(slotPos);
	}

	private static final HashMap<ToolType, ItemStack> toolBase = new HashMap<>();
	public static final ToolType SWORD = ToolType.get("sword");
	static {
		toolBase.put(ToolType.PICKAXE, new ItemStack(Items.WOODEN_PICKAXE));
		toolBase.put(ToolType.SHOVEL, new ItemStack(Items.WOODEN_SHOVEL));
		toolBase.put(ToolType.AXE, new ItemStack(Items.WOODEN_AXE));
		toolBase.put(SWORD, new ItemStack(Items.WOODEN_SWORD));
	}

	public static ToolType getToolTypeOfBlock(World world, Pair<BlockPos, BlockState> blockInfo) {
		BlockPos pos = blockInfo.getFirst();
		BlockState state = blockInfo.getSecond();

		ToolType effectiveTool = state.getHarvestTool();
		if (effectiveTool == null) {
			float hardness = state.getBlockHardness(world, pos);
			if (hardness > 0.0F) {
				for (Map.Entry<ToolType, ItemStack> testToolEntry : toolBase.entrySet()) {
					ItemStack testTool = testToolEntry.getValue();
					if (testTool != null && !testTool.isEmpty() && testTool.getItem() instanceof TieredItem
							&& testTool.getDestroySpeed(state) >= ItemTier.WOOD.getEfficiency()) {
						effectiveTool = testToolEntry.getKey();
						break;
					}
				}
			}
		}
		return effectiveTool;
	}

}