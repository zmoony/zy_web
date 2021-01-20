package com.web.demo.service;

import com.web.demo.common.R;
import com.web.demo.exception.GlobalException;
import com.web.demo.vo.powershell.WinRMBean;
import com.web.demo.vo.powershell.WinRmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yuez
 * @title: PowershellService
 * @projectName zy_web
 * @description: PowershellService
 * @date 2020/9/22 11:00
 */
@Slf4j
@Service
public class PowershellService {

    /**
    　　* @description: 远程执行powershell命令
    　　* @param [vo]
    　　* @return com.web.demo.common.R
    　　* @throws
    　　* @author yuez
    　　* @date 2020/9/22 11:01
    　　*/
    public R exeCommond(WinRMBean vo) throws GlobalException {
        return WinRmUtil.executeCommand(vo);
    }
}
