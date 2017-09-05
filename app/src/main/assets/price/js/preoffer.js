



$(".A").change(function(){
    var A = $(this).val();
    $("#Adeadline").val(A)
});
$(".B").change(function(){
    var A = $(this).val();
    $("#Bdeadline").val(A)
});
req["companyCode"] = "";
$.ajax({
    url: API_ADDR + "/insurance/insurancePlanPackage.json",
    type: "POST",
    dataType: "json",
    headers: headers,
    contentType: "application/json; charset=utf-8",
    data: genJsonRequest(req),
    beforeSend:function(xhr){
        $(".loadings").show()
    },
    success:function(data){
        if(data.status!=0){
            errorMsg(data.msg);
            return false;
        };
        var list = data.list;
        for(var i=0;i<list.length;i++){
            if(list[i].package_type=="D"){//判断是否有上一年报价
                //显示详情
                $("#oldP").show();
                $(".kind").show();

                sessionStorage.setItem("type","D");
                $("#package_type").val(list[i].package_type);
                console.log(list[i]);
                $(".preprice").html(list[i].package_des);
                var primary = list[i].primary;
                var additional = list[i].additional;
                var vehicle = list[i].vehicle;
                var priName="";
                var n_priName ="";
                var e_priName = "";
                var addName,e_addName,n_addName;
                addName = e_addName = n_addName="";
                var name= "";
                var name_p= "";
                var name_a = "";
                var input_p=input_v=input_a="";
                for(var j=0;j<primary.length;j++){//主险
                    if(primary[j].selected==1){//判断选择险种
                        if(primary[j].exempt==1){
                            e_priName +=primary[j].insuranceName+"&nbsp";
                        }else if(primary[j].exempt==0){
                            n_priName += primary[j].insuranceName+"&nbsp";
                        }
                    }
                    //主险隐藏域
                     input_p += '<input type="hidden" name="primary[][insuranceName]" class="name" value="' + primary[j].insuranceName+
                        '"> ' + '<input type="hidden" name="primary[][insuranceCode]" value="' + primary[j].insuranceCode + '">'+
                         '<input type="hidden" name="primary[][selected]"  class="selected" value="' + primary[j].selected+
                        '"> <input type="hidden" class="exempt" name="primary[][exempt]" value="' + primary[j].exempt+
                        '"> <input type="hidden" name="primary[][exemptStatus]" value="' + primary[j].exemptStatus+
                        '"> <input type="hidden" class="amount" name="primary[][amount]" value="' + primary[j].amount + '"> <input type="hidden" name="primary[][des]" value="' + primary[j].des+
                        '"> <input type="hidden" name="primary[][exemptPrice]" value="' + primary[j].exemptPrice + '"> <input type="hidden" name="primary[][buyRate]" value="' + primary[j].buyRate+
                        '"> <input type="hidden" name="primary[][price]" value="' + primary[j].price + '"> <input type="hidden" name="primary[][diffPrice]" value="' + primary[j].diffPrice+
                        '"> <input type="hidden" name="primary[][taxPrice]" value="' + primary[j].taxPrice + '">';
                }

                for(var k=0;k<additional.length;k++){//附加险
                    if(additional[k].selected==1){//判断选择险种
                        if(additional[k].exempt==1){
                            e_addName += additional[k].insuranceName+" ";
                        }else if(additional[k].exempt==0){
                            n_addName += additional[k].insuranceName+" ";
                        }
                    }
                    //附加险隐藏域
                    input_a += '<input type="hidden" class="glassNotifyFlag" name="additional[][glassNotifyFlag]" value="'+additional[k].glassNotifyFlag+
                        '"><input type="hidden" data-name="additional" name="additional[][insuranceName]" class="name" value="' + additional[k].insuranceName+
                        '"> <input type="hidden" name="additional[][insuranceCode]" value="' + additional[k].insuranceCode+
                        '"> <input type="hidden" name="additional[][selected]"  class="selected a_selected" value="' + additional[k].selected +
                        '"> <input type="hidden" name="additional[][exempt]" class="exempt a_exempt" value="'+additional[k].exempt + '"> <input type="hidden" name="additional[][exemptStatus]" value="'+
                        additional[k].exemptStatus+'"> <input type="hidden" class="amount" name="additional[][amount]" value="' + additional[k].amount +
                        '"> <input type="hidden" name="additional[][des]" value="'+additional[k].des + '"> <input type="hidden" name="additional[][exemptPrice]" value="' + additional[k].exemptPrice+
                        '"> <input type="hidden" name="additional[][buyRate]" value="' + additional[k].buyRate + '"> <input type="hidden" name="additional[][price]" value="'+
                        additional[k].price + '"> <input type="hidden" name="additional[][diffPrice]" value="' + additional[k].diffPrice+
                        '"> <input type="hidden" name="additional[][taxPrice]" value="' + additional[k].taxPrice + '">';
                }
                if(e_priName==""){
                    name_p = n_priName
                }else{
                    name_p = e_priName+n_priName;
                }
                if(e_addName==""){
                    name_a = n_addName;
                }else{

                    name_a = e_addName+n_addName;
                }

                name = name_p+" 交强险（含车船税）"+name_a
                offer_price.insudenceName_selected = name;
                if(name_a == ""){
                    name_a = "-"
                }
                if(name_p == ""){
                    name_p = "-"
                }
                $(".priName").html(name_p);
                $(".addName").html(name_a);
                if(list[i].vehicle.selected==1){
                    $(".vehName").html("车船税")
                }
                //交强险隐藏域
                input_v = '<input type="hidden" name="vehicle[insuranceName]" class="name" value="' + vehicle.insuranceName+
                    '"> <input type="hidden" name="vehicle[insuranceCode]" value="' + vehicle.insuranceCode+
                    '"> <input type="hidden" name="vehicle[selected]" value="' + vehicle.selected+
                    '"> <input type="hidden" name="vehicle[exempt]" value="' + vehicle.exempt+
                    '"> <input type="hidden" name="vehicle[exemptStatus]" value="' + vehicle.exemptStatus+
                    '"> <input type="hidden" name="vehicle[amount]" value="' + vehicle.amount+
                    '"> <input type="hidden" name="vehicle[des]" value="' + vehicle.des + '"> <input type="hidden" name="vehicle[exemptPrice]" value="' + vehicle.exemptPrice+
                    '"> <input type="hidden" name="vehicle[buyRate]" value="' + vehicle.buyRate + '"> <input type="hidden" name="vehicle[price]" value="' + vehicle.price+
                    '"> <input type="hidden" name="vehicle[diffPrice]" value="' + vehicle.diffPrice + '"> <input type="hidden" name="vehicle[taxPrice]" value="' + vehicle.taxPrice + '">';

                //添加节点
                $(".hide").append(input_p+input_a+input_v);
                console.log($(".hide").serializeJSON());

                //获取保险公司列表并缓存
                delete req["companyCode"];
                $.ajax({
                    url:API_ADDR+"/insurance/insuranceCompany.json",
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
                //选择报价
                $("#reP").html("重新选择险种").click(function(){
                    var vehicleExpiredDate = $("#Adeadline").val();
                    var businessExpiredDate = $("#Bdeadline").val();
                    console.log(vehicleExpiredDate,businessExpiredDate)
                    if(vehicleExpiredDate==''||vehicleExpiredDate==0||businessExpiredDate==''||businessExpiredDate==0){
                        errorMsg('请选择日期');
                        return false
                    }else{
                        localStorage.setItem('vehicleExpiredDate',vehicleExpiredDate);
                        localStorage.setItem('businessExpiredDate',businessExpiredDate);
                        window.location.href = 'offer.html';
                    }

                });
                $("#oldP").click(function(){
                    data = $(".hide").serializeJSON();
                    dataS = JSON.stringify(data);
                    localStorage.setItem("dataForm", dataS);
                    var vehicleExpiredDate = $("#Adeadline").val();
                    var businessExpiredDate = $("#Bdeadline").val();
                    if(vehicleExpiredDate==''||vehicleExpiredDate==0||businessExpiredDate==''||businessExpiredDate==0){
                        errorMsg('请选择日期');
                        return false
                    }else{
                        offer_price.vehicleExpiredDate = vehicleExpiredDate;
                        offer_price.businessExpiredDate = businessExpiredDate;
                        localStorage.setItem("offer_price",JSON.stringify(offer_price));
                        window.location.href = 'price.html';
                    }

                })
            }else{
                $("#reP").click(function(){
                    var vehicleExpiredDate = $("#Adeadline").val();
                    var businessExpiredDate = $("#Bdeadline").val();
                    console.log(vehicleExpiredDate,businessExpiredDate)
                    if(vehicleExpiredDate==''||vehicleExpiredDate==0||businessExpiredDate==''||businessExpiredDate==0){
                        errorMsg('请选择日期');
                        return false
                    }else{
                        localStorage.setItem('vehicleExpiredDate',vehicleExpiredDate);
                        localStorage.setItem('businessExpiredDate',businessExpiredDate);
                        window.location.href = 'offer.html';
                    }

                });
            }
        }
    },
    complete:function(){
        $(".loadings").hide()
    }
})

