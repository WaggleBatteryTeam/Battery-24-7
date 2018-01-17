from django.shortcuts import render
import RPi.GPIO as GPIO
import Adafruit_DHT as Adafruit_DHT
import datetime
import time
from django.http import HttpResponse
# Create your views here.

sensor = Adafruit_DHT.DHT11
wtime = datetime.datetime.now()

def index(request):
    h,t = Adafruit_DHT.read_retry(sensor, 2)
    print (wtime, 'Temp = {0:0.1f}*C Humidity = {1:0.1f}%'.format(t,h))

    time.sleep(1)
    return HttpResponse("Temp:{0:0.1f}*C Humidity:{1:0.1f}%".format(t,h))


