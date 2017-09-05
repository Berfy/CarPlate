/**
 * Created by Jin on 2016/12/6.
 */
require.config({
    baseUrl: "js",
    paths: {
        "zepto": "../../js/zepto",
        "touch": "../../js/touch",
        "server": "../../config/server",
        "md5": "../../js/md5",
        "wlbrequest": "../../js/wlbrequest",
        "timeFormat": "../../js/timeFormat",
        "serializejson": "../../js/jquery.serializejson.min",
        "offerabb": "offerabb"
    }
});
require(["zepto", "touch", "server", "md5", "wlbrequest", "timeFormat", "serializejson", "offerabb"], function () {
    console.log("成功加载")
});