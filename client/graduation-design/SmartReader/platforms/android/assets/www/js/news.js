/**
 * Created by xingyongqiang on 15-5-5.
 */

document.addEventListener("deviceready", function () {
    var newsList = JSON.parse(localStorage.getItem("news"));
    var newsElem = document.getElementById("news");
    var template = '<li class="table-view-cell" data-id="%id%"><a class="navigate-right" href="%link%">%title%</a></li>';
    var elemList = [];

    for (var i = 0, length = newsList.length; i < length; i++) {
        var id = newsList[i].id;
        var title = newsList[i].title;
        var elemStr = template.replace("%link%", "speak.html?id=" + id)
                    .replace("%title%", title)
                    .replace("%id%", id);
        elemList.push(elemStr);
    }

    newsElem.innerHTML = elemList.join("");

    $(".table-view-cell").longTap(function () {
        var $target = $(this);
        navigator.notification.confirm(
            "真的要删除该条新闻？",
            onConfirm,
            "删除新闻",
            ["否", "是"]
        );

        function onConfirm(buttonIndex) {
            if (buttonIndex == 2) {
                var id = $target.attr("data-id");

                for (var i = 0, length = newsList.length; i < length; i++) {
                    if (id == newsList[i].id) {
                        newsList.splice(i, 1);
                        break;
                    }
                }

                localStorage.setItem("news", JSON.stringify(newsList));
                $target.remove();
            }
        }
    });
}, false);