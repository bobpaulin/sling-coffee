<%@page session="false"%>
<%@page import="javax.jcr.Node" %>
<%@page import="javax.jcr.Property" %>
<%@page import="org.apache.sling.api.resource.Resource" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<%@taglib prefix="coffee" uri="http://sling.apache.org/taglibs/sling/coffee/1.0.0"%>
<sling:defineObjects/>
<% 
	Resource demoScript = resource.getChild("demo.coffee"); 
	String demoContent = null;
	if(demoScript != null)
	{
	    Node demoNode = demoScript.adaptTo(Node.class);
	    Node jcrContent = demoNode.getNode(Property.JCR_CONTENT);
	    demoContent = jcrContent.getProperty(Property.JCR_DATA).getString().trim();
	}
%>
<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML 2.0//EN\">
<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 -->
<html>
<head>
  <title>Try Sling CoffeeScript</title>
  <script type="text/javascript" src="/system/sling.js"></script>
  <coffee:coffee path="/content/coffee/demo/demo.coffee" wrapWithTag="script"/>
</head>
<body>
	<div>
		<form action="/content/coffee/demo/demo.coffee" method="POST" enctype="multipart/form-data">
			<textarea name="jcr:content/jcr:data" cols="50" rows="20"><%= demoContent%></textarea>
			<input name="jcr:primaryType" type="hidden" value="nt:file" />
			<input name="jcr:content/jcr:primaryType" type="hidden" value="nt:resource"/>
			<input name="jcr:content/jcr:lastModified" type="hidden" value=""/>
			<input type="hidden" name=":redirect" value="<%= resource.getPath() %>.html"/>
			</br>
			<input type="submit" value="Submit" />
		</form>
	</div>
  	<div id="coffeeResult">
		<coffee:coffee path="/content/coffee/demo/demo.coffee"/>
  	</div>
  	<button onclick="eval(document.getElementById('coffeeResult').childNodes[0].nodeValue.trim())" >Evaluate</button>
</body>
</html>