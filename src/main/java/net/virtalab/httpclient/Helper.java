package net.virtalab.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Bunch of static methods that help to extract some data from HttpResponse
 */
public class Helper {

    public static int getStatusCode(HttpResponse resp){
        return resp.getStatusLine().getStatusCode();
    }

    public static String bodyToString(HttpResponse resp) throws IOException {
        StringBuilder body = new StringBuilder("");

        HttpEntity entity = resp.getEntity();
        if(entity==null){
            //no body -  no problem
            return body.toString();
        }
        InputStream content = entity.getContent();

        if(content != null){
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(( content )));

            String output;
            while ((output = br.readLine()) != null) {
                body.append(output);
            }
        }

        return body.toString();
    }


    public static BasicHeader[] getDefaultGetHeaders(){
        BasicHeader acceptHeader = new BasicHeader("accept","application/json");

        BasicHeader[] headers = {acceptHeader};
        return headers;
    }

    public static BasicHeader[] getDefaultPostHeaders(){
        BasicHeader acceptHeader = new BasicHeader("accept","application/json");
        BasicHeader contentType = new BasicHeader("Content-Type", "application/json");

        BasicHeader[] headers = {acceptHeader, contentType};
        return headers;
    }
}
