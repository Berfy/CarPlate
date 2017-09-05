/**
 * Created by wlb on 2017/6/14.
 */
localStorage.clear();

var num = 1;
//$(document).ready(function(){
function picCallback(str1,str2){
    $("#text1").val(str1);
     $("#text2").val(str2);
    //alert(str2)
    content(str1,str2)
}
// content("京nrt582",23);
function content(a,b){
    $(".loadings").show();
    var licenseNo = a;
   // alert(281+a);
    localStorage.setItem('token',"Gd8s9O0lE5DzcReUEDJOkNz");
    localStorage.setItem('access_token',"5HOEgvn1o4ER2w4382FMv9");
    var renewVehicle = 0;
    var access_token = localStorage.getItem("access_token");
    var req = {
        licenseNo: licenseNo,
        region: "北京",
        renewVehicle: renewVehicle,
        access_token: "5HOEgvn1o4ER2w4382FMv9",
        token: "Gd8s9O0lE5DzcReUEDJOkNz"
    };
    localStorage.setItem("region", '北京');
    localStorage.setItem("licenseNo", licenseNo);
    $.ajax({
        type: "POST",
        url: API_ADDR + "/vehicle/addVehicle.json",
        dataType: "json",
        headers: headers,
        contentType: "application/json; charset=utf-8",
        data: genJsonRequest(req),
        success: function (data) {
           if(data.status!=0){
               $(".loadings").hide();

               alert(data.msg);
           return false;
           }
            //loading移除
            $("#num").html(num++);

            //$("#p").val("立即报价");
            //$("#p").removeAttr("disabled");
            var addVehicle = JSON.stringify(data);
            localStorage.setItem("addVehicle", addVehicle);
            if (data.status != 0) {
                errorMsg(data.msg);
                return false;
            } else {
                $("#name").html(data.owner);
                console.log(data.owner)
                localStorage.setItem("vehicleId", data.vehicleId);
                localStorage.setItem("showText", data.showText);
                localStorage.setItem("carLogoUrl", data.carLogoUrl);
                localStorage.setItem("owner", data.owner);
                localStorage.setItem("engineNo", data.engineNo);
                localStorage.setItem("vin", data.vin);
                localStorage.setItem("brandModel", data.brandModel);
                localStorage.setItem("enrollDate", data.enrollDate);
                localStorage.setItem("vehicleId", data.vehicleId);
                if(data.vin==undefined){
                    alert("无法获得发动机号");
                }
                var req = {
                    licenseNo: licenseNo,
                    owner: data.owner,
                    region: "北京",
                    engineNo: data.engineNo,
                    vin: data.vin,
                    brandModel: data.brandModel,
                    enrollDateStr: data.enrollDate,
                };
                $.ajax({
                    type: "POST",
                    url: API_ADDR + "/vehicle/add.json",
                    processData: false,
                    dataType: "json",
                    headers: headers,
                    contentType: "application/json; charset=utf-8",
                    data: genJsonRequest(req),
                    success: function (data) {
                        $("#num").html(num++)
                        //loading移除
                        $(".loadings").hide();
                        if (data.status != 0) {
                            errorMsg(data.msg)
                        } else {
                            var brandModel = localStorage.setItem("brandModel", data.brandModel);
                            var vehicleId = localStorage.setItem("vehicleId", data.vehicle_id);
                            var vehicleId = data.vehicle_id
                            $("#vehicleId").val(vehicleId);
                            var req = {
                                vehicleId:vehicleId
                            };
                            var offer_price = {};
//获取文字描述
                            $.ajax({
                                type:"post",
                                headers:headers,
                                url:API_ADDR+"/vehicle/vehicle_info.json",
                                dataType:"json",
                                async:false,
                                data:genJsonRequest(req),
                                success:function(data) {
                                    if(data.status!=0){
                                        $(".loadings").hide();

                                        alert(data.msg);
                                        return false;
                                    }
                                    $("#num").html(num++);
                                    offer_price.licenseNo = data.licenseNo;
                                    offer_price.carLogoUrl = data.carLogoUrl;
                                    offer_price.showText = data.showText;
                                }
                            });
//获取保险到期日
                            $.ajax({
                                url:API_ADDR + "/vehicle/insuranceExpiredDate.json",
                                type: "POST",
                                dataType: "json",
                                headers: headers,
                                async:false,
                                contentType: "application/json; charset=utf-8",
                                data: genJsonRequest(req),
                                success: function (data)  {
                                    $("#num").html(num++);
                                    if (data.status != 0) {
                                        $(".loadings").hide();

                                        alert(data.msg);
                                        return false;
                                    }else{
                                        var vdeadline = data.vehicleExpiredDate;
                                        var bdeadline = data.businessExpiredDate;
                                        if(vdeadline!=0){
                                            $(".A").val(getNowFormatDate(new Date(parseInt(vdeadline))));
                                            $("#Adeadline").val(getNowFormatDate(new Date(parseInt(vdeadline))))
                                        }
                                        if(bdeadline!=0){
                                            $(".B").val(getNowFormatDate(new Date(parseInt(bdeadline))));
                                            $("#Bdeadline").val(getNowFormatDate(new Date(parseInt(bdeadline))))
                                        }

                                    }
                                }
                            });
//获取预设保险方案 初始化
                            var package = {
                                vehicleId: vehicleId,
                                companyCode: ""
                            };
                            $.ajax({
                                headers: headers,
                                url: API_ADDR + "/insurance/insurancePlanPackage.json",
                                async:false,
                                type: "POST",
                                dataType: "json",
                                headers: headers,
                                contentType: "application/json; charset=utf-8",
                                data: genJsonRequest(package),
                                success: function (res) {
                                    $("#num").html(num++);

                                    if(res.status!=0){
                                        $(".loadings").hide();

                                        alert(res.msg);
                                        return false;
                                    }
                                    if(res.list.length==3){
                                        var TheOne = res.list[0];
                                    }else if(res.list.length==4){
                                        var TheOne = res.list[3];
                                    }
//                数据初步处理
                                    var primary = TheOne.primary;
                                    var additional = TheOne.additional;
                                    function addSelected(a){
                                        for(var i=0;i<a.length;i++){
                                            if(a[i].options!=undefined&&a[i].selected==1){
                                                for(var j=0;j<a[i].options.length;j++){
                                                    if(a[i].options[j].key==a[i].amount){
                                                        a[i].options[j].selected=1;
                                                        a[i].options[j].default=1;
                                                    }
                                                }
                                            }
                                        }
                                        return a;
                                    }
                                    primary= addSelected(primary);
                                    additional= addSelected(additional);
                                    var myTemplate = Handlebars.compile($("#list").html());
                                    $("#wrapper").html(myTemplate(res.list[0]));
                                    var data = $("#priceForm").serializeJSON();
                                    console.log(data);
                                    $.ajax({
                                        url: API_ADDR + "/insurance/insurancePrice.json",
                                        data: genJsonRequest(data),
                                        type: "POST",
                                        dataType: "json",
                                        headers: headers,
                                        beforeSend:function () {
                                            $(".loadings").show();
                                        },
                                        contentType: "application/json; charset=utf-8",
                                        success: function (res) {
                                            $("#num").html(num++);
                                            $(".loadings").hide();
                                            if(res.status!=0){
                                                alert(res.msg);
                                                return false;
                                            }
                                            var myTemplate = Handlebars.compile($("#priceList").html());
                                            $("#wrapper").html(myTemplate(res));
                                            $("#priceForm").show();
                                            if(res.vehicleEndDate!=0&&res.vehicleEndDate!=undefined&&res.vehicleEndDate!=null&&res.vehicleEndDate!=''){
                                                var Vdate = getNowFormatDate(new Date(res.vehicleEndDate));
                                            }
                                            if(res.businessEndDate!=0&&res.businessEndDate!=undefined&&res.businessEndDate!=null&&res.businessEndDate!=''){
                                                var Bdate = getNowFormatDate(new Date(res.businessEndDate));
                                            }
                                            console.log(Vdate);
                                            $("#Adeadline").html(Vdate);
                                            $("#Bdeadline").html(Bdate);
                                            var total = parseFloat($("#totalPremium").html());
                                            var numT = total*(100-parseFloat(b))/100;
                                            $("#rate").html(numT.toFixed(2));
                                            $("#upDate").hide();
                                        },
                                        complete:function(){
                                            $(".loadings").hide()
                                        },
                                    })
                                }
                            })
                        }
                    }

                });





            }
        }
    });
}

