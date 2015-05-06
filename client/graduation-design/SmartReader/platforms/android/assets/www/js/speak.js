/**
 * Created by xingyongqiang on 15-5-5.
 */

document.addEventListener("deviceready", function () {
    var text;
    var title;

    if (location.search == "") {
        text = localStorage.getItem("text");

        var obj = {};
        var current = parseInt(localStorage.getItem("current")) + 1;
        title = text.split("\n")[0];
        var content = text.split("\n").slice(1).join("");

        obj.id = current;
        obj.title = title;
        obj.content = content;
        var newsList = JSON.parse(localStorage.getItem("news"));
        newsList.push(obj);
        localStorage.setItem("news", JSON.stringify(newsList));
        localStorage.setItem("current", current);
    } else {
        var id = location.search.slice(1).split("=")[1];
        var newsList = JSON.parse(localStorage.getItem("news"));

        for (var i = 0, length = newsList.length; i < length; i++) {
            if (newsList[i].id == id) {
                text = newsList[i].content;
                title = newsList[i].title;
                break;
            }
        }
    }

    document.getElementById("speaking-title").innerHTML = title ? title : "";

    //alert(text.split("\n")[0]);
    alert(text)
    TTS.speak({
        text: text,
        locale: "zh-CN",
        rate: 0.75
    }, function () {
    });
    //alert(localStorage.getItem("text"))

    window.onbeforeunload = function (e) {
        TTS.stop();
    };
}, false);