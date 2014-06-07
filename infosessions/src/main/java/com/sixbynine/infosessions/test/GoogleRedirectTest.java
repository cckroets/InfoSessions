package com.sixbynine.infosessions.test;

import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author curtiskroetsch
 */
public class GoogleRedirectTest
{

  /**
   * Proof of concept test that we can access a crunchbase permalink of a google search
   * for the employer
   * @throws Exception
   */
  @Test public void testRedirect() throws Exception {

    final String url = "http://www.google.com/search?q=crunchbase+microsoft&btnI";
    URLConnection con = new URL(url).openConnection();
    System.out.println("Orignal URL: " + con.getURL());

    /* Google blocks the default Java User-Agent, trick it instead! */
    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
    con.connect();
    InputStream is = con.getInputStream();
    System.out.println("Redirected URL: " + con.getURL()); // http://www.crunchbase.com/organization/microsoft
    is.close();
  }

}
