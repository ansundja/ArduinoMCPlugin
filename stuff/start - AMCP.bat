@ECHO OFF
TITLE MINECRAFT-CRAFTBUKKIT

java -Xms512M -Xmx512M -cp "plugins\ArduinoMCPlugin\lib\RXTXcomm.jar" -jar "%~dp0craftbukkit.jar"

PAUSE