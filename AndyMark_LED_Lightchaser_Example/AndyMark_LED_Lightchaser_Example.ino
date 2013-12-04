//This is a mashup of FastSPI_LED2 firstlight example and some other light chase hacks for the AM-2640 5V, Addressable LED strips http://www.andymark.com/product-p/am-2640.htm
//We ran this demo off of our AM-2287 ethernet arduino's http://www.andymark.com/product-p/am-2287.htm
//DO NOT try to power the whole strip off the arduino 5v regulator.  
//At full bright white, the strip can draw 1.5Amps or so. This will overheat or burnout the regulator.
//We recommend running these led strips off of the AM-0899 10-30Vin to 5V 5A out stepdown converter http://www.andymark.com/product-p/am-0899.htm
//AndyMark, Inc.
//Craig Kessler 12/3/2013
//Works with the AndyMark AM-2640 LED Strip based on the WS2801 chipset
//This code requires that the fastspi library be put in your arduino\libraries folder

//https://code.google.com/p/fastspi/
//https://code.google.com/p/fastspi/wiki/CRGBreference
#include "FastSPI_LED2.h"


// How many leds are in the strip? AndyMark's 2.5 meter strip has 80 leds 
#define NUM_LEDS 80

//Remember on the AM-2640 LED strip's yellow wire is ground! (don't blame us they come that way)

// Data pin that led data will be written out over
#define DATA_PIN 11	//Green wire from AM-2640's power connector

// Clock pin SPI 
#define CLOCK_PIN 13    //Blue wire from AM-2640's power connector

// This is an array of leds.  One item for each led in your strip.
CRGB leds[NUM_LEDS];

// This function sets up the leds and tells the controller about them
void setup() 
{
  // Uncomment one of the following lines for your leds arrangement.
  // FastLED.addLeds<TM1803, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<TM1804, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<TM1809, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<WS2811, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<WS2812, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<WS2812B, DATA_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<UCS1903, DATA_PIN, RGB>(leds, NUM_LEDS);

  //This is the chipset in the AM-2640 LED strip
  FastLED.addLeds<WS2801, RGB>(leds, NUM_LEDS);

  // FastLED.addLeds<SM16716, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<LPD8806, RGB>(leds, NUM_LEDS);

  // FastLED.addLeds<WS2801, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<SM16716, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
  // FastLED.addLeds<LPD8806, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);
}

// This function runs over and over, and is where you do the magic to light
// your leds.
void loop()
{
  //https://code.google.com/p/fastspi/wiki/CRGBreference	
  color_chase(CRGB::Green, 10);
  color_chase(CRGB::BlueViolet, 10);
  CRGB temp_rgb;
  CHSV hsv(215,255,255);
  hsv2rgb_rainbow(hsv, temp_rgb);
  missing_dot_chase(CRGB::White, 25);
  missing_dot_chase(CRGB::Red, 25);
  missing_dot_chase(CRGB::Yellow, 25);
  missing_dot_chase(CRGB::Green, 25);
  missing_dot_chase(CRGB::Cyan, 25);
  missing_dot_chase(CRGB::Blue, 25);
  missing_dot_chase(0x3000cc, 25) ;
}

void color_chase(uint32_t color, uint8_t wait)
{	
  FastLED.clear();
  FastLED.setBrightness(100);
  // Move a single led
  for(int led_number = 0; led_number < NUM_LEDS; led_number++)
  {
    // Turn our current led ON, then show the leds
    leds[led_number] = color;

    // Show the leds (only one of which is set to white, from above)
    FastLED.show();

    // Wait a little bit
    delay(50);

    // Turn our current led back to black for the next loop around
    leds[led_number] = CRGB::Black;
  }
}

//Move an "empty" dot down the strip
void missing_dot_chase(uint32_t color, uint8_t wait)
{
  int led_number;

  for (int led_brightness = 100; led_brightness > 10; led_brightness/=2)
  {
    FastLED.setBrightness(led_brightness);
    // Start by turning all pixels on:
    for(led_number = 0; led_number < NUM_LEDS; led_number++) leds[led_number] = color;

    // Then display one pixel at a time:
    for(led_number = 0; led_number < NUM_LEDS; led_number++)
    {
      leds[led_number] = CRGB::Black; // Set new pixel 'off'
      if( led_number > 0 && led_number < NUM_LEDS)
      {
        leds[led_number-1] = color; // Set previous pixel 'on'
      }
      FastLED.show();
      delay(wait);
    }
  }
}

