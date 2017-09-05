/**
 * Created by hanxue on 2016/11/17.
 */
//获取地址信息
var LocString = String(window.document.location.href);
function getQueryStr(str) {
    var rs = new RegExp("(^|)" + str + "=([^&]*)(&|$)", "gi").exec(LocString), tmp;
    if (tmp = rs) {
        return tmp[2];
    }
    return "";
}
//loadingLittle
function loadingLittle() {
    var loadingLittle = '<div id="loadingLittle" class="maskT"><div class="loadings"></div></div>'
    $("body").append(loadingLittle);
}
//loadingLittle delet
function deletLoadingLittle() {
    $("#loadingLittle").remove();
}
//APP时间控件回调函数
function timeRsult(time, tag) {
    var dateTime = getNowFormatDate(new Date(parseInt(time)));
    var obj = "#" + tag;
    $(obj).val(dateTime);
};

//errorMsg
function errorMsg(errorMsg) {
    $(".errorMsg").fadeIn().children().html(errorMsg);
    setTimeout(function () {
        $(".errorMsg").fadeOut();
    }, 5000)
}

//选择已有联系人
window.personInfo = function (keyword, lastId) {
    $(".loadings").show();
    var req = {
        keyword: keyword,
        last_id: lastId,
        page_count: 10,

    }
    $.ajax({
        type: "GET",
        url: API_ADDR + "/insurance/personInfo.json",
        data: req,
        headers: headers,
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            $(".loadings").hide();
            var data = JSON.parse(data);
            if (data.status != 0) {
                errorMsg(data.msg);
            } else {
                var html = "";
                var list = data.list;
                var listLenght = list.length;
                for (var i = 0; i < listLenght; i++) {
                    html += '<form id="person' + list[i].id + '"><dl><dd><span>' +
                        '<input type="text" value="' + list[i].userName + '" name="userName" id="userName" readonly>' +
                        '<input type="hidden" id="startDate' + list[i].id + '" name="startDate" value="' + list[i].startDate + '">' +
                        '<input type="hidden" id="endDate' + list[i].id + '" name="endDate" value="' + list[i].endDate + '"></span>'
                        + '<em><input type="text" value="' + list[i].phoneNO + '" name="phoneNO" id="phoneNO' + list[i].id + '" readonly></em></dd>'
                        + '<dt><input type="text" value="' + list[i].identityCard + '" name="identityCard" id="identityCard' + list[i].id + '" readonly></dt></dl></form>'
                }
                if (listLenght < 10) {
                    $("#personMore").hide();
                } else {
                    $("#personMore").show();
                }
                $(".personList").append(html)
            }
        }

    })
}

//选择已有联系人搜索
window.personInfo_search = function (keyword, lastId) {
    $(".loadings").show();
    var req = {
        keyword: keyword,
        last_id: lastId,
        page_count: 10,

    }
    $.ajax({
        type: "GET",
        url: API_ADDR + "/insurance/personInfo.json",
        data: req,
        headers: headers,
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            $(".loadings").hide();
            var data = JSON.parse(data);
            if (data.status != 0) {
                errorMsg(data.msg);
            } else {
                var html = "";
                var list = data.list;
                var listLenght = list.length;
                for (var i = 0; i < listLenght; i++) {
                    html += '<form id="person' + list[i].id + '"><dl><dd><span>' +
                        '<input type="text" value="' + list[i].userName + '" name="userName" id="userName" readonly>' +
                        '<input type="hidden" id="startDate' + list[i].id + '" name="startDate" value="' + list[i].startDate + '">' +
                        '<input type="hidden" id="endDate' + list[i].id + '" name="endDate" value="' + list[i].endDate + '"></span>'
                        + '<em><input type="text" value="' + list[i].phoneNO + '" name="phoneNO" id="phoneNO' + list[i].id + '" readonly></em></dd>'
                        + '<dt><input type="text" value="' + list[i].identityCard + '" name="identityCard" id="identityCard' + list[i].id + '" readonly></dt></dl></form>'
                }
                if (listLenght < 10) {
                    $("#personMore").hide();
                } else {
                    $("#personMore").show();
                }
                $(".personList").html(html)
            }
        }

    })
}

