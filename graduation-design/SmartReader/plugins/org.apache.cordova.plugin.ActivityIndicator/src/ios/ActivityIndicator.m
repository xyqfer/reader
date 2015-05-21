#import <Cordova/CDV.h>
#import "ActivityIndicator.h"
#import "MBProgressHUD.h"

@implementation ActivityIndicator
@synthesize activityIndicator;

/**
 * This show the ProgressDialog
 */
- (void)show:(CDVInvokedUrlCommand*)command
{
	NSString* text = [command.arguments objectAtIndex:0];
	self.activityIndicator = nil;
	self.activityIndicator = [MBProgressHUD showHUDAddedTo:self.webView.superview animated:YES];
	self.activityIndicator.mode = MBProgressHUDModeIndeterminate;
    self.activityIndicator.labelText = text;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/**
 * This hide the ProgressDialog
 */
- (void)hide:(CDVInvokedUrlCommand*)command
{
	if (!self.activityIndicator) {
		CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
		[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
		return;
	}
	[self.activityIndicator hide:YES];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
