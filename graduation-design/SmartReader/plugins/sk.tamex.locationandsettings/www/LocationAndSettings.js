// cordova.define("cordova/plugin/locationandsettings",
//   function(require, exports, module) {
//     var exec = require("cordova/exec");
//     var LocationAndSettings = function () {};

//     var LocationAndSettingsError = function(code, message) {
//         this.code = code || null;
//         this.message = message || '';
//     };

//     LocationAndSettings.prototype.isGpsEnabled = function(success,fail) {
//         exec(success,fail,"LocationAndSettings",
//             "isGpsEnabled",[]);
//     };
//     LocationAndSettings.prototype.isWirelessNetworkLocationEnabled = function(success,fail) {
//         exec(success,fail,"LocationAndSettings",
//             "isWirelessNetworkLocationEnabled",[]);
//     };
//     LocationAndSettings.prototype.switchToLocationSettings = function(success,fail) {
//         exec(success,fail,"LocationAndSettings",
//             "switchToLocationSettings",[]);
//     };
//     LocationAndSettings.prototype.switchToWifiSettings = function(success,fail) {
//         exec(success,fail,"LocationAndSettings",
//             "switchToWifiSettings",[]);
//     };
//     LocationAndSettings.prototype.switchToWirelessSettings = function(success,fail) {
//         exec(success,fail,"LocationAndSettings",
//             "switchToWirelessSettings",[]);
//     };
    

//     var locationAndSettings = new LocationAndSettings();
//     module.exports = locationAndSettings;
// });

exports.switchToWifiSettings = function() {
    cordova.exec(function() {}, function() {}, "LocationAndSettings", "switchToWifiSettings",[]);
};

exports.switchToInstallTTS = function() {
    cordova.exec(function() {}, function() {}, "LocationAndSettings", "switchToInstallTTS",[]);
};

exports.switchToTTSSettings = function(success, error) {
    cordova.exec(function() {
        success();
    }, function() {
        error();
    }, "LocationAndSettings", "switchToTTSSettings",[]);
};
