//This is a mashup of the FastSPI library and code from http://www.instructables.com/id/Arduino-Persistence-of-Vision-Display/ for POV
//https://code.google.com/p/fastspi/
//https://code.google.com/p/fastspi/wiki/CRGBreference
#include "FastSPI_LED2.h"

// How many leds are in the strip?
// The AM-2640 LED lightstrip is 2.5m long and has 80 leds.
// That is probably too long for a lightsaber but the strips can be cut to length at the junctions marked on the strip
// For easy wiring of cut strips use the AM-2641 clip on connector
#define NUM_LEDS 80

// Data pin that led data will be written out over
#define DATA_PIN 11   //Green wire from AM-2640 power connector

// Clock pin only needed for SPI based chipsets when not using hardware SPI
#define CLOCK_PIN 13  //Blue wire from AM-2640 power connector

// This is an array of leds.  One item for each led in your strip.
CRGB leds[NUM_LEDS];

// This function sets up the leds and tells the controller about them
void setup()
{
	// Uncomment one of the following lines for your leds arrangement.
	// FastLED.addLeds<TM1803, DATA_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<TM1804, DATA_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<TM1809, DATA_PIN, RGB>(leds, NUM_LEDS);
	//FastLED.addLeds<WS2811, DATA_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<WS2812, DATA_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<WS2812B, DATA_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<UCS1903, DATA_PIN, RGB>(leds, NUM_LEDS);
        
        //This is the chipset in the AM-2640 LED strip
	FastLED.addLeds<WS2801, RGB>(leds, NUM_LEDS);

	// FastLED.addLeds<SM16716, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<LPD8806, RGB>(leds, NUM_LEDS);

	//FastLED.addLeds<WS2801, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<SM16716, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
	// FastLED.addLeds<LPD8806, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
	////SdPlay.setSDCSPin(10); // Enable if your SD card CS-Pin is not at Pin 4...
	//SdPlay.init(SSDA_MODE_FULLRATE | SSDA_MODE_MONO | SSDA_MODE_AUTOWORKER);
	//SdPlay.setFile("lightsab.wav");
	
        //Change the color value for the lightsaber color of your choice.  
        //The last number controls how long the saber takes to complete the lighting sequence.
	light_saber_on(CRGB::Green, 35);
}

// This function runs over and over, and is where you do the magic to light
// your leds.
void loop()
{
  //Nothing to do here.  lightsaber function call is done once in setup()
}

void light_saber_on(uint32_t c, uint8_t wait)
{
	int led_number = 0;
	FastLED.clear();
	FastLED.setBrightness(5);
	//Need to get a speaker so we can build a call to light saber power up sound.

	//SdPlay.play();
		
	for(led_number = 0; led_number < NUM_LEDS; led_number++)
	{
		delay(wait);
		leds[led_number] = c;
		FastLED.show();
		//FastLED.show_one(led_number, led_brightness);
	}
	//Increase "beam" intensity with "power flicker"
	for (int led_brightness = 5; led_brightness <= 100; led_brightness+=5  )
	{
		FastLED.setBrightness(led_brightness);
		FastLED.show();
		delay(40);
	}
	for (int led_brightness = 100; led_brightness >= 50; led_brightness-=5  )
	{
		FastLED.setBrightness(led_brightness);
		FastLED.show();
		delay(30);
	}	
	for (int led_brightness = 50; led_brightness <= 100; led_brightness+=5  )
	{
		FastLED.setBrightness(led_brightness);
		FastLED.show();
		delay(25);
	}
	return;
}
