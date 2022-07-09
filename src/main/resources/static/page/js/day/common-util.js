var url_db_transform = "/db/transform";
layui.use(['util', 'laydate','jquery','form'], function(){
    var util = layui.util
        ,laydate = layui.laydate
        ,$ = layui.$
        ,layer = layui.layer;
    var js_timestamp_now = $('#js_timestamp_now');
    var js_timestamp = $('#js_timestamp');
    var js_timestamp_o = $('#js_timestamp_o');
    var js_timestamp_unit = $('#js_timestamp_unit');
    var js_timestamp_unit_o = $('#js_timestamp_unit_o');
    var js_datetime_o = $('#js_datetime_o');
    var js_timer_stop = $('#js_timer_stop');
    var js_timer_start = $('#js_timer_start');
    var js_convert_timestamp = $('#js_convert_timestamp');
    var msNow = +new Date();
    var now = Math.round(msNow / 1000);
    if (window.localStorage['js_timestamp_unit'] !== 'ms') {
        js_timestamp.val(now);
    } else {
        js_timestamp.val(msNow);
    }
    js_datetime_o.val(moment(msNow).format('YYYY-MM-DD HH:mm:ss'));
    var clock = function () {
        var clockNow = +new Date();
        if (window.localStorage['js_timestamp_unit'] !== 'ms') {
            clockNow = Math.round(clockNow / 1000);
        }
        js_timestamp_now.text(clockNow);
    };
    var timer = setInterval(clock, 1000);
    js_timer_stop.off().on('click',function (e) {
        js_timer_stop.hide();
        e.preventDefault();
        if (timer) clearInterval(timer);
        js_timer_start.show();
    });
    js_timer_start.off().on('click', function (e) {
        e.preventDefault();
        js_timer_start.hide();
        if (timer) clearInterval(timer);
        timer = setInterval(clock, 1000);
        js_timer_stop.show();
    });
    js_timestamp_now.off().on('click',function (e) {
        js_timestamp.val($(this).text())
    });
    js_convert_timestamp.off().on('click', function (e) {
        e.preventDefault();
        var timestamp = js_timestamp.val();
        timestamp = timestamp.replace(/^\s+|\s+$/, '');
        if (!/^-?\d+$/.test(timestamp)) {
            alert("时间戳格式不正确");
            return;
        }
        timestamp *= 1;
        if (js_timestamp_unit.val() === 's') {
            timestamp *= 1000;
        }
        $('#js_datetime').val(moment(timestamp).format('YYYY-MM-DD HH:mm:ss'));
    });
    $('#js_convert_datetime').off().on('click', function (e) {
        e.preventDefault();
        var date = +moment(js_datetime_o.val(),'YYYY-MM-DD HH:mm:ss');
        if (js_timestamp_unit_o.val() === 'ms') {
            js_timestamp_o.val(date );
        } else {
            js_timestamp_o.val(date/1000);
        }
    });
    $("#format").off().on('click',function (e) {
        var json_o = JSON.parse($("#src").val());
        $("#dest").jsonViewer(json_o);
    });
    $("#bean").off().on('click',function (e) {
        var json_o = $("#src").val();
        var beans = getBeanFieldFromJson(json_o, "JsonRootBean");
        var count = 1;
        var html = "";
        $.each(beans,function (i,o) {
            var beanText = toBeanText(o, "package com.web.demo");
            html =  count == 1 ? beanText : html + "\n\n=======================================================\n" + beanText;
            count++;
        });
        $("#dest").html(html);

    });
    $("#zip").off().on('click',function (e) {
        var json_o =JSON.parse($("#src").val());
        $("#dest").text(JSON.stringify(json_o));
    });
    $("#src").off().on('input',function (e) {
        if($(this).val() == ''){
            $(".in").hide();
        }else {
            $(".in").show();
        }
    });
    $(".in").off().on('click',function (e) {
        $("#src").val('');
    });
    $(".out").off().on('click',function (e) {
        copyCommand("dest");
    });



    //固定块
    util.fixbar({
        bar1: false
        ,bar2: false
        ,css: {right: 50, bottom: 100}
        ,bgcolor: '#393D49'
        ,click: function(type){
            if(type === 'bar1'){
                layer.msg('icon 是可以随便换的')
            } else if(type === 'bar2') {
                layer.msg('两个 bar 都可以设定是否开启')
            }
        }
    });

    //倒计时
    var thisTimer, setCountdown = function(y, M, d, H, m, s){
        var endTime = new Date(y, M||0, d||1, H||0, m||0, s||0) //结束日期
            ,serverTime = new Date(); //假设为当前服务器时间，这里采用的是本地时间，实际使用一般是取服务端的

        clearTimeout(thisTimer);
        util.countdown(endTime, serverTime, function(date, serverTime, timer){
            var str = date[0] + '天' + date[1] + '时' +  date[2] + '分' + date[3] + '秒';
            lay('#test2').html(str);
            thisTimer = timer;
        });
    };
    setCountdown(2021,2-1,10);

    laydate.render({
        elem: '#test1'
        ,type: 'datetime'
        ,done: function(value, date){
            setCountdown(date.year, date.month - 1, date.date, date.hours, date.minutes, date.seconds);
        }
    });


    //某个时间在当前时间的多久前
    var setTimeAgo = function(y, M, d, H, m, s){
        var str = util.timeAgo(new Date(y, M||0, d||1, H||0, m||0, s||0));
        lay('#test4').html(str);
    };
    laydate.render({
        elem: '#test3'
        ,type: 'datetime'
        ,done: function(value, date){
            setTimeAgo(date.year, date.month - 1, date.date, date.hours, date.minutes, date.seconds);
        }
    });

    //转换日期格式
    var toDateString = function(format){
        var dateString = util.toDateString(new Date(), format); //执行转换日期格式的方法
        $('#test6').html(dateString);
    };
    toDateString($('#test5').val());
    //监听输入框事件
    $('#test5').on('keyup', function(){
        toDateString(this.value);
    });

    //处理属性 为 lay-active 的所有元素事件
    util.event('lay-active', {
        e1: function(){
            layer.msg('触发了事件1');
        }
        ,e2: function(){
            layer.msg('触发了事件2');
        }
        ,e3: function(){
            layer.msg('触发了事件3');
        }
    });

    //XSS 过滤
    $('#test8').on('click', function(){ //监听按钮事件
        var str = util.escape($('#test7').val()); //执行 xss 过滤方法
        alert(str);
    });
});