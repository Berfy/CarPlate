/**
 * Created by hanxue on 2016/11/19.
 */
//    全局定义
var space = {};
//初始化数据
var n = sessionStorage.getItem('num');
if(n==null||n==undefined||n==''){
    sessionStorage.setItem('num',1);
}
var offer_price = localStorage.getItem("offer_price");
var data_price = JSON.parse(offer_price);
//车牌号
var licenseNo = data_price.licenseNo;
$("#licenseNo").html(licenseNo);
//logo
var carLogoUrl = data_price.carLogoUrl;
$("#carLogoUrl").attr("src", carLogoUrl);
//车辆详情
var showText = data_price.showText;
$("#showText").html(showText);
//id
var vehicleId = localStorage.getItem("vehicleId");
//token
var token = localStorage.getItem("token");


//选择的保险名字
var name = data_price.insudenceName_selected;
$(".insuranceName").html(name);
//获取表单
var dataS = localStorage.getItem("dataForm");
/*模拟数据加载*/
//获取companyCode
var code = [];
var company = data_price.companyList;
var companyList = JSON.parse(company);
//循环上传并加载数据；
var len = companyList.length;
var token = localStorage.getItem("token");
var access_token = localStorage.getItem("access_token");
rate = [];
asd = 0;
for (var i = 0; i < len; i++) {
    code[i] = companyList[i].companyCode;
    if(companyList[i].rate==0){
        rate[i] = ''

    }else{
        rate[i] = companyList[i].rate;

    }
    var t = '{"companyCode":"' + code[i] + '","token":"' + token + '","access_token":"' + access_token + '",';
    var datat = dataS.replace('{', t);
    space.oData = JSON.parse(datat);//上传的数据json
    console.log(space.oData);
    var n = sessionStorage.getItem('num');
    if(n==1){
        $.ajax({
            url: API_ADDR + "/insurance/insurancePrice.json",
            data: genJsonRequest(space.oData),
            type: "POST",
            dataType: "json",
            headers: headers,
            beforeSend:function () {
                $(".loadings").show();
            },
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                
                var data = data;
                sessionStorage.setItem(data.companyCode,JSON.stringify(data));
                console.log(77,rate[asd]);

                init(data,rate[asd]);//r 为显示商业险返
                asd++;
            },
            complete:function(){
                $(".loadings").hide()
            },
        })
    }else if(n==0){
        console.log(1);
        var sd = JSON.parse(sessionStorage.getItem(code[i]));
        init(sd);
    }

}
//保险公司insurance列表显示效果
$(".priceList").on("click", ".btnInfo", function () {
    $(this).parent().children(".priceList_info").toggleClass('hidden');
    $(this).parent().siblings().children(".priceList_info").addClass('hidden');
    if($(this).parent().attr('data-id')!=1){
        $(this).parent().attr('data-id',1);
        $(this).addClass('ty');
    }else{
        $(this).parent().attr('data-id',0);
        $(this).removeClass('ty')
    }
    $(this).parent().siblings().attr('data-id',0);
    $(this).parent().siblings().find('.btnInfo').removeClass('ty')
});
function init(data,r){
    console.log(r,96);
    var n = sessionStorage.getItem('num');
    if(data.vehicleStartDate!=0&&data.vehicleStartDate!=undefined&&data.vehicleStartDate!=null&&data.vehicleStartDate!=''){
        var Vdate = getNowFormatDate(new Date(data.vehicleStartDate));
    }
    if(data.businessStartDate!=0&&data.businessStartDate!=undefined&&data.businessStartDate!=null&&data.businessStartDate!=''){
        var Bdate = getNowFormatDate(new Date(data.businessStartDate));
    }
    space.Bdate = Bdate;
    $(".V").val(Vdate);
    $(".B").val(Bdate);
    $(".v").html(Vdate);
    $(".b").html(Bdate);

    $("#loading").hide();
    var companyName = data.companyName;

    var src = WEB_ADDR+"/static/img/companyIcon/" + data.companyCode + ".png";
    var strli = '';
    var strdiv = str_p = str_a = str_v = str_end = '';
    var strexempt = '';
    var price, price_a, price_v, com;
    var pricetotal = 0;
    var type_name;
    var type_name_a;
    var exempt_name = ee = '';
    /*循环主险*/
    if(data.status==0){
        var len_p = data.primary.length;
        for (var k = 0; k < len_p; k++) {
            if (data.primary[k].selected == 0) {
                continue;
            }
            if (data.primary[k].price == 0) {
                price = "无法承保"
            } else {
                price = parseFloat(data.primary[k].price);
            }
            if (data.primary[k].amount != 0) {
                if (data.primary[k].options != undefined) {
                    for (var i = 0; i < data.primary[k].options.length; i++) {
                        if (data.primary[k].amount == data.primary[k].options[i].key) {//判断显示的选择框的值
                            type_name = data.primary[k].options[i].value;
                        }
                    }
                } else {
                    type_name = data.primary[k].amount;
                }

            } else if (data.primary[k].amount == 0) {
                type_name = '';
            }
            str_p += '<li> <span><em style="background:#6295fe">主险</em>' + data.primary[k].insuranceName + '</span> <span>' + type_name + '</span> <span>' + price + '</span></li>';
            if(data.primary[k].exempt==1){
                if(data.primary[k].exemptPrice==0){
                    var ePri = '无法承保'
                }else{
                    var ePri = data.primary[k].exemptPrice;
                }
                strexempt+='<li> <span style="width: auto"><em style="background:#00ceba">附加险</em><i> (不计免赔)' + data.primary[k].insuranceName + '</i></span> <span style="width: auto"></span> <span>'
                    +ePri + '</span> </li>'
            }
        }
    }
    /*循环附加险*/
    if(data.status==0){
        var len_a = data.additional.length;
        for (var j = 0; j < len_a; j++) {
            if (data.additional[j].selected == 0) {
                continue;
            }
            if (data.additional[j].price == 0) {
                price_a = "无法承保";
            } else {
                price_a = parseFloat(data.additional[j].price);
            }
            if (data.additional[j].amount != 0) {
                if (data.additional[j].options != undefined) {
                    for (var i = 0; i < data.additional[j].options.length; i++) {
                        if (data.additional[j].amount == data.additional[j].options[i].key) {//判断显示的选择框的值
                            type_name_a = data.additional[j].options[i].value;
                        }
                    }
                } else {
                    type_name_a = data.additional[j].amount;
                }

            } else if (data.additional[j].amount == 0) {
                type_name_a = '';
            }
            str_a += '<li> <span><em style="background:#00ceba">附加险</em><i>' + data.additional[j].insuranceName + '</i></span> <span>' + type_name_a + '</span> <span>' + price_a + '</span> </li>';
            if(data.additional[j].exempt==1){
                if(data.additional[j].exemptPrice==0){
                    var ePri = '无法承保'
                }else{
                    var ePri = data.additional[j].exemptPrice;
                }
                strexempt+='<li> <span style="width: auto"><em style="background:#00ceba">附加险</em><i> (不计免赔)' + data.additional[j].insuranceName + '</i></span> <span style="width: auto"></span> <span>'
                    +ePri + '</span> </li>'
            }

        }
    }

    /*交强险*/
   if(data.status==0){
       if (data.vehicle.price == 0) {
           price_v = "已购买";
       } else {
           price_v = data.vehicle.price;
       }
       if (data.vehicle.taxPrice == 0) {
           var price_taxv = "已购买";
       } else {
           var price_taxv = data.vehicle.taxPrice;
       }
       var price_total_v = parseFloat(data.vehicle.price) + parseFloat(data.vehicle.taxPrice);
       if (price_total_v == 0) {
           price_total_v = "已购买"
       }
       if (data.totalPrice.businessPrice == null || data.totalPrice.businessPrice == "null") {
           var businessPrice = 0;
       } else {
           businessPrice = data.totalPrice.businessPrice;
       }
       if (data.totalPrice.exemptPrice == null || data.totalPrice.exemptPrice == "null") {
           var exemptPrice = 0;
       } else {
           exemptPrice = data.totalPrice.exemptPrice;
       }
       if (data.totalPrice.taxPrice == null || data.totalPrice.taxPrice == "null") {
           var taxPrice = 0;
       } else {
           taxPrice = data.totalPrice.taxPrice;
       }
       if (data.totalPrice.vehiclePrice == null || data.totalPrice.vehiclePrice == "null") {
           var vehiclePrice = 0;
       } else {
           vehiclePrice = data.totalPrice.vehiclePrice;
       }
   }
    //判断该保单是否已核保
    //判断是否核保
    var vitd={};
    var seqNo = data.seqNo;
    var companyCode = data.companyCode;
    $.ajax({
        headers:headers,
        dataType:"JSON",
        type:"post",
        async:false,
        data:genJsonRequest({
            vehicleId:vehicleId,
            seqNo:seqNo,
            companyCode:companyCode
        }),
        url:API_ADDR+'/insurance/applyCheck.json',
        success:function(vit){
            if(vit.status!=0){
                vitd.error = vit.msg;
            }
        }
    });
    //做判断，当初次进入该页所有a标签将不可跳转，弹出时间选择或再次进入后可做跳转
    space.dataSeqNo = "policyCheck.html?vehicleId=" + vehicleId + "&seqNo=" + data.seqNo + "&bindPhoneFlag=1";
    if (data.companyCode == 'dd') {
        str_v = '<li> <span><em class="bgRed">交强险(车船税)</em></span> <span></span> <span>' + price_total_v + '</span> </li>'
    } else{
        str_v = '<li> <span><em class="bgRed">交强险</em></span> <span></span> <span>' + price_v + '</span> </li>'
            + '<li> <span><em class="bgRed">车船税</em></span> <span></span> <span>' + price_taxv + '</span> </li>'
    }
    if (str_a == '' && str_p == '' && price_total_v == '已购买') {
        strli = '<dl><dd><img class="logo" src="' + src + '"></dd> <dt><h3>交强险（车船税）已购买</h3><p></p></dt> </dl>'
    } else {
        //分享内容定义
        var shareTitle = encodeURI("老板！您的车【"+licenseNo+"】报价已经生成。");
        var shareCon = encodeURI("我来保APP已经为您的爱车提供最新的爱车报价，总价约"+data.totalPremium+"元，详情点击查看您的爱车车险。");
        var shareUrl = encodeURIComponent(WEB_ADDR + "/h5/app/wlb_html/price/priceShare.html?seqNo=" + data.seqNo+'&token='+token);
        var hrefs = 'wlb://share?title=' + shareTitle + '&content=' + shareCon + '&url=' + shareUrl;
        var tipMsg = data.tipMsg;
        if(tipMsg==undefined){
            tipMsg=''
        }
        strli = '<dl class="btnInfo"><dd><img class="logo" src="' + src + '"></dd> <dt class="clearfix"><h3 class="fl">¥' + data.totalPremium + '<span></span></h3><p>' +
            '<a href="'+hrefs+'" id="priceShare_'+data.companyCode+'" class="btnBlue priceShare">发给朋友</a></p></dt> </dl>'
    }
    str_end = '</ul> <div class="priceList_Btn"> ' +
        '<a class="overLoad" href="javascript:void(0)">立即购买</a> </div> <p class="priceList_copy"></p>';
    if(data.biRate==0){
        var br = '-'
    }else{
        var br = data.biRate
    }
    if(data.ciRate==0){
        var vr = '-'
    }else{
        var vr = data.ciRate
    }
    //判断出险次数
    var icount = '-'
    if(data.occurredNum!=undefined&&data.occurredNum!=''&&data.occurredNum!=null){
        icount = data.occurredNum+'次'
    }
    var strf='<li>' +
        '<span >折扣率</span>' +
        '<span style="color:#898989;">商业险:'+br+'</span>' +
        ' <span style="color:#898989;">交强险:'+vr+'</span>' +
        '</li>' +
        '<li>' +
        '<span style="color:#898989;">出险次数</span>' +
        '<span style="color:#898989;">'+icount+'</span>' +
        '</li>';
    strdiv = '<ul>'+strf+'<li> <span>险种</span> <span>保额/限额(元)</span> <span>保费(元)</span> </li>' + str_p + str_a+ strexempt + str_v;
    var oli = $("<li class='priceList_line "+data.companyCode+"' data-No='"+data.seqNo+"'>");
    var div = $('<div class="priceList_info hidden">');

    //判断过滤是否展示该公司详细信息
    if (data.status != 0) {
        //未添加的公司账单，显示内容
        strli = '<dl class=""><dd><img class="logo" src="' + src + '"></dd> <dt class="clearfix" style="padding-right: .53rem;width: 6rem;">'+data.msg+'</dt> </dl>';
        oli.append(strli);
        $(".cList").append(oli);
        return false;
    }
    oli.append(strli);
    div.append(strdiv);
    oli.append(div);
    var re = sessionStorage.getItem("replace");

    //判断是否为替换原Dom
    if(re==0){
        $(".cList "+data.companyCode+"").replaceWith(oli);
        //替换之后，防止再次进入该页面后没有此节点造成无法替换展示。
        sessionStorage.setItem("replace","1");
    }else{
        $(".cList").append(oli);
    }

//首次进入该页面弹出填写时间
    var fir = function(){
        $('.priceList_line').each(function(){
            if($(this).attr('data-id')==1){
                oS = $(this).attr('data-No')
            }
        })
        if(vitd.error!=undefined){
            errorMsg(vitd.error)
        }else{
            var n = sessionStorage.getItem('num');
            if(n==1){
                $(".boxW").show();
            }else if(n==0){
                window.location.href = "policyCheck.html?vehicleId=" + vehicleId + "&seqNo=" + oS + "&bindPhoneFlag=1";
            }
        }
    }
    //$(".cList").on("click",".overLoad",fir);
    $('.goNext').off();
    $('.goNext').on('click',fir)
}
$(".B").on("click", function () {
    window.location.href = 'wlb://openUI/timeSpinner?time=&tag=B';
});
//时间修改后
$("#dateCheck").click(function(){
    $(".boxW").hide();
    //后台日期自加1，此处做处理
    space.oData["vehicleExpiredDateStr"]=getNowFormatDate(new Date(parseInt(getTime($(".V").val()))-86400000));
    space.oData["businessExpiredDateStr"]=getNowFormatDate(new Date(parseInt(getTime($(".B").val()))-86400000));
    sessionStorage.setItem('num',0);
    //对比时间
    if($(".B").val()==space.Bdate){
        window.location.href=space.dataSeqNo;
    }else{
        //判断 init函数调取来源
        sessionStorage.setItem('replace',0);
        space.Bdate = $(".B").val();
        $.ajax({
            url: API_ADDR + "/insurance/insurancePrice.json",
            data: genJsonRequest(space.oData),
            type: "POST",
            dataType: "json",
            headers: headers,
            beforeSend:function () {
                $(".alter .alertDate").html(space.Bdate);
                $(".alter").show();
            },
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                var data = data;
                sessionStorage.setItem(data.companyCode,JSON.stringify(data));
                space.dataSeqNo = "policyCheck.html?vehicleId=" + vehicleId + "&seqNo=" + data.seqNo + "&bindPhoneFlag=1";
                init(data);
            },
            complete:function(){
                $(".alter").hide()
            },
        })
    }
});




//弹出事件框，点击空间之外阴影部分隐藏
$(".boxW").click(function(){
    $(this).hide();
    return false
});
$('.date').click(function(e){e.stopPropagation();});