//    });
//    不计免赔是否支持
Handlebars.registerHelper('support', function (v) { //这边的state是等下要用在模板文件里面的
    return v==1?'show':'hidden';
});
//    初始不计免赔是否选择
Handlebars.registerHelper('checked', function (v) { //这边的state是等下要用在模板文件里面的
    return v==1?'checked':'';
});
//    初始判断是否选择保险 （无option）;有（option）使用自定义属性
Handlebars.registerHelper('selInsurance', function (selected) { //这边的state是等下要用在模板文件里面的
    return selected==1?"selected":"";
});
//    初始判断是否禁用checkbox
Handlebars.registerHelper('disable', function (v) { //这边的state是等下要用在模板文件里面的
    return v==0?"disabled":"";
});

Handlebars.registerHelper('vprice', function (v) { //这边的state是等下要用在模板文件里面的
    return v==0?"已购买":v;
});
Handlebars.registerHelper('oprice', function (v) { //这边的state是等下要用在模板文件里面的
    return v==0?"":v;
});
Handlebars.registerHelper('vamount', function (v) { //这边的state是等下要用在模板文件里面的
    return v==0?"已承保":v;
});
Handlebars.registerHelper('exemptP', function (v) { //这边的state是等下要用在模板文件里面的
    return v==0?"":v;
});
Handlebars.registerHelper('Iamount', function (v,selected) { //这边的state是等下要用在模板文件里面的
    if(selected==1){
        if(v==1){
            return "国产";
        }else if(v==2){
            return "进口";
        }else if(v==0){
            return "";
        }
        else{
            return v;
        }
    }else {
        return "";
    }

    });
