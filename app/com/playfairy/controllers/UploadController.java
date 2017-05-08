package com.playfairy.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UploadController extends Controller {
	
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
//        return ok(views.html.index.render());
    	return ok(com.playfairy.controllers.views.html.upload.index.render());
    }
    
    public Result ipa() {
    	MultipartFormData<File> ipaBody = request().body().asMultipartFormData();
    	Map<String, String[]> params = ipaBody.asFormUrlEncoded();
    	FilePart<File> ipaFile = ipaBody.getFile("ipa");
    	if ( ipaFile != null ) {
    		String szFileName = ipaFile.getFilename();
    		String szContentType = ipaFile.getContentType();
    		String szFullFilePath = ipaFile.getFile().getAbsolutePath();
    		String szDebug = "File received: " + szFileName + " Path: " + szFullFilePath  + " ContentType: " + szContentType + " size: " + ipaFile.getFile().length();
    		if ( params != null ) {
    			szDebug += "\n";
    			Iterator<Entry<String, String[]>> it = params.entrySet().iterator();
    			while ( it.hasNext() ) {
    				Entry<String, String[]> entry = it.next();
    				szDebug += entry.getKey() + " : " + entry.getValue()[0] + "\n";
    			}
    		}
    		try {
    			File destFile = new File("public/ipa/uploads/01.ipa");
    			if ( destFile.exists() ) {
    				destFile.delete();
    			}
    			FileUtils.moveFile(ipaFile.getFile(), destFile);
    		} catch ( IOException ioe ) {
    			return ok(ioe.getMessage());
    		}
    		return ok(szDebug);
    	} else {
    		return badRequest();
    	}
//    	return ok(com.playfairy.controllers.views.html.upload.index.render());
    }

}
