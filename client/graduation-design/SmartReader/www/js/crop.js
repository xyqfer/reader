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
                ["取消", "去设置"]
            );

            function onConfirm(buttonIndex) {
                if (buttonIndex == 2) {
                    LocationAndSettings.switchToWifiSettings();
                }
            }
        } else {
            window.plugins.spinnerDialog.show(null, "处理中...", true);

            var dataURL = $("#container #crop").cropper("getCroppedCanvas").toDataURL();
            var options = new FileUploadOptions();
            options.fileKey = "ocrImage";
            options.chunkedMode = false;

            var uri = encodeURI("http://dev.paper-reader.avosapps.com/upload");
            var ft = new FileTransfer();

            ft.upload(dataURL, uri, function (r) {
                window.plugins.spinnerDialog.hide();
                var text = r.response;
                localStorage.setItem("text", text);
                location.href = "speak.html";
            }, function (error) {
                //todo
                alert("error " + error);
            }, options);
        }
    });
}, false);
