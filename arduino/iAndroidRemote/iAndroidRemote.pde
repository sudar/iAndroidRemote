#include <IRremote.h>
#include <IRremoteInt.h>
#include <MeetAndroid.h>

int IR_PIN = 11; // IR Receiver Pin

const long Plus = 2011254788;
const long Next = 2011258884;
const long Minus = 2011246596;
const long Prev = 2011271172;
const long Center = 2011275268;
const long Menu = 2011283460;

MeetAndroid meetAndroid;

IRrecv irrecv(IR_PIN);
decode_results results;

void setup () {
  Serial.begin(115200); 
  irrecv.enableIRIn(); // Start the receiver  
}

void loop() {
  meetAndroid.receive(); // you need to keep this in your loop() to receive events

  if (irrecv.decode(&results)) {  // if an IR signal is obtained from IR receiver

    if (results.value == Plus) {
      meetAndroid.send("Plus");
    }

    if (results.value == Minus) {
      meetAndroid.send("Minus");
    }    

    if (results.value == Next) {
      meetAndroid.send("Next");
    }

    if (results.value == Prev) {
      meetAndroid.send("Prev");
    }

    if (results.value == 2011275268) {
      meetAndroid.send("Center");
    }

    if (results.value == Menu) {
      meetAndroid.send("Menu");
    }    

    irrecv.resume(); // Receive the next value    
  }
}
