/**
 * Created by hanxue on 2016/11/30.
 */
//获取seqno
var seqNo = getQueryStr("seqNo");
$("#seqNo").val(seqNo);
//获取vehicleId
var vehicleId = getQueryStr("vehicleId");
$("#vehicleId").val(vehicleId);
//获取token
var token = localStorage.getItem("token");
var access_token = localStorage.getItem("access_token");
//调用APP时间控件
$(".checkStatus_date").click(function () {
    var tag = $(this).attr("id");
    window.location.href = 'wlb://openUI/timeSpinner?time=&tag=' + tag;
})
$("#ownerCheck_insurerInfo").hide();
//投保人、被保险人、车主是否同一人
$("input[name='all']").change(function () {
    var allValue = $("input[name='all']:checked").val();
    $("#insurerInfoBox").show();
    if (allValue == "是") {
        $("#policyCheck_boxTwo").hide();
        $("#insurantInfoBox").hide();
        $("#ownerInfoBox").hide();
        $(".ownerCheck").hide();
    } else {
        $("#policyCheck_boxTwo").show();
        $("#insurantInfoBox").show();
        $("#ownerInfoBox").show();
        $(".ownerCheck").show();
        $("#policyCheck_twoun").prop("checked", true);
    }
})

//投保人、被保险人是否同一人
$("input[name='two']").change(function () {
    var allValue = $("input[name='two']:checked").val();
    if (allValue == "是") {
        $("#insurantInfoBox").hide();
        $(".ownerCheck").hide();
        $(".ownerCheck input").prop("checked", false);
    } else {
        $("#insurantInfoBox").show();
        $("#ownerInfoBox").show();
        $(".ownerCheck").show();
    }
})

//投保人与车主是同一人
$("#insurerInfoCheck").change(function () {
    var val = $(this).prop("checked");
    if (val == true) {
        $("#ownerInfoBox").hide();
        $("#ownerCheck_insurantInfo").hide();
    } else {
        $("#ownerInfoBox").show();
        $("#ownerCheck_insurantInfo").show();
    }
})

