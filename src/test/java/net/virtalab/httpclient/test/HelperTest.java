package net.virtalab.httpclient.test;

import net.virtalab.httpclient.Helper;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Add headers
 */
public class HelperTest {

    @Test
    public void addHeaders(){
        BasicHeader[] headers = Helper.getDefaultPostHeaders();
        int expected = 2;
        Assert.assertEquals(expected,headers.length);
    }
}
