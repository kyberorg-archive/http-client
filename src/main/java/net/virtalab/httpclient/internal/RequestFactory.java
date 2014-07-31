package net.virtalab.httpclient.internal;

import net.virtalab.httpclient.HttpMethod;
import org.apache.http.client.methods.*;


/**
 * Creates Request object (HttpGet, HttpPost...)
 */
public class RequestFactory {

    public static HttpRequestBase produceObject(HttpMethod method, String url){
        HttpRequestBase object;
        switch (method){
            case GET:
                object = new HttpGet(url);
                break;
            case POST:
                object = new HttpPost(url);
                break;
            case PUT:
                object = new HttpPut(url);
                break;
            case DELETE:
                object = new HttpDelete(url);
                break;
            default:
                throw new IllegalArgumentException("Method not supported yet.");
        }
        return object;
    }

}
