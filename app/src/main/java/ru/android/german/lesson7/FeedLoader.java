package ru.android.german.lesson7;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.ResultReceiver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by german on 08.11.14.
 */
public class FeedLoader extends IntentService {
    public FeedLoader() {
        super("FeedLoader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        URL url;
        String urlString = intent.getStringExtra("url");
        System.out.println("I'm in handle");
        try {
            url = new URL(urlString);

            URLConnection connection;
            connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document document = db.parse(in);
                Element element = document.getDocumentElement();
                Element channel = (Element)element.getElementsByTagName("channel").item(0);
                NodeList items = channel.getElementsByTagName("item");
                if (items != null && items.getLength() > 0) {
                    for (int i = 0; i < items.getLength(); i++) {
                        Element item = (Element)items.item(i);
                        Element titleElement = (Element)item.getElementsByTagName("title").item(0);
                        Element linkElement = (Element)item.getElementsByTagName("link").item(0);

                        String title = titleElement.getFirstChild().getNodeValue();
                        String link = linkElement.getFirstChild().getNodeValue();

                        ContentValues values = new ContentValues();
                        values.put(FeedContentProvider.FEED_TITLE, title);
                        values.put(FeedContentProvider.FEED_LINK, link);
                        Uri uri = getContentResolver().insert(FeedContentProvider.FEEDS_CONTENT_URI,
                                values);
//                        System.out.println(uri.toString());
//                        receiver.send(0, null);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class RSSHandler extends DefaultHandler {
        String currentElement = "";

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        }
    }
}
