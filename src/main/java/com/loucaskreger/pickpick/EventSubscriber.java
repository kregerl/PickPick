package com.loucaskreger.pickpick;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.loucaskreger.pickpick.config.ClientConfig;
import com.loucaskreger.pickpick.config.Config;
import com.loucaskreger.pickpick.util.ItemComparator;
import com.loucaskreger.pickpick.util.ItemInfo;
import com.loucaskreger.pickpick.util.ScrollHandler;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TieredItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

//1.15.2
@Mod.EventBusSubscriber(modid = PickPick.MODID)
public class EventSubscriber {

	private static final HashMap<ToolType, ItemStack> toolBase = new HashMap<>();
	public static final ToolType SWORD = ToolType.get("sword");


	private static final KeyBinding pickTool = new KeyBinding(PickPick.MODID + ".key.pickTool", GLFW_KEY_P,
			PickPick.MODID + ".key.categories");
	private static final KeyBinding quickScroll = new KeyBinding(PickPick.MODID + ".key.quickScroll",
			GLFW_KEY_LEFT_CONTROL, PickPick.MODID + ".key.categories");

	static {
		ClientRegistry.registerKeyBinding(pickTool);
		ClientRegistry.registerKeyBinding(quickScroll);
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == Config.CLIENT_SPEC) {
			Config.bakeConfig();
		}
	}

	@SubscribeEvent
	public static void onMouseEvent(final InputEvent.MouseScrollEvent event) {
		// -1 -> down 1 -> Up 0 -> Keybind not pressed
		double scrollDelta = quickScroll.isPressed() ? event.getScrollDelta() : 0;
		Minecraft mc = Minecraft.getInstance();
		PlayerInventory inventory = mc.player.inventory;
		PlayerController pc = mc.playerController;
		ClientPlayerEntity player = mc.player;
		if (scrollDelta != 0) {
//			MiniBar b = new MiniBar(inventory);
//			b.getNextItem(player, pc, (int)scrollDelta);
			ScrollHandler.onMouseScrolled(scrollDelta);
			event.setCanceled(true);
			
//			if (scrollDelta == 1) {
//				event.setCanceled(true);
//			} else if (scrollDelta == -1) {
//				event.setCanceled(true);
//			}
		}

	}

	@SubscribeEvent
	public static void onClientTick(final ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (pickTool.isPressed()) {
			RayTraceResult result = ((RayTraceResult) mc.objectMouseOver);
			PlayerController pc = mc.playerController;
			PlayerInventory inventory = mc.player.inventory;
			if (result.getType() == RayTraceResult.Type.BLOCK) {
				if (getBlockInfo(mc, result) == null) {
					return;
				}
				Pair<BlockPos, BlockState> blockInfo = getBlockInfo(mc, result);
				ToolType effectiveToolType = getToolTypeOfBlock(mc.world, blockInfo);

				if (effectiveToolType == null) {
					List<Block> shearBlocks = Arrays.asList(Blocks.SEAGRASS, Blocks.TALL_GRASS, Blocks.TALL_SEAGRASS,
							Blocks.DEAD_BUSH, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN);

					if (ClientConfig.shearWool.get() && (blockInfo.getSecond().getBlock().isIn(BlockTags.WOOL)
							|| blockInfo.getSecond().getBlock().isIn(BlockTags.LEAVES)
							|| shearBlocks.contains(blockInfo.getSecond().getBlock()))) {
						pick(pc, inventory, Items.SHEARS);

					} else if (ClientConfig.fnsTNT.get() && blockInfo.getSecond().getBlock().equals(Blocks.TNT)) {
						pick(pc, inventory, Items.FLINT_AND_STEEL);

					} else if (ClientConfig.bonemealSaplings.get()
							&& (blockInfo.getSecond().getBlock().isIn(BlockTags.SAPLINGS)
									|| blockInfo.getSecond().getBlock().isIn(BlockTags.TALL_FLOWERS))) {
						pick(pc, inventory, Items.BONE_MEAL);

					} else if (blockInfo.getSecond().getMaterial().equals(Material.GLASS)
							&& !blockInfo.getSecond().getBlock().equals(Blocks.BEACON)
							&& !blockInfo.getSecond().getBlock().equals(Blocks.CONDUIT)) {
						pick(blockInfo, ClientConfig.stTool.get().toolType(), Enchantments.SILK_TOUCH, inventory, pc,
								mc);

					} else if (blockInfo.getSecond().getBlock() instanceof CoralFanBlock) {
						pick(blockInfo, ToolType.PICKAXE, Enchantments.SILK_TOUCH, inventory, pc, mc);
					}

				} else if (effectiveToolType != null) {
					pick(blockInfo, effectiveToolType, null, inventory, pc, mc);
				}
			} else if (result.getType() == RayTraceResult.Type.ENTITY) {
				Entity entity = ((EntityRayTraceResult) result).getEntity();
				if (entity.getClassification(true).equals(EntityClassification.CREATURE)
						&& entity instanceof SheepEntity) {
					pick(pc, inventory, Items.SHEARS);

				} else if (entity.getClassification(true).equals(EntityClassification.CREATURE)
						&& entity instanceof CowEntity) {
					pick(pc, inventory, Items.BUCKET);

				} else if (entity.getClassification(true).equals(EntityClassification.MONSTER)) {
					pick(null, SWORD, null, inventory, pc, mc);

				}

			} else if (result.getType() == RayTraceResult.Type.MISS) {
				if (getBlockInfo(mc, result) == null) {
					return;
				}
				Pair<BlockPos, BlockState> blockInfo = getBlockInfo(mc, result);
				if (ClientConfig.bucketFluids.get() && blockInfo.getSecond().getBlock() instanceof FlowingFluidBlock) {
					pick(pc, inventory, Items.BUCKET);
				}
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

	private static void pick(PlayerController playerController, PlayerInventory inventory, Item tool) {
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
	private static void pick(Pair<BlockPos, BlockState> blockInfo, ToolType toolType, Enchantment e,
			PlayerInventory inventory, PlayerController playerController, Minecraft mc) {
		NonNullList<ItemStack> mainInventory = inventory.mainInventory;
		List<ItemInfo> tools = new ArrayList<ItemInfo>();
		for (ItemStack item : inventory.mainInventory) {
			if (toolType.equals(SWORD)) {
				if (item.getItem() instanceof SwordItem && blockInfo == null) {
					Collection<AttributeModifier> test = ((SwordItem) item.getItem())
							.getAttributeModifiers(EquipmentSlotType.MAINHAND)
							.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
					AttributeModifier attributeModifier = new ArrayList<AttributeModifier>(test).get(0);
					tools.add(new ItemInfo(item, mainInventory.indexOf(item), (int) attributeModifier.getAmount()));
				}
			} else {
				tools.add(new ItemInfo(item, mainInventory.indexOf(item),
						item.getHarvestLevel(toolType, mc.player, blockInfo.getSecond())));
			}
		}
		Collections.sort(tools, new ItemComparator());
		int slotPos;
		if (e != null) {
			slotPos = checkForEnchantment(tools, e, inventory);
			moveItem(playerController, inventory, slotPos);
			return;
		}
		tools = tools.stream().filter(tool -> tool.hasToolType(toolType)).collect(Collectors.toList());
		if (tools.isEmpty())
			return;

		slotPos = tools.get(0).getItemPos();
		if (ClientConfig.enableSilkTouch.get() && ClientConfig.blocksToBeSilkTouched.get()
				.contains(blockInfo.getSecond().getBlock().getRegistryName().toString())) {
			slotPos = checkForEnchantment(tools, Enchantments.SILK_TOUCH, inventory);
		} else if (ClientConfig.enableFortune.get() && ClientConfig.blocksToBeFortuned.get()
				.contains(blockInfo.getSecond().getBlock().getRegistryName().toString())) {
			slotPos = checkForEnchantment(tools, Enchantments.FORTUNE, inventory);
		}

		moveItem(playerController, inventory, slotPos);
	}

	private static int checkForEnchantment(List<ItemInfo> tools, Enchantment e, PlayerInventory inventory) {
		int slotPos = inventory.currentItem;
		for (ItemInfo tool : tools) {
			if (tool.hasEnchantment(e)) {
				slotPos = tool.getItemPos();
				break;
			}
		}
		return slotPos;
	}

	private static void moveItem(PlayerController playerController, PlayerInventory inventory, int slotPos) {
		if (PlayerInventory.isHotbar(slotPos))
			inventory.currentItem = slotPos;
		else
			playerController.pickItem(slotPos);
	}

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