/**
 * Created by hanxue on 2016/11/15.
 */
(function (a) {
    console.log()
    var m = {
        defaults: {
            dateOrder: "Mddyy",
            mode: "scroller",
            rows: 3,
            width: 8,
            height: 3.65,
            showLabel: !1,
            useShortLabels: !0
        }
    };
    a.mobiscroll.themes["android-ics"] = m;
    a.mobiscroll.themes["android-ics light"] = m
})(jQuery);