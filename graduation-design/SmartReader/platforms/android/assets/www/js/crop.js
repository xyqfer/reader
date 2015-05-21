/**
 * Created by xingyongqiang on 15-5-5.
 */

document.addEventListener("deviceready", function () {
    $("#crop").attr("src", localStorage.getItem("imgUrl"))
        .show()
        .cropper({
            autoCropArea: 0.85
        });

    $("#cancel").on("click", function () {
        location.href = "index.html";
    });

    $("#submit").on("click", function () {
        var networkState = navigator.connection.type;

        if (networkState == "none") {
            navigator.notification.confirm(
                "检测到您已经断开网络，是否马上打开？",
                onConfirm,
                "未联网",
                ["取消", "去打开"]
            );

            function onConfirm(buttonIndex) {
                if (buttonIndex == 2) {
                    LocationAndSettings.switchToWifiSettings();
                }
            }
        } else {
            var ft = new FileTransfer();
            ActivityIndicator.show("处理中...");
            document.addEventListener("backbutton", cancelOcr, false);


            var dataURL = $("#container #crop").cropper("getCroppedCanvas").toDataURL();
            var options = new FileUploadOptions();
            options.fileKey = "ocrImage";
            options.chunkedMode = false;

            var uri = encodeURI("http://dev.paper-reader.avosapps.com/upload");

            ft.upload(dataURL, uri, function (r) {
                document.removeEventListener("backbutton", cancelOcr, false);
                ActivityIndicator.hide();
                var text = r.response;
                localStorage.setItem("text", text);
                location.href = "speak.html";
            }, function (error) {
                //todo
                //alert("error " + error);
            }, options);

            function cancelOcr() {
                document.removeEventListener("backbutton", cancelOcr, false);
                ActivityIndicator.hide();
                ft.abort();
                location.href = "index.html";
            }
        }
    });
}, false);
