package com.playfairy.controllers;

import java.io.File;

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
    	FilePart<File> ipaFile = ipaBody.getFile("ipa");
    	if ( ipaFile != null ) {
    		String szFileName = ipaFile.getFilename();
    		String szContentType = ipaFile.getContentType();
    		String szDebug = "File received: " + szFileName + " ContentType: " + szContentType + " size: " + ipaFile.getFile().length();
    		return ok(szDebug);
    	} else {
    		return badRequest();
    	}
//    	return ok(com.playfairy.controllers.views.html.upload.index.render());
    }

}
