package com.playfairy.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

public class DownloadController extends Controller {
	
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result hardcoded() {
//        return ok(views.html.index.render());
    	return ok(com.playfairy.controllers.views.html.download.hardcoded.render());
    }
    
    public Result test() {
    	Logger.info(routes.DownloadController.ipa().url().toString()); // /playfairy/download/ipa
    	Logger.info(routes.DownloadController.ipa().absoluteURL(request()).toString()); // http://10.6.0.18:9443/playfairy/download/ipa
    	Logger.info(request().toString()); // GET /playfairy/download/test
    	Logger.info(request().host());	// 10.6.0.81:9443
    	return ok(request().uri().toString());
    }
    
    public Result index() {
    	String szHostname = request().host();
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
    		return ok(com.playfairy.controllers.views.html.download.index.render(szHostname, ipaNames));
    	}
    	return ok(com.playfairy.controllers.views.html.download.index.render(szHostname, ipaNames));
  }
    
    public Result ipa() {
    	return index();
    }
    
    public Result plist(String id) {
    	String szHostname = request().host();
    	response().setHeader("Accept-Ranges", "bytes");
    	response().setHeader("Cache-Control", "no-cache");
    	response().setHeader("Content-Type", "application/octet-stream");
//    	return ok(com.playfairy.controllers.views.html.download.plist.render(id)).as("application/octet-stream");
    	return ok(com.playfairy.controllers.views.html.download.plist.render(szHostname, id));
    }

}
