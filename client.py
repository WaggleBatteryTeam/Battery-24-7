import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import time
import requests
import urllib
#import ina219 as INA219
import serial #for arduino

sensor = Adafruit_DHT.DHT22

GPIO.setmode(GPIO.BCM)
#GPIO.setup(14,GPIO.OUT) #channel 4
GPIO.setup(15,GPIO.OUT) #channel 3
GPIO.setup(18,GPIO.OUT) #channel 2
#GPIO.setup(23,GPIO.OUT) #channel 1
GPIO.setup(3,GPIO.OUT) #DHT22

##All switch off before running
#GPIO.output(14,True)
GPIO.output(15,True)
GPIO.output(18,True)
#GPIO.output(23,True)

URL = "http://18.219.74.113/test/waggle_receive_1.php"
cur_temperature=0.00 #current temperature
bvolt=0.00 #battery voltage
ccurr=0.00 #current from generator controller
cnt=0 #count for requset
fan="OFF"
heater="OFF"


def run():
    global URL, cur_temperature, bvolt, ccurr, cnt, heater, fan
    port = serial.Serial("/dev/ttyUSB0", baudrate=9600, timeout=None)
    is_fan_on=False
    is_heater_on=False

    while(1):
        cnt += 1

        now = time.localtime()
        wtime = "%04d-%02d-%02d %02d:%02d:%02d" % (now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
        h,t = Adafruit_DHT.read_retry(sensor,22) # check temp & humid
        bvolt=port.readline() #check voltage

        arr=bvolt.split()

        bvolt=float(arr[0])
        temperature = t
        data = 'Temp = {0:0.1f} Humid = {1:0.1f} Volt = {2:0.1f}'.format(t,h,bvolt)

        waggle_id = 1
        remain_battery = check_remain(bvolt)
        current = 10.0
        charging="OFF"

        PARAMS = {'waggle_id':waggle_id, 'remain_battery':remain_battery,
        'voltage':bvolt, 'charging':charging,
        'temperature' : temperature, 'humidity':h, 'heater':heater,
        'fan':fan, 'updated_time':wtime}

        if cnt%80 == 0  : #after 80 loop, send to server
            r = requests.post(url=URL,data=PARAMS)
            cnt = 0
            print(wtime,data); #print time,temp,humid,volt

        if cur_temperature==temperature :
            continue
        is_fan_on, is_heater_on = control_temperature(temperature, is_fan_on, is_heater_on, PARAMS)
        cur_temperature=temperature

def control_temperature(temp, f, h, params):
    global fan, heater
    if f :
        if temp < 20.0 :
            print "turn off the Fan"
            GPIO.output(18, True)
            f = False
            fan = "OFF"
            r = requests.post(url=URL,data=params)
    else :
        if temp > 28.0 :
            print "turn on the Fan"
            GPIO.output(18, False)
            f = True
            fan ="ON"
            r = requests.post(url=URL,data=params)
    if h :
        if temp > 20.0 :
            print "turn off the Heater"
            GPIO.output(15, True)
            h = False
            heater="OFF"
            r = requests.post(url=URL,data=params)
    else :
        if temp < 16.0 :
            print "turn on the Heater"
            GPIO.output(15, False)
            h = True
            heater="ON"
            r = requests.post(url=URL,data=params)
    return f, h

SHUNT_OHMS = 0.1
MAX_EXPECTED_AMPS = 2.0
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

def measure_vol():
    ina = INA219(SHUNT_OHMS, MAX_EXPECTED_AMPS)
    ina.configure(ina.RANGE_16V)
    bvolt2 =  ina.voltage() #"%.3f" % ina.voltage()
    shvol = ina.shut_voltage() #"%.3f" % ina.shunt_voltage()
    curr = ina.current() #"%.3f" % ina.current()
    pwr = ina.power() #"%.3f" % ina.power()
    tvolt = ina.voltage()+ina.shut_voltage() / 1000 # "%.3f" % ( ina.voltage() + ina.shunt_voltage() / 1000 )
    time.sleep(1)
    return bvolt2

def measure_curr():
    ina = INA219(SHUNT_OHMS, MAX_EXPECTED_AMPS)
    ina.configure(ina.RANGE_16V)
    bvolt2 =  ina.voltage() #"%.3f" % ina.voltage()
    shvol = ina.shut_voltage() #"%.3f" % ina.shunt_voltage()
    curr = ina.current() #"%.3f" % ina.current()
    pwr = ina.power() #"%.3f" % ina.power()
    tvolt = ina.voltage()+ina.shut_voltage() / 1000 # "%.3f" % ( ina.voltage() + ina.shunt_voltage() / 1000 )
    time.sleep(1)
    return curr

def check_charging(current):
    if current < 0.1 :
        charging='OFF'
        print("Charging Canceled.")
    else :
        charging='ON'
        print("Now Chaging!")

def notice(battery, temperature, humid):
    if battery<30 : return "Battery too low!"
    elif temperature >70 : return "Temperature too high!"
    elif temperature <-20 : return "Temperature too low!"
    elif humid >90 : return "Humidity too high!"
    else : return "Operating!"

if __name__ == '__main__':
    run()
