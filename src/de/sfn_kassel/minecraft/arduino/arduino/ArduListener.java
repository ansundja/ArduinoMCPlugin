package de.sfn_kassel.minecraft.arduino.arduino;

import java.io.Closeable;
import java.io.IOException;

import de.sfn_kassel.minecraft.arduino.com.Com;

public class ArduListener implements Runnable, Closeable {
	private final ArduController arduController;
	private Com comPort;
	private boolean running = false;
	private boolean waitingForCom = true;
	private String sendLater = "";

	public ArduListener(ArduController arduController, Com comPort) {
		this.arduController = arduController;
		this.comPort = comPort;
		this.arduController.setArduListener(this);
		new Thread(this).start();
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			//listen at comPort and set in MC
			char c;
			String cmd = "";
			try {
				while (running && comPort.getInputStream() != null &&
						(c = (char) comPort.getInputStream().read()) != -1 && c != '\n') {
					if (!waitingForCom && !sendLater.isEmpty()) {
						comPort.sendSerialPort(sendLater);
						sendLater = "";
					}
					if (c != 65535)// prevent '?' / -1
						cmd += c;
				}
				parseCmd(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
 		}
		arduController.info("ArduListener closed");
		comPort.close();
	}

	/*
	 * Communication Arduino --> PC
	 * 
	 *   ch1   ch2   ch3   chn
	 * +-----+-----+-----+-----+
	 * |  D  | PIN | Val |            digital input
	 * +-----+-----+-----+
	 * |  A  | PIN | Val |            analog input
	 * +-----+-----+-----+-----+
	 * |  S  |       ...       |      success / response
	 * +-----+-----------------+
	 * |  E  |       ...       |      error
	 * +-----+-----------------+
	 */
	/**
	 * 
	 * Communication Arduino --> PC
	 * <table border="1">
	 * <tr><td>ch1</td><td>ch2</td><td>ch3</td><td>chn</td></tr>
	 * <tr><td>D</td><td><b>PIN</b></td><td><b>Val</b></td><td></td><td>digital input</td></tr>
	 * <tr><td>A</td><td><b>PIN</b></td><td><b>Val</b></td><td></td><td>analog input</td></tr>
	 * <tr><td>S</td><td colspan = "3" align="center">...</td><td>success / response</td></tr>
	 * <tr><td>E</td><td colspan = "3" align="center">...</td><td>error</td></tr>
	 * </table>
	 * 
	 * @param cmd see above; contains <b>PIN</b> and <b>Val</b>
	 * @param PIN given as a capital letter (@ -> 0, A -> 1, B -> 2, ...)
	 * @param Val given as a capital letter (@ -> 0, A -> 1, B -> 2, ...)
	 * 
	 */
	private void parseCmd(String cmd) {
		if (cmd.length() == 0)
			return;
		if(arduController.getPlugin().isInDebugMode())
			arduController.info(cmd);
		
		char type = cmd.charAt(0);
		int pin = 0, value = 0;

		if (cmd.length() >= 3) {
			pin = cmd.charAt(1) - '@';
			value = cmd.charAt(2) - '@';
		}

		switch (type) {
		case 'a'://analog in
		case 'A':
			if (cmd.length() < 3)
				break;
			if(arduController.getPlugin().isInDebugMode())
				arduController.info("setMCAnalog("+pin+", "+value+")");
			arduController.setMCAnalog(pin, value); 
			break;
		case 'd'://digital in
		case 'D':
			if (cmd.length() < 3)
				break;
			if(arduController.getPlugin().isInDebugMode())
				arduController.info("setMCDigital("+pin+", "+value+")");
			arduController.setMCDigital(pin, value);
			break;
		case 's'://success
		case 'S':
			waitingForCom = false;
			if(arduController.getPlugin().isInDebugMode())
				arduController.info("success: "+cmd);
			break;
		case 'e'://error
		case 'E':
			arduController.warn("error: "+cmd);
			break;
		default:
			arduController.warn("unknown command: \""+cmd+"\"");//unknown command
			break;
		}
	}

	@Override
	public void close() throws IOException {
		arduController.info("closing ArduListener...");
		this.running = false;
	}
	
	public void sendLater(String s) {
		if (!s.endsWith("\n"))
			s += "\n";
		sendLater += s;
	}
}
