import java.io.IOException;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {
    public static Map<String, List<String>> scrape(String url, List<String> options) throws IOException {
        Map<String, List<String>> data = new HashMap<>();
        Document doc = Jsoup.connect(url).get();

        if (options.contains("title")) {
            data.put("title", Collections.singletonList(doc.title()));
        }

        if (options.contains("headings")) {
            List<String> headings = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                Elements elems = doc.select("h" + i);
                for (Element e : elems) {
                    headings.add("h" + i + ": " + e.text());
                }
            }
            data.put("headings", headings);
        }

        if (options.contains("links")) {
            List<String> links = new ArrayList<>();
            Elements elems = doc.select("a[href]");
            for (Element e : elems) {
                links.add(e.attr("abs:href") + " - " + e.text());
            }
            data.put("links", links);
        }

        return data;
    }
}
