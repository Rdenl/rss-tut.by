package by.example.denisstepushchik.testprojectmobexs.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class Rss {
    @Element
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }
}