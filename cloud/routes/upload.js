module.exports = function(req, res) {

	var appId = 'paper-reader';
	// Password should be sent to your e-mail after application was created
	var password = 'XrmcliEauxX4TIM4LwDrIf90';

	var imagePath = __dirname + '/routes/QQ20141128-3.png';
	var outputPath = __dirname + '/cloud/routes/result1.txt';

	var fs = require('fs');

	var iconFile = req.files.file;
	  if(iconFile) {
	    fs.readFile(iconFile.path, function(err, data){
	      if(err) {
	        	return res.send("读取文件失败");
	        }

	      var base64Data = data.toString('base64');
	      var data2 = data;
	      // var theFile = new AV.File(iconFile.name, {base64: base64Data});
	      // theFile.save().then(function(theFile){
	      // 	console.log(theFile);
	      //   res.send("上传成功！");
	      // });


		    try {
		    	console.log("ABBYY Cloud OCR SDK Sample for Node.js");

		    	var ocrsdkModule = require('cloud/routes/ocrsdk.js');
		    	var ocrsdk = ocrsdkModule.create(appId, password);
		    	ocrsdk.serverUrl = "http://cloud.ocrsdk.com"; // change to https for secure connection

		    	if (appId.length == 0 || password.length == 0) {
		    		throw new Error("Please provide your application id and password!");
		    	}
		    	
		    	if( imagePath == 'myFile.jpg') {
		    		throw new Error( "Please provide path to your image!")
		    	}

		    	function downloadCompleted(error, text) {
		    		if (error) {
		    			console.log("Error: " + error.message);
		    			return;
		    		}
		    		console.log("Done.");

		    		// TODO

		    		// var fs = require('fs');
		    		// var text = fs.readFileSync(outputPath, 'utf-8');
		    		res.send(text);
		    	}

		    	function processingCompleted(error, taskData) {
		    		if (error) {
		    			console.log("Error: " + error.message);
		    			return;
		    		}

		    		if (taskData.status != 'Completed') {
		    			console.log("Error processing the task.");
		    			if (taskData.error) {
		    				console.log("Message: " + taskData.error);
		    			}
		    			return;
		    		}

		    		console.log("Processing completed.");
		    		console.log("Downloading result to " + outputPath);

		    		ocrsdk
		    				.downloadResult(taskData.resultUrl.toString(), outputPath,
		    						downloadCompleted);
		    	}

		    	function uploadCompleted(error, taskData) {
		    		if (error) {
		    			console.log("Error: " + error.message);
		    			return;
		    		}

		    		console.log("Upload completed.");
		    		console.log("Task id = " + taskData.id + ", status is " + taskData.status);
		    		if (!ocrsdk.isTaskActive(taskData)) {
		    			console.log("Unexpected task status " + taskData.status);
		    			return;
		    		}

		    		ocrsdk.waitForCompletion(taskData.id, processingCompleted);
		    	}

		    	var settings = new ocrsdkModule.ProcessingSettings();
		    	// Set your own recognition language and output format here
		    	settings.language = "ChinesePRC"; // Can be comma-separated list, e.g. "German,French".
		    	settings.exportFormat = "txt"; // All possible values are listed in 'exportFormat' parameter description 
		                                       // at http://ocrsdk.com/documentation/apireference/processImage/

		    	console.log("Uploading image..");
		    	ocrsdk.processImage(data2, settings, uploadCompleted);

		    } catch (err) {
		    	console.log("Error: " + err.message);
		    }
	    });
	  } else {
	    res.send("请选择一个文件。");
	  }

	// try {
	// 	console.log("ABBYY Cloud OCR SDK Sample for Node.js");

	// 	var ocrsdkModule = require('cloud/routes/ocrsdk.js');
	// 	var ocrsdk = ocrsdkModule.create(appId, password);
	// 	ocrsdk.serverUrl = "http://cloud.ocrsdk.com"; // change to https for secure connection

	// 	if (appId.length == 0 || password.length == 0) {
	// 		throw new Error("Please provide your application id and password!");
	// 	}
		
	// 	if( imagePath == 'myFile.jpg') {
	// 		throw new Error( "Please provide path to your image!")
	// 	}

	// 	function downloadCompleted(error, text) {
	// 		if (error) {
	// 			console.log("Error: " + error.message);
	// 			return;
	// 		}
	// 		console.log("Done.");

	// 		// TODO

	// 		// var fs = require('fs');
	// 		// var text = fs.readFileSync(outputPath, 'utf-8');
	// 		res.send(text);
	// 	}

	// 	function processingCompleted(error, taskData) {
	// 		if (error) {
	// 			console.log("Error: " + error.message);
	// 			return;
	// 		}

	// 		if (taskData.status != 'Completed') {
	// 			console.log("Error processing the task.");
	// 			if (taskData.error) {
	// 				console.log("Message: " + taskData.error);
	// 			}
	// 			return;
	// 		}

	// 		console.log("Processing completed.");
	// 		console.log("Downloading result to " + outputPath);

	// 		ocrsdk
	// 				.downloadResult(taskData.resultUrl.toString(), outputPath,
	// 						downloadCompleted);
	// 	}

	// 	function uploadCompleted(error, taskData) {
	// 		if (error) {
	// 			console.log("Error: " + error.message);
	// 			return;
	// 		}

	// 		console.log("Upload completed.");
	// 		console.log("Task id = " + taskData.id + ", status is " + taskData.status);
	// 		if (!ocrsdk.isTaskActive(taskData)) {
	// 			console.log("Unexpected task status " + taskData.status);
	// 			return;
	// 		}

	// 		ocrsdk.waitForCompletion(taskData.id, processingCompleted);
	// 	}

	// 	var settings = new ocrsdkModule.ProcessingSettings();
	// 	// Set your own recognition language and output format here
	// 	settings.language = "ChinesePRC"; // Can be comma-separated list, e.g. "German,French".
	// 	settings.exportFormat = "txt"; // All possible values are listed in 'exportFormat' parameter description 
	//                                    // at http://ocrsdk.com/documentation/apireference/processImage/

	// 	console.log("Uploading image..");
	// 	ocrsdk.processImage(imagePath, settings, uploadCompleted);

	// } catch (err) {
	// 	console.log("Error: " + err.message);
	// }
};