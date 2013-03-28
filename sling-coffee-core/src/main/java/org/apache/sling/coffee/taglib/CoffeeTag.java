package org.apache.sling.coffee.taglib;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.coffee.SlingCoffeeScriptCache;
import org.apache.sling.coffee.exception.CoffeeCompileException;
/**
 * 
 * This is a custom tag that renders JavaScript based on a path to a 
 * CoffeeScript file.
 * 
 * @author bpaulin
 *
 */
public class CoffeeTag extends TagSupport {
    
    private String path;
    
    private SlingScriptHelper sling;
    
    private Node currentNode;
    
    private SlingCoffeeScriptCache coffee;
    
    private String wrapWithTag;
    
    private boolean shouldThrowException;
    
    @Override
    public int doStartTag() throws JspException {
        
        JspWriter out = pageContext.getOut();
        
        try {
            if(wrapWithTag != null)
            {
                out.write("<"+ wrapWithTag +">\n");
            }
        } catch (IOException e) {
            throw new JspException(e);
        }
        
        return super.doStartTag();
    }
    
    @Override
    public void setPageContext(PageContext pageContext) {
        
        super.setPageContext(pageContext);
        sling = (SlingScriptHelper) pageContext.findAttribute("sling");
        currentNode = (Node) pageContext.findAttribute("currentNode");
        coffee = sling.getService(SlingCoffeeScriptCache.class);
    }
    
    @Override
    public int doEndTag() throws JspException {
        JspWriter out = null;
        try {
            out = pageContext.getOut();
            String[] paths = path.split(",");
            
            for(String currentPath: paths)
            {
                String javaScript = coffee.getJavascript(currentNode.getSession(), currentPath);
                out.write(javaScript);
            }
            
            
            if(wrapWithTag != null)
            {
                out.write("\n</" + wrapWithTag + ">");
            }
        } catch (CoffeeCompileException e) {
            if(shouldThrowException)
            {
                throw new JspException(e);
            }
            try {
                out.write("/*" + e.toString() +"*/");
            } catch (IOException e1) {
                throw new JspException(e1);
            }
        } catch (IOException e) {
            try {
                out.write("/*" + e.toString() +"*/");
            } catch (IOException e1) {
                throw new JspException(e1);
            }
        } catch (RepositoryException e) {
            try {
                out.write("/*" + e.toString() +"*/");
            } catch (IOException e1) {
                throw new JspException(e1);
            }
        }
        
        return super.doEndTag();
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getWrapWithTag() {
        return wrapWithTag;
    }
    
    public void setWrapWithTag(String wrapWithTag) {
        this.wrapWithTag = wrapWithTag;
    }
    
    public boolean shouldThrowException() {
        return shouldThrowException;
    }
    
    public void setShouldThrowException(boolean shouldThrowException) {
        this.shouldThrowException = shouldThrowException;
    }
}
