package com.web.demo.controller;

import com.web.demo.common.R;
import com.web.demo.service.PicService;
import com.web.demo.service.PowershellService;
import com.web.demo.vo.pic.PicCompactionVo;
import com.web.demo.vo.powershell.WinRMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

/**
 * @author yuez
 * @title: DayController
 * @projectName zy_web
 * @description: 通用工具类
 * @date 2020/9/22 10:52
 */
@RequestMapping("/day")
@RestController
public class DayController {
    @Autowired
    private PowershellService powershellService;
    @Autowired
    private PicService picService;

    @PostMapping(value = "/ps/commond")
    public R powershellexe(@RequestBody WinRMBean vo) {
        return powershellService.exeCommond(vo);
    }

    @GetMapping(value = "/pic/urlToBase64")
    public R urlToBase64(@RequestParam("url") String url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return picService.urlToBase64(new String(Base64.getDecoder().decode(url)));
    }
    @PostMapping(value = "/pic/base64ToByte")
    public R base64ToByte(@RequestParam("base64") String base64) {
        return R.ok().setData(Collections.singletonMap("bytes", Arrays.toString(Base64.getDecoder().decode(base64))));
    }
    @PostMapping(value = "/pic/compaction")
    public R picCompaction(@RequestBody PicCompactionVo picCompactionVo) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        return picService.picCompaction(picCompactionVo);
    }
}
