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

	/**
	 * 
	 * <b>Communication PC --> Arduino</b><br>
	 * <p>
	 * Generates and sends a command to the Arduino via COM-port.
	 * </p>
	 * 
	 * @param cmd One of the followings: {@code DIGITAL_IN, ANALOG_IN}
	 * @param pin Number of the pin (analog output A1 = ??TODO??)
	 * 
	 */
	public void sendToArduino(ArduinoCommand cmd, int pin) {
		sendToArduino(cmd, pin, 0);
	}

	/**
	 * 
	 * <b>Communication PC --> Arduino</b><br>
	 * <p>
	 * Generates and sends a command to the Arduino via COM-port.
	 * </p>
	 * 
	 * @param cmd One of the followings: {@code ANALOG_OUT, DIGITAL_IN, ANALOG_IN}
	 * @param pin Number of the pin (analog output A1 = ??TODO??)
	 * @param value The value to be set. For analog output it should be 0-15.
	 * 
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
	
	/*
	 * Communication PC --> Arduino
	 * 
	 *   ch1   ch2   ch3
	 * +-----+-----+-----+
	 * |  d  | PIN |  X  |            digital input
	 * +-----+-----+-----+
	 * |  a  | PIN |  X  |            analog input
	 * +-----+-----+-----+
	 * |  o  | PIN | Val |            analog output
	 * +-----+-----+-----+
	 */
	/**
	 * 
	 * <b>Communication PC --> Arduino</b><br>
	 * <p>
	 * Sends a command to the Arduino via COM-port.
	 * </p>
	 * <table border="1">
	 * <tr><td>ch1</td><td>ch2</td><td>ch3</td></tr>
	 * <tr><td>d</td><td><b>PIN</b></td><td><b>Val</b></td><td></td><td>digital input</td></tr>
	 * <tr><td>a</td><td><b>PIN</b></td><td><b>Val</b></td><td></td><td>analog input</td></tr>
	 * <tr><td>o</td><td><b>PIN</b></td><td><b>Val</b></td><td></td><td>analog output</td></tr>
	 * </table>
	 * 
	 * @param cmd see above; contains <b>PIN</b> and <b>Val</b>
	 * @param PIN given as a capital letter (@ -> 0, A -> 1, B -> 2, ...)
	 * @param Val given as a capital letter (@ -> 0, A -> 1, B -> 2, ...); might not be used by input commands
	 * 
	 */
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
