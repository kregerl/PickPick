package com.loucaskreger.pickpick.config;

import java.util.Arrays;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeFortuned;

	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blocksToBeSilkTouched;

	public ClientConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Pick tool will switch to the correct tool with the highest total enchantment level by default").push("Priorities");
		blocksToBeFortuned = builder.comment(
				"When the pick tool button is pressed while facing one of the blocks in this list, fortune is given first priority.")
				.translation("pickpick.config.blocksToBeFortuned").defineList("blocksToBeFortuned",
						Arrays.asList(/* Put any defaults here Ex.("","","") */), i -> i instanceof String);

		blocksToBeFortuned = builder.comment(
				"When the pick tool button is pressed while facing one of the blocks in this list, Silk Touch is given first priority.")
				.translation("pickpick.config.blocksToBeSilkTouched").defineList("blocksToBeSilkTouched",
						Arrays.asList(/* Put any defaults here Ex.("","","") */), j -> j instanceof String);
		builder.pop();

	}

}
