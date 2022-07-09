package com.web.demo.util;

import com.web.demo.vo.pic.PicCompactionVo;
import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author yuez
 * @title: PicCompactionUtil
 * @projectName zy_web
 * @description: 图片压缩工具类
 * @date 2020/11/25 9:30
 */
public class PicCompactionUtil {
    //根据url压缩图片
    public static BufferedImage urlCompaction(PicCompactionVo vo) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        byte[] bytes = HttpClientUtil.doGetUrl(vo.getImgUrl());
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
        return thumbnailsUtil(bufferedImage,vo);
    }
    //根据base64压缩图片
    public static BufferedImage base64Compaction(PicCompactionVo vo) throws IOException {
        String base64 = vo.getBase64();
        base64 = base64.substring(base64.indexOf(",")+1);
        byte[] decode = Base64Util.decode(base64);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decode));
        return thumbnailsUtil(bufferedImage,vo);
    }

    //压缩方法--压缩质量还原读最高
    private static BufferedImage thumbnailsUtil (BufferedImage image,PicCompactionVo vo) throws IOException {
        BufferedImage bufferedImage;
        if(StringUtil.isBlank(vo.getScale()+"")){
            bufferedImage = Thumbnails.of(image).size(vo.getWidth(),vo.getHeight()).asBufferedImage();
        }else{
            bufferedImage = Thumbnails.of(image).scale(vo.getScale()).asBufferedImage();
        }
        //压缩质量还原读最高
//        Thumbnails.of(image).scale(1d).outputQuality(0.5d).outputFormat("jpg").toOutputStream(new ByteArrayOutputStream());
        return bufferedImage;
    }
}
