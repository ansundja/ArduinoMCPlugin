## already done:
- [x] digitaler input
- [x] arduino seitiger code
- [x] protokoll

## TODO:
- [x] zwichenspeichern/ initialisieren des ardu
- [ ] NUR die Locations von den Schildern speichern -> alles andere beim Laden durch lesen der Schilder f�llen!
- [ ] restart != sterben
- [x] analoger output
  //wir haben eine andere m�lichkeit gefunden - hashmap umdrehen
- [x] Wie herum die Hashmap f�r die Schilder? --> Block -> Schild
- [ ] Schilder-Hashmap vollst�ndig implementieren
- [ ] Arduino Reset comand
- [ ] (analoger input)(�ber <>= operatoren)
- [ ] (Schalter sperren wenn er vom ardu genutzt wird)
- [ ] Soll das nur funktionieren, wenn das Schild NACH dem Schalter/Redstonekabel platziert wird? (falls nein, �berlegen und implementieren...; aufw�ndig.. m�h ... nach Know IT?) Jaro�s meinung : statt einem on sign changed listener einen on block listenr benutzen!! ;)
- [x] Soll der Output nur mit Wires funktionieren, oder auch mit anderen Bl�cken? (falls auch mit anderen, muss man glaube nur die �berpr�fung rausnehmen, obs ein Kabel ist, da beim Event ja gleich eine neue Current �bergeben wird und das Plugin so nichtmal wissen muss, was das f�r ein Block ist...) Jaro�s meinung: Jo machen wir
- [x] debug mode
- [x] Changelog entweder ordentlich f�hren oder abschaffen... Jaro�s meinung: ABSCHAFFEN UND GIT BEUTZEN!!!
- [ ] Schilder unregistrieren

- [ ] verwendugszweck �berlgen
  * vllt. rapid prototyping
- [ ] beispiel aufbau
- [ ] vortrag
  - [ ] ausdenken
  - [ ] �ben




##### INPUT:
gegeben: pin NR
wollen: schalter
Array[int] = {Schalter}

##### OUTPUT:
gegeben: redstonekabel
wollen: PINNR
HashMap<Kabel, Int>
