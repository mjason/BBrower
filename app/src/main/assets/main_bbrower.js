(function() {
    document.getElementById("header").remove();
    document.getElementsByClassName("t")[0].remove();
    var link = document.body.textContent.match(/(http:\/\/www\.rmdown\.com\/link\.php\?hash=\w+)/g);
    if (link != null) {
        NativeInterface.download(link[0].split("=")[1].slice(3));
    } else {
        NativeInterface.cancel();
    }
})()