//被保险人与车主是同一人
$("#insurantInfoCheck").change(function () {
    var val = $(this).prop("checked");
    if (val == true) {
        $("#ownerInfoBox").hide();
        $("#ownerCheck_insurerInfo").hide();
    } else {
        $("#ownerInfoBox").show();
        $("#ownerCheck_insurerInfo").show();
    }

})
$(document).ready(function () {
    $("#policyCheck").validate({
        errorElement: 'i',
        errorClass: 'helpLine',
        showErrors: function (errorMap, errorList) {
            this.defaultShowErrors();
        },
        rules: {
            "insurerInfo[userName]": {
                required: true,
                isName: true
            },
            "insurerInfo[identityCard]": {
                required: true,
                isId: true
            },
            "insurerInfo[startDate]": {
                required: true,
            },
            "insurerInfo[endDate]": {
                required: true,
                compareDate: "#insurer_startDate"
            },
            "insurerInfo[phoneNO]": {
                required: true,
                isMobile: true
            },
            "insurantInfo[userName]": {
                required: true,
                isName: true
            },
            "insurantInfo[identityCard]": {
                required: true,
                isId: true
            },
            "insurantInfo[startDate]": {
                required: true,
            },
            "insurantInfo[endDate]": {
                required: true,
                compareDate: "#insurant_startDate"
            },
            "insurantInfo[phoneNO]": {
                required: true,
                isMobile: true
            },
            "ownerInfo[userName]": {
                required: true,
                isName: true
            },
            "ownerInfo[identityCard]": {
                //required: true,
                isId: true
            },
            //"ownerInfo[startDate]": {
            //    //required: true,
            //},
            "ownerInfo[endDate]": {
                //required: true,
                compareDate: "#owner_startDate"
            },
            "ownerInfo[phoneNO]": {
                required: true,
                isMobile: true
            },
            /*"addressInfo[address]": {
             required: true,
             },*/
            "recipient[name]": {
                required: true,
                isName: true
            },
            "recipient[phone]": {
                required: true,
                isMobile: true
            },
            "email": {
                required: true,
                email: true
            }
        },
        messages: {
            "insurerInfo[userName]": {
                required: "请输入投保人姓名",
                isName: "请输入正确的投保人姓名"
            },
            "insurerInfo[identityCard]": {
                required: "请输入投保人身份证号",
                isId: "请输入正确的投保人身份证号"
            },
            "insurerInfo[startDate]": {
                required: "初始日期",
            },
            "insurerInfo[endDate]": {
                required: "截止日期",
                compareDate: "截止日期无效"
            },
            "insurerInfo[phoneNO]": {
                required: "请输入投保人手机号",
                isMobile: "请输入正确的投保人手机号"
            },
            "insurantInfo[userName]": {
                required: "请输入被保人姓名",
                isName: "请输入正确的被保人姓名"
            },
            "insurantInfo[identityCard]": {
                required: "请输入被保人身份证号",
                isId: "请输入正确的被保人身份证号"
            },
            "insurantInfo[startDate]": {
                required: "初始日期",
            },
            "insurantInfo[endDate]": {
                required: "截止日期",
                compareDate: "截止日期无效"
            },
            "insurantInfo[phoneNO]": {
                required: "请输入被保人手机号",
                isMobile: "请输入正确的被保人手机号"
            },
            "ownerInfo[userName]": {
                required: "请输入车主姓名",
                isName: "请输入正确的车主姓名"
            },
            "ownerInfo[identityCard]": {
                //required: "请输入车主身份证号",
                isId: "请输入正确的车主姓名"
            },
            "ownerInfo[phoneNO]": {
                required: "请输入车主手机号",
                isMobile: "请输入正确的车主手机号"
            },
            //"ownerInfo[startDate]": {
            //    //required: "初始日期",
            //},
            "ownerInfo[endDate]": {
                //required: "截止日期",
                compareDate: "截止日期无效"
            },
            /* "addressInfo[address]": {
             required: "请输入配送地址",
             },*/
            "recipient[name]": {
                required: "请输入接收人姓名",
                isName: "请输入正确的接收人姓名"
            },
            "recipient[phone]": {
                required: "请输入接收人电话",
                isMobile: "请输入正确的接收人电话"
            },
            "email": {
                required: "请输入邮箱地址",
                email: "请输入正确的邮箱地址"
            }
        },
        submitHandler: function (form) {
            //上传身份证
            var img_url = [];
            $("#idBox img").each(function (i) {
                if ($(this).attr("src") != '') {
                    img_url[i] = $(this).attr("src");
                }
            })
            if (img_url.length != 2) {
                errorMsg("请完整填入选项");
                return false;
            }
            var data = $("#policyCheck").serializeJSON();
            //投保人、被保险人、车主是否同一人
            if (data.insurantInfo.userName == "") {
                data.insurantInfo.userName = data.insurerInfo.userName;
                data.insurantInfo.phoneNO = data.insurerInfo.phoneNO;
                data.insurantInfo.identityCard = data.insurerInfo.identityCard;
                data.insurantInfo.endDate = data.insurerInfo.endDate;
                data.insurantInfo.startDate = data.insurerInfo.startDate;
            }
            if (data.ownerInfo.userName == "") {
                data.ownerInfo.userName = data.insurerInfo.userName;
                data.ownerInfo.phoneNO = data.insurerInfo.phoneNO;
                data.ownerInfo.identityCard = data.insurantInfo.identityCard;
                data.ownerInfo.endDate = data.insurerInfo.endDate;
                data.ownerInfo.startDate = data.insurerInfo.startDate;
            }
            data.insurantImgUrl = img_url;
            data.token = token;
            data.access_token = access_token;
            console.log(data);
            $.ajax({
                type: "POST",
                dataType: "json",
                headers: headers,
                contentType: "application/json; charset=utf-8",
                data: genJsonRequest(data),
                url: API_ADDR + "/insurance/insuranceUnderwrite.json",
                success: function (data) {
                    if (data.status != 0) {
                        errorMsg(data.msg);
                        return false;
                    } else {
                        $("#submitBox input").attr("disabled",true)
                        window.location.href = "wlb://orderDetail?orderId=" + data.policyId + "&bindPhoneFlag=1&closePage=1"
                    }
                }
            });
        }

    })
})
//上传被保人身份证
//判断是否是安卓APP
if (_IsAndroid()==true) {
    //上传被保人身份证隐藏file
    $(".idUpload input[type=file]").attr("id", "");
    //身份证识别隐藏file
    $(".uploadId_box input[type=file]").attr("id", "");
    //安卓APP触发回调函数
    $(".idUpload label").bind("click", function () {
        window.location.href = "wlb://uploadFile?callback=idUpload_android&box=" + $(this).parent().attr("id");
        return false;
    });
    //安卓APP触发回调函数
    $(".uploadId_box label").bind("click", function () {
        window.location.href = "wlb://uploadFile?callback=idUpload_android&box=" + $(this).parent().attr("id");
        return false;
    });
}

