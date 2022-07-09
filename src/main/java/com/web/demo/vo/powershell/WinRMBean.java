package com.web.demo.vo.powershell;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class WinRMBean {
    private String host_ip; //主机名
    private String username; //用户名
    private String password; //密码
    private String command; //命令
    private int ifps=1;//powershell/cmd
}