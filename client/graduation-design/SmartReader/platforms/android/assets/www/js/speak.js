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

    alert(text)

    VolumeControl.setVolume(50, onVolSuccess, onVolError);

    TTS.speak({
        text: text,
        locale: "zh-CN",
        rate: 0.75
    }, function () {
    });

    var elem = document.getElementById("range");
    new Powerange(elem, {
        min: 0,
        max: 100,
        start: parseInt(localStorage.getItem("volume")),
        callback: function() {
            VolumeControl.setVolume(parseInt(elem.value), onVolSuccess, onVolError);
            localStorage.setItem("volume", elem.value + "");
        }
    });

    window.onbeforeunload = function (e) {
        TTS.stop();
    };

    function onVolSuccess(){
        //alert("Volume changed");
    }
    function onVolError(){
        //Manage Error
    }

    var relativeFilePath = localStorage.getItem("imgUrl").slice(7);
    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function(fileSystem){
        fileSystem.root.getFile(relativeFilePath, {create:false}, function(fileEntry){
            fileEntry.remove(function(file){
                localStorage.setItem("imgUrl", "");
                //alert("File removed!");
            },function(){
                //alert("error deleting the file " + error.code);
            });
        },function(){
            //alert("file does not exist");
        });
    },function(evt){
        //alert(evt.target.error.code);
    });

    document.getElementById("tts").addEventListener("click", function () {
        TTS.stop();
        LocationAndSettings.switchToTTSSettings();
    }, false);

    document.getElementById("repeat").addEventListener("click", function () {
        TTS.stop();
        TTS.speak({
            text: text,
            locale: "zh-CN",
            rate: 0.75
        }, function () {
        });
    }, false);
}, false);

