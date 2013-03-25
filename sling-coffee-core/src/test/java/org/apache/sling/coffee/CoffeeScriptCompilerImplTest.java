package org.apache.sling.coffee;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.coffee.impl.CoffeeScriptCompilerImpl;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

import static org.easymock.EasyMock.*;

import junit.framework.TestCase;

public class CoffeeScriptCompilerImplTest extends TestCase {
    
    private CoffeeScriptCompilerImpl coffeeEx;
    
    private ResourceResolverFactory mockResourceResolverFactory;
    
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        coffeeEx = new CoffeeScriptCompilerImpl();
        
        mockResourceResolverFactory = createMock(ResourceResolverFactory.class);
        
        coffeeEx.setResourceResolverFactory(mockResourceResolverFactory);
    }
    
    @Test
    public void testCompile() throws Exception {
        ComponentContext mockComponentContext = createMock(ComponentContext.class);
        ResourceResolver mockResourceResolver = createMock(ResourceResolver.class);
        Resource mockResource = createMock(Resource.class);
        Node mockNode = createMock(Node.class);
        Property mockProperty = createMock(Property.class);
        Binary mockBinary = createMock(Binary.class);
        
        InputStream coffeeInputStream = getClass().getResourceAsStream("/coffee-script.js");
        
        Hashtable bundleProperties = new Hashtable();
        bundleProperties.put("coffee.compiler.path", "/test/path");
        expect(mockComponentContext.getProperties()).andReturn(bundleProperties);
        expect(mockResourceResolverFactory.getAdministrativeResourceResolver(null)).andReturn(mockResourceResolver);
        expect(mockResourceResolver.getResource("/test/path")).andReturn(mockResource);
        expect(mockResource.adaptTo(Node.class)).andReturn(mockNode);
        expect(mockNode.getNode(Property.JCR_CONTENT)).andReturn(mockNode);
        expect(mockNode.getProperty(Property.JCR_DATA)).andReturn(mockProperty);
        expect(mockProperty.getBinary()).andReturn(mockBinary);
        expect(mockBinary.getStream()).andReturn(coffeeInputStream);
        mockResourceResolver.close();
        
        replay(mockBinary);
        replay(mockProperty);
        replay(mockComponentContext);
        replay(mockNode);
        replay(mockResource);
        replay(mockResourceResolver);
        replay(mockResourceResolverFactory);
        
        coffeeEx.activate(mockComponentContext);
        
        String result = coffeeEx.compile("do -> console.log 'Hello, Sling world!'");
        System.out.println(result);
        
        verify(mockBinary);
        verify(mockProperty);
        verify(mockComponentContext);
        verify(mockNode);
        verify(mockResource);
        verify(mockResourceResolver);
        verify(mockResourceResolverFactory);
    }

}
