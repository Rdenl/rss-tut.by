package by.example.denisstepushchik.testprojectmobexs.data;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {

    @ElementList(name = "item", inline = true)
    List<Article> articleList;

    public List<Article> getArticles() {
        return this.articleList;
    }
}
