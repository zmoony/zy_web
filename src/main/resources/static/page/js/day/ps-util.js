var url_ps_commond = "/day/ps/commond";
layui.use(['form','jquery'], function(){
    var form = layui.form;
    var $ = layui.jquery;
    //事件监听textarea的内容
    $("textarea[name='command']").on('input propertychange',function(){
        $(".layui-icon.layui-icon-file-b").html(StrUtils.getBytes($("textarea[name='command']").val(),Encode.UTF8),"");
    });
    //监听提交
    form.on('submit(formDemo)', function(data){
        formAjax(data,$("#ddl"));
        return false;
    });
    //提交表单
    //通用后台请求
    var formAjax = function(data,div){
        layer.msg('开始异步生成');
        $.ajax({
            dataType:'json',
            type:'post',
            url: url_ps_commond,
            contentType: 'application/json',
            data:JSON.stringify(data.field),
            success:function(data){
                if(data.success){
                    layer.msg('生成成功');
                }else{
                    layer.msg(data.message);
                }
                div.html('')
                $.each(data.data,function(k,n){
                    div.append("<div class=\"layui-form-item layui-form-text layui-col-md11\">\n" +
                        "                            <label class=\"layui-form-label\">"+k+"</label>\n" +
                        "                            <div class=\"layui-input-block\">\n" +
                        "                                <textarea style=\"min-height: 200px;\" name=\"result\" placeholder=\"语句\" class=\"layui-textarea\">"+n+"</textarea>\n" +
                        "                            </div>\n" +
                        "                        </div>");
                });
            },
            error:function(e){
                layer.msg(e);
                console.log(e);
            }
        });
    };
});