function idUpload_android(imgUrl, box) {
    $("#" + box).find("img").attr("src", imgUrl);
}
//非安卓APP状态上传身份证
$(".idUpload input").bind("change", function () {
    var box = $(this);
    //base64并压缩
    lrz(this.files[0], {width: 400}, function (results) {
        //解析成URL
        demo_report(box.next(), results.base64, results.base64.length * 0.8);
    });
});
//投保人信息列表
$(".addPerson").click(function () {
    $(".personBox").show();
    var ids = $(this).attr("id");
    var id = ids.substring(0, ids.length - 6);
    $(".personList").attr("id", id + "Box");
    var keyword = "";
    var lastId = 0;
    //分页
    personInfo(keyword, lastId);
})
$("#personMore").click(function () {
    var last_ids = $(".personList form:last").attr("id");
    var lastId = parseInt(last_ids.substring(6, last_ids.length));
    var keyword = $("#personSearch_text").val();
    personInfo(keyword, lastId);
})
//投保人信息赋值
$(".personList").on("click", "form", function () {
    var boxes = $(this).parent().attr("id");
    var box = boxes.substring(0, boxes.length - 3);
    var form = $(this).attr("id");
    var personInfo = $("#" + form).serializeJSON();
    for (var i in personInfo) {
        $("#" + box + "_" + i).val(personInfo[i]);
    }
    $(".personBox").hide();
    $(".personList").empty();
})
//搜索
$("#personSearch_btn").click(function () {
    var keyword = $("#personSearch_text").val();
    personInfo_search(keyword, 0);
})
//关闭列表
$(".checkPerson_close").click(function () {
    $(".personBox").hide();
    $(".personList").empty();
})
//识别身份证
$(".checkId").click(function () {
    var ids = $(this).attr("id");
    var id = ids.substring(0, ids.length - 4);
    $(".idCard").attr("id", id);
    $(".idCard").show();
})
$("#idCancel").click(function () {
    $(".idCard").hide();
})


$(".uploadId_box input").bind("change", function () {
    var box = $(this);
    //base64并压缩
    lrz(this.files[0], {width: 400}, function (results) {
        //解析成URL
        //demo_report(box.next(), results.base64, results.base64.length * 0.8);
        var boxs = box.next();
        console.log(boxs);
        var img = new Image();
        img.src = results.base64;
        boxs.find("img").attr("src", img.src);
        var src = img.src.replace(/data:image\/jpeg;base64,/, '');
    });
});
$("#idImg").click(function () {

    var api_body;
    var faceImage = $("#idFront_imgs").attr("src").replace(/data:image\/jpeg;base64,/, '');
    var backImage = $("#idBack_imgs").attr("src").replace(/data:image\/jpeg;base64,/, '');
    if (faceImage == "" || backImage == "") {
        errorMsg("请上传身份证正反两面");
        return false
    }
    $(".loadings").show();
    var ocrIdcard="";
    //判断是否是安卓环境
    if (_IsAndroid()==true) {
        api_body = {
            "faceImgUrl": faceImage,
            "backImgUrl": backImage,
        };
        ocrIdcard="/common/ocrIdCardPub.json";
    }else{
        api_body = {
            "faceImage": faceImage,
            "backImage": backImage,
        };
        ocrIdcard="/common/ocrIdcard.json";
    }
    $.ajax({
        type: "POST",
        url: API_ADDR + ocrIdcard,
        crossDomain: true,
        headers: headers,
        type: 'POST',
        data: JSON.stringify(api_body),
        success: function (data) {
            $(".loadings").hide();
            var data = JSON.parse(data);
            console.log(data.status);
            if (data.status != 0) {
                errorMsg(data.msg)
            } else {
                console.log(data);
                var boxs = $(".idCard").attr("id");
                $("#" + boxs + "_userName").val(data.name);
                $("#" + boxs + "_identityCard").val(data.idCard);
                $("#" + boxs + "_startDate").val(data.startDate);
                $("#" + boxs + "_endDate").val(data.endDate);
                if ($(".idCard").attr("id") == "insurant") {
                    $("#idFront_img").attr("src", data.faceImgUrl);
                    $("#idBack_img").attr("src", data.backImgUrl);
                }
                $(".idCard").hide();
            }
        },
        error: function (res) {
        },
        complete: function () {
        }
    })
})