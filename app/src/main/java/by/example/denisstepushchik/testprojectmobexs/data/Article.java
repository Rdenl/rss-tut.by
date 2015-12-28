package by.example.denisstepushchik.testprojectmobexs.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "item", strict = false)
public class Article {

    public String getPubDate() {
        return pubDate;
    }

    @Element
    private String title;


    @Element(required = false)
    private String description;

    @Element
    private String link;

    @Element(required = false)
    private String pubDate;


    @Element(name = "enclosure", required = false)
    private Enclosure enclosure;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLink() {
        return this.link;
    }

    public Enclosure getEnclosure() {
        return this.enclosure;
    }
}
