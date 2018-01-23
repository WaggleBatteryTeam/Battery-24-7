import httplib
import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import datetime
import time
import requests
import urllib
#import INA219

sensor = Adafruit_DHT.DHT22
wtime = datetime.datetime.now()
GPIO.setmode(GPIO.BCM)

#GPIO.setup(18,GPIO.OUT) #channel 1
GPIO.setup(24,GPIO.OUT) #channel 2
GPIO.setup(25,GPIO.OUT) #channel 3
#GPIO.setup(27,GPIO.OUT) #channel 4
GPIO.setup(3,GPIO.OUT) #DHT22

#print r.status_code, r.reason
#conn = httplib.HTTPConnection("192.168.2.148")
#response = conn.getresponse()
#status = response.status
#print status
#conn.request("HEAD","/")

#All switch off before running
#GPIO.output(18,True)
GPIO.output(24,True)
GPIO.output(25,True)
#GPIO.output(27,True)

SHUNT_OHMS=0.1
MAX_EXPECTED_AMPS=0.2
cur_temperature=0.00

def run():
    global cur_temperature
    while(1):
        h,t = Adafruit_DHT.read_retry(sensor,20)
        data = 'Temperature = {0:0.1f}*C Humidity = {1:0.1f}%'.format(t,h)
        temperature = t
        
	URL = "http://192.168.2.52/test/waggle_receive.php"

	waggle_name = "Waggle21"
        battery = 10.5
        wind = 10.5
        solar = 10.5
	PARAMS = {'waggle_name':waggle_name,'date':wtime, 'battery':battery,'wind':wind, 'solar':solar, 'temperature' : temperature, 'humidity' : h}

        r= requests.post(url=URL,data=PARAMS)

        #measure_vol()
        print(wtime,data)
        if cur_temperature==temperature :
            continue

        control_temperature(temperature)
#        turn_on_fan(temperature)
        cur_temperature=temperature
        time.sleep(2)

def control_temperature(temp):
    print(temp)
    ##Fan_Control##
    if temp > 30.0 :
        GPIO.output(25,False)
    elif temp <= 30.0 :
        GPIO.output(25,True)
        h,t = Adafruit_DHT.read_retry(sensor,20)

    ##Heater_Control##
    if temp < 29.0 :
        GPIO.output(24,False)
    elif temp >= 29.5 :
        GPIO.output(24,True)
        h,t = Adafruit_DHT.read_retry(sensor,20)

#def turn_on_fan(temp):
#    print(temp)
#    if temp > 30.0 :
#        GPIO.output(25,True)
#	print("turn on the fan")
#    elif temp <= 30.0 :
#        GPIO.output(25,False)
#	h,t = Adafruit_DHT.read_retry(sensor,20)
#	print("turn off the fan")

#def recharge():
 #       if voltage>13:
           # GPIO.output(4,GPIO.low) 
  #      if voltage<11.8:
	   # GPIO.output(4,GPIO.high)

SHUNT_OHMS = 0.1 
MAX_EXPECTED_AMPS = 0.2 

#def measure_ele():
#     ina = INA219(SHUNT_OHMS, MAX_EXPECTED_AMPS)
#     ina.configure(ina.RANGE_16V)
#     print "Bus Voltage: %.3f V" % ina.voltage()
#     print "Bus Current: %.3f mA" % ina.current()
#     print "Power: %.3f mW" % ina.power()
#     print "Shunt voltage: %.3f mV" % ina.shunt_voltage()    

if __name__ == '__main__':
    run()

