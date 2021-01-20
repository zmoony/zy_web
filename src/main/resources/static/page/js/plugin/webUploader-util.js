/**
 * 小文件上传没有问题，大文件上传出错
 *
 */
$(function () {
    URL = {
        url_file_upload: '/plugin/webUploader/upload'
    }
    PROPERTIES = {
        //限制上传个数
        fileNumLimit: 8,
        //分片大小,5MB
        chunk_size: 5 * 1024 * 1024,
        //文件最大大小,100G
        file_max_size: 100 * 1024 * 1024 * 1024,
        //文件md5验证位置
        file_check_block_part: [5, 15, 25, 35, 45, 55, 65, 75, 85, 95],
        //并发上传数
        upload_thread_num: 20
    }
    //需要进行后期赋值，如果不加var 将会报错 此对象找不到未进行赋值操作
    var uploader;

    _constructor();

    function _constructor() {
        _createUploader();
        _registerHook();
    }

    //初始化uploader
    function _createUploader() {
        uploader = WebUploader.create({
            // auto: true, // 选完文件后，是否自动上传
            swf: '/plugin/webuploader-0.1.5/Uploader.swf',// swf文件路径
            // 文件接收服务端。
            server: URL.url_file_upload,
            pick: '#picker',// 选择文件的按钮。可选
            fileNumLimit: PROPERTIES.fileNumLimit, //限制上传个数
            // fileSingleSizeLimit: 2048000, //限制单个上传图片的大小 1M=1024000B。
            // 只允许选择图片文件。
            /*accept: {
                title: 'Images',
                extensions: 'jpg,jpeg,png',
                mimeTypes: 'image/!*'
            },*/
            chunked: true,//是否分片
            chunkSize: PROPERTIES.chunk_size,//分片大小，默认5M
            threads: PROPERTIES.upload_thread_num,//上传并发数，默认3
            fileSingleSizeLimit: PROPERTIES.file_max_size,//单个文件大小验证
            //[可选] 配置生成缩略图的选项
            /* thumb: {
                 type: 'image/jpg,jpeg,png'
             },*/
            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            // resize: false
        });

        // 当有文件被添加进队列的时候
        uploader.on('fileQueued', function (file) {
            var tr = $(['<tr id="' + file.id + '">'
                , '<td>' + file.name + '</td>'
                , '<td>' + (file.size / 1024).toFixed(1) + 'kb</td>'
                , '<td class="state">等待上传</td>'
                , '<td>'
                , '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                , '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                , '</td>'
                , '</tr>'].join(''));

            //单个重传
            tr.find('.demo-reload').on('click', function () {
                uploader.upload($(this).parent().parent().attr('id'));
            });

            //删除
            tr.find('.demo-delete').on('click', function () {
                //可传文件file或者id
                uploader.removeFile($(this).parent().parent().attr('id'), true);//删除对应的文件
                tr.remove();
            });

            $("#demoList").append(tr);
        });
        // 文件上传过程中创建进度条实时显示。
        uploader.on('uploadProgress', function (file, percentage) {
            var $li = $('#' + file.id);
            $li.find('td.state').text('上传中:' + percentage * 100 + '%');
        });

        uploader.on('uploadSuccess', function (file) {
            $('#' + file.id).find('td.state').text('已上传');
        });

        uploader.on('uploadError', function (file) {
            $('#' + file.id).find('td.state').text('上传出错');
            $('#' + file.id).find('.demo-reload').removeClass('layui-hide');
        });

        uploader.on('uploadComplete', function (file) {
            $('#' + file.id).find('.progress').fadeOut();
        });

        $("#testListAction").off().on('click', function (e) {
            uploader.upload();
        });

    }

    //添加hook监听(hook是全局的)
    function _registerHook() {
        _unRegisterHook();
        WebUploader.Uploader.register({
            'name': 'webUploaderHookCommand',
            'before-send-file': 'beforeSendFile',////在文件发送之前request，此时还没有分片（如果配置了分片的话），可以用来做文件整体md5验证。
            "before-send": "beforeSend",////在分片发送之前request，可以用来做分片验证，如果此分片已经上传成功了，可返回一个rejected promise来跳过此分片上传
            "after-send-file":"afterSendFile",////在所有分片都上传完毕后，且没有错误后request，用来做分片验证，此时如果promise被reject，当前文件上传会触发错误。
            "add-file":"addFile" //用来向队列中添加文件。
        },{
            beforeSendFile:function (file){

            },
            beforeSend:function (block){

            },
            afterSendFile:function (file){

            },
            addFile:function (files){

            }
        });
    }

    //移除hook事件
    function _unRegisterHook() {
        WebUploader.Uploader.unRegister('addFile');
        WebUploader.Uploader.unRegister('beforeSendFile');
        WebUploader.Uploader.unRegister('beforeSend');
        WebUploader.Uploader.unRegister('afterSendFile');
    }


});