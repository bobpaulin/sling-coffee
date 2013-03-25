package org.apache.sling.coffee.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Dictionary;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.coffee.CoffeeScriptCompiler;
import org.apache.sling.coffee.SlingCoffeeScriptCache;
import org.apache.sling.coffee.exception.CoffeeCompileException;
import org.apache.sling.coffee.util.JCRUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

/**
 * 
 * Implementation of the CoffeeScript Compiler.
 * 
 * 
 * @author bpaulin
 * 
 */
@Component(immediate = true, metatype = true)
@Service
public class SlingCoffeeScriptCacheImpl implements SlingCoffeeScriptCache {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CoffeeScriptCompiler coffeeScriptCompiler;

    @org.apache.felix.scr.annotations.Property(value = "/var/coffeescript/")
    private static final String COFFEESCRIPT_COMPILE_ROOT = "coffeescript.compile.root";

    private String coffeeScriptCompileRoot;

    public void activate(final ComponentContext context) {
        Dictionary config = context.getProperties();
        this.coffeeScriptCompileRoot = PropertiesUtil.toString(
                config.get(COFFEESCRIPT_COMPILE_ROOT), "/var/coffeescript/");

    }

    /**
     * 
     * Compile CoffeeScript
     * 
     * @param source
     * @return
     * @throws CoffeeCompileException
     */
    protected String compile(String source) throws CoffeeCompileException {
        // return coffee.compile(source);
        return coffeeScriptCompiler.compile(source);
    }

    /**
     * 
     * Compiles and creates JavaScript File from CoffeeScript path
     * 
     * @param path
     * @return
     * @throws CoffeeCompileException
     */
    protected String compileCoffeeResource(String path)
            throws CoffeeCompileException {

        ResourceResolver resolver = null;
        String result = null;
        try {
            resolver = resourceResolverFactory
                    .getAdministrativeResourceResolver(null);
            Session session = resolver.adaptTo(Session.class);
            Node rootNode = session.getRootNode();

            Node coffeeScriptNode = getScriptContentNode(path, rootNode);
            result = compile(getScriptAsString(coffeeScriptNode));

            Node javascriptNode = JCRUtils.createNode(rootNode,
                    coffeeScriptCompileRoot + path);

            javascriptNode.setPrimaryType("nt:file");
            Node javascriptContent = null;
            if (javascriptNode.hasNode(Property.JCR_CONTENT)) {
                javascriptContent = javascriptNode
                        .getNode(Property.JCR_CONTENT);
            } else {
                javascriptContent = javascriptNode.addNode(
                        Property.JCR_CONTENT, "nt:resource");
            }

            ValueFactory valueFactory = session.getValueFactory();
            Binary javascriptBinary = valueFactory
                    .createBinary(new ByteArrayInputStream(result
                            .getBytes("UTF-8")));

            javascriptContent.setProperty(Property.JCR_DATA, javascriptBinary);
            Calendar lastModified = Calendar.getInstance();
            javascriptContent.setProperty(Property.JCR_LAST_MODIFIED,
                    lastModified);

            session.save();
        } catch (Exception e) {
            throw new CoffeeCompileException(e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }

        return result;
    }

    /**
     * 
     * Convert JCR Script Content to a String
     * 
     * @param coffeeContent
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws IOException
     * @throws ValueFormatException
     */
    protected String getScriptAsString(Node coffeeContent)
            throws PathNotFoundException, RepositoryException, IOException,
            ValueFormatException {
        Property coffeeData = coffeeContent.getProperty(Property.JCR_DATA);

        return IOUtils.toString(coffeeData.getBinary().getStream());
    }

    /**
     * 
     * Retrieve Script Content from JCR
     * 
     * @param path
     * @param rootNode
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     */
    protected Node getScriptContentNode(String path, Node rootNode)
            throws PathNotFoundException, RepositoryException {
        String relativePath = JCRUtils.convertPathToRelative(path);
        Node coffeeItem = rootNode.getNode(relativePath);

        Node coffeeContent = coffeeItem.getNode(Property.JCR_CONTENT);
        return coffeeContent;
    }

    /**
     * 
     * Implementation of SlingCoffeeScriptCache.String getJavascript(Session
     * session, String path)
     * 
     */
    public String getJavascript(Session session, String path)
            throws CoffeeCompileException {

        String javaScriptString = null;
        String relativePath = JCRUtils.convertPathToRelative(path);

        String cachedJavascriptPath = coffeeScriptCompileRoot + relativePath;
        try {
            if (session.nodeExists(cachedJavascriptPath)) {
                Node javaScriptContent = getScriptContentNode(
                        cachedJavascriptPath, session.getRootNode());
                Node coffeeScriptContent = getScriptContentNode(relativePath,
                        session.getRootNode());
                Property javaScriptLastModified = javaScriptContent
                        .getProperty(Property.JCR_LAST_MODIFIED);
                Property coffeeScriptLastModified = coffeeScriptContent
                        .getProperty(Property.JCR_LAST_MODIFIED);

                if (javaScriptLastModified.getDate().after(
                        coffeeScriptLastModified.getDate())) {
                    javaScriptString = getScriptAsString(javaScriptContent);
                }
            }

            // Script is either not compiled or out of date.
            if (javaScriptString == null) {
                javaScriptString = compileCoffeeResource(relativePath);
            }
        } catch (Exception e) {
            throw new CoffeeCompileException(e);
        }

        return javaScriptString;

    }

    public void setCoffeeScriptCompilier(
            CoffeeScriptCompiler coffeeScriptCompiler) {
        this.coffeeScriptCompiler = coffeeScriptCompiler;
    }

}
