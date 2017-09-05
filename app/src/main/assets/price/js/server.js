WEB_ADDR = 'http://test.wolaibao.com/wlb_official';
//API_ADDR = 'http://192.168.1.7:8080/wlb_svr';
// API_ADDR = 'http://192.168.1.102:8080';
// API_ADDR = 'https://www.wolaibao.com';
API_ADDR = 'http://test.wolaibao.com/wlb_svr';
var token = localStorage.getItem("token");
headers = {
    platform: "wlb_html",
    "Wlb-Token":token,
}
