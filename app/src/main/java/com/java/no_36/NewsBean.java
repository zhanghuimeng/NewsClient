package com.java.no_36;

import java.net.URL;
import java.util.Date;

/**
 * 封装新闻的工具类
 * Created by admin on 2017/9/5.
 */

public class NewsBean
{
    private Keyword[] keywords;
    private Keyword[] bag_of_words;
    private String crawl_source; // 爬取来源的主页
    private Date crawl_time;
    private String[] inborn_keywords;
    private String lang_type;
    private Keyword[] locations;
    private int news_class_tag;
    private String news_author;
    private String news_category;
    private String news_content;
    private String news_id;
    private String news_journal;
    private String[] news_pictures;
    private String news_source;
    private Date news_time; // 和crawl time形成了鲜明的对比
    private String news_title;
    private String news_url; // 实际的新闻URL
    private String[] news_video;
    private Keyword[] organizations;
    private Keyword[] persons;
    private int repeat_id;
    private String segged_p_list_of_content;
    private String segged_title;
    private int word_count_of_content;
    private int word_count_of_title;

    public void setKeywords(Keyword[] keywords) { this.keywords = keywords; }
    public Keyword[] getKeywords() { return this.keywords; }

    public void setBag_of_words(Keyword[] bag_of_words) { this.bag_of_words = bag_of_words; }
    public Keyword[] getBag_of_words() { return bag_of_words; }

    public void setCrawl_source(String crawl_source) { this.crawl_source = crawl_source; }
    public String getCrawl_source() { return crawl_source; }

    public void setCrawl_time(Date crawl_time) { this.crawl_time = crawl_time; }
    public Date getCrawl_time() { return crawl_time; }

    public void setInborn_keywords(String[] inborn_keywords) { this.inborn_keywords = inborn_keywords; }
    public String[] getInborn_keywords() { return inborn_keywords; }

    public void setLang_type(String lang_type) { this.lang_type = lang_type; }
    public String getLang_type() { return lang_type; }

    public void setLocations(Keyword[] locations) { this.locations = locations; }
    public Keyword[] getLocations() { return locations; }

    public void setNews_class_tag(int news_class_tag) { this.news_class_tag = news_class_tag; }
    public void setNews_class_tag(String tag)
    {
        for (int i = 1; i <= 12; i++)
            if (NewsBriefBean.NEWS_CLASS_TO_STRING[i].equals(tag))
            {
                news_class_tag = i;
                return;
            }
        news_class_tag = 0;
    }
    public int getNews_class_tag() { return this.news_class_tag; }

    public void setNews_author(String news_author) { this.news_author = news_author; }
    public String getNews_author() { return this.news_author; }

    public void setNews_category(String news_category) { this.news_category = news_category; }
    public String getNews_category() { return news_category; }

    public void setNews_content(String news_content) { this.news_content = news_content; }
    public String getNews_content() { return news_content; }

    public void setNews_id(String news_id) { this.news_id = news_id; }
    public String getNews_id() { return news_id; }

    public void setNews_journal(String news_journal) { this.news_journal = news_journal; }
    public String getNews_journal() { return news_journal; }

    public void setNews_pictures(String[] news_pictures) { this.news_pictures = news_pictures; }
    public String[] getNews_pictures() { return news_pictures; }

    public void setNews_source(String news_source) { this.news_source = news_source; }
    public String getNews_source() { return news_source; }

    public void setNews_time(Date news_time) { this.news_time = news_time; }
    public Date getNews_time() { return news_time; }

    public void setNews_title(String news_title) { this.news_title = news_title; }
    public String getNews_title() { return news_title; }

    public void setNews_url(String news_url) { this.news_url = news_url; }
    public String getNews_url() { return news_url; }

    public void setNews_video(String[] news_video) { this.news_video = news_video; }
    public String[] getNews_video() { return news_video; }

    public void setOrganizations(Keyword[] organizations) { this.organizations = organizations; }
    public Keyword[] getOrganizations() { return organizations; }

    public void setPersons(Keyword[] persons) { this.persons = persons; }
    public Keyword[] getPersons() { return persons; }

    public void setRepeat_id(int repeat_id) { this.repeat_id = repeat_id; }
    public int getRepeat_id() { return repeat_id; }

    public void setSegged_p_list_of_content(String segged_p_list_of_content) { this.segged_p_list_of_content = segged_p_list_of_content; }
    public String getSegged_p_list_of_content() { return segged_p_list_of_content; }

    public void setSegged_title(String segged_title) {
        this.segged_title = segged_title;
    }
    public String getSegged_title() {
        return segged_title;
    }

    public void setWord_count_of_content(int word_count_of_content) {
        this.word_count_of_content = word_count_of_content;
    }
    public int getWord_count_of_content() {
        return word_count_of_content;
    }

    public void setWord_count_of_title(int word_count_of_title) {
        this.word_count_of_title = word_count_of_title;
    }
    public int getWord_count_of_title() {
        return word_count_of_title;
    }
}

class Keyword
{
    String word;
    double score;

    Keyword (String word, double score)
    {
        this.word = word;
        this.score = score;
    }
}