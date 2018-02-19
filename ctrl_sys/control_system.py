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

waggle_id = "7" #waggle id. It will be different by waggles
URL = "http://18.219.74.113/test/waggle_receive_function_with_csv.php"
cur_temperature=0.00 #current temperature
bvolt=0.00 #battery voltage
ccurr=0.00 #current from generator to controller
fan="OFF"
heater="OFF"
charging="OFF"

PARAMS = {}

#sub thread - send the data every 1min
#if you want, you can change second on time.sleep()
def send_log() :
        while True :
                global PARAMS
                r = requests.post(url=URL,data=PARAMS)
                time.sleep(60)

def run():
        global waggle_id, URL, bvolt, ccurr, cnt, heater,fan, PARAMS
        is_fan_on=False
        is_heater_on=False

        now = time.localtime()
        wtime = "%04d-%02d-%02d %02d:%02d:%02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
        h,t = Adafruit_DHT.read_retry(sensor,22) # check temp & humid
        bvolt, ccurr = read_ina219()
        data = 'Temp = {0:0.1f} Humid = {1:0.1f} Volt = {2:0.1f}'.format(t,h,bvolt)
        remain_battery = check_remain(bvolt)
        check_charging_status(ccurr)
        #run sub thread
        _thread.start_new_thread(send_log,())

        #main thread - If break events on the system, send data to server
        while True:
                time.sleep(3)
                now = time.localtime()
                wtime = "%04d-%02d-%02d %02d:%02d:%02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
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

                notice(bvolt, t, h)

                # Decide whether we should turn on/off fan or Heater
                if not is_fan_on:
                        if t >30.0:
                                print ("turn on the Fan")
                                GPIO.output(18, False)
                                fan="ON"
                                PARAMS['fan'] = "ON"
                                is_fan_on = True
                                r = requests.post(url=URL,data=PARAMS)
                else:
                        if t <23.0:
                                print ("turn off the Fan")
                                GPIO.output(18, True)
                                fan="OFF"
                                PARAMS['fan'] = "OFF"
                                is_fan_on = False
                                r = requests.post(url=URL,data=PARAMS)
                if is_heater_on:
                        if t >17.0:
                                print ("turn off the Heater")
                                GPIO.output(15, True)
                                heater="OFF"
                                PARAMS['heater']="OFF"
                                is_heater_on = False
                                r = requests.post(url=URL,data=PARAMS)
                else:
                        if t <10.0:
                                print ("turn on the Heater")
                                GPIO.output(15, False)
                                heater="ON"
                                PARAMS['heater']="ON"
                                is_heater_on = True
                                r = requests.post(url=URL,data=PARAMS)

def read_ina219():
        v = ina.voltage()
        c = ina.current()
        return v,c

def check_remain(voltage):
        if voltage>13.7: return 100
        elif 13.3<voltage<=13.7 : return 96
        elif 12.9<voltage<=13.3 : return 93
        elif 12.8<voltage<=12.9 : return 90
        elif 12.7<voltage<=12.8 : return 85
        elif 12.6<voltage<=12.7 : return 84
        elif 12.5<voltage<=12.6 : return 79
        elif 12.4<voltage<=12.5 : return 68
        elif 12.3<voltage<=12.4 : return 63
        elif 12.2<voltage<=12.3 : return 57
        elif 12.1<voltage<=12.2 : return 52
        elif 12.0<voltage<=12.1 : return 47
        elif 11.9<voltage<=12.0 : return 44
        elif 11.8<voltage<=11.9 : return 41
        elif 11.7<voltage<=11.8 : return 35
        elif 11.6<voltage<=11.7 : return 31
        elif 11.5<voltage<=11.6 : return 25
        elif 11.4<voltage<=11.5 : return 17
        elif 11.3<voltage<=11.4 : return 13
        else: return 0

def check_charging_status(current):
        global charging
        if current < 0.1 :
                charging='OFF'
        else :
                charging='ON'

def notice(voltage, temperature, humidity):
        if voltage < 30 : return "Battery too low!"
        elif temperature > 40 : return "Temperature too high!"
        elif temperature < -5 : return "Temperature too low!"
        elif humidity > 90 : return "Humidity too high!"

        else : return "Operating!"


if __name__ == '__main__':
        run()
