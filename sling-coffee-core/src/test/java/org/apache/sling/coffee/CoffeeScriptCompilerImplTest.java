package org.apache.sling.coffee;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import org.apache.sling.coffee.CoffeeScriptCompilerImpl;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import static org.easymock.EasyMock.*;

import junit.framework.TestCase;

/**
 * 
 * CoffeeScript compiler tests
 * 
 * Based on the language demo at http://coffeescript.org
 * 
 * @author bpaulin
 *
 */

public class CoffeeScriptCompilerImplTest extends TestCase {
    
    private CoffeeScriptCompilerImpl coffeeEx;
    
    private ResourceResolverFactory mockResourceResolverFactory;
    
    private ComponentContext mockComponentContext;
    
    private ResourceResolver mockResourceResolver;
    
    private Resource mockResource;
    
    private Node mockNode;
    
    private Property mockProperty;
    
    private Binary mockBinary;
    
    @BeforeClass
    public void setUp() throws Exception
    {
        super.setUp();
        coffeeEx = new CoffeeScriptCompilerImpl();
        
        mockResourceResolverFactory = createMock(ResourceResolverFactory.class);
        
        coffeeEx.setResourceResolverFactory(mockResourceResolverFactory);
        
        mockComponentContext = createMock(ComponentContext.class);
        mockResourceResolver = createMock(ResourceResolver.class);
        mockResource = createMock(Resource.class);
        mockNode = createMock(Node.class);
        mockProperty = createMock(Property.class);
        mockBinary = createMock(Binary.class);
        
        InputStream coffeeInputStream = getClass().getResourceAsStream("/coffee-script.js");
        
        Hashtable bundleProperties = new Hashtable();
        bundleProperties.put("coffee.compiler.path", "/test/path");
        bundleProperties.put("coffee.cache.path", "/test/path2");
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
    }
    
    @Test
    public void testFunctionCompile() throws Exception {
        testFileCompile("functions.coffee");
    }
    
    @Test
    public void testDefaultFunctionCompile() throws Exception {
        testFileCompile("defaultFunctions.coffee");
    }

    @Test
    public void testObjectAndArrayCompile() throws Exception {
        testFileCompile("objectAndArrays.coffee");
    }
    
    @Test
    public void testJavaScriptReservedWordCompile() throws Exception {
        testFileCompile("reservedWords.coffee");
    }
    
    @Test
    public void testLexicalScopingSafetyCompile() throws Exception {
        testFileCompile("lexicalScopingSafety.coffee");
    }
    
    @Test
    public void testIfElseUnlessCompile() throws Exception {
        testFileCompile("ifElseUnless.coffee");
    }
    
    @Test
    public void testSplatsCompile() throws Exception {
        testFileCompile("splats.coffee");
    }
    
    @Test
    public void testLoopsCompile() throws Exception {
        testFileCompile("loops.coffee");
    }
    
    @Test
    public void testComprehensionCompile() throws Exception {
        testFileCompile("comprehension.coffee");
    }
    
    @Test
    public void testArraySlicingSplicingCompile() throws Exception {
        testFileCompile("arraySlicingSplicing.coffee");
    }
    
    @Test
    public void testExpressionsCompile() throws Exception {
        testFileCompile("expressions.coffee");
    }
    
    @Test
    public void testOperatorsAliasesCompile() throws Exception {
        testFileCompile("operatorsAliases.coffee");
    }
    
    @Test
    public void testClassesCompile() throws Exception {
        testFileCompile("classes.coffee");
    }
    
    @Test
    public void testAssignmentCompile() throws Exception {
        testFileCompile("assignment.coffee");
    }
    
    @Test
    public void testFunctionBindingCompile() throws Exception {
        testFileCompile("functionBinding.coffee");
    }
    
    @Test
    public void testEmbeddedJsCompile() throws Exception {
        testFileCompile("embeddedJs.coffee");
    }
    
    @Test
    public void testSwitchWhenElseCompile() throws Exception {
        testFileCompile("switchWhenElse.coffee");
    }
    
    @Test
    public void testTryCatchFinallyCompile() throws Exception {
        testFileCompile("tryCatchFinally.coffee");
    }
    
    @Test
    public void testChainedComparisionsCompile() throws Exception {
        testFileCompile("chainedComparisons.coffee");
    }
    
    @Test
    public void testStringInterpolationCommentsCompile() throws Exception {
        testFileCompile("stringInterpolationComments.coffee");
    }
    
    @Test
    public void testBlockRegularExpressionsCompile() throws Exception {
        testFileCompile("blockRegularExpressions.coffee");
    }
    
    private void testFileCompile(String fileName) throws Exception {
        String coffeeScriptString = getTestCoffeeFileString(fileName);
        
        String result = coffeeEx.compile(coffeeScriptString);
        
        assertEquals("File named: " + fileName + " should compile to proper JavaScript", getResultJavaScriptFileString(fileName), result);
    }
    
    private String convertFileToString(String filePath) throws Exception
    {
        
        InputStream inputStream = getClass().getResourceAsStream(filePath);

        
        return IOUtils.toString(inputStream, "UTF-8");
    }
    
    private String getTestCoffeeFileString(String fileName) throws Exception
    {
        return convertFileToString("/test-coffee/"+ fileName);
    }
    
    private String getResultJavaScriptFileString(String fileName) throws Exception
    {
        fileName = fileName.replaceFirst(".coffee", "");
        return convertFileToString("/result-js/"+ fileName + ".js");
    }
    
    @AfterClass
    public void after()
    {
        verify(mockBinary);
        verify(mockProperty);
        verify(mockComponentContext);
        verify(mockNode);
        verify(mockResource);
        verify(mockResourceResolver);
        verify(mockResourceResolverFactory);
    }

}
