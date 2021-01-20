layui.use(['form','jquery','element'], function(){
    var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
    var form = layui.form;
    form.on('select(component)', function(data){
        var value = data.value;
        location.href =  value;
    });
    //寻找对应的index
    var pathname = location.pathname;
    //增加选中的样式
    $("a[href='"+pathname+"']").parents("li").addClass("layui-nav-itemed");
    $("a[href='"+pathname+"']").addClass("layui-this");
    var findex=0;
    if(pathname.indexOf("plugin") != -1){
        findex=1;
    }
    // var findex = $("a[href='"+pathname+"']").parent("li").index();
    //添加主标题样式
    // var findex = $(".layui-nav").find(".layui-this").index();
    $(".layui-nav-item").removeClass("layui-this");
    $(".layui-nav-item").eq(findex).addClass("layui-this");
    //添加副标题样式
    $(".layui-nav.layui-nav-tree.site-demo-nav").removeClass("layui-show");
    $(".layui-nav.layui-nav-tree.site-demo-nav").eq(findex).addClass("layui-show");

    /*$(".layui-nav-item").off().on('click',function (e) {
        var index = $(this).index()//获取当前点击的索引
        $(".layui-nav.layui-nav-tree.site-demo-nav").removeClass("layui-show");
        $(".layui-nav.layui-nav-tree.site-demo-nav").eq(index).addClass("layui-show");
    });*/
});
$(function(){
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-bottom-right",
        "onclick": null,
        "showDuration": "500",
        "hideDuration": "300",
        "timeOut": "2000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
});
