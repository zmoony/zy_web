package com.web.demo.service;

import com.sun.tools.rngom.parse.host.Base;
import com.web.demo.common.R;
import com.web.demo.util.Base64Util;
import com.web.demo.util.HttpClientUtil;
import com.web.demo.util.PicCompactionUtil;
import com.web.demo.util.StringUtil;
import com.web.demo.vo.pic.PicCompactionVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuez
 * @title: PicService
 * @projectName zy_web
 * @description: 图片处理
 * @date 2020/11/6 11:08
 */
@Service
@Log4j2
public class PicService {
    @Autowired
    private HttpClientUtil httpClientUtil;

    public R urlToByte (String url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        byte[] bytes = httpClientUtil.doGetUrl(url);
        return R.ok().setData(Collections.singletonMap("byte",bytes));
    }

    public R urlToBase64 (String url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        byte[] bytes = httpClientUtil.doGetUrl(url);
        return R.ok().setData(Collections.singletonMap("base64", "data:image/jpeg;base64,"+Base64.getEncoder().encodeToString(bytes)));
    }

    public R picCompaction(PicCompactionVo vo) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Map<String,Object> map = new HashMap<>();
        BufferedImage bufferedImage;
        int oldSize;
        int newSize;
        String imgUrl = vo.getImgUrl();
        String prefx = "data:image/jpeg;base64,";
        if(StringUtil.isBlank(imgUrl)){
            prefx = vo.getBase64().substring(0,vo.getBase64().indexOf(",")+1);
            oldSize = Base64Util.decode(vo.getBase64().substring(vo.getBase64().indexOf(",")+1)).length;
            bufferedImage  = PicCompactionUtil.base64Compaction(vo);
        }else{
            oldSize = HttpClientUtil.doGetUrl(vo.getImgUrl()).length;
            bufferedImage = PicCompactionUtil.urlCompaction(vo);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if(prefx.contains("png")){
            ImageIO.write(bufferedImage, "png", outputStream);
        }else{
            ImageIO.write(bufferedImage, "jpg", outputStream);
        }
        newSize = outputStream.size();
        map.put("oldSize",oldSize);
        map.put("newSize",newSize);
        map.put("base64",prefx+Base64.getEncoder().encodeToString(outputStream.toByteArray()));
        return R.ok().setData(map);
    }
}
