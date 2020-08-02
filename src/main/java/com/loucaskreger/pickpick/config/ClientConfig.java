package com.loucaskreger.pickpick.config;

import java.util.Arrays;


import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public class ClientConfig {
	public static BooleanValue shearWool;
	public static BooleanValue bucketFluids;
	public static BooleanValue enableSilkTouch;
	public static BooleanValue enableFortune;
	
	public static EnumValue<ToolPreferenceEnum> stTool;
	
	
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeFortuned;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeSilkTouched;

	public ClientConfig(ForgeConfigSpec.Builder builder) {
		shearWool = builder.comment("If false, the pick tool button will not work on wool.").translation("pickpick.config.shearWool").define("shearWool", true);
		bucketFluids = builder.comment("If false, the pick tool button will not work on fluids.").translation("pickpick.config.bucketFluids").define("bucketFluids", true);
		builder.push("Preferences");
		stTool = builder.comment("Pick tool will choose the following tool type, with silk touch, when pressed facing glass, glass panes, glowstone and sea lanterns. If ANY is chosen, the first tool found in your inventory with silk touch will be used. Coral fans default to any pickaxe with silk touch").translation("pickpick.config.stTool").defineEnum("stTool", ToolPreferenceEnum.ANY);
		
		builder.push("Silk Touch");
		enableSilkTouch	= builder.comment("If true, the list below will be used to determine if a block should be silk touched or not.").translation("pickpicik.config.enableSilkTouch").define("enableSilkTouch", false);
		blocksToBeSilkTouched = builder.comment(
				"This is a list of blocks that pick tool will use to determine if a block should be silk touched. Ex. [\"minecraft:grass_block\", \"minecraft:ender_chest\"")
				.translation("pickpick.config.blocksToBeSilkTouched").defineList("blocksToBeSilkTouched",
						Arrays.asList(""), j -> j instanceof String);
		builder.pop();
		builder.push("Fortune");
		enableFortune = builder.comment("If true, the list below will be used to determine if a block should be fortuned or not.").translation("pickpicik.config.enableFortune").define("enableFortune", false);
		blocksToBeFortuned = builder.comment(
				"This is a list of blocks that pick tool will use to determine if a block should be fortuned. Ex. [\"minecraft:diamond_ore\", \"minecraft:redstone_ore\"")
				.translation("pickpick.config.blocksToBeFortuned").defineList("blocksToBeFortuned",
						Arrays.asList(""), i -> i instanceof String);
		builder.pop();
		builder.pop();
		

	}

}
