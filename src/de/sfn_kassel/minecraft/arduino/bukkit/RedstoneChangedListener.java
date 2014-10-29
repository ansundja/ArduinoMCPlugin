package de.sfn_kassel.minecraft.arduino.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import de.sfn_kassel.minecraft.arduino.arduino.ArduController.ArduinoCommand;

public class RedstoneChangedListener implements Listener {
	final ArduinoMCPlugin plugin;

	public RedstoneChangedListener(ArduinoMCPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getLogger().info("new RedstoneChangedListener(..)");
	}

	@EventHandler
	public void redstoneChanged(BlockRedstoneEvent event) {
		if(plugin.isInDebugMode())
			plugin.getLogger().info("BlockRedstoneEvent; new current: "+event.getNewCurrent());
		
		Integer pin = plugin.getController().getOutWires().get(event.getBlock());
		if (pin == null)
			return;
		
		plugin.getController().sendToArduino(ArduinoCommand.ANALOG_OUT, pin, event.getNewCurrent());
	}

}
