var url_db_transform = "/db/transform";
layui.use(['form','jquery'], function(){
    var form = layui.form;
    var $ = layui.jquery;
    //初始赋值
    var value_init = function(){
        $("textarea[name='ddl']").html("").html("CREATE TABLE 'userinfo' (\n" +
            "  'user_id' int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',\n" +
            "  'username' varchar(255) NOT NULL COMMENT '用户名',\n" +
            "  'addtime' datetime NOT NULL COMMENT '创建时间',\n" +
            "  PRIMARY KEY ('user_id')\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息'\n" +
            "        ");
    };
    //通用后台请求
    var formAjax = function(data,div){
        layer.msg('开始异步生成');
        $.ajax({
            dataType:'json',
            type:'post',
            url: url_db_transform,
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

    //初始化
    value_init();
    form.on('select(type)',function (data) {
        if(data.value == 'JSON'){
            $("textarea[name='ddl']").html("").html("{\n" +
                "    \"author\": \"aurora\",\n" +
                "    \"type\": \"DDL\",\n" +
                "    \"nameRex\": \"camel\"\n" +
                "}");
        }else{
            $("textarea[name='ddl']").html("").html("CREATE TABLE 'userinfo' (\n" +
                "  'user_id' int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',\n" +
                "  'username' varchar(255) NOT NULL COMMENT '用户名',\n" +
                "  'addtime' datetime NOT NULL COMMENT '创建时间',\n" +
                "  PRIMARY KEY ('user_id')\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息'\n" +
                "        ");
        }
    });
    //监听提交
    form.on('submit(formDemo)', function(data){
        formAjax(data,$("#ddl"));
        return false;
    });

    form.on('submit(formDemo2Code)', function(data){
        if(data.field.sql==''&&data.field.tableName==''){
            layer.msg('表名和SQL不可都为空',{icon: 5});
            return false;
        }
        data.field.type = 'TABLE2CODE';
        formAjax(data,$("#ddl2"));
        return false;
    });

    form.on('submit(formDemo2Json)', function(data){
        if(data.field.sql==''&&data.field.tableName==''){
            layer.msg('表名和SQL不可都为空',{icon: 5});
            return false;
        }
        data.field.type = 'TABLE2JSON';
        formAjax(data,$("#ddl2"));
        return false;
    });

    form.on('submit(formDemo2Column)', function(data){
        if(data.field.sql==''&&data.field.tableName==''){
            layer.msg('表名和SQL不可都为空',{icon: 5});
            return false;
        }
        data.field.type = 'TABLE2COLUMN';
        formAjax(data,$("#ddl2"));
        return false;
    });
});