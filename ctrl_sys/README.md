# Control System

The control system supports to manage the status of battery boxes. The first goal of this system is maintaing the temperature inside battery boxes. To keep up the certain temperature, the control system decides to turn on/off heater or fan to make it heat up or cool down. The control system determines that based on the data from the temperature sensor.

## Contents

* control_system.py

The file is executed on Raspberry Pi.

## Hardware Spec
* Raspberry Pi 3 model B
* 4 channel relay module
* Sensors
  - DHT 22
  - INA 219
* Heating System
	- Docreate 5V-12V ZVS driver board Low Voltage Induction Heating CoilPower Supply Module + heating Coil
* Fan
	- Gdstime Ultrathin 60mm x 60mm x 10mm 12V Brushless Computer Case Cooling Replacement fan
* Inverter
	- Foval 150W Power Inverter DC 12V to 110V AC Converter with 3.1A Dual USB Car Charger

## Software Spec
* Python 3.6 - [How to install Python 3.6 on Raspbian](https://gist.github.com/dschep/24aa61672a2092246eaca2824400d37f)
* Python modules
  - INA219 - [How to install INA219 lib](https://pypi.python.org/pypi/pi-ina219/1.1.0)
  - Adafruit_DHT 
  - RPi_GPIO
  - requests
  - urllib

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
