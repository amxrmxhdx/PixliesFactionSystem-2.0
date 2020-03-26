package me.mickmmars.minimick.utils;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class googleSearch {

    public static void sendSearch(TextChannel channel, String key) throws IOException {
        String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
        String charset = "UTF-8";
        Reader reader = null;
        String userAgent = "MiniMick";
        EmbedBuilder builder = new EmbedBuilder();

        Elements links = Jsoup.connect(google + URLEncoder.encode(key, charset)).userAgent(userAgent).get().select(".g>.r>a");
        int results = 0;

        builder.setColor(Color.CYAN);
        builder.setTitle("Search results for `" + key + "`");

        for (Element link : links) {
            String title = link.text();
            String url = link.absUrl("href");
            url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

            if (results > 3)
                break;

            if (!url.startsWith("http"))
                continue;

            results++;

            builder.addField(title, url, false);

        }

        channel.sendMessage(builder.build());
    }

}
