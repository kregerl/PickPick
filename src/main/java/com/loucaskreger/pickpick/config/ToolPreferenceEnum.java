package com.loucaskreger.pickpick.config;

import net.minecraftforge.common.ToolType;

public enum ToolPreferenceEnum {
	PICKAXE (ToolType.get("pickaxe")),
	AXE (ToolType.get("axe")),
	HOE (ToolType.get("hoe")),
	SHOVEL (ToolType.get("shovel"));
	
	private ToolType toolType;
	
	ToolPreferenceEnum(ToolType toolType) {
		this.toolType = toolType;
	}
	/**
	 * Returns the tool type
	 * @return
	 */
	public ToolType toolType() {
		return this.toolType;
	}
	
	/**
	 * Returns true if the preference is ANY
	 * @return
	 */
	public Boolean isAny() {
		return toolType == null;
	}

}
