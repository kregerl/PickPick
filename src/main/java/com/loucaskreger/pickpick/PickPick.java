package com.loucaskreger.pickpick;

import com.loucaskreger.pickpick.config.Config;


import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("pickpick")
public class PickPick {
	public static final String MODID = "pickpick";

	public PickPick() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setupClient);
		modEventBus.addListener(this::setupCommon);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
	}

	private void setupCommon(final FMLCommonSetupEvent event) {

	}

	private void setupClient(final FMLClientSetupEvent event) {

	}
}