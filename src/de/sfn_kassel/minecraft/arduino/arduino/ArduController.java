package de.sfn_kassel.minecraft.arduino.arduino;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;

import de.sfn_kassel.minecraft.arduino.bukkit.ArduinoMCPlugin;
import de.sfn_kassel.minecraft.arduino.bukkit.util.ConfigLoader;
import de.sfn_kassel.minecraft.arduino.com.Com;

public class ArduController implements Closeable {

	private ArduinoMCPlugin plugin;
//	private HashMap<Location, Location> knownSigns = new HashMap<>();
	private ArduListener arduListener = null;
	private Com com;
	public enum ArduinoCommand{ANALOG_OUT, DIGITAL_IN, ANALOG_IN};
	private BlockState[] levers = new BlockState[128];
	private HashMap<Block, Integer> outWires = new HashMap<>();
	private ConfigLoader configLoader;

	public ArduController(ArduinoMCPlugin plugin) {
		this.plugin = plugin;
		this.configLoader = new ConfigLoader(plugin, this);
		try {
			configLoader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		resetComAndListener();
	}
	
	public void resetComAndListener() {
		try {
			Com oldCom = com;
			ArduListener oldListener = getArduListener();
			this.com = new Com(plugin.isInDebugMode() ? plugin.getLogger() : null, plugin.getConfig().getString("controller.COM"));
			arduListener = new ArduListener(this, this.com);
			if (oldListener != null)
				oldListener.close();
			if (oldCom != null)
				oldCom.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void info(Object o) {
		plugin.getLogger().info(o.toString());
	}

	public void warn(Object o) {
		plugin.getLogger().warning(o.toString());
	}

	public void setMCAnalog(int pin, int value) {
		//TODO: erst nach vortrag implementiren
		warn("[NOT_IMPLEMENTED] setMCAnalog("+pin+", "+value+")");
	}

	public void setMCDigital(int pin, int value) {
		if (pin >= levers.length)
			throw new IndexOutOfBoundsException("pinnr \""+pin+"\" is invalid");
		Lever lever = (Lever) levers[pin].getData();
		lever.setPowered(value != 0);
		levers[pin].update();
	}

	public void sendToArduino(ArduinoCommand cmd, int pin) {
		sendToArduino(cmd, pin, 0);
	}

	/**
	 * TODO ...
	 * 
	 * @param cmd
	 * @param pin
	 * @param value
	 */
	public void sendToArduino(ArduinoCommand cmd, int pin, int value) {
		switch (cmd) {
		case DIGITAL_IN:
			sendToArduino("d"+((char) ('@'+pin))+"X\n");
			break;

		case ANALOG_IN:
			sendToArduino("a"+((char) ('@'+pin))+"X\n");
			break;

		case ANALOG_OUT:
			sendToArduino("o"+((char) ('@'+pin))+((char) ('@'+value))+"\n");
			break;
		}
	}
	
	public void sendToArduino(String cmd) {
		if (!cmd.endsWith("\n"))
			cmd += '\n';
		com.sendSerialPort(cmd);
	}
	
	public void removeLever(int pin) {
		addLever(null, pin);
	}
	
	public void addLever(BlockState lever, int pin) {
		if (pin >= levers.length)
			throw new IndexOutOfBoundsException("pinnr \""+pin+"\" is invalid");
		levers[pin] = lever;
	}
	
	@Deprecated
	public void checkSign(Location sign) {
		if (sign.getBlock().getType() == Material.WALL_SIGN)
			if (((org.bukkit.block.Sign) sign.getBlock().getState().getData()).getLine(0).trim().equalsIgnoreCase("arduino"))//TODO check
				return;
//		knownSigns.remove(sign);
	}

	@Override
	public void close() throws IOException {
		info("closing ArduController...");
		configLoader.save();
		if (arduListener != null)
			arduListener.close();
	}

	public void setArduListener(ArduListener arduListener) {
		this.arduListener = arduListener;
	}

	public Com getCom() {
		return com;
	}

	public void setCom(Com com) {
		this.com = com;
	}

	public ArduListener getArduListener() {
		return arduListener;
	}

	public BlockState[] getLevers() {
		return levers;
	}

	public HashMap<Block,Integer> getOutWires() {
		return outWires;
	}

	public ArduinoMCPlugin getPlugin() {
		return plugin;
	}
	
}
