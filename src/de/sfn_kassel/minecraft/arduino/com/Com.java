package de.sfn_kassel.minecraft.arduino.com;

import gnu.io.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;


/**
 * zT ausm Inet...
 * Aus Projekt "DuoPod", deshalb unten viele spezielle funktionen
 * 
 * TODO Anselm: unnötigen code kicken
 * 
 * @author anonymus, Anselm von Wangenheim
 *
 */
public class Com implements Closeable {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {//TODO only a test
		Com c = new Com(null);
		Thread.sleep(3000);
		for (int i = 0; i < 64; i++) {
			c.sendSerialPort("oK"+((char) ('@' + (i % 16)))+"\n");//pin 11 4 times from 0 to 15
			c.sendSerialPort("oM"+((char) ('@' + 15*(i % 2)))+"\n");//pin 13 switch on/off 32 times
			Thread.sleep(150);
		}
		Thread.sleep(3000);
		System.exit(0);
	}
	
//	private static final String[] LIBARYS_WINDOWS = {
//		"rxtxParallel.dll",
//		"rxtxSerial.dll"
//	};
//	@SuppressWarnings("unused")
//	private static final String[] LIBARYS_LINUX = {
//		"librxtxParallel.so",
//		"librxtxSerial.so"
//	};

	private int tmclAddress = 1;
	private int tmclType = 0;
	private int tmclMotot_Bank = 0;
	private String lastCMD = "";
	
	public Object out = System.out;

	CommPortIdentifier serialPortId;
//	@SuppressWarnings("rawtypes")
//	Enumeration enumComm;
	SerialPort serialPort;
	private OutputStream outputStream;
	InputStream inputStream;
	boolean serialPortGeoeffnet = false;

	int baudrate = 9600;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;
	String portName = "COM4";

	int secondsRuntime = 1;

	public Com(String portName) {
		this(System.out, portName);
	}
	
	public Com(Object out, String portName) {
		this.out = out;
		if (portName != null)
			this.portName = portName;
		if (openSerialPort(this.portName) != true)
			return;
	}
	
