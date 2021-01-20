var url_db_export = "/db/export";

layui.use(['form','jquery'], function(){
    var form = layui.form;
    var $ = layui.jquery;
    //监听提交
    form.on('submit(formDemo)', function(data){
        layer.msg('开始异步导出');
        $.ajax({
            dataType:'json',
            type:'post',
            url: url_db_export,
            contentType: 'application/json',
            data:JSON.stringify(data.field),
            success:function(data){
                if(data.success){
                    layer.msg('导出成功');
                }else{
                    layer.msg(data.message);
                }
            },
            error:function(e){
                layer.msg(e);
                console.log(e);
            }
        });
        return false;
    });
});