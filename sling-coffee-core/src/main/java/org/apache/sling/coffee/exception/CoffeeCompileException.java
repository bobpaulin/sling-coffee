package org.apache.sling.coffee.exception;

/**
 * 
 * Exception for CoffeeScript compilation
 * 
 * @author bpaulin
 *
 */
public class CoffeeCompileException extends Exception {
    
    public CoffeeCompileException() {
        super();
    }
    
    public CoffeeCompileException(Throwable e)
    {
        super(e);
    }
    
    public CoffeeCompileException(String message)
    {
        super(message);
    }
    
    public CoffeeCompileException(String message, Throwable e)
    {
        super(message, e);
    }
}
