/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.web.demo.vo.powershell;

import com.mysql.cj.util.StringUtils;
import com.web.demo.common.R;
import io.cloudsoft.winrm4j.client.WinRmClientContext;
import io.cloudsoft.winrm4j.winrm.WinRmTool;
import io.cloudsoft.winrm4j.winrm.WinRmToolResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.config.AuthSchemes;

import java.util.Collections;

/**
 * 成功代码为 0  异常10086
 * @author Administrator
 */
@Log4j2
public class WinRmUtil {
    public static R executeCommand(WinRMBean winrm){
        WinRmClientContext context = WinRmClientContext.newInstance();
        
        try{
            WinRmTool.Builder builder = WinRmTool.Builder.builder(winrm.getHost_ip(), winrm.getUsername(), winrm.getPassword());
            builder.setAuthenticationScheme(AuthSchemes.NTLM);
            builder.port(5985);
            builder.useHttps(false);
            builder.context(context);

            WinRmTool tool = builder.build();
            tool.setOperationTimeout(new Long(3)); //超时设置
            tool.setRetriesForConnectionFailures(1); //连接尝试次数
            WinRmToolResponse resp;
            if(winrm.getIfps()==1){
               resp=tool.executePs(winrm.getCommand()); //执行powershell脚本
            }else{
               resp=tool.executeCommand(winrm.getCommand()); //执行常规dos脚本
            }
            //如果网络异常以及无法远程操作 执行不到下面代码
            return R.ok().setData(Collections.singletonMap("stdOut", StringUtils.isEmptyOrWhitespaceOnly(resp.getStdOut())?resp.getStdErr():resp.getStdOut()));
        }catch(Exception ex){
            log.error("服务器远程失败:{}",ex);
            return R.error().setMessage("服务器远程失败，请检查网络或winrm配置！");
        }finally {
            context.shutdown();
        }
    }
    
}
