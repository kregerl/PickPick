package com.loucaskreger.pickpick;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.pickpick.keybinds.KeyBinds;

import net.fabricmc.api.ModInitializer;

public class PickPick implements ModInitializer {
	public static final String MODID = "pickpick";
	public static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void onInitialize() {
		Events.initKeyBinds(KeyBinds.key);
		
		System.out.println("Hello Fabric world!");
	}
}
