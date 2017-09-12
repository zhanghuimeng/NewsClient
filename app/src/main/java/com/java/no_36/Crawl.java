package com.java.no_36;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawl {

    // 地址
    private static final String URL = "http://www.1tu.com/search/?k=";
    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";


    public static String[] get_picture(String keyword) {
        try {
            String new_URL = URL + keyword;
            //获得html文本内容
            String HTML = getHtml(new_URL);
            //获取图片标签
            String[] imgUrl = getImageUrl(HTML);
            //获取图片src地址
            String[] imgSrc = getImageSrc(imgUrl);
            //下载图片
            //Download(imgSrc);
            return imgSrc;

        }catch (Exception e){
            System.out.println("发生错误");
            return null;
        }

    }

    //获取HTML内容
    static private String getHtml(String url)throws Exception{
        URL url1=new URL(url);
        URLConnection connection=url1.openConnection();
        InputStream in=connection.getInputStream();
        InputStreamReader isr=new InputStreamReader(in);
        BufferedReader br=new BufferedReader(isr);

        String line;
        StringBuffer sb=new StringBuffer();
        while((line=br.readLine())!=null){
            sb.append(line,0,line.length());
            sb.append('\n');
        }
        br.close();
        isr.close();
        in.close();
        return sb.toString();
    }

    //获取ImageUrl地址
    static private String[] getImageUrl(String html){
        Matcher matcher=Pattern.compile(IMGURL_REG).matcher(html);
        String []listimgurl = new String[3];
        int count = 0;
        while (matcher.find()){
            listimgurl[count] = new String(matcher.group());
            count ++;
            if(count == 3)
                break;
        }
        return listimgurl;
    }

    //获取ImageSrc地址
    static private String[] getImageSrc(String[] listimageurl){
        String[] listImageSrc=new String[3];
        int i = 0;
        for (String image:listimageurl){
            Matcher matcher=Pattern.compile(IMGSRC_REG).matcher(image);
            if (matcher.find()){
                listImageSrc[i] = new String(matcher.group().substring(0, matcher.group().length()-1));
                i ++;
            }
            if(i == 3)
                break;
        }
        return listImageSrc;
    }
}