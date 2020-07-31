package com.loucaskreger.pickpick.config;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ClientConfig {
	public static BooleanValue shearWool;
	public static BooleanValue bucketFluids;
	public static BooleanValue customEnchantmentPriorities;

	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeFortuned;

	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeSilkTouched;

	public ClientConfig(ForgeConfigSpec.Builder builder) {
		shearWool = builder.comment("If false, the pick tool button will not work on wool.").translation("pickpick.config.shearWool").define("shearWool", true);
		bucketFluids = builder.comment("If false, the pick tool button will not work on fluids.").translation("pickpick.config.bucketFluids").define("bucketFluids", true);
		customEnchantmentPriorities = builder.comment("Set to true to use the following config options.").translation("pickpick.config.customEnchantmentPriotities").define("customEnchantmentPriorities", true);
		builder.push("Priorities");
		
		blocksToBeFortuned = builder.comment(
				"When the pick tool button is pressed while facing one of the blocks in this list, fortune is given first priority. Ex. [\"minecraft:diamond_ore\", \"minecraft:redstone_ore\"")
				.translation("pickpick.config.blocksToBeFortuned").defineList("blocksToBeFortuned",
						Arrays.asList(""), i -> i instanceof String);

		blocksToBeSilkTouched = builder.comment(
				"When the pick tool button is pressed while facing one of the blocks in this list, Silk Touch is given first priority. Ex. [\"minecraft:grass\", \"minecraft:ender_chest\"")
				.translation("pickpick.config.blocksToBeSilkTouched").defineList("blocksToBeSilkTouched",
						Arrays.asList(""), j -> j instanceof String);
		builder.pop();

	}

}