	public ArrayList<String> getSerialPorts() {
		Enumeration<?> enumComm = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> list = new ArrayList<>();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (serialPortId.isCurrentlyOwned())
				list.add("[in use] "+serialPortId.getName());
			else
				list.add(serialPortId.getName());
		}
		return list;
	}
	
	private boolean openSerialPort(String portName) {
		boolean foundPort = false;
		if (serialPortGeoeffnet) { 
			info("Serialport bereits geöffnet");
			return false;
		}
		info("Öffne Serialport");
		Enumeration<?> enumComm = CommPortIdentifier.getPortIdentifiers();
		while(enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			info(serialPortId.getName());
			if (portName.contentEquals(serialPortId.getName())) {
				foundPort = true;
				break;
			}
		}
		if (foundPort != true) {
			info("Serialport nicht gefunden: " + portName);
			return false;
		}
		try {
			serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
		} catch (PortInUseException e) {
			info("Port belegt");
			error("Fehler: Port belegt! \n\n" +
					"Bitte achten Sie darauf, dass das Programm nicht mehrmals gleichzeitig ausgeführt wird.\n" +
					"Das Programm beendet sich jetzt.\n\n" +
					e.toString());
			return false;
		}
		try {
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			error("Keinen Zugriff auf OutputStream");
		}
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			error("Keinen Zugriff auf InputStream");
		}
		/*try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			info("TooManyListenersException fï¿½r Serialport");
		}
		serialPort.notifyOnDataAvailable(true);
		 */
		try {
			serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch(UnsupportedCommOperationException e) {
			info("Konnte Schnittstellen-Paramter nicht setzen");
		}

		serialPortGeoeffnet = true;
		return true;
	}

	@Override
	public void close() {
		info("closing Com...");
		if ( serialPortGeoeffnet == true) {
			info("Schlieï¿½e Serialport");
			try {
				getInputStream().close();
			} catch (IOException e) {}
			try {
				outputStream.close();
			} catch (IOException e) {}
			serialPort.close();
			serialPortGeoeffnet = false;
		} else {
			info("Serialport bereits geschlossen");
		}
	}

	public void readSerialPortBinary() {
		info(stringToBinary(getSerialPortInput()));
	}
	
	public String getSerialPortInput() {
		if (serialPortGeoeffnet != true)
			return null;
		try {
			int i;
			String s = "";
			while ((i = inputStream.read()) != -1) {
				s += (char) i;
			}
			return s;
		} catch (IOException e) {
			info("Fehler beim Lesen");
		}
		return null;
	}
	
	public void readSerialPort() {
		String in = getSerialPortInput();
		if (!in.isEmpty())
			info(in);
	}
	
	private void sendTMCLCommand(int tmclInstruction, int tmclValue) {
		sendTMCLCommand(tmclInstruction, tmclType, tmclValue);
	}
	
	private void sendTMCLCommand(int tmclInstruction, int tmclType, int tmclValue) {
		char tmclSum = 0;
		String cmd = convertToASC(tmclAddress, 1) + convertToASC(tmclInstruction, 1) + convertToASC(tmclType, 1) + convertToASC(tmclMotot_Bank, 1) + convertToASC(tmclValue, 4);
		for (int i = 0; i < cmd.length(); i++) {
			tmclSum += cmd.charAt(i);
		}
		String cmdAndSum = cmd + (char) tmclSum;
//		sendSerialPort(cmdAndSum);
		sendSerialPort(binaryStringToString(stringToBinary(cmdAndSum)));
		lastCMD = stringToBinary(cmdAndSum);
	}
	
	public void sendSerialPort(final String nachricht)
	{
//		if(arduController.getPlugin().isInDebugMode())//mach mal dass es geht :) // garnicht... siehe Variable out oben bzw methode info() unten
			info("Sende: " + nachricht);
		if (serialPortGeoeffnet != true)
			return;
		try {
			synchronized (outputStream) {
				outputStream.write(nachricht.getBytes());
			}
		} catch (IOException e) {
			info("Fehler beim Senden");
		}
	}
	
	private static String binaryStringToString(String s) {
		s = s.toUpperCase();
		String ret = "";
		if (!s.startsWith("0X"))
			s = "0X"+s;
		if (!s.substring(2).matches("^[0-9|A-F]*$"))
			return "";
		for (int i = 2; i < s.length()-1; i+=2) {
			ret += "" + (char) Integer.parseInt(s.substring(i, i+2), 16);
		}
		return ret;
	}
	
	private static String stringToBinary(String s) {
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}
		return bytesToBinary(bytes);
	}
	
	private static String bytesToBinary(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		sb.append("0x");
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	public void setAngularVelocity(double angularVelocity) {
		if (angularVelocity > 2047)
			angularVelocity = 2047;
		if (angularVelocity < -2048)
			angularVelocity = -2048;
		sendTMCLCommand(1/*ROR - rotate right*/, (int) angularVelocity);
	}
	
	private String convertToASC(int value, int len) {
		long lval = value + ((value < 0) ? 2*(long)Integer.MAX_VALUE+2 : 0);
		String ret = "";
		for (int i = 0; i < len; i++) {
			ret = ((char) lval) + ret;
			lval /= 0x100;
		}
		return ret;
	}

	public String getLastCMD() {
		return lastCMD;
	}
	
//	public OutputStream getOutputStream() {
//		return outputStream;
//	}

	public InputStream getInputStream() {
		return inputStream;
	}

	private void info(Object s) {
		if (out instanceof Logger)
			((Logger) out).info(s.toString());
		else if (out instanceof PrintStream)
			((PrintStream) out).println(s.toString());
	}

	private void error(Object s) {
		if (out instanceof Logger)
			((Logger) out).warning(s.toString());
		else if (out instanceof PrintStream)
			((PrintStream) out).println(s.toString());
	}

	public String getComName() {
		return portName;
	}

}