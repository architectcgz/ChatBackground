package com.example.chatplatform.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Random;

/**
 * @author archi
 */
public class CaptchaUtils {
    //图片宽度
    private int width = 160;
    //图片高度
    private int height = 40;
    //验证码字符个数
    private int codeCount = 4;
    //验证码干扰线数
    private int lineCount = 20;
    //验证码
    private String code = null;
    private BufferedImage bufferedImage = null;
    Random random = new Random();

    public void CreateImageCode(){
        createImage();
    }
    public void CreateImageCode(int width,int height){
        this.width = width;
        this.height = height;
        createImage();
    }
    public void CreateImageCode(int width,int height,int codeCount){
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        createImage();
    }
    public void CreateImageCode(int width,int height,int codeCount,int lineCount){
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        createImage();
    }
    private void createImage(){
        int fontWidth = width/codeCount;//字体宽度
        int fontHeight = height - 5;//字体高度
        int codeY = height - 8;
        //图像buffer
        bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        //设置背景颜色
        g.setColor(getRandColor(200,250));
        g.fillRect(0,0,width,height);
        //设置字体
        Font font = new Font("Fixedsys",Font.BOLD,fontHeight);
        //设置干扰线
        for(int i =0;i<lineCount;i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }
        //添加噪点
        float yawpRate = 0.01f;//噪声率
        int area = (int)(yawpRate*width*height);
        for(int i = 0;i<area;i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bufferedImage.setRGB(x,y,random.nextInt(255));
        }
        String str1 = randomStr(codeCount);
        this.code = str1;
        for(int i =0;i<codeCount;i++){
            String randStr = str1.substring(i,i+1);
            g.setColor(getRandColor(1,255));
            g.drawString(randStr,i*fontWidth+3,codeY);
        }
    }
    //得到随机字符
    private String randomStr(int n){
        String str1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str2 = "";
        int str1Length = str1.length()-1;
        double r;
        for(int i =0;i<n;i++){
            r = (Math.random())*str1Length;
            str2 = str2+str1.charAt((int)r);
        }
        return str2;
    }
    //得到随机颜色
    private Color getRandColor(int fc,int bc){//给定范围得到随机颜色
        if(fc>255) {
            fc = 255;
        }
        if(bc>255) {
            bc = 255;
        }
        int r = fc+random.nextInt(bc-fc);
        int g = fc+ random.nextInt(bc-fc);
        int b = fc+ random.nextInt(bc-fc);
        return new Color(r,g,b);
    }
    public BufferedImage getBufferedImage(){return bufferedImage;}
    public String getCode() {return code.toLowerCase(Locale.ROOT);}
}
