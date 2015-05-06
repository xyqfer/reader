document.addEventListener("deviceready", function () {
    if (localStorage.getItem("news") == null) {
        localStorage.setItem("news", JSON.stringify([]));
        localStorage.setItem("current", "0");
    }

    var takePhoto = document.getElementById("takePhoto");
    var browserNews = document.getElementById("browserNews");

    takePhoto.addEventListener("click", function () {
        //location.href = "speak.html";

        navigator.camera.getPicture(onSuccess, onFail, {
            quality: 100,
            destinationType: Camera.DestinationType.FILE_URI
        });

        function onSuccess(imageURI) {
            localStorage.setItem("imgUrl", imageURI);
            location.href = "crop.html";
        }

        function onFail(error) {}
    }, false);

    browserNews.addEventListener("click", function () {
        location.href = "news.html";
    }, false);
}, false);