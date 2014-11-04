package de.sfn_kassel.minecraft.arduino.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import de.sfn_kassel.minecraft.arduino.arduino.ArduController;
import de.sfn_kassel.minecraft.arduino.bukkit.ArduinoMCPlugin;
import de.sfn_kassel.minecraft.arduino.bukkit.SignChangedListener;

public class ConfigLoader {
	
	private ArduinoMCPlugin plugin;
//	private ArduController controller;
//	private ArrayList<HashMap<BlockState,Condition>> levers;
//	private HashMap<Block, Integer> outWires;

	public ConfigLoader(ArduinoMCPlugin plugin, ArduController controller) {
		this.plugin = plugin;
//		this.controller = controller;
//		this.outWires = controller.getOutWires();
	}

	public void load(SignChangedListener sCL) {//TODO load sign-list
//		FileConfiguration config = plugin.getConfig();
		
		Location loc;
		int k = 0;
//		SignChangedListener sCL = new SignChangedListener(plugin, controller);
		while ((loc = loadLocation("controller.signs."+k++)) != null) {
			sCL.addSign(loc);
		}
//		FileConfiguration config = plugin.getConfig();
//		for (int i = 0; i < levers.size(); i++) {
//			Location loc;
//			int k = 0;
//			while ((loc = loadLocation("controller.levers."+i+"."+k++)) != null) {
////					levers.get(i).put(key, value) = loc.getBlock().getState();
//			}
////			plugin.getLogger().info("controller.levers."+i+" = "+plugin.getConfig().getString("controller.levers."+i)+" = "+loc+";  "+levers[i]);
//		}
//
//		ConfigurationSection owSection = config.getConfigurationSection("controller.outWires");
//		if (owSection != null) {
//			for (String key : owSection.getKeys(false)) {
//				try {
//					Integer pin = Integer.parseInt(key);
//					Location loc = loadLocation("controller.outWires."+key);
//					System.out.println("key="+key+", pin="+pin+", loc="+loc);
//					if (loc != null)
//						outWires.put(loc.getBlock(), pin);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} else {
//			plugin.getLogger().warning("owSection = null");
//		}
	}
	
	private Location loadLocation(String path) {
		try {
			String locString = plugin.getConfig().getString(path);
			if (locString == null || locString.isEmpty())
				return null;
			String[] locArray = locString.split("\\|");
			Location loc = new Location(Bukkit.getWorld(locArray[0]), Integer.parseInt(locArray[1]), Integer.parseInt(locArray[2]), Integer.parseInt(locArray[3]));
			return loc;
		} catch (Exception e) {
			plugin.getLogger().warning("could not load Location \""+path+"\"");
		}
		return null;
	}
	
	private String locationToString(Location loc) {
		return loc.getWorld().getName()+"|"+loc.getBlockX()+"|"+loc.getBlockY()+"|"+loc.getBlockZ();
	}
	
	public void save() {//TODO save as sign-list
		FileConfiguration config = plugin.getConfig();
		try {
			config.set("controller.COM", plugin.getController().getCom().getComName());
			config.set("controller.signs", null);
			
			int i = 0;
			for (Location loc : plugin.getController().getKnownSigns()) {
				config.set("controller.signs."+i++, locationToString(loc));
			}
//			config.set("controller.levers", null);
//			for (int i = 0; i < levers.length; i++) {
//				if (levers[i] != null)
//					config.set("controller.levers."+i, locationToString(levers[i].getLocation()));
//			}
			
//			config.set("controller.outWires", null);//clear?!
//			for (Block b : outWires.keySet()) {
//				config.set("controller.outWires."+outWires.get(b), locationToString(b.getLocation()));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
