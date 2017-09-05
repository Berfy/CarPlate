sessionStorage.setItem('num', 1);
function timeRsult(time, tag) {
    var dateTime = getNowFormatDate(new Date(parseInt(time)));
    var obj = "#" + tag;
    $(obj).val(dateTime);
};
$(".Adeadline").on("tap", function () {
    window.location.href = 'wlb://openUI/timeSpinner?time=&tag=Adeadline';
})
$(".Bdeadline").on("tap", function () {
    window.location.href = 'wlb://openUI/timeSpinner?time=&tag=Bdeadline';
})

var LocString = String(window.document.location.href);
function getQueryStr(str) {
    var rs = new RegExp("(^|)" + str + "=([^&]*)(&|$)", "gi").exec(LocString), tmp;
    if (tmp = rs) {
        return tmp[2];
    }
    return "";
}
//获取车牌号Id
if (getQueryStr("vehicleId") == '' || getQueryStr("vehicleId") == undefined || getQueryStr("vehicleId") == 'undefined') {
    var vehicleId = localStorage.getItem("vehicleId");
} else {
    var vehicleId = getQueryStr("vehicleId")
    localStorage.setItem("vehicleId", vehicleId);
}
var token = localStorage.getItem("token");
var access_token = localStorage.getItem("access_token");

if (vehicleId == '' || vehicleId == undefined || vehicleId == 'undefined') {
    $(".errorMsg").fadeIn().children().html("车辆信息错误");
    setTimeout(function () {
        $(".errorMsg").fadeOut();
        window.location.href = "wlb://user/regist?closePage=1"
    }, 1000)
}
$("#vehicleId").val(vehicleId);

var req = {
    vehicleId: vehicleId,
    token: token,
    access_token: access_token
}
var offer_price = {}
//获取文字描述
$.ajax({
    type: "post",
    headers: headers,
    url: API_ADDR + "/vehicle/vehicle_info.json",
    dataType: "json",
    data: genJsonRequest(req),
    success: function (data) {
        if (data.status != 0) {
            errorMsg(data.msg);
            return false
        }
        //车牌号
        var licenseNo = data.licenseNo;
        $(".licenseNo").html(licenseNo+"的车辆信息")
        $("#Num").html(licenseNo);
        //车辆logo
        var carLogoUrl = data.carLogoUrl;

        $("#carLogoUrl").attr("src", carLogoUrl);
        //车辆详情写入
        var showText = data.showText;
        $("#showText").text(showText);
        //车辆价格
        var vehiclePrices = data.vehiclePrice;
        $("#showText").append($("<span id='vehiclePrice' style='color:#B81C22'>").html("¥" + vehiclePrices));
        //行驶区域
        var regionCode = data.regionCode;
        $("#regionCode").val(regionCode);
        offer_price.licenseNo = licenseNo;
        offer_price.carLogoUrl = carLogoUrl;
        offer_price.showText = showText;
    }
});

//初始化保险到期日
req["token"] = token
$.ajax({
    type: "POST",
    headers: headers,
    url: API_ADDR + "/vehicle/insuranceExpiredDate.json",
    dataType: "json",
    headers: headers,
    contentType: "application/json; charset=utf-8",
    data: genJsonRequest(req),
    success: function (data) {
        if (data.status != 0) {
            errorMsg(data.msg)
            return false;
        } else {
            var vdeadline = data.vehicleExpiredDate;
            var bdeadline = data.businessExpiredDate;
            if (vdeadline != 0) {
                timeRsult(vdeadline, 'Adeadline');
            }
            if (bdeadline != 0) {
                timeRsult(bdeadline, 'Bdeadline');
            }

        }

    }

})

