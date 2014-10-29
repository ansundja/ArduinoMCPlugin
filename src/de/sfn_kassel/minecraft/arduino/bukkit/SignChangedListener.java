package de.sfn_kassel.minecraft.arduino.bukkit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;

import de.sfn_kassel.minecraft.arduino.arduino.ArduController.ArduinoCommand;

public class SignChangedListener implements Listener {
	final ArduinoMCPlugin plugin;

	public SignChangedListener(ArduinoMCPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void signChanged(SignChangeEvent event) {
		if(plugin.isInDebugMode()) {
			plugin.getLogger().info("SignChangeEvent: "+event);
			if(plugin.isInDebugMode()) {
				info(event.getBlock());
				//		info(event.getBlock().getClass());
				info(event.getBlock().getType());
				info(event.getBlock().getLocation());
				info(((Sign) event.getBlock().getState().getData()).getAttachedFace());
				info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()));
				info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()).getBlock());
				info(event.getBlock().getLocation().add(event.getBlock().getLocation().getDirection()).getBlock().getType());
			}
		}
		Block block = event.getBlock().getRelative(((Sign) event.getBlock().getState().getData()).getAttachedFace()).getLocation().add(new Vector(0, 1, 0)).getBlock();
//		Block io = block;//getRedstoneOnWool(block);

//		if (io.getType() == Material.REDSTONE_WIRE) {
//			MaterialData data = io.getState().getData();
//			info(data);
//		} else if (io.getType() == Material.LEVER) {
//			BlockState leverState = io.getState();
//			((Lever) leverState.getData()).setPowered(true);
//			leverState.update();
//			info(io.getState().getData());
//		} else {
//			info("BAUM");
//		}

		//		if(block.getType() == Material.WOOL){
		//			try{
		//				Wool wool = (Wool) block.getState().getData();
		//				info(wool.getColor());
		//				if(wool.getColor() == DyeColor.BLACK){
		//					info("input");
		//					info(redstone.getState().getData());
		//					
		//				} else if(wool.getColor() == DyeColor.PINK){
		//					info("output");
		//					Block redstone = getRedstoneOnWool(block);
		//					RedstoneWire wire = (RedstoneWire) redstone.getState().getData();
		//					
		////					redstone.setData((byte) 4);//getState().setData(((RedstoneWire) new MaterialData(Material.REDSTONE_WIRE)).);
		//					info(redstone.getState().getData());
		//					
		//					new MaterialData(Material.REDSTONE_BLOCK).setData((byte) 5);
		//					
		//					
		//				}
		//			} catch(Exception e){
		//				e.printStackTrace();
		//			}
		//		}

		if (event.getLine(0).trim().equalsIgnoreCase("arduino")) {
//			plugin.getController().addSign(event.getBlock());
			//			new RedstoneChangedListener(plugin);
			//			event.getBlock().isBlockPowered()
			SignChangeEvent sign = event;
			MaterialData data = sign.getBlock().getState().getData();
			info("!(data instanceof Sign): "+!(data instanceof Sign));
			if (!(data instanceof Sign))
				return;
			info("!equalsIgnoreCase: "+!sign.getLine(0).trim().equalsIgnoreCase("arduino"));
			if (!sign.getLine(0).trim().equalsIgnoreCase("arduino"))
				return;
			int signPin = Byte.parseByte(sign.getLine(1));
			info("!= Material.LEVER: "+(block.getType() != Material.LEVER));
			if (block.getType() == Material.LEVER) {
				plugin.getController().addLever(block.getState(), signPin);
				plugin.getController().sendToArduino(ArduinoCommand.DIGITAL_IN, signPin);
//				plugin.getController().sendToArduino("d"+((char) ('@'+signPin))+"X\n");
			} else if (block.getType() == Material.REDSTONE_WIRE) {
				plugin.getController().getOutWires().put(block, signPin);
			} else if (block.getType() == Material.REDSTONE_BLOCK) {
				plugin.getController().getOutWires().put(block, signPin);
			} else if (block.getType() == Material.REDSTONE) {
				plugin.getController().getOutWires().put(block, signPin);
			} else if (block.getType() == Material.REDSTONE_ORE) {
				plugin.getController().getOutWires().put(block, signPin);
			} 
			
			
		} else {
			//TODO remove sign
//			plugin.getController().getKnownSigns().remove(event.getBlock().getLocation());
		}
	}

//	@SuppressWarnings("unused")// TODO remove or use it
//	private Block getRedstoneOnWool(Block wool) {
//		return wool.getLocation().add(0, 1, 0).getBlock();
//	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
//		event.setCancelled(true);
	}

	private void info(Object o) {
		plugin.getLogger().info(o.toString());
	}
}