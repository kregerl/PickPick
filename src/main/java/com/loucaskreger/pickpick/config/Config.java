package com.loucaskreger.pickpick.config;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static List<? extends String> blocksToBeFortuned;
	public static List<? extends String> blocksToBeSilkTouched;
	static {
		final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static void bakeConfig() {
		blocksToBeFortuned = ClientConfig.blocksToBeFortuned.get();
		blocksToBeSilkTouched = ClientConfig.blocksToBeSilkTouched.get();
	}

}