//功能
function s_offer(opt) {
    this.config = {
        name: '',
        oClass: ''
    }
    $.extend(true, this.config, opt);
}
s_offer.prototype = {
    _init: function () {
        var len = $(".insurance").length;
        var name = this.config.name;
        for (var i = 0; i < len; i++) {//主险初始选择
            var val = $(".insurance").eq(i).find('input[name="' + name + '[][selected]"]').val();
            if (val == 1) {
                $(".insurance").eq(i).addClass("S_I");
                $(".insurance").eq(i).siblings(".opition").find(".bt").addClass("itembtn_off").removeClass("itembtn")//初始化按钮显示状态
            }
            //初始不计免赔选择
            var exemptVal = $(".insurance").eq(i).find('input[name="' + name + '[][exempt]"]').val();
            if (exemptVal == 1) {
                $(".insurance").eq(i).find(".check").addClass("neck")
            }
            var quantity = $(".S_I").length;
            $("#s_num").html(quantity)
        }
        for (var i = 0; i < len; i++) {//主险初始选择
            var val = $(".insurance").eq(i).find('input[name="additional[][selected]"]').val();
            if (val == 1) {
                $(".insurance").eq(i).addClass("S_I");
                $(".insurance").eq(i).siblings(".opition").find(".bt").addClass("itembtn_off").removeClass("itembtn")//初始化按钮显示状态
            }
            //初始不计免赔选择
            var exemptVal = $(".insurance").eq(i).find('input[name="additional[][exempt]"]').val();
            if (exemptVal == 1) {
                $(".insurance").eq(i).find(".check").addClass("neck")
            }
            var quantity = $(".S_I").length;
            $("#s_num").html(quantity);
        }
        var damage_val = $("#damage").next().val();
        //提示弹窗
        if(damage_val==0){
            $("._add").parents().on("click",function(){
                $(".o").show()
            });
        }
        $(".click").on("click",function(){
            $(this).parent().parent().hide()
        })

    },
    //保险选择
    _select: function () {
        var name = this.config.name;
        var oClass = this.config.oClass;
        var damage_val = $("#damage").next().val()//取得车损险标记
        $(".insure").off("click", oClass);
        $(".insure").on("click", oClass, function () {//为主险添加事件
            //保险是否选中
            $(this).toggleClass("S_I").find(".sel").find(".check").removeClass("neck");
            var se_val = $(this).find('input[name="' + name + '[][selected]"]').val();
            //下拉框显示效果
            var len = $(this).siblings(".opition").find(".sbox option").length;
            var st = $(this).siblings(".opition").find(".sbox");
            if(se_val==0){//判断select默认事件是否阻止
                $(this).siblings(".opition").find(".bt").addClass("itembtn_off").removeClass("itembtn");
                var add = parseInt($(this).find(".amount").val());
                for(var i=0;i<len;i++){
                    var abb = parseInt(st.children().eq(i).val());
                    if(add==abb){
                        var aval = st.children().eq(i).val();
                        st.val(aval)
                    }
                }
            }else{
                $(this).siblings(".opition").find(".bt").removeClass("itembtn_off").addClass("itembtn");
                var aval = st.children().eq(0).val();
                st.val(aval)
            }
            //不计免赔默认勾选
            $(this).find(".sel").find(".check").toggleClass("neck");
            var val = $(this).find(".sel").siblings(".exempt").val();
            if (val == 0) {
                val = 1
            } else if (val == 1){
                val = 0
            }
            $(this).find(".sel").siblings(".exempt").val(val);
            //取消后归值
            if (se_val == 0) {
                se_val = 1
            } else if (se_val == 1) {
                se_val = 0;
                $(this).find('input[name="' + name + '[][exempt]"]').val(0)//当主险取消，不计免赔取消
            }
            $(this).find('input[name="' + name + '[][selected]"]').val(se_val);
            //保险不计免赔是否选中
            damage_val = $("#damage").next().val();
            if (damage_val == 1) {//已选择车损险后添加点击事件
                $(".insure").off("click", '._add');
                $("._add").parent().off("click")
                $(".insure").on("click", '._add', _ad);
                $("._add").siblings().children().attr("disabled",false);
            } else {//解绑事件
                $(".insure").off("click", '._add');
                $(".insure ._add").removeClass("S_I");
                $(".a_selected").val(0);
                $(".a_exempt").val(0);
                $(".insure ._add").siblings().find(".bt").removeClass("itembtn_off").addClass("itembtn");
                $("._add").siblings().children().attr("disabled",true);
                var addv = $("._add").siblings().children().children().eq(0).val();
                $("._add").siblings().find(".sbox").val(addv);
                $("._add").parent().on("click",function(){
                    $(".o").show();
                });
            }
            var quantity = $(".S_I").length;
            $("#s_num").html(quantity);
            return false;
        });
        if (damage_val == 1) {//已选择车损险后添加点击事件
            $(".insure").off("click", '._add');
            $(".insure").on("click", '._add', _ad);
            $("._add").siblings().children().attr("disabled",false);
        } else {//解绑事件
            $(".insure").off("click", '._add');
            $(".insure ._add").removeClass("S_I");
            $(".a_selected").val(0);
            $(".a_exempt").val(0);
            $(".insure ._add").siblings().find(".bt").removeClass("itembtn_off").addClass("itembtn");
            $("._add").siblings().children().attr("disabled",true);
            var addv = $("._add").siblings().children().children().eq(0).val();
            $("._add").siblings().find(".sbox").val(addv)
        };
        //交强险事件
        var aval = $("._insurance").find('.selected').val();
        if(aval==1){
            $("._insurance").addClass('S_I')
        }
        $("._insurance").on('click',function(){
            $(this).toggleClass('S_I');
            var daval = $("._insurance").find('.selected').val();
            if(daval==0){
                $(this).find('.selected').val(1);
            }else if(daval==1){
                $(this).find('.selected').val(0);
            }
        })
//select值获取 select判断状态改变后的保险选择状态
        $(".insure").on("change", ".sbox", function (event) {
            $(this).parent().siblings().find(".sel").find(".check").addClass("neck");
            var val = $(this).val();
            $(this).parent().siblings().find(".amount").val(val);
            if(val==0){
                //记录选单状态
                $(this).parent().siblings().removeClass("S_I");
                $(this).parent().siblings().find(".selected").val(0).siblings(".exempt").val(0);
            }else{
                //记录选单状态
                $(this).parent().siblings().addClass("S_I");
                $(this).parent().siblings().find(".selected").val(1).siblings(".exempt").val(1);
            }
        });
        //按钮事件
        $(".opition").on("click",'.bt',function(){
            $(this).parent().siblings().find(".sel").find(".check").addClass("neck");
            $(this).toggleClass("itembtn").toggleClass("itembtn_off");
            $(this).parent().siblings().toggleClass("S_I");
            var a = $(this).parent().siblings().find(".selected").val();
            if(a==1){
                $(this).parent().siblings().find(".selected").val(0);
                $(this).parent().siblings().find(".exempt").val(0)
            }else{
                $(this).parent().siblings().find(".selected").val(1);
                $(this).parent().siblings().find(".exempt").val(1)
            }
            var damage_val = $("#damage").next().val()//取得车损险标记
            if (damage_val == 1) {//已选择车损险后添加点击事件
                $(".insure").off("click", '._add');
                $(".insure").on("click", '._add', _ad);
                $("._add").siblings().children().attr("disabled",false);
            } else {//解绑事件
                $(".insure").off("click", '._add');
                $(".insure ._add").removeClass("S_I");
                $(".a_selected").val(0);
                $(".a_exempt").val(0);
                $(".insure ._add").siblings().find(".bt").removeClass("itembtn_off").addClass("itembtn");
                $("._add").siblings().children().attr("disabled",true);
                var addv = $("._add").siblings().children().children().eq(0).val();
                $("._add").siblings().find(".sbox").val(addv)
            }
        });

    },
};
function _ad() {//附加险
    $(this).toggleClass("S_I").find(".sel").find(".check").removeClass("neck");
    var se_val = $(this).find('input[name="additional[][selected]"]').val();
    //下拉框显示效果
    var len = $(this).siblings(".opition").find(".sbox option").length;
    var st = $(this).siblings(".opition").find(".sbox");
    if(se_val==0){//判断select默认事件是否阻止
        $(this).siblings(".opition").find(".bt").addClass("itembtn_off").removeClass("itembtn");
        var add = parseInt($(this).find(".amount").val());
        for(var i=0;i<len;i++){
            var abb = parseInt(st.children().eq(i).val());
            if(add==abb){
                var aval = st.children().eq(i).val();
                st.val(aval)
            }
        }
    }else{
        $(this).siblings(".opition").find(".bt").removeClass("itembtn_off").addClass("itembtn");
        var aval = st.children().eq(0).val();
        st.val(aval)
    }
    //不计免赔默认勾选
    $(this).find(".sel").find(".check").toggleClass("neck");
    var val = $(this).find(".sel").siblings(".exempt").val();
    if (val == 0) {
        val = 1
    } else if (val == 1) {
        val = 0
    }
    $(this).find(".sel").siblings(".exempt").val(val);
    //取消后归值
    if (se_val == 0) {
        se_val = 1;
    } else if (se_val == 1) {
        se_val = 0;
        $(this).find('input[name="additional[][exempt]"]').val(0);
    }
    $(this).find('input[name="additional[][selected]"]').val(se_val);
    var quantity = $(".S_I").length;
    $("#s_num").html(quantity);
    return false;
}


