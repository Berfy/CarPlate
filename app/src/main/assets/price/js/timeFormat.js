/**
 * Created by hanxue on 2016/11/24.
 */
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