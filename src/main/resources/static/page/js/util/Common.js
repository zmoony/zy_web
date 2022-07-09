// 【字符串通用】
var StrUtils = {
    getBytes:function (str,encode) {
        if(encode.toUpperCase() == Encode.GBK){
            return StrUtils.conversizeType(str.replace(/[^\u0000-\u00ff]/g,"aa").length);
        }
        if(encode.toUpperCase() == Encode.UTF8){
            return StrUtils.conversizeType(str.replace(/[^\u0000-\u00ff]/g,"aaa").length);
        }
    },
    conversizeType:function (limit) {
        var size = "";
        if( limit < 0.1 * 1024 ){ //如果小于0.1KB转化成B
            size = limit.toFixed(2) + "B";
        }else if(limit < 0.1 * 1024 * 1024 ){//如果小于0.1MB转化成KB
            size = (limit / 1024).toFixed(2) + "KB";
        }else if(limit < 0.1 * 1024 * 1024 * 1024){ //如果小于0.1GB转化成MB
            size = (limit / (1024 * 1024)).toFixed(2) + "MB";
        }else{ //其他转化成GB
            size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB";
        }

        var sizestr = size + "";
        var len = sizestr.indexOf("\.");
        var dec = sizestr.substr(len + 1, 2);
        if(dec == "00"){//当小数点后为00时 去掉小数部分
            return sizestr.substring(0,len) + sizestr.substr(len + 3,2);
        }
        return sizestr;
    },
    isEmptyObject:function (tmp) {
        if(typeof(tmp) == "undefined"||tmp==null||tmp==''){
            return true;
        }
        return false;
    }

};
// 【时间通用】---采用moment.js
/*
var strToDate = moment("1994-10-10", "YYYY-MM-DD");// 字符串转日期
var dateTostr = moment().format('YYYY-MM-DD HH:mm:ss');// 日期转字符串
var date = moment();// 当前时间戳
var date1 = moment(1566011829275).format('YYYY-MM-DD HH:mm:ss');// 时间戳转时间
var day = new Date(2019, 8, 17);// 设置时间
var dayWrapper = moment(day);
var isAfter = moment('2019-01-18').isAfter('2019-05-10');// 判断一个日期是否在某个日期之后
*/
var DateUtils = {
    tempToDate:function (timestamp) {
        var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        var D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + ' ';
        var h = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours()) + ':';
        var m = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes()) + ':';
        var s = (date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds());

        strDate = Y+M+D+h+m+s;
        return strDate;
    }
};

//【base64转blob流和文件】
function dataURLtoFile(dataurl, filename) {//将base64转换为文件
    var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, {type:mime});
}
function dataURItoBlob(base64Data) {
    //console.log(base64Data);//data:image/png;base64,
    var byteString;
    if(base64Data.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(base64Data.split(',')[1]);//base64 解码
    else{
        byteString = unescape(base64Data.split(',')[1]);
    }
    var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0];//mime类型 -- image/png

    // var arrayBuffer = new ArrayBuffer(byteString.length); //创建缓冲数组
    // var ia = new Uint8Array(arrayBuffer);//创建视图
    var ia = new Uint8Array(byteString.length);//创建视图
    for(var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    var blob = new Blob([ia], {
        type: mimeString
    });
    return blob;
}
//blob流转换为base64
function blobToDataURI(blob, callback) {
    var reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onload = function (e) {
        callback(e.target.result);
    }
}
function getByte(base64, callback) {
   /* base64 = base64.replace('data:image/jpeg;base64,','');
    callback($.base64.decode(base64));*/
    var geturl = "/day/pic/base64ToByte";
    base64 = base64.replace('data:image/jpeg;base64,','');
    $.post(geturl,{'base64':base64},function (data) {
        if(data.success){
            callback(data.data.bytes);
        }else{
            callback(data.message);
        }

    });
}
//网络图片转base64
function getBase64(url, callback) {
    var geturl = "/day/pic/urlToBase64?url="+$.base64.encode(url);
    $.ajaxSettings.async = false;
    $.get(geturl,function (data) {
        if(data.success){
            callback(data.data.base64);
        }else{
            callback(data.message);
        }
    });
    $.ajaxSettings.async = true;
    /*存在跨域问题
    var Img = new Image(),
        dataURL = '';
    // Img.src = url + '?v=' + Math.random();
    Img.src = url;
    // Img.setAttribute('crossOrigin', 'Anonymous');
    Img.onload = function() {
        var canvas = document.createElement('canvas'),
            width = Img.width,
            height = Img.height;
        canvas.width = width;
        canvas.height = height;
        canvas.getContext('2d').drawImage(Img, 0, 0, width, height);
        dataURL = canvas.toDataURL('image/jpeg');
        return callback ? callback(dataURL) : null;
    };*/
}

//将内容复制到剪切板
function copyCommand(id)
{
    var source = $("#" + id);
    /*let textArea = document.createElement('textarea');
    textArea.style.display = 'none';
    textArea.value = source.text();
    textArea.select();*/
    try {
        copyAllText(source);
        document.execCommand('copy');
    } catch (err) {
        console.log('不能使用这种方法复制内容' + err.toString());
    }
    toastr.info("文本已复制到剪切板, 请直接粘贴");
}

//设置div文本全选
function copyAllText(source)
{
    var text= source[0];
    if (document.body.createTextRange)
    {
        console.log("文本复制：createTextRange");
        var range = document.body.createTextRange();
        range.moveToElementText(text);
        range.select();
    }
    else if (window.getSelection)
    {
        console.log("文本复制：getSelection");
        var selection = window.getSelection();
        var range = document.createRange();
        range.selectNodeContents(text);
        selection.removeAllRanges();
        selection.addRange(range);
    }
    else
    {
        console.log("文本复制：none");
    }
};
/*******************常量的定义********************/
var Encode = {
    GBK:"GBK",UTF8:"UTF8"
};
var sizeType = {
    B:"B",KB:"KB",MB:"MB",GB:"GB"
};