//获取预设保险方案 初始化
var package = {
    vehicleId: vehicleId,
    companyCode: "",
    token: token,
    access_token: access_token
}
$(".loadings").show();
$.ajax({
    headers: headers,
    url: API_ADDR + "/insurance/insurancePlan.json",
    type: "POST",
    dataType: "json",
    headers: headers,
    contentType: "application/json; charset=utf-8",
    data: genJsonRequest(package),
    success: function (data) {
        $(".loadings").hide();
        var oData = data;//待留
        var str = '', package_name, package_des;
        var meal_0 = vehicle = additional = primary = vehicle_s = additional_s = primary_s = select_p = select_a = oSelect = input_p = input_a = input_v = "", index;
        var stt;
        if (oData.status != 0) {//判断
            return false
        }
        //初始化
        function init(a) {
            index = a || 0;
            vehicle = oData.vehicle;
            additional = oData.additional;
            primary = oData.primary;

            var input_v1 = '<input type="hidden" name="vehicle[insuranceName]" class="name" value="' + vehicle.insuranceName;
            var input_v2 = '"> <input type="hidden" name="vehicle[insuranceCode]" value="' + vehicle.insuranceCode;
            var input_v3 = '"> <input type="hidden" name="vehicle[selected]" class="selected" value="' + vehicle.selected;
            var input_v4 = '"> <input type="hidden" name="vehicle[exempt]"  value="' + vehicle.exempt;
            var input_v5 = '"> <input type="hidden" name="vehicle[exemptStatus]" value="' + vehicle.exemptStatus;
            var input_v6 = '"> <input type="hidden" name="vehicle[amount]" value="' + vehicle.amount;
            var input_v7 = '"> <input type="hidden" name="vehicle[des]" value="' + vehicle.des + '"> <input type="hidden" name="vehicle[exemptPrice]" value="' + vehicle.exemptPrice;
            var input_v8 = '"> <input type="hidden" name="vehicle[buyRate]" value="' + vehicle.buyRate + '"> <input type="hidden" name="vehicle[price]" value="' + vehicle.price;
            var input_v9 = '"> <input type="hidden" name="vehicle[diffPrice]" value="' + vehicle.diffPrice + '"> <input type="hidden" name="vehicle[taxPrice]" value="' + vehicle.taxPrice + '">';
            input_v = input_v1 + input_v2 + input_v3 + input_v4 + input_v5 + input_v6 + input_v7 + input_v8 + input_v9;
            //交强险字符串拼接
            var vehicle_s1 = ' <div class="_insurance vehicle"> <div class="_wd"> <div class=""> <span></span> </div> <div class="part"> <div> <p class="part_name">' + vehicle.insuranceName
            var vehicle_s2 = '<span class="rate"><b id="des">' + vehicle.buyRate + '%</b>车主购买</span></p> '
            var vehicle_s3 = '</div> <div class="sign"> <span>交强险</span> </div> </div> </div>' + input_v + ' </div>';
            vehicle_s = vehicle_s1 + vehicle_s2 + vehicle_s3;

            //主险字符串拼接
            var oinput, oSelect, select_p, exempt;
            for (var i = 0; i < primary.length; i++) {
                if (primary[i].options != undefined) {
                    for (var k = 0; k < primary[i].options.length; k++) {
                        if(primary[i].selected==1){
                            if (primary[i].amount == primary[i].options[k].key) {//初始化判断显示的选择框的值
                                oSelect += '<option selected="selected" value="' + primary[i].options[k].key + '">' + primary[i].options[k].value + '</option>'
                            } else{
                                oSelect += '<option value="' + primary[i].options[k].key + '">' + primary[i].options[k].value + '</option>'
                            }
                        }else{
                            oSelect += '<option value="' + primary[i].options[k].key + '">' + primary[i].options[k].value + '</option>'
                        }
                        select_p = '<select class="sbox">' + oSelect + ' </select>'
                    }
                } else {//按钮显示
                    select_p = '<input type="radio" class="bt itembtn">'
                }
                if (primary[i].supportExempt == 1) {//判断是否显示不记免赔框
                    exempt = '<div class="sel"> <div class="check">  </div> <span>不计免赔</span> </div>'
                } else {
                    exempt = ''
                }
                if (primary[i].insuranceCode == "damage") {//为车损险做标记
                    oinput = '<input type="hidden" name="primary[][insuranceCode]" value="' + primary[i].insuranceCode + '" id="damage">'

                } else {
                    oinput = '<input type="hidden" name="primary[][insuranceCode]" value="' + primary[i].insuranceCode + '">'
                }

                oSelect = '';
                //表单数据存储

                var input_p1 = '<input type="hidden" name="primary[][insuranceName]" class="name" value="' + primary[i].insuranceName;
                var input_p2 = '"> ' + oinput + '<input type="hidden" name="primary[][selected]"  class="selected" value="' + primary[i].selected;
                var input_p3 = '"> <input type="hidden" class="exempt" name="primary[][exempt]" value="' + primary[i].exempt;
                var input_p4 = '"> <input type="hidden" name="primary[][exemptStatus]" value="' + primary[i].exemptStatus;
                var input_p5 = '"> <input type="hidden" class="amount" name="primary[][amount]" value="' + primary[i].amount + '"> <input type="hidden" name="primary[][des]" value="' + primary[i].des;
                var input_p6 = '"> <input type="hidden" name="primary[][exemptPrice]" value="' + primary[i].exemptPrice + '"> <input type="hidden" name="primary[][buyRate]" value="' + primary[i].buyRate;
                var input_p7 = '"> <input type="hidden" name="primary[][price]" value="' + primary[i].price + '"> <input type="hidden" name="primary[][diffPrice]" value="' + primary[i].diffPrice;
                var input_p8 = '"> <input type="hidden" name="primary[][taxPrice]" value="' + primary[i].taxPrice + '">';
                input_p = input_p1 + input_p2 + input_p3 + input_p4 + input_p5 + input_p6 + input_p7 + input_p8;
                var primary_s1 = '<div class="z_b"><div class="insurance _pri" data-name="primary" data-id=' + i + '> <div class="_w"> <div class="part"> <div class=""> <p class="part_name">';
                var primary_s2 = primary[i].insuranceName + ' <span class="rate"> <b id="des">' + primary[i].buyRate + '%</b>车主购买 </span> </p>';
                var primary_s3 = '</div>  </div> </div>' + exempt + '<div class="sign"> <span>主险</span> </div>' + input_p + ' </div> <div class="opition">' + select_p + '</div></div>'
                primary_s += primary_s1 + primary_s2 + primary_s3;
            }

            //附加险字符串拼接
            for (var i = 0; i < additional.length; i++) {
                if (additional[i].options != undefined) {//判断显示的默认值
                    for (var k = 0; k < additional[i].options.length; k++) {
                        if(additional[i].selected==1){
                            if (additional[i].amount == additional[i].options[k].key) {//初始化判断显示的选择框的值
                                oSelect += '<option selected="selected" value="' + additional[i].options[k].key + '">' + additional[i].options[k].value + '</option>'
                            } else{
                                oSelect += '<option value="' + additional[i].options[k].key + '">' + additional[i].options[k].value + '</option>'
                            }
                        }else{
                            oSelect += '<option value="' + additional[i].options[k].key + '">' + additional[i].options[k].value + '</option>'
                        }
                        select_p = '<select class="sbox">' + oSelect + ' </select>'
                    }
                } else {//按钮显示
                    select_p = '<input type="radio" class="bt itembtn">'
                }
                if (additional[i].supportExempt == 1) {//判断是否显示不记免赔框
                    exempt = '<div class="sel"> <div class="check">  </div> <span>不计免赔</span> </div>'
                } else {
                    exempt = ''
                }
                oSelect = '';
                var input_a1 = '<input type="hidden" class="glassNotifyFlag" name="additional[][glassNotifyFlag]" value="' + additional[i].glassNotifyFlag + '"><input type="hidden" data-name="additional" name="additional[][insuranceName]" class="name" value="' + additional[i].insuranceName;
                var input_a2 = '"> <input type="hidden" name="additional[][insuranceCode]" value="' + additional[i].insuranceCode;
                var input_a3 = '"> <input type="hidden" name="additional[][selected]"  class="selected a_selected" value="' + additional[i].selected + '"> <input type="hidden" name="additional[][exempt]" class="exempt a_exempt" value="';
                var input_a4 = additional[i].exempt + '"> <input type="hidden" name="additional[][exemptStatus]" value="' + additional[i].exemptStatus;
                var input_a5 = '"> <input type="hidden" class="amount" name="additional[][amount]" value="' + additional[i].amount + '"> <input type="hidden" name="additional[][des]" value="';
                var input_a6 = additional[i].des + '"> <input type="hidden" name="additional[][exemptPrice]" value="' + additional[i].exemptPrice;
                var input_a7 = '"> <input type="hidden" name="additional[][buyRate]" value="' + additional[i].buyRate + '"> <input type="hidden" name="additional[][price]" value="';
                var input_a8 = additional[i].price + '"> <input type="hidden" name="additional[][diffPrice]" value="' + additional[i].diffPrice;
                var input_a9 = '"> <input type="hidden" name="additional[][taxPrice]" value="' + additional[i].taxPrice + '">';
                input_a = input_a1 + input_a2 + input_a3 + input_a4 + input_a5 + input_a6 + input_a7 + input_a8 + input_a9;

                var additional_s1 = '<div class="z_b"><div class="insurance _add" id="' + additional[i].insuranceCode + '" data-id=' + i + '> <div class="_w "><div class="part"> <div class=""> <p class="part_name"> ';
                var additional_s2 = additional[i].insuranceName + ' <span class="rate"> <b id="des">' + additional[i].buyRate + '%</b>车主购买 </span> </p>';
                var additional_s3 = '</div>  </div> </div>  ' + exempt + ' <div class="sign"> <span>附加险</span> </div> ' + input_a + '</div><div class="opition">' + select_p + '</div></div>';
                additional_s += additional_s1 + additional_s2 + additional_s3;

            }
            meal_0 = vehicle_s + primary_s + additional_s;
            $(".in_wrap").html(meal_0);
            //重置字符串
            primary_s = additional_s = "";
        }

        init();
        //套餐选择
        $(".pri_pac").on("click", ".pro", function () {
            $(this).addClass("select").siblings().removeClass("select");
            var type = $(this).attr("data-type");
            $("#package_type").val(type)
            var in_index = $(this).index()
            sessionStorage.setItem("in_index", in_index)
            index = sessionStorage.getItem("in_index");
            init(index);
            var p = new s_offer({
                name: "primary",
                oClass: "._pri"
            });
            p._init();
            p._select();
        });
        $(".pri_pac").append(str).find(".pro").eq(0).addClass("select")
        var p = new s_offer({
            name: "primary",
            oClass: "._pri"
        });
        p._init();
        p._select();
    }

})
//初始选择 函数
//缓存套餐选择
var index = sessionStorage.getItem("in_index");
if (index != null) {
    $(".pri_pac .pro").eq(index).addClass("select").siblings().removeClass("select")
}
//不计免赔选择
$(".insure").on("click", ".S_I .sel", function (event) {
    $(this).find(".check").toggleClass("neck");
    var val = $(this).siblings(".exempt").val();
    if (val == 0) {
        val = 1
    } else if (val == 1) {
        val = 0
    }
    $(this).siblings(".exempt").val(val);
    event.stopPropagation();
});
//获取保险公司列表并缓存
$.ajax({
    url: API_ADDR + "/insurance/insuranceCompany.json",
    type: "POST",
    dataType: "json",
    headers: headers,
    contentType: "application/json; charset=utf-8",
    data: genJsonRequest(req),
    success: function (data) {
        var data = JSON.stringify(data.list);
        offer_price.companyList = data;
    }
});
/*表单序列化*/
$.fn.serializeObject = function () {
    var json = {};
    var arrObj = this.serializeArray();
    $.each(arrObj, function () {
        if (json[this.name]) {
            if (!json[this.name].push) {
                json[this.name] = [json[this.name]];
            }
            json[this.name].push(this.value || '');
        } else {
            json[this.name] = this.value || '';
        }
    });
    return json;
};
$("#btn_space").click(function () {
    data = $("#po").serializeJSON();
    dataS = JSON.stringify(data);
    localStorage.setItem("dataForm", dataS);
    /*遍历数据*/
    var name_a = name_e = name = '';
    $(".S_I").each(function (i) {
        var val_e = $(this).find(".exempt").val();
        if (val_e == 1) {
            name_e += $(this).find(".name").val() + ' ';
        } else {
            name_a += $(this).find(".name").val() + ' ';
        }
        if (name_e == '') {
            name = name_a;
        } else {
            name = '不计免赔( ' + name_e + ')' + '  ' + name_a;
        }
    })
    var primary = $("[data-name='primary']");
    var len = $("[data-name='primary']").length;
    var vehicleExpiredDate = $("#Adeadline").val();
    var businessExpiredDate = $("#Bdeadline").val();
    /*判断是否选中主险*/
    offer_price.insudenceName_selected = name;
    offer_price.vehicleExpiredDate = vehicleExpiredDate;
    offer_price.businessExpiredDate = businessExpiredDate;
    localStorage.setItem("offer_price", JSON.stringify(offer_price));
    window.location.href = 'price.html';
});


//玻璃险选择
$(".in_wrap").on("change", "#glass select", function () {
    var glassNotifyFlag = $("#glass .glassNotifyFlag").val();
    if (glassNotifyFlag == "1") {
        $("#glass").append("<p class='glassNotifyFlag_info'>提示：此车型核保只支持进口玻璃，建议选择进口玻璃</p>");
    }
})
sessionStorage.setItem('num',1);
