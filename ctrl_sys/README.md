# Control System

The control system supports to manage the status of battery boxes. The first goal of this system is maintaing the temperature inside battery boxes. To keep up the certain temperature, the control system decides to turn on/off heater or fan to make it heat up or cool down. The control system determines that based on the data from the temperature sensor.

## Contents

* control_system.py

The file is executed on Raspberry Pi.

## Hardware Spec
* Raspberry Pi 3 model B
* 4 channel relay module
* Sensors
  - DHT 22 (temperature & humidity)
  - INA 219 (voltage & current)
* Heating System
	- Docreate 5V-12V ZVS driver board Low Voltage Induction Heating CoilPower Supply Module + heating Coil
* Fan
	- Gdstime Ultrathin 60mm x 60mm x 10mm 12V Brushless Computer Case Cooling Replacement fan
* Inverter
	- Foval 150W Power Inverter DC 12V to 110V AC Converter with 3.1A Dual USB Car Charger

## Software Spec
* [Python 3.6](https://gist.github.com/dschep/24aa61672a2092246eaca2824400d37f)
* Python modules
  - [INA219](https://pypi.python.org/pypi/pi-ina219/1.1.0)
  - [Adafruit_DHT](https://learn.adafruit.com/dht-humidity-sensing-on-raspberry-pi-with-gdocs-logging/software-install-updated)
  - [RPi_GPIO](https://learn.adafruit.com/playing-sounds-and-using-buttons-with-raspberry-pi/install-python-module-rpi-dot-gpio?gclid=Cj0KCQiAiKrUBRD6ARIsADS2OLnOuLc3FpbCGW6sa73oVR8v83-yj8Q2jvgfYV1tIxeVswp4KauqfA8aAvjrEALw_wcB)
  - [requests](https://docs.python.org/3/installing/index.html)
  - [urllib](https://docs.python.org/3/library/urllib.html#module-urllib)

### 
The below code should be appened on `~/.profile`.
You can access `~/.profile` with `nano ~/.profile` or `vi ~/.profile`.
```sh
tmux new-session -d -s control
tmux send-keys -t control:0 "python3.6 ~/Battery-24-7/control_system.py" C-m
```

## Quick Guide for tmux
**Install**
```sh
sudo apt-get install tmux
```

`C-b` means `Ctrl`+`b`

**On main session**
```
tmux                                        create new session
tmux new-session -s "Session Name"          create new session with "Session Name"   
tmux attach -s "Session Name"               attach to "Session Name" session with new window
tmux ls                                     show the tmux list
```

**Session Control**
```
C-b (                           previous session
C-b )                           next session
C-b &                           close and exit the current session(kill the current session)
C-b d                           detach the session(exit the current session and return to the main session)
```
