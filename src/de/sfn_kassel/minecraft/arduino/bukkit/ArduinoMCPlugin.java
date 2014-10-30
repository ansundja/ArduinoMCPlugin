package de.sfn_kassel.minecraft.arduino.bukkit;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.sfn_kassel.minecraft.arduino.arduino.ArduController;
import de.sfn_kassel.minecraft.arduino.arduino.ArduController.ArduinoCommand;
import de.sfn_kassel.minecraft.arduino.arduino.ArduListener;
import de.sfn_kassel.minecraft.arduino.com.Com;
import de.sfn_kassel.minecraft.arduino.com.LibaryLoader;

public class ArduinoMCPlugin extends JavaPlugin {
	private ArduController controller;
	private boolean debug = false;

	public ArduinoMCPlugin() {
		super();
		LibaryLoader.loadLibarys();
	}
	
	public void onEnable() {
		this.getLogger().info("ArduinoMCPlugin.onEnable()");
		reloadConfig();
		LibaryLoader.loadLibarys();
		try {
			if (controller == null) {
//				Com c = new Com(getLogger(), null);
				controller = new ArduController(this);
//				new ArduListener(controller, controller.getCom());//new
				new SignChangedListener(this);
				new RedstoneChangedListener(this);
			}
		} catch (Error e) {
			this.getLogger().info("Com futsch :/ (ERROR)");
			e.printStackTrace();
		} catch (Exception e) {
			this.getLogger().info("Com futsch :/ (EXCEPTION)");
			e.printStackTrace();
		}
	}
	
	public ArduController getController() {
		return controller;
	}

	public void onDisable() {
		this.getLogger().info("ArduinoMCPlugin.onDisable()");
		try {
			this.getLogger().info("Trying to close controller...");
			controller.close();
			this.getLogger().info("closed controller");
		} catch (IOException e) {
			this.getLogger().info("closing failed!");
			e.printStackTrace();
		}
		saveConfig();
	}
	
	@SuppressWarnings("resource")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			if(cmd.getName().equalsIgnoreCase("arduino")) { // Wenn der Spieler /arduino eingibt, dann tue das folgende...
	//			this.getLogger().info("ArduinoMCPlugin.onCommand(., "+cmd.getName()+",.,.)");
				
				String arg0 = (args.length > 0) ? args[0] : "help";
				switch (arg0.toLowerCase()) {
				case "help":
				case "h":
				case "?":
					sender.sendMessage("Help...");
					break;
	
				case "send":
					if (args.length < 2)
						return false;
					sender.sendMessage("sending "+args[1]);
					controller.sendToArduino(args[1]);
					break;
	
				case "set":
					if (args.length < 3) {
						sender.sendMessage("usage: arduino set [PIN] [LEVEL]\n\tPIN: the pin number\n\tLEVEL: the level (0-15)");
						return false;
					}
//					String msg = "o"+((char) ('@'+Byte.parseByte(args[1])))+((char) ('@'+Byte.parseByte(args[2])));
//					sender.sendMessage("sending "+msg);
					byte pin = Byte.parseByte(args[1]), value =  Byte.parseByte(args[2]);
					sender.sendMessage("setting analog output pin "+pin+" at value "+value);
					controller.sendToArduino(ArduinoCommand.ANALOG_OUT, pin, value);
					break;
					
				case "comset": //this is fucking useless ;)
				case "setcom":
					try {
						if (!args[1].toUpperCase().startsWith("COM"))
							args[1] = "COM" + args[1];
						Com c = new Com(getLogger(), args[1].toUpperCase());
						Com oldCom = controller.getCom();
						ArduListener oldListener = controller.getArduListener();
						controller.setCom(c);
						new ArduListener(controller, controller.getCom());
						oldListener.close();
						oldCom.close();
					} catch (Exception e) {
						sender.sendMessage("cannot set com");
					}
					break;
				case "debug":
					if(args[1] == "0")
						debug = false;
					else
						debug = true;
					try {
						controller.resetComAndListener();
//						Com oldCom = controller.getCom();
//						ArduListener oldListener = controller.getArduListener();
//						new ArduListener(controller, controller.getCom());
//						oldListener.close();
//						oldCom.close();
					} catch (Exception e) {
						sender.sendMessage("cannot set com");
					}
					break;

				default:
					sender.sendMessage("unknown command");
					return false;
				}
				
				return true;
			} else if(cmd.getName().equalsIgnoreCase("arduino-info")) {
				sender.sendMessage("Levers:");
				sender.sendMessage(Arrays.toString(controller.getLevers()));
				sender.sendMessage("Wires:");
				sender.sendMessage(controller.getOutWires().toString());
				return true;
			}// Wenn das passiert, wird die Funktion abbrechen und true als Wert zurückgeben. Wenn nicht, dann wird false als Wert zurückgegeben.
			return false;
		} catch (NumberFormatException e) {// sender wrote a wrong number
			return false;
		}
	}

	public boolean isInDebugMode() {
		return debug;
	}
}
