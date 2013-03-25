package org.apache.sling.coffee;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.coffee.exception.CoffeeCompileException;
/**
 * 
 * Service that interfaces to the CoffeeScript compiler and JCR to produce JavaScript.
 * 
 * @author bpaulin
 *
 */
public interface SlingCoffeeScriptCache {
    
    
    /**
     * 
     * Returns Cache of compiled CofffeeScript.
     * If it's out of date or does not yet exists 
     * the coffeescript is compiled and saved to /var/coffeescript
     * 
     * @param session JCRSession
     * @param path Path to CoffeeScript file
     * @return
     * @throws RepositoryException
     * @throws IOException
     */
    public String getJavascript(Session session, String path) throws CoffeeCompileException;
    

}
