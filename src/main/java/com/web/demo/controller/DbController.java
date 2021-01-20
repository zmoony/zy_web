package com.web.demo.controller;

import com.web.demo.common.R;
import com.web.demo.service.DbService;
import com.web.demo.vo.DBExportVo;
import com.web.demo.vo.DBTransformVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuez
 * @title: DbController
 * @projectName zy_web
 * @description: 数据库工具集
 * @date 2020/8/28 15:40
 */
@RequestMapping("/db")
@RestController
public class DbController {
    @Autowired
    private DbService DbService;

    @PostMapping(value = "/export")
    public R dbExport(@RequestBody DBExportVo dBExportVo) {
        return DbService.exportDML(dBExportVo);
    }
    @PostMapping(value = "/transform")
    public R dbTransform(@RequestBody DBTransformVo vo) {
        return DbService.transformJAVA(vo);
    }
}
