layui.use(['util', 'laydate','form'], function(){
    var util = layui.util
        ,laydate = layui.laydate
        ,layer = layui.layer;
    //******************************jstree的初始化*****************************
    // $('#jstree_demo_div').jstree();
    function customMenu(node)
    {
        var items={
            "upload" : {
                "label" : "上传",
                "icon"  : "layui-icon layui-icon-upload-drag",
                "action" : function (data){
                    //弹出windows的选择按钮
                    $("body").append("<input type=\"file\" id=\"file\"   \n" +
                        "            style=\"filter:alpha(opacity=0);opacity:0;width: 0;height: 0;\"/>   ")
                    $("#file").trigger("click");
                    $("#file").off().on("change",function (){
                        /*let fileName = data.target.value;
                            let file = data.target.file[0];*/
                        var formData = new formData();
                        formData.append('file',$("#file").get(0).getFiles[0]);
                        //上传操作
                    });
                }
            },
            "rename" : {
                "label" : "重命名",
                "icon"	:"layui-icon layui-icon-edit",
                "action" : function(data) {
                    //直接修改
                    var inst = $.jstree.reference(data.reference),
                        obj = inst.get_node(data.reference);
                    //后台更新名称
                    inst.edit(obj);
                }
            },
            "deleteItems" : {
                "label" : "删除",
                "icon"	:"layui-icon layui-icon-delete",
                "action" : function(data) {
                    //弹框删除
                    layer.confirm('确定删除当前选择？', {
                        btn: ['确定','取消'] //按钮
                    }, function(){
                        var ref = $.jstree.reference(data.reference),
                            obj = ref.get_node(data.reference);
                        //后台删除，返回成功后
                        ref.delete_node(obj);
                        layer.msg('删除成功', {icon: 1});
                    });
                }

            }
        }
        //console.log(node);
        if(node.text.indexOf(".") != -1){    //如果是文件
            delete items.upload;
        }
        return items;    //注意要有返回值
    }
    $('#jstree_demo_div').jstree({
        'core' : {
            "check_callback" : true,//允许选中后操作
            'data' : [
                {
                    'text' : '目录',
                    'state' : {
                        'opened' : true,
                        'selected' : false
                    },
                    // 'children' :jstreeFunc(url,appName,ip,'getdirandfile')
                    'children' :[
                        {
                            'text' : 'config'

                        },
                        {
                            'text' : 'install',
                            'state' : {
                                'opened' : true,
                                'selected' : false
                            },
                            'children' : [
                                { 'text' : 'install.bat',"icon": "jstree-file" },
                                { 'text' : 'uninstall.bat',"icon": "jstree-file" }
                            ]
                        },
                        {
                            'text' : 'log',
                            'state' : {
                                'opened' : false,
                                'selected' : false
                            },
                            'children' : [
                                { 'text' : '12log.log',"icon": "jstree-file" },
                                { 'text' : '13log.log' ,"icon": "jstree-file"}
                            ]
                        },
                        {text:'alltoes.jar',"icon": "jstree-file"},
                        {text:'alltoes.exe',"icon": "jstree-file"},
                        {text:'alltoes.xml',"icon": "jstree-file"}
                    ]
                }
            ]
            ,"themes" : {
                "variant" : "large",//加大
                "ellipsis" : true ,//文字多时省略
                "icons" : true//是否显示图标,为true时，默认显示图标为文件夹图标。
            }
            ,"multiple" : false//禁止多选
        }
        ,'expand_selected_onload' : true //选中项蓝色底显示
        ,"types" : {
            "default" : {
                "valid_children" : ["default","file"]
            },
            "file" : {
                "icon" : "glyphicon glyphicon-file",
                "valid_children" : []
            }
        }
        ,"contextmenu":{
            "items" : customMenu
        }
       /* ,"checkbox" : {
            "keep_selected_style" : false
        // }*/
        ,"plugins" : [ "dnd","search","types","themes","contextmenu","state" ]
    })
        /*获取选择的节点
        .on('select_node.jstree', function(event, data) {
        console.log(data.node);
        }).on('changed.jstree', function(event,data) {
        console.log("-----------changed.jstree");
        console.log("action:" + data.action);
        console.log(data.node);
        });*/
       /* 合并所有节点
       .bind("loaded.jstree", function (e, data) {
            data.inst.open_all(-1); // -1 打开所有节点
        }).delegate("a", "click", function (event, data) {
        event.preventDefault();
        window.location.href = $(this).attr("href");
    });*/
    /*监听节点变化
    $('#jstree_demo_div')
        .bind("rename_node.jstree", function (event, data) {
            console.log(data)
            var r = jstreeFunc(url,data.node.original.fullname,ip,'rename',data.node.text);
            if(r.flag){
                z_alert.tip_info("修改成功");
            }
        })
        .bind("create_node.jstree", function (event, data) {

        });*/
    //******************************监听*****************************
    $("#create").off().on('click',create);
    $("#rename").off().on('click',rename);
    $("#del").off().on('click',del);
    $("#moveup").off().on('click',moveup);
    $("#movedown").off().on('click',movedown);
    //输入框输入定时自动搜索
    var to = false;
    $('#search_ay').keyup(function () {
        if(to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $('#jstree_demo_div').jstree(true).search($('#search_ay').val());

        }, 250);
    });
    function create(){
        var ref = $('#jstree_demo_div').jstree(true);
        var currNode = _getCurrNode();
        currNode = ref.create_node(currNode, {"type":"file"});
        if(currNode) {
            ref.edit(currNode);
        }
    }
    function rename(){
        var ref = $('#jstree_demo_div').jstree(true);
        var currNode = _getCurrNode();
        ref.rename_node(currNode,"rename node222");
    }
    function del(){
        var ref = $('#jstree_demo_div').jstree(true);
        var currNode = _getCurrNode();
        ref.delete_node(currNode);
    }
    function moveup(){
        var ref = $('#jstree_demo_div').jstree(true);
        var currNode = _getCurrNode();
        var prevNode = ref.get_prev_dom(currNode,true);
        ref.move_node(currNode,prevNode,'before');
    }
    function movedown(){
        var ref = $('#jstree_demo_div').jstree(true);
        var currNode = _getCurrNode();
        var nextNode = ref.get_next_dom(currNode,true);//返回兄弟节点的下一个节点
        ref.move_node(currNode,nextNode,'after');
    }
    /**
     *    获取当前所选中的结点
     */
    function _getCurrNode(){
        var ref = $('#jstree_demo_div').jstree(true),
            sel = ref.get_selected();
        console.log(sel);
        if(!sel.length) {
            console.log("----");
            return false;
        }
        sel = sel[0];
        return sel;
    }
    /*$('#jstree_demo_div').on("changed.jstree", function (e, data) {
        console.log(data.selected);
    });*/
    //******************************与按钮的交互*****************************
    /*$('button').on('click', function () {
        $('#jstree_demo_div').jstree(true).select_node('child_node_1');
        $('#jstree_demo_div').jstree('select_node', 'child_node_1');
        $.jstree.reference('#jstree_demo_div').select_node('child_node_1');
    });*/
});