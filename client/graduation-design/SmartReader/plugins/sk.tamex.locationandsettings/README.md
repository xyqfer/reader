# Location and Settings #
## Adding the Plugin to your project ##
&lt;script type="text/javascript" charset="utf-8" src="LocationAndSettings.js"&gt;&lt;/script&gt;
<pre>
function switchToLocationSettings() {
    var telephoneNumber = cordova.require("cordova/plugin/telephonenumber");
    telephoneNumber.switchToLocationSettings(function(result) {
      }, function() {
          alert("error");
      });
}
function switchToWifiSettings() {
    var telephoneNumber = cordova.require("cordova/plugin/telephonenumber");
    telephoneNumber.switchToWifiSettings(function(result) {
      }, function() {
          alert("error");
      });
}
function isGpsEnabled() {
    var telephoneNumber = cordova.require("cordova/plugin/telephonenumber");
    telephoneNumber.isGpsEnabled(function(result) {
          if (result == true) { alert("GPS location ENABLED"); }
          else { alert("GPS location DISABLED"); }
      }, function() {
          alert("error");
      });
}
function isWirelessNetworkLocationEnabled() {
    var telephoneNumber = cordova.require("cordova/plugin/telephonenumber");
    telephoneNumber.isWirelessNetworkLocationEnabled(function(result) {
          if (result == true) { alert("WIFI/GSM location ENABLED"); }
          else { alert("WIFI/GSM location DISABLED"); }
      }, function() {
          alert("error");
      });
}
function switchToWirelessSettings() {
    var telephoneNumber = cordova.require("cordova/plugin/telephonenumber");
    telephoneNumber.switchToWirelessSettings(function(result) {
      }, function() {
          alert("error");
      });
}
</pre>
## LICENSE ##

This plugin is available under the MIT License (2008). 
The text of the MIT license is reproduced below. 

---

### The MIT License

Copyright (c) <2014> <Filip Lazistan >

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 