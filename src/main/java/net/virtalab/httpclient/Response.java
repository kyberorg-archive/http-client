package net.virtalab.httpclient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Object rep
 */
public class Response {
    private int statusCode;
    private Header[] headers;
    private String body;

    private Status execStatus;
    private Throwable exceptionSlot;


    public int getStatusCode(){
        return this.statusCode;
    }
    public int getCode(){
        return this.getStatusCode();
    }

    public Header[] getHeaders(){
        return this.headers;
    }

    public List<Header> getHeadersAsList(){
        return Arrays.asList(this.headers);
    }

    public String getBody(){
        return this.body;
    }

    public Status status(){
        return this.execStatus;
    }

    public Throwable showException(){
        return this.exceptionSlot;
    }

    public void globalSet(HttpResponse response){
        this.execStatus = Status.OK;

        this.statusCode = Helper.getStatusCode(response);
        this.headers = response.getAllHeaders();

        try {
            this.body = Helper.bodyToString(response);
        } catch (IOException e) {
            this.execStatus = Status.FAIL;
            this.exceptionSlot = e;
        }
    }

    public void failSet(Throwable exception){
        this.execStatus = Status.FAIL;
        this.exceptionSlot = exception;
    }

    //TODO override toString and do var_dump
    @Override
    public String toString(){
        String NEWLINE = System.lineSeparator();

        StringBuilder dump = new StringBuilder();

        dump.append("StatusCode: ").append(statusCode).append(NEWLINE);

        for (Header header : headers) {
            dump.append("Header: ")
                .append(header.getName()).append(":").append(header.getValue())
                .append(NEWLINE);
        }

        if(body!=null)
        dump.append("Body: ").append(body).append(NEWLINE);

        if(execStatus!=null)
        dump.append("ExecStatus").append(execStatus.name()).append(NEWLINE);

        if(exceptionSlot!=null)
        dump.append("Exception Slot: ").append(exceptionSlot).append(NEWLINE);

        return dump.toString();
    }

    public enum Status{
        OK,
        FAIL
    }
}
