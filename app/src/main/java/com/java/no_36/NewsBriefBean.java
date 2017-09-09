package com.java.no_36;

/**
 * 封装新闻简介的工具类
 */
/*
{"lang_Type":"zh-CN","newsClassTag":"科技","news_Author":"中国网","news_ID":"201609130413037a073b2cdb4768aff90eff46f2665b",
"news_Pictures":"http://imge.gmw.cn/attachement/png/site2/20160912/f44d305ea5951940d3fd4f.png;http://imge.gmw.cn/attachement/png/site2/20160912/f44d305ea5951940d3fd50.png;http://imge.gmw.cn/attachement/png/site2/20160912/f44d305ea5951940d3fd51.png;http://imge.gmw.cn/attachement/png/site2/20160912/f44d305ea5951940d3fd52.png;http://imge.gmw.cn/attachement/png/site2/20160912/f44d305ea5951940d3fd53.png",
"news_Source":"光明网","news_Time":"20160912000000","news_Title":"吴亦凡亮相荣耀极光派对 全新定义不凡潮品荣耀8",
"news_String":"http://e.gmw.cn/2016-09/12/content_21944565.htm","news_Video":"",
"news_Intro":"　　9月9日，一场科技撞击潮流的派对在北京举行。荣耀品牌携手吴亦凡华丽亮相荣..."}
 */

import java.util.Date;

// lang_type, news_class_tag, news_author, news_id,
// news_pictures(list), news_source, news_time, news_title, news_String, news_video(list), news_intro
public class NewsBriefBean
{
    public static final String[] NEWS_CLASS_TO_STRING = {"", "科技", "教育", "军事", "国内", "社会", "文化",
        "汽车", "国际", "体育", "财经", "健康", "娱乐"};
    private String lang_type;
    private int news_class_tag;
    private String news_author;
    private String news_id;
    private String[] news_pictures;
    private String news_source;
    private Date news_time;
    private String news_title;
    private String news_url;
    private String[] news_video;
    private String news_intro;
    private int news_isread;

    void setLang_type(String lang_type) { this.lang_type = lang_type; }
    String getLang_type() { return lang_type; }

    void setNews_class_tag(int news_class_tag) { this.news_class_tag = news_class_tag; }
    void setNews_class_tag(String tag)
    {
        for (int i = 1; i <= 12; i++)
            if (NEWS_CLASS_TO_STRING[i].equals(tag))
            {
                news_class_tag = i;
                return;
            }
        news_class_tag = 0;
    }
    int getNews_class_tag() { return news_class_tag; }

    void setNews_author(String news_author) { this.news_author = news_author; }
    String getNews_author() { return news_author; }

    void setNews_id(String news_id) { this.news_id = news_id; }
    String getNews_id() { return news_id; }

    void setNews_pictures(String[] news_pictures) { this.news_pictures = news_pictures; }
    String[] getNews_pictures() { return news_pictures; }

    void setNews_source(String news_source) { this.news_source = news_source; }
    String getNews_source() { return news_source; }

    void setNews_time(Date news_time) { this.news_time = news_time; }
    Date getNews_time() { return news_time; }

    void setNews_title(String news_title) { this.news_title = news_title; }
    String getNews_title() { return this.news_title; }

    void setNews_url(String news_String) { this.news_url = news_String; }
    String getNews_url() { return news_url; }

    void setNews_video(String[] news_video) { this.news_video = news_video; }
    String[] getNews_video() { return this.news_video; }

    void setNews_intro(String news_intro) { this.news_intro = news_intro; }
    String getNews_intro() { return news_intro; }

    void setNews_isread(int news_isread) { this.news_isread = news_isread; }
    int getNews_isread() { return news_isread; }
}
