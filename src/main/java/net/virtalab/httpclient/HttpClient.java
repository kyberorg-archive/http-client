package net.virtalab.httpclient;

import net.virtalab.httpclient.internal.RequestFactory;
import net.virtalab.logger.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Our HttpClient
 */
@SuppressWarnings("UnusedDeclaration")
public class HttpClient {

    private static Logger log = Logger.getLogger("HTTP");
    public static final String REQUEST = "REQUEST";
    public static final String RESPONSE = "RESPONSE";
    public static final String NEWLINE = System.lineSeparator();

    private HttpClient(){}

    private static HttpClient self = new HttpClient();
    private Request req;

    private class Request{
        public HttpMethod method;
        public String url;
        public List<Header> headers;
        public String payload;
    }

    public static HttpClient method(HttpMethod method){
        self.init(method);
        return self;
    }

    public static HttpClient get(){
        self.init(HttpMethod.GET);
        return self;
    }

    public static HttpClient post(){
        self.init(HttpMethod.POST);
        return self;
    }

    public static HttpClient put(){
        self.init(HttpMethod.PUT);
        return self;
    }

    public static HttpClient delete(){
        self.init(HttpMethod.DELETE);
        return self;
    }

    private void init(HttpMethod method){
        this.req = new Request();
        this.req.method = method;
    }

    public HttpClient url(String url){
        this.req.url = url;
        return this;
    }

    public HttpClient headers(List<Header> headers){
        this.req.headers = headers;
        return this;
    }

    public HttpClient headers(Header[] headers){
        this.req.headers = Arrays.asList(headers);
        return this;
    }

    public HttpClient header(Header header){
        List<Header> list;
        if(this.req.headers!=null && ! this.req.headers.isEmpty()){
            list = this.req.headers;
        } else {
            list = new ArrayList<Header>();
        }
        //add and store it back
        list.add(header);
        this.req.headers = list;

        return this;
    }

    public HttpClient payload(String payload){
        this.req.payload = payload;
        return this;
    }

    public Response exec() {
        //checking state
        Throwable t = null;
        if (this.req.method == null) {
            t = new IllegalStateException("Method cannot be NULL");
        }
        if (this.req.url == null) {
            t = new IllegalStateException("URL cannot be NULL: nowhere no send request");
        }
        if (t != null) {
            Response response = new Response();
            response.failSet(t);
            return response;
        }
        //log
        if (Logger.getCurrentLogLevelAsInt() >= Logger.Level.DEBUG.asInt()) {
            StringBuilder logLine = new StringBuilder();
            logLine.append(REQUEST).append(NEWLINE);
            logLine.append("URL: ").append(this.req.url).append(NEWLINE);
            logLine.append("Method: ").append(this.req.method.name()).append(NEWLINE);
            if (this.req.headers != null && !this.req.headers.isEmpty()) {
                logLine.append("Headers:").append(NEWLINE);
                for (Header h : this.req.headers) {
                    logLine.append(h.getName()).append(" : ").append(h.getValue()).append(NEWLINE);
                }
            }
            if (this.req.payload != null && !this.req.payload.isEmpty()) {
                logLine.append("Payload: ").append(NEWLINE);
                logLine.append(this.req.payload).append(NEWLINE);
            }

            log.debug(logLine.toString());
        }

        //action
        HttpRequestBase httpRequest = RequestFactory.produceObject(this.req.method, this.req.url);

        if (this.req.headers != null && !this.req.headers.isEmpty()) {
            for (Header h : this.req.headers) {
                httpRequest.addHeader(h);
            }
        }

        //payload
        if (this.req.payload != null && !this.req.payload.isEmpty()) {

            HttpEntityEnclosingRequestBase payloadableObject;
            try {
                payloadableObject = (HttpEntityEnclosingRequestBase) httpRequest;
                HttpEntity entity = new StringEntity(this.req.payload, HTTP.UTF_8);
                payloadableObject.setEntity(entity);
            } catch (ClassCastException cce) {
                log.warn("Cannot load payload to request. Method " + this.req.method + " doesn't support payload. Payload ignored.");
            }
        }
        HttpResponse httpResponse;

        CloseableHttpClient client = new DefaultHttpClient();

        try {
            httpResponse = client.execute(httpRequest);
        } catch (IOException e) {
            Response response = new Response();
            response.failSet(e);
            return response;
        } finally {
            client.getConnectionManager().shutdown();
        }

        //after successful execution:
        // create Response
        Response response = new Response();
        response.globalSet(httpResponse);

        // ... and log
        if (Logger.getCurrentLogLevelAsInt() >= Logger.Level.DEBUG.asInt()) {
            StringBuilder logLine = new StringBuilder();
            logLine.append(REQUEST).append(NEWLINE);
            logLine.append("StatusCode: ").append(response.getCode()).append(NEWLINE);
            logLine.append("Body: ").append(NEWLINE);
            logLine.append(response.getBody());

            log.debug(logLine.toString());
        }

        return response;
    }
}
