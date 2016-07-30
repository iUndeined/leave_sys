package me.cjd.message;

import java.util.ArrayList;
import java.util.List;

import me.cjd.utils.Config;

public class NewsMsg extends BaseMsg {
	
	public News news;
	
	public NewsMsg(String touser, String title, String description, String url) {
		super(touser, "news", String.valueOf(Config.getQyApi().getAppId()));
		this.news = new News(title, description, url);
	}
	
}

class News {
	
	public List<Articles> articles;
	
	public News(String title, String description, String url){
		this.articles = new ArrayList<>(1);
		this.articles.add(new Articles(title, description, url));
	}
	
	public News(List<Articles> articles) {
		super();
		this.articles = articles;
	}
	
}

class Articles {
	
	public String title;
	public String description;
	public String url;
	public String picurl;
	
	public Articles(String title, String description, String url) {
		super();
		this.title = title;
		this.description = description;
		this.url = url;
	}
	
}
