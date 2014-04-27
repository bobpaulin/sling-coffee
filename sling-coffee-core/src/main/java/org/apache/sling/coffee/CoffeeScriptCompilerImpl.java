package org.apache.sling.coffee;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.webresource.WebResourceScriptCompiler;
import org.apache.sling.webresource.WebResourceScriptRunner;
import org.apache.sling.webresource.WebResourceScriptRunnerFactory;
import org.apache.sling.webresource.exception.WebResourceCompileException;
import org.apache.sling.webresource.util.JCRUtils;
import org.apache.sling.webresource.util.ScriptUtils;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Implementation of CoffeeScript compiler using Rhino
 * 
 * @author bpaulin
 *
 */

@Component(label="CoffeeScript Compiler Service", immediate=true, metatype=true)
@Service
public class CoffeeScriptCompilerImpl implements WebResourceScriptCompiler {
    
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    private WebResourceScriptRunnerFactory webResourceScriptRunnerFactory;
    
    @org.apache.felix.scr.annotations.Property(label="CoffeeScript Compiler Script Path", value="/system/coffee/coffee-script.js")
    private final static String COFFEE_COMPILER_PATH = "coffee.compiler.path";
    
    @org.apache.felix.scr.annotations.Property(label="CoffeeScript Cache Path", value="/var/coffeescript")
    private final static String COFFEE_CACHE_PATH = "coffee.cache.path";
    
    @org.apache.felix.scr.annotations.Property(label="CoffeeScript Compiler Bare Option", boolValue=false)
    private final static String COFFEE_COMPILE_OPTION_BARE = "coffee.compile.option.bare";
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private String coffeeCompilerPath;
    
    private String coffeeCachePath;
    
    private boolean coffeeCompileOptionBare;
    
    private WebResourceScriptRunner scriptRunner;
    
    public void activate(final ComponentContext context) throws Exception
    {
        Dictionary config = context.getProperties();
        coffeeCompilerPath = PropertiesUtil.toString(config.get(COFFEE_COMPILER_PATH), "/system/coffee/coffee-script.js");
        coffeeCachePath = PropertiesUtil.toString(config.get(COFFEE_CACHE_PATH), "/var/coffeescript");
        coffeeCompileOptionBare = PropertiesUtil.toBoolean(config.get(COFFEE_COMPILE_OPTION_BARE), false);
        
        loadCoffeeScriptRunner();
    }

	private void loadCoffeeScriptRunner() throws LoginException,
			PathNotFoundException, RepositoryException, ValueFormatException {
		ResourceResolver resolver = null;
        try{
            resolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            
            InputStream content = getCoffeeScriptJsStream(resolver);
            this.scriptRunner = webResourceScriptRunnerFactory.createRunner("coffee-script.js", content);
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
     *  Compile CoffeeScript with Rhino
     */
    public InputStream compile(InputStream coffeeScriptStream) throws WebResourceCompileException
    {
        return compile(coffeeScriptStream, null);
    }
    
    public InputStream compile(InputStream coffeeScriptStream, Map<String, Object> compileOptions) throws WebResourceCompileException
    {
        Map<String, Object> coffeeCompileOptions = new HashMap<String, Object>();
        processCompileOptions(compileOptions,
                coffeeCompileOptions);
        try{
            String coffeeScript = IOUtils.toString(coffeeScriptStream);
            StringBuffer scriptBuffer = new StringBuffer();
            scriptBuffer.append("CoffeeScript.compile(");
            scriptBuffer.append(ScriptUtils.toJSMultiLineString(coffeeScript));
            scriptBuffer.append(", ");
            if(coffeeCompileOptions.isEmpty())
            {
                scriptBuffer.append("{}");
            }
            else
            {
                scriptBuffer.append(ScriptUtils.generateCompileOptionsString(coffeeCompileOptions));
            }
            scriptBuffer.append(");");
            InputStream coffeeScriptCompileStream = new ByteArrayInputStream(scriptBuffer.toString().getBytes());
            
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            
            String compiledScript = scriptRunner.evaluateScript(coffeeScriptCompileStream, new HashMap<String, Object>());
            
            stopWatch.stop();
            log.debug("Completed CoffeeScript Compile " + stopWatch);
            
            return new ByteArrayInputStream(compiledScript.getBytes());
        }
        catch(Exception e)
        {
           throw new WebResourceCompileException(e);
        }
    }
    /**
     * 
     * Merges JCR and OSGi Compile options.
     * 
     * @param compileOptions
     * @param coffeeCompileOptions
     * @return
     */
    protected Map<String, Object> processCompileOptions(
            Map<String, Object> compileOptions,
            Map<String, Object> coffeeCompileOptions) {
        if(coffeeCompileOptionBare)
        {
            coffeeCompileOptions.put("bare", "true");
        }
        
        if(compileOptions != null && compileOptions.get("coffeescript") != null)
        {
            coffeeCompileOptions.putAll((Map<String, Object>) compileOptions.get("coffeescript"));
        }
        
        return coffeeCompileOptions;
    }
    
    public String getCacheRoot()
    {
        return this.coffeeCachePath;
    }
    
    public boolean canCompileNode(Node sourceNode)
    {
        String extension = null;
        String mimeType = null;
        try{
           
            if (sourceNode.hasNode(Property.JCR_CONTENT)) {
                Node sourceContent = sourceNode.getNode(Property.JCR_CONTENT);
                if(sourceContent.hasProperty(Property.JCR_MIMETYPE))
                {
                    mimeType = sourceContent.getProperty(Property.JCR_MIMETYPE).getString();
                }
            }
           extension = JCRUtils.getNodeExtension(sourceNode);

        }catch(RepositoryException e)
        {
            //Log Exception
            log.info("Node Name can not be read.  Skipping node.");
        }
        
        return "coffee".equals(extension) || "text/coffeescript".equals(mimeType);
    }
    
    public String compiledScriptExtension()
    {
        return "js";
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
        return jcrContent.getProperty(Property.JCR_DATA).getBinary().getStream();
    }
    
    public void setResourceResolverFactory(
            ResourceResolverFactory resourceResolverFactory) {
        this.resourceResolverFactory = resourceResolverFactory;
    }
    
    public void setWebResourceScriptRunnerFactory(
			WebResourceScriptRunnerFactory webResourceScriptRunnerFactory) {
		this.webResourceScriptRunnerFactory = webResourceScriptRunnerFactory;
	}
}
