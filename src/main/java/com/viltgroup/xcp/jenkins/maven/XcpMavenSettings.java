/*
 * Copyright 2015 VILT Group, www.vilt-group.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.viltgroup.xcp.jenkins.maven;

import hudson.FilePath;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import com.viltgroup.xcp.jenkins.utils.OSPathHacks;

/**
 * Utility class that handles the generation of maven settings.xml for a given xCP Designer project. 
 *
 * <p>
 * See <tt>src/main/resources/com/viltgroup/xcp/jenkins/maven/XcpMavenSettings/template.jelly</tt>
 * for the actual template
 */
public class XcpMavenSettings {

	public static void generateFile(String workspacePath, String xcpDesignerPath, String localRepositoryPath, FilePath generatedFilePath) {
	    XMLOutput output = null;
	    try {
	    	// create directories if needed
	    	generatedFilePath.getParent().mkdirs();

	    	// prepare output writer
	        output = XMLOutput.createXMLOutput(generatedFilePath.write());

	        // load jelly template
	        String templateLocation = String.format("/%s/template.jelly",XcpMavenSettings.class.getName().replace('.', '/'));
	        URL templateURL = XcpMavenSettings.class.getResource(templateLocation);
	        
	        // generate settings.xml from template
	        Jelly jelly = new Jelly(); 
	        jelly.setUrl( templateURL );
	        Script script = jelly.compileScript();
	        // add xCP Designer path to the jelly context
	        JellyContext context = new JellyContext();
	        context.setVariable("workspacePath", OSPathHacks.processFilePath(workspacePath));
	        context.setVariable("xcpDesignerPath", xcpDesignerPath);
	        context.setVariable("xcpDesignerMavenPath", String.format("%s/maven/designer", OSPathHacks.processFilePath(xcpDesignerPath)));
	        context.setVariable("localRepositoryPath", OSPathHacks.processFilePath(localRepositoryPath));
	        script.run( context, output );
	        output.flush();
	    } catch (IOException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyTagException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (JellyException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} catch (InterruptedException e) {
	    	throw new RuntimeException("Error generating xCP settings.xml file.", e);
		} finally {
	    	if (output != null) try { output.close(); } catch (IOException e) { }
	    }
	}
}