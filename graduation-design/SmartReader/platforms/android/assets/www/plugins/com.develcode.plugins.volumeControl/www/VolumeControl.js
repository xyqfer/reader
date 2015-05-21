cordova.define("com.develcode.plugins.volumeControl.VolumeControl", function(require, exports, module) { /*
 * Phonegap VolumeControl Plugin for Android
 * Cordova 2.2.0
 * Email: manusimpson[at]gmail[dot]com
 * Author: Manuel Simpson
 * Date: 12/28/2012
 */

exports.setVolume = function(vol, successCallback, failureCallback, playSound) {
	return cordova.exec(
		successCallback,
		failureCallback,
		'VolumeControl',
		'setVolume',
		[vol, playSound]
	);
};

exports.getVolume = function (successCallback, failureCallback) {
	return cordova.exec(
		successCallback,
		failureCallback,
		'VolumeControl',
		'getVolume',
		[]);
};

});
