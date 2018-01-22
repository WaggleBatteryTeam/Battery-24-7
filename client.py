import httplib
import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import datetime
import time
import requests
import urllib

sensor = Adafruit_DHT.DHT22
wtime = datetime.datetime.now()
GPIO.setmode(GPIO.BCM)
#GPIO.setup(18,GPIO.OUT) #relay ch.1
#GPIO.setup(24,GPIO.OUT) #relay ch.2
GPIO.setup(25,GPIO.OUT) #relay ch.3
#GPIO.setup(27,GPIO.OUT) #relay ch.4
GPIO.setup(3,GPIO.OUT)
#print r.status_code, r.reason
#conn = httplib.HTTPConnection("192.168.2.148")
#response = conn.getresponse()
#status = response.status
#print status
#conn.request("HEAD","/")

a=0.00
GPIO.output(25,False)


def run():
    global a
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

        r= requests.post(url=URL,params=PARAMS)
         
        measure_vol()
        #print(wtime,data)
        if a==temperature :
            continue
        turn_on(temperature)
        a=temperature

        time.sleep(2)

#def turn_on(temp):
    print(temp)
    if temp > 30.0 :
        GPIO.output(25,True)
	print("turn on the fan")
	measure_vol()
    elif temp<=30.0 :
        GPIO.output(25,False)
	h,t = Adafruit_DHT.read_retry(sensor,20)
	print("turn off the fan")
	measure_vol()

#def recharge():
 #       if voltage>13:
           # GPIO.output(4,GPIO.low) 
  #      if voltage<11.8:
	   # GPIO.output(4,GPIO.high)

#float vout = 0.0;
#float vin = 0.0;
#float R1 = 30000.0;
#float R2 = 7500.0;

#def measure_vol():
#    print "Starting mesure vol!"
    
#    value = GPIO.output(3,True)
#    print(value)
    #vout = (value * 5.0) / 1024.0
    #vin = vout / (R2/(R1+R2))
#    time.sleep(2)
    #print(vin)
#    GPIO.cleanup() ## Clean the GPIO


if __name__ == '__main__':
    run()
