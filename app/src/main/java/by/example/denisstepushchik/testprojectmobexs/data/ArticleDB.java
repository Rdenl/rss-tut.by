package by.example.denisstepushchik.testprojectmobexs.data;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "article")
public class ArticleDB {

    @DatabaseField(dataType = DataType.STRING)
    private String iconURL;

    @DatabaseField(dataType = DataType.STRING, id = true)
    private String title;

    @DatabaseField(dataType = DataType.STRING)
    private String pubDate;

    @DatabaseField(dataType = DataType.STRING)
    private String imageURL;

    @DatabaseField(dataType = DataType.STRING)
    private String text;

    @DatabaseField(dataType = DataType.STRING)
    private String link;


    ArticleDB() {
    }

    public ArticleDB(String title, String pubDate, String imageURL, String text, String iconURL, String link) {
        this.title = title;
        this.pubDate = pubDate;
        this.imageURL = imageURL;
        this.text = text;
        this.iconURL = iconURL;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getImageUrl() {
        return imageURL;
    }

    public String getIconUrl() {
        return iconURL;
    }

    public String getText() {
        return text;
    }

    public String getLink(){
        return link;
    }
}
