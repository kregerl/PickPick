package com.loucaskreger.pickpick;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.loucaskreger.pickpick.config.Config;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.TieredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
			"key.categories." + PickPick.MODID);
	
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
			PlayerInventory inventory = mc.player.inventory;
			NonNullList<ItemStack> mainInventory = inventory.mainInventory;
			Pair<BlockPos, BlockState> blockInfo = getBlockInfo(mc);
			ToolType effectiveTool = getToolTypeOfBlock(mc.world, blockInfo);
			//Make this prettier
			if (effectiveTool == null) {

				if (blockInfo.getSecond().getBlock().isIn(BlockTags.WOOL))
					mainInventory.forEach(i -> {
						if (i.getItem().equals(Items.SHEARS)) {
							int slotPos = mainInventory.indexOf(i);
							if (PlayerInventory.isHotbar(slotPos))
								inventory.currentItem = slotPos;
							else
								mc.playerController.pickItem(slotPos);

						}
					});
			} else if (effectiveTool != null) {
				mainInventory.forEach((slot) -> {
					 Set<ToolType> itemToolTypes = slot.getToolTypes();
					 if (itemToolTypes.contains(effectiveTool)) {
						 int slotPos = mainInventory.indexOf(slot);
							if (PlayerInventory.isHotbar(slotPos))
								inventory.currentItem = slotPos;
							else
								mc.playerController.pickItem(slotPos);
					 }
				});
			}
		}
	}

	private static Pair<BlockPos, BlockState> getBlockInfo(Minecraft mc) {
		BlockPos pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
		BlockState state = mc.world.getBlockState(pos);
		// Block cant be air
		if (state.isAir(mc.world, pos))
			return null;
		return new Pair<BlockPos, BlockState>(pos, state);
	}
	
	
//	private static Boolean hasCorrectEnchantment(ItemStack item) {
//		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
//		for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
//		}
//		return false;
//
//	}

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