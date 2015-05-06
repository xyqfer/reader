Phonegap-Android-VolumeControl
==============================

Manage the music volume to your app.  Create a slider bar or control buttons.  Cordova / PhoneGap plugin.

Installation:
-------------
1. Install using Cordova CLI:
    `cordova plugin add git+https://github.com/manusimpson/Phonegap-Android-VolumeControl.git`

__Example of usage (Don't forget include the rest of necessary .js libs such as cordova,jquery and css, etc):__

  	<!DOCTYPE html>
    <html>
      <head>
        <script type="text/javascript" charset="utf-8" src="cordova-X.X.X.js"></script>
        <script type="text/javascript" charset="utf-8" src="jquery.js"></script>
        <script type="text/javascript" charset="utf-8" src="VolumeControl.js"></script>
        <script type="text/javascript" charset="utf-8">
          //Set volume to 95 when click button
          $('#volUp').bind('click',function(){
             VolumeControl.setVolume(95, onVolSuccess, onVolError);
          });
          //Set volume to 25 when click button
          $('#volDown').bind('click',function(){
              VolumeControl.setVolume(25, onVolSuccess, onVolError);
          });
          //Get current volume
          $('#currVol').bind('click',function(){
              VolumeControl.getVolume(getVolSuccess, getVolError);
          });
          //Callbacks
          function onVolSuccess(){
              console.log("Volume changed");
          }
          function onVolError(){
              //Manage Error
          }
          function getVolSuccess(r){
              alert(r);
          }
          function getVolError(){
              //Manage Error
          }
        </script>
      </head>
      <body>
        <input type="button" id="volUp" value="Volume up"/>
        <input type="button" id="volDown" value="Volume down"/>

        <input type="button" id="currVol" value="Get current Volume"/>
      </body>
    </html>
LICENSE
-------
Copyright (c) 2012 AVANTIC ESTUDIO DE INGENIEROS

The MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
