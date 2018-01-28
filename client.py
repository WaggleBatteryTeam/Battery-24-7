import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import time
import requests
import urllib
#import INA219

sensor = Adafruit_DHT.DHT22

GPIO.setmode(GPIO.BCM)
#GPIO.setup(18,GPIO.OUT) #channel 1
GPIO.setup(24,GPIO.OUT) #channel 2
GPIO.setup(25,GPIO.OUT) #channel 3
#GPIO.setup(27,GPIO.OUT) #channel 4
GPIO.setup(3,GPIO.OUT) #DHT22

##All switch off before running
#GPIO.output(18,True)
GPIO.output(24,True)
GPIO.output(25,True)
#GPIO.output(27,True)

URL = "http://192.168.2.52/test/waggle_receive.php"
cur_temperature=0.00
bvolt=0.00
ccurr=0.00
cnt=0

def run():
    global URL, cur_temperature, bvolt, ccurr, cnt
    is_fan_on = False
    is_heater_on = False

    while(1):
        cnt += 1
        #print(cnt) for debugging
        now = time.localtime()
        wtime = "%04d-%02d-%02d %02d:%02d:%02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
        print(wtime)
        h,t = Adafruit_DHT.read_retry(sensor,17)
        data = 'Temperature = {0:0.1f} Humidity = {1:0.1f}'.format(t,h)

        temperature = t

        waggle_name = "Waggle1"
        battery = 10.5
        wind = 10.5
        solar = 10.5
        PARAMS = {'waggle_name':waggle_name,'date':wtime, 'battery':battery,'wind':wind, 'solar':solar, 'temperature' : temperature, 'humidity' : h}

        if cnt%80 == 0 :
            r = requests.post(url=URL,data=PARAMS)
            cnt = 0
        #measure_vol()
        print(wtime,data)
        if cur_temperature==temperature :
            continue

        is_fan_on, is_heater_on = control_temperature(temperature, is_fan_on, is_heater_on)
        cur_temperature=temperature

def control_temperature(temp, f, h):
    print(temp)
    if f :
        if temp < 25.0 :
            print "turn off the Fan"
            GPIO.output(25, True)
            f = False
    else :
        if temp > 28.0 :
            print "turn on the Fan"
            GPIO.output(25, False)
            f = True

    if h :
        if temp > 20.0 :
            print "turn off the Heatern"
            GPIO.output(24, True)
            h = False
    else :
        if temp < 15.0 :
            print "turn on the Heater"
            GPIO.output(24, False)
            h = True

    return f, h

SHUNT_OHMS = 0.1
MAX_EXPECTED_AMPS = 2.0

if __name__ == '__main__':
    run()
