package com.playfairy.controllers;

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
  	return ok(com.playfairy.controllers.views.html.download.index.render());
  }
    
    public Result plist(int id) {
    	return ok(com.playfairy.controllers.views.html.download.plist.render(id));
    }

}
