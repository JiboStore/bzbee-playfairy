package com.playfairy.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import play.mvc.Controller;
import play.mvc.Result;

public class DownloadController extends Controller {
	
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
//        return ok(views.html.index.render());
    	return ok(com.playfairy.controllers.views.html.download.index.render());
    }
    
    public Result ipa() {
    	List<String> ipaNames = new ArrayList<String>();
    	try {
    		File ipaDir = new File("public/ipa/uploads/");
    		if ( ipaDir.exists() && ipaDir.isDirectory() ) {
    			File[] ipaFiles = ipaDir.listFiles();
    			if ( ipaFiles != null ) {
    				for ( int i = 0; i < ipaFiles.length; i++ ) {
    					if ( "ipa".equals(FilenameUtils.getExtension(ipaFiles[i].getName())) ) {
    						ipaNames.add(FilenameUtils.getBaseName(ipaFiles[i].getName()));
    					}
    				}
    			}
    		}
    	} catch ( Exception ioe ) {
    		return ok(com.playfairy.controllers.views.html.download.ipa.render(ipaNames));
    	}
    	return ok(com.playfairy.controllers.views.html.download.ipa.render(ipaNames));
  }
    
    public Result plist(String id) {
    	return ok(com.playfairy.controllers.views.html.download.plist.render(id));
    }

}