//微信配置
function weiconfig() {
    var url = location.href.split('#')[0];
    var token = getQueryStr("token");
    var uv = {
        appid: "wx81dcd38d5100641f",
        url: url,
        token: token,
        access_token: token,
    };
    $.ajax({
        headers: {
            platform: "wlb_html",
            "Wlb-Token": token,
        },
        dataType: "JSON",
        type: "POST",
        url: API_ADDR + "/getConfigParam.json",
        data: genJsonRequest_token(uv),
        success: function (data) {
            if (data.status != 0) {
                errorMsg(data.msg);
            }else{
                console.log(data);
                var timestamp = data.timestamp;
                var nonceStr = data.noncestr;
                var signature = data.signature;
                wx.config({
                    debug: false,
                    appId: 'wx81dcd38d5100641f',
                    timestamp: timestamp,
                    nonceStr: nonceStr,
                    signature: signature,
                    jsApiList: [
                        'checkJsApi',
                        'onMenuShareTimeline',
                        'onMenuShareAppMessage',
                        'onMenuShareQQ',
                        'onMenuShareWeibo',
                        'hideMenuItems',
                        'showMenuItems',
                        'hideAllNonBaseMenuItem',
                        'showAllNonBaseMenuItem',
                        'translateVoice',
                        'startRecord',
                        'stopRecord',
                        'onRecordEnd',
                        'playVoice',
                        'pauseVoice',
                        'stopVoice',
                        'uploadVoice',
                        'downloadVoice',
                        'chooseImage',
                        'previewImage',
                        'uploadImage',
                        'downloadImage',
                        'getNetworkType',
                        'openLocation',
                        'getLocation',
                        'hideOptionMenu',
                        'showOptionMenu',
                        'closeWindow',
                        'scanQRCode',
                        'chooseWXPay',
                        'openProductSpecificView',
                        'addCard',
                        'chooseCard',
                        'openCard'
                    ]
                });
            }

        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.status);
            alert(XMLHttpRequest.readyState);
            alert(textStatus);
        },
    })
}
function getNowFormatDate(time) {
    var day = time;
    var Year = 0;
    var Month = 0;
    var Day = 0;
    var CurrentDate = "";
//初始化时间
//Year= day.getYear();//有火狐下2008年显示108的bug
    Year = day.getFullYear();//ie火狐下都可以
    Month = day.getMonth() + 1;
    Day = day.getDate();
    CurrentDate += Year + "-";
    if (Month >= 10) {
        CurrentDate += Month + "-";
    }
    else {
        CurrentDate += "0" + Month + "-";
    }
    if (Day >= 10) {
        CurrentDate += Day;
    }
    else {
        CurrentDate += "0" + Day;
    }
    return CurrentDate;
}
window.getTime=function (time){
    return new Date(time.replace(/-/g,'/')).getTime()
}

//是否为IOS
function _IsIOS() {
    var ua = navigator.userAgent.toLowerCase();
    if (ua.match(/iPhone\sOS/i) == "iphone os") {
        return true;
    } else {
        return false;
    }
}
//是否为安卓
function _IsAndroid() {
    var ua = navigator.userAgent.toLowerCase();
    if (ua.match(/Android/i) == "android") {
        return true;
    } else {
        return false;
    }
}

//判断请求是否超时
$.ajaxSetup({
    timeout:30000,
    complete:function(xhr) {
        if (JSON.parse(xhr.responseText).status == 1010) {
            if(token==null||token==undefined||token==""||token=="null"){
                window.location.href="wlb://user/regist?closePage=1"
            }
            var tokenStr = '{"token":"' + token + '"}';
            var url = "wlb://encrypt/des?data=" + encodeURI(tokenStr);
            window.location.href = url;

        }
    },
})