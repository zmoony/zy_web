layui.use(['util', 'laydate','jquery','form','upload'], function(){
    var util = layui.util
        ,laydate = layui.laydate
        ,$ = layui.$
        ,form = layui.form
        ,upload = layui.upload
        ,layer = layui.layer;
    var loading_compaction;
    var url_picCompaction = "/day/pic/compaction";
    //提交
    $("#submitpic").off().on('click',function (e) {
        $("#demo2 img").attr('src',$("input[name='title']").val());
    });
    $("#submitpic1").off().on('click',function (e) {
        $("#demo3 img").attr('src',$("input[name='title']").val());
    });
    $("#resizeSubmit").off().on("click",function (e) {
        var type = $("input[name='fileType2']:checked").val();
        var base64,imgUrl,width,height,scale;
        if(type == 1){
             base64 = $('#uploadDemoView1').find('img').attr('src');
        }else{
             imgUrl = $("#preload").attr('src');
        }
        if(StrUtils.isEmptyObject(base64)&&StrUtils.isEmptyObject(imgUrl)){
            layer.msg('必须先有原图',{icon: 5});
            return false;
        }
        width = $("input[name='width']").val();
        height = $("input[name='height']").val();
        scale = $("input[name='scale']").val();
        var data = {
            "base64":base64,
            "imgUrl":imgUrl,
            "width":width,
            "height":height,
            "scale":scale
        };
        $.ajax({
            url:url_picCompaction,
            data:JSON.stringify(data),
            type:"POST",
            contentType:'application/json;charset=UTF-8',
            // dataType:"application/json",
            success:function (e) {
                layer.close(loading_compaction);
                if(e.success){
                    $("#compactionpic").attr('src',e.data.base64);
                    $("#compactionpic").show();
                    $("#download").show();
                    $("#oldsize").html('原图：'+e.data.oldSize+'B');
                    $("#newsize").html('压缩后：'+e.data.newSize+'B');
                }else{
                    layer.msg('压缩失败:'+e.message);
                }
            },
            error:function (e) {
                layer.close(loading_compaction);
                layer.msg('压缩失败');
            }
        });
    });
    //base64
    $("#base").off().on('click',function (e) {
        var type = $("input[name='fileType']:checked").val();
        if(type == 1){
            $("#dest").text($('#uploadDemoView').find('img').attr('src'));
        }else{
            var imgUrl = $("#preload").attr('src');
            getBase64(imgUrl, dataURL => {
                $("#dest").text(dataURL);
            });
        }
    });
    //字节流
    $("#byte").off().on('click',function (e) {
        var type = $("input[name='fileType']:checked").val();
        var baser;
        if(type == 1){
            baser = $('#uploadDemoView').find('img').attr('src');
        }else{
            var imgUrl = $("#preload").attr('src');
            getBase64(imgUrl, dataURL => {
                baser = dataURL;
            });
        }
        getByte(baser,byte => {
            $("#dest").text(byte);
        });
    });
    //全选
    $("fieldset i[title='全选']").off().on('click',function (e) {
        copyAllText($("#dest"));
    });
    $(".layui-form-label i[title='全选']").off().on('click',function (e) {
        copyAllText($("#src"));
    });
    //base64转换
    $(".layui-form-label i[title='运行']").off().on('click',function (e) {
        $("#base64pic").attr('src',$("#src").val())
    });
    //弹出框
    $("#resize").off().on('click',function (e) {
        loading_compaction = layer.open({
            type:1,//类型
            title:'压缩参数',//题目
            shadeClose:true,//点击遮罩层关闭
            content: $('#resizeboot')//打开的内容
        });
    });
    //监听
    form.on('radio(fileType)', function(data){
        var type = data.value;
        if(type == 1){
            $("#localpic").show();
            $("#onlinepic").hide();
        }else{
            $("#localpic").hide();
            $("#onlinepic").show();
        }
    });
    form.on('radio(fileType2)', function(data){
        var type = data.value;
        if(type == 1){
            $("#oldpic").show();
            $("#onlinepic1").hide();
        }else{
            $("#oldpic").hide();
            $("#onlinepic1").show();
        }
    });

    //拖拽上传
    upload.render({
        elem: '#test2'
        // ,url: 'https://httpbin.org/post' //改成您自己的上传接口
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                layui.$('#uploadDemoView').removeClass('layui-hide').find('img').attr('src', result);
            });
        }
        ,done: function(res){
            layer.msg('上传成功');
        }
        ,error: function(){
            layer.msg('上传失败');
        }
    });
    upload.render({
        elem: '#test3'
        // ,url: 'https://httpbin.org/post' //改成您自己的上传接口
        ,before: function(obj){
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                layui.$('#uploadDemoView1').removeClass('layui-hide').find('img').attr('src', result);
            });
        }
        ,done: function(res){
            layer.msg('上传成功');
        }
        ,error: function(){
            layer.msg('上传失败');
        }
    });


    //固定块
    util.fixbar({
        bar1: true
        ,bar2: false
        ,css: {right: 50, bottom: 100}
        ,bgcolor: '#393D49'
        ,click: function(type){
            if(type === 'bar1'){
                layer.msg('数据流是以Data URL协议来显示图片的，因此，将图片数据流直接复制到浏览器地址栏就可以显示出图片。而在网页中，数据流图片的典型应用如下：' +
                    'data:image/jpeg;base64,/9j/4AAQSkZJRg......OyJs1yro2/9k=;图片数据流的形成，是以二进制格式读取原图数据，然后用Base64编码数据形成64个ASCII字符组成的字符串，把这个字符串保存在代码中，浏览器需要显示该图像时根据数据代码直接绘制，而无需向服务器发送请求。Base64编码后的数据是原图数据量的4/3，这也是它的缺点之一。\n' +
                    '\n' +
                    '总之，数据流图片只适用于少量的较小图片。')
            }
        }
    });

});