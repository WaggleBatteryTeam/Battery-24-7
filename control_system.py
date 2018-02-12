import _thread
import os
import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import time
import requests
import urllib
import signal
import sys

from ina219 import INA219, DeviceRangeError

SHUNT_OHMS = 0.1
MAX_EXPECTED_AMPS = 2.0
ina = INA219(SHUNT_OHMS, MAX_EXPECTED_AMPS)
ina.configure(ina.RANGE_16V)

GPIO.setmode(GPIO.BCM)
#GPIO.setup(14,GPIO.OUT) #channel 4
GPIO.setup(15,GPIO.OUT) #channel 3
GPIO.setup(18,GPIO.OUT) #channel 2
#GPIO.setup(23,GPIO.OUT) #channel 1

##All switch off before running
#GPIO.output(14,True)
GPIO.output(15,True)
GPIO.output(18,True)
#GPIO.output(23,True)

sensor = Adafruit_DHT.DHT22

waggle_id = 3
URL = "http://18.219.74.113/test/waggle_receive_1.php"
cur_temperature=0.00 #current temperature
bvolt=0.00 #battery voltage
ccurr=0.00 #current from generator controller
fan="OFF"
heater="OFF"
charging="OFF"

PARAMS = {}

def send_log() :
    while(1) :
        global PARAMS
        r = requests.post(url=URL,data=PARAMS)
        time.sleep(10)

def run():
    # Run a thread to send server values
    _thread.start_new_thread(send_log,())

    global waggle_id, URL, bvolt, ccurr, cnt, heater,fan, PARAMS
    is_fan_on=False
    is_heater_on=False

    while(1):
        now = time.localtime()
        wtime = "%04d-%02d-%02d %02d:%02d:%02d" % (now.tm_year, now.tm_mon,
        now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
        h,t = Adafruit_DHT.read_retry(sensor,22) # check temp & humid

        bvolt, ccurr = read_ina219()

        data = 'Temp = {0:0.1f} Humid = {1:0.1f} Volt = {2:0.1f}'.format(t,h,bvolt)

        remain_battery = check_remain(bvolt)
        check_charging_status(ccurr)

        # Parameters to be sent
        PARAMS = {'waggle_id':waggle_id, 'remain_battery':remain_battery,
        'voltage':bvolt, 'charging':charging,
        'temperature' : t, 'humidity':h, 'heater':heater,
        'fan':fan, 'updated_time':wtime}

        print(wtime, data)
        print(ccurr)
        # Decide whether we should turn on/off fan or Heater
        if not is_fan_on:
            if t >25.0:
                print ("turn on the Fan")
                GPIO.output(18, False)
                fan="ON"
                PARAMS['fan'] = "ON"
                is_fan_on = True
                r = requests.post(url=URL,data=PARAMS)
        else:
            if t <10.0:
                print ("turn off the Fan")
                GPIO.output(18, True)
                fan="OFF"
                PARAMS['fan'] = "OFF"
                is_fan_on = False
                r = requests.post(url=URL,data=PARAMS)
        if is_heater_on:
            if t >20.0:
                print ("turn off the Heater")
                GPIO.output(15, True)
                PARAMS['heater']="OFF"
                is_heater_on = False
                r = requests.post(url=URL,data=PARAMS)
        else:
            if t <19.0:
                print ("turn on the Heater")
                GPIO.output(15, False)
                PARAMS['heater']="ON"
                is_heater_on = True
                r = requests.post(url=URL,data=PARAMS)

def read_ina219():
    v = ina.voltage()
    c = ina.current()
    #print('Bus Voltage: {0:0.2f}V'.format(ina.voltage()))
    #print('Bus Current: {0:0.2f}mA'.format(ina.current()))
    #print('Power: {0:0.2f}mW'.format(ina.power()))
    #print('Shunt Voltage: {0:0.2f}mV\n'.format(ina.shunt_voltage()))
    return v,c

def check_remain(voltage):
    if voltage>13.7: return 100
    elif voltage<=13.7 and voltage>13.3 : return 96
    elif voltage<=13.3 and voltage>12.9 : return 93
    elif voltage<=12.9 and voltage>12.8 : return 90
    elif voltage<=12.8 and voltage>12.7 : return 85
    elif voltage<=12.7 and voltage>12.6 : return 84
    elif voltage<=12.6 and voltage>12.5 : return 79
    elif voltage<=12.5 and voltage>12.4 : return 68
    elif voltage<=12.4 and voltage>12.3 : return 63
    elif voltage<=12.3 and voltage>12.2 : return 57
    elif voltage<=12.2 and voltage>12.1 : return 52
    elif voltage<=12.1 and voltage>12.0 : return 47
    elif voltage<=12.0 and voltage>11.9 : return 44
    elif voltage<=11.9 and voltage>11.8 : return 41
    elif voltage<=11.8 and voltage>11.7 : return 35
    elif voltage<=11.7 and voltage>11.6 : return 31
    elif voltage<=11.6 and voltage>11.5 : return 25
    elif voltage<=11.5 and voltage>11.4 : return 17
    elif voltage<=11.4 and voltage>11.3 : return 13
    else: return 0

def check_charging_status(current):
    global charging
    if current < 0.1 :
        charging='OFF'
    else :
        charging='ON'

def notice(battery, temperature, humid):
    if battery<30 : return "Battery too low!"
    elif temperature >70 : return "Temperature too high!"
    elif temperature <-20 : return "Temperature too low!"
    elif humid >90 : return "Humidity too high!"
    else : return "Operating!"


if __name__ == '__main__':
    run()
