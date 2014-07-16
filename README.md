## GlassGeigie

Google Glass application to display data from bGeigie nano devices. Connection between Glass and bGeigie nano via BLEBee (Bluetooth Low Energy).

THIS SOFTWARE IS AT AN EARLY DEVELOPMENT STAGE - USE WITH CAUTION!

### Pre-Requisites

For this app, you need a working bGeigie nano (http://blog.safecast.org/bgeigie-nano/) and additionally the BLEBee Bluetooth module (http://www.mkroll.mobi/?page_id=1070). The BLEBee module should be installed in your bGeigie. No configuration should be necessary, if you have a recent bGeigie firmware.
### Installation

It should be possible to install the included apk file using the Android Debug Bridge (adb). 


### Usage

Start software via voice command ("measure radiation") or via the menu. Voice commands do not work in App-internal menus.

After starting the app, follow these steps to connect and display data:
1. Swipe to front until you reach the entry "Scan for bGeigie nano with BLEBee". Click once. The App will start bluetooth and search for BLEBees for 10 seconds. 

2. The BLEBees that have been found are list as additional menu items. Swipe to front until you reach the BLEBee that you want to connect to. The entry should say "Connect to BLEBee <address>". Click on the entry to connect. Now a blue LED on the BLEBee-board in your bGeigie nano should be on.

3. Swipe to back until "Show radiation data (connect first)". A click starts a "LiveCard" which updates every 5 seconds with data received from bGeigie nano. You can swipe forward to go to your normal timeline. The LiveCard will remain open, swiping backward brings you back.

4. To close the LiveCard: Go to LiveCard, tap once. A menu with "stop" opens, tap another time. The app should end the bluetooth connection and close.


### Development

The code currently is not very neat and clean, but it works. Any improvements are welcome!