package com.loucaskreger.pickpick.keybinds;

import org.lwjgl.glfw.GLFW;

import com.loucaskreger.pickpick.PickPick;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBinds {
	public static KeyBinding key;

	static {
		key = KeyBindingHelper.registerKeyBinding(new KeyBinding(String.format("key.%s.pickpick", PickPick.MODID),
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, String.format("category.%s.pickpick", PickPick.MODID)));
	}

}
