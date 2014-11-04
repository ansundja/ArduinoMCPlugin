package de.sfn_kassel.minecraft.arduino.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

import de.sfn_kassel.minecraft.arduino.arduino.ArduController;
import de.sfn_kassel.minecraft.arduino.arduino.ArduController.ArduinoCommand;
import de.sfn_kassel.minecraft.arduino.bukkit.util.Condition;
import de.sfn_kassel.minecraft.arduino.bukkit.util.Conditions;

public class SignChangedListener implements Listener {
	final private ArduinoMCPlugin plugin;
	final private ArduController controller;

	public SignChangedListener(ArduinoMCPlugin plugin, ArduController controller) {
		this.plugin = plugin;
		this.controller = controller;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void signChanged(SignChangeEvent event) {
		if(plugin.isInDebugMode()) {
			info("SignChangeEvent: "+event);
			info(event.getBlock());
			info(event.getBlock().getType());
			info(event.getBlock().getLocation());
			info(((Sign) event.getBlock().getState().getData()).getAttachedFace());
			info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()));
			info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()).getBlock());
			info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()).getBlock().getType());
		}
		
		addSign(event.getBlock(), event.getLines());
		
//		Block block = event.getBlock().getRelative(((Sign) event.getBlock().getState().getData()).getAttachedFace()).getLocation().add(new Vector(0, 1, 0)).getBlock();
////		Block io = block;//getRedstoneOnWool(block);
//
////		if (io.getType() == Material.REDSTONE_WIRE) {
////			MaterialData data = io.getState().getData();
////			info(data);
////		} else if (io.getType() == Material.LEVER) {
////			BlockState leverState = io.getState();
////			((Lever) leverState.getData()).setPowered(true);
////			leverState.update();
////			info(io.getState().getData());
////		} else {
////			info("BAUM");
////		}
//
//		//		if(block.getType() == Material.WOOL){
//		//			try{
//		//				Wool wool = (Wool) block.getState().getData();
//		//				info(wool.getColor());
//		//				if(wool.getColor() == DyeColor.BLACK){
//		//					info("input");
//		//					info(redstone.getState().getData());
//		//					
//		//				} else if(wool.getColor() == DyeColor.PINK){
//		//					info("output");
//		//					Block redstone = getRedstoneOnWool(block);
//		//					RedstoneWire wire = (RedstoneWire) redstone.getState().getData();
//		//					
//		////					redstone.setData((byte) 4);//getState().setData(((RedstoneWire) new MaterialData(Material.REDSTONE_WIRE)).);
//		//					info(redstone.getState().getData());
//		//					
//		//					new MaterialData(Material.REDSTONE_BLOCK).setData((byte) 5);
//		//					
//		//					
//		//				}
//		//			} catch(Exception e){
//		//				e.printStackTrace();
//		//			}
//		//		}
//
//		if (event.getLine(0).trim().equalsIgnoreCase("arduino")) {
//			SignChangeEvent sign = event;
//			MaterialData data = sign.getBlock().getState().getData();
//			info("!(data instanceof Sign): "+!(data instanceof Sign));
//			if (!(data instanceof Sign))
//				return;
//			info("!equalsIgnoreCase: "+!sign.getLine(0).trim().equalsIgnoreCase("arduino"));
//			if (!sign.getLine(0).trim().equalsIgnoreCase("arduino"))
//				return;
//			int signPin = Byte.parseByte(sign.getLine(1));
//			info("!= Material.LEVER: "+(block.getType() != Material.LEVER));
//			controller.addSign(sign.getBlock().getLocation(), block.getLocation());
//			if (block.getType() == Material.LEVER) {
//				String statement = sign.getLine(2);
//				if(!statement.isEmpty())// @jaro: ' statement != "" ' never ever do that! 
//					info("jay you typed a statement :D");
//				Condition condition = null;
//				if (!statement.isEmpty())
//					condition = Conditions.parseCondition(statement);
//				
//				controller.addLever(block.getState(), signPin, condition);
//				controller.sendToArduino((condition == null) ? ArduinoCommand.DIGITAL_IN : ArduinoCommand.ANALOG_IN, signPin);
//				
//			} else if (block.getType() == Material.REDSTONE_WIRE
//					|| block.getType() == Material.REDSTONE_BLOCK
//					|| block.getType() == Material.REDSTONE
//					|| block.getType() == Material.REDSTONE_ORE) {
//				controller.getOutWires().put(block, signPin);
//			}
//		} else {
//			//TODO remove sign
////			controller.getKnownSigns().remove(event.getBlock().getLocation());
//		}
	}
	
	private void addSign(Block signBl, String[] lines) {
		Block block = signBl.getRelative(((Sign) signBl.getState().getData()).getAttachedFace()).getLocation().add(new Vector(0, 1, 0)).getBlock();

		if (lines != null && lines.length >= 3 && (lines[0].trim().equalsIgnoreCase("arduino") || lines[0].trim().equalsIgnoreCase("ardu"))) {
			
			controller.addSign(signBl.getLocation(), block.getLocation());
			int signPin = Byte.parseByte(lines[1].trim());
			if (block.getType() == Material.LEVER) {
				String statement = lines[2].trim();
				Condition condition = null;
				if (!statement.isEmpty())
					condition = Conditions.parseCondition(statement);
				
				controller.addLever(block.getState(), signPin, condition);
				controller.sendToArduinoLater((condition == null) ? ArduinoCommand.DIGITAL_IN : ArduinoCommand.ANALOG_IN, signPin, 12);
				
			} else {
				controller.getOutWires().put(block, signPin);
			}
		} else {
			//TODO remove sign
//			controller.getKnownSigns().remove(event.getBlock().getLocation());
		}
	}
	
	public void addSign(Location loc) {//TODO
//		if (plugin.isInDebugMode())
//			plugin.getLogger().warning("[NOT IMPLEMENTED] should create read the Sign at "+loc);
		try {
			if (plugin.isInDebugMode())
				plugin.getLogger().info("Initializing sign at "+loc);
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) loc.getBlock().getState();
			addSign(loc.getBlock(), sign.getLines());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
//		event.setCancelled(true);
	}

	private void info(Object o) {
		plugin.getLogger().info(o.toString());
	}
}
