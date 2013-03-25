package org.apache.sling.coffee.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.coffee.CoffeeScriptCompiler;
import org.apache.sling.coffee.exception.CoffeeCompileException;
import org.apache.sling.commons.osgi.PropertiesUtil;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.osgi.service.component.ComponentContext;

/**
 * 
 * Implementation of CoffeeScript compiler using Rhino
 * 
 * @author bpaulin
 *
 */

@Component(immediate=true, metatype=true)
@Service
public class CoffeeScriptCompilerImpl implements CoffeeScriptCompiler {
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    private ScriptableObject scope = null;
    
    @org.apache.felix.scr.annotations.Property(value="/system/coffee/coffee-script.js")
    private final static String COFFEE_COMPILER_PATH = "coffee.compiler.path";
    
    private String coffeeCompilerPath;
    
    public void activate(final ComponentContext context) throws Exception
    {
        Dictionary config = context.getProperties();
        coffeeCompilerPath = PropertiesUtil.toString(config.get(COFFEE_COMPILER_PATH), "/system/coffee/coffee-script.js");
        loadCoffeeScriptCompiler();
    }
    
    /**
     *  Compile CoffeeScript with Rhino
     */
    public String compile(String coffeeScript) throws CoffeeCompileException
    {
        StringBuffer scriptBuffer = new StringBuffer();
        scriptBuffer.append("CoffeeScript.compile(");
        scriptBuffer.append(toJSMultiLineString(coffeeScript));
        scriptBuffer.append(", ");
        scriptBuffer.append("{});");
        try{
            Context rhinoContext = getContext();
            rhinoContext.initStandardObjects(scope);
            return (String)rhinoContext.evaluateString(scope, scriptBuffer.toString(), "CoffeeScript", 1, null);
        }
        catch(Exception e)
        {
           throw new CoffeeCompileException(e);
        }
        finally
        {
            if (Context.getCurrentContext() != null) {
                Context.exit();
            }
        }
    }
    
    /**
     * 
     * Loads CoffeeScript compiler stream to Rhino
     * 
     * @throws LoginException
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws ValueFormatException
     * @throws IOException
     */
    protected void loadCoffeeScriptCompiler() throws LoginException,
            PathNotFoundException, RepositoryException, ValueFormatException,
            IOException {
        Context rhinoContext = getContext();
        ResourceResolver resolver = null;
        try{
            resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            
            InputStream content = getCoffeeScriptJsStream(resolver);
            scope = (ScriptableObject) rhinoContext.initStandardObjects(null);
            rhinoContext.evaluateReader(scope, new InputStreamReader(content), "coffee-script.js", 1, null);
        }
        finally
        {
            if(resolver != null)
            {
                resolver.close();
            }
        }

    }

    /**
     * 
     * Finds CoffeeScript compiler JS and converts it to a stream.
     * 
     * @param resolver
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws ValueFormatException
     */
    protected InputStream getCoffeeScriptJsStream(ResourceResolver resolver)
            throws PathNotFoundException, RepositoryException,
            ValueFormatException {
        Resource coffeeCompilerResource = resolver.getResource(coffeeCompilerPath);
        Node coffeeNode = coffeeCompilerResource.adaptTo(Node.class);
        Node jcrContent = coffeeNode.getNode(Property.JCR_CONTENT);
        InputStream content = jcrContent.getProperty(Property.JCR_DATA).getBinary().getStream();
        return content;
    }
    
    /**
     * Transforms a java multi-line string into javascript multi-line string. This technique was found at {@link http
     * ://stackoverflow.com/questions/805107/multiline-strings-in-javascript/}
     * 
     * @param data
     *          a string containing new lines.
     * @return a string which being evaluated on the client-side will be treated as a correct multi-line string.
     */
    public String toJSMultiLineString(final String data) {
      final String[] lines = data.split("\n");
      final StringBuffer result = new StringBuffer("[");
      if (lines.length == 0) {
        result.append("\"\"");
      }
      for (int i = 0; i < lines.length; i++) {
        final String line = lines[i];
        result.append("\"");
        result.append(line.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
        // this is used to force a single line to have at least one new line (otherwise cssLint fails).
        if (lines.length == 1) {
          result.append("\\n");
        }
        result.append("\"");
        if (i < lines.length - 1) {
          result.append(",");
        }
      }
      result.append("].join(\"\\n\")");
      return result.toString();
    }
    
    /**
     * 
     * Retrieves Rhino Context and sets language and optimizations.
     * 
     * @return
     */
    public Context getContext()
    {
        Context result = null;
        if(Context.getCurrentContext() == null)
        {
            Context.enter(); 
        }
        result = Context.getCurrentContext();
        result.setOptimizationLevel(-1);
        //result.setLanguageVersion(Context.VERSION_1_8);
        result.setLanguageVersion(Context.VERSION_1_7);
        return result;
    }
    
    public void setResourceResolverFactory(
            ResourceResolverFactory resourceResolverFactory) {
        this.resourceResolverFactory = resourceResolverFactory;
    }
}
