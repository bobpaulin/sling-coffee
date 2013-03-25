package org.apache.sling.coffee;

import org.apache.sling.coffee.exception.CoffeeCompileException;

/**
 * 
 * Service to compile CoffeeScript.
 * 
 * @author bpaulin
 *
 */

public interface CoffeeScriptCompiler {
    /**
     * 
     * Compile CoffeeScript String to JavaScript
     * 
     * @param coffeeScript
     * @return
     */
    public String compile(String coffeeScript) throws CoffeeCompileException;
}
