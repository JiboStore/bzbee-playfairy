package com.playfairy.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.playfairy.models.UploadIpaEntry;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UploadController extends Controller {
	
	/**
	 * @param ipaBasename - the base filename of the ipa file eg: for CasinoDeluxeByIGG.ipa, id is CasinoDeluxeByIGG
	 * @return
	 */
    public int generatePlist(String szHostname, String ipaBasename) {
    	String szIpaDir = "public/ipa/uploads/";
    	String szPlistDir = "public/ipa/plists/";
    	File ipaDir = new File(szIpaDir);
    	File plistDir = new File(szPlistDir);
    	List<String> ipaNames = new ArrayList<String>();
    	try {
    		try {
    			ipaDir.mkdirs();
    			plistDir.mkdirs();
    		} catch ( Exception e ) {
    			Logger.debug("UploadController.genplist exception: " + e.getMessage());
    		}
    		if ( "0".equals(ipaBasename) ) {
    			// generate for all ipas
    			if ( ipaDir.exists() && ipaDir.isDirectory() ) {
        			File[] ipaFiles = ipaDir.listFiles();
        			if ( ipaFiles != null ) {
        				for ( int i = 0; i < ipaFiles.length; i++ ) {
        					if ( "ipa".equals(FilenameUtils.getExtension(ipaFiles[i].getName())) ) {
        						if ( ipaFiles[i].exists() ) {
        							ipaNames.add(FilenameUtils.getBaseName(ipaFiles[i].getName()));
        						}
        					}
        				}
        			}
        		}
    		} else {
    			String szIpaName = szIpaDir + ipaBasename + ".ipa";
    			File ipaFile = new File(szIpaName);
    			if ( ipaFile.exists() ) {
    				ipaNames.add(ipaBasename);
    			}
    		}
    		for ( int i = 0; i < ipaNames.size(); i++ ) {
    			String szIpaFile = szIpaDir + ipaNames.get(i) + ".ipa";
    			String szPlistFile = szPlistDir + ipaNames.get(i) + ".plist";
    			File ipaFile = new File(szIpaFile);
    			if ( ipaFile.exists() ) {
    				String szPlistContent = com.playfairy.controllers.views.html.download.plisttemplate.render(szHostname, ipaNames.get(i)).toString();
    				FileUtils.writeStringToFile(new File(szPlistFile), szPlistContent, false);
    			}
    		}
    		return ipaNames.size();
    	} catch ( Exception ioe ) {
    		return -1;
    	}
    }
    
    public boolean saveUploadedIpa(String revision, String sha, File ipaFile) {
    	String szDestDir = "public/ipa/uploads/";
    	String szDestFilename = szDestDir + revision + ".ipa";
    	File pDestFile = new File(szDestFilename);
    	if ( pDestFile.exists() ) {
    		// delete old file
    		pDestFile.delete();
    	}
    	try {
			FileUtils.moveFile(ipaFile, pDestFile);
		} catch (IOException e) {
			Logger.debug(e.getMessage());
			return false;
		}
    	// save entry to DB
    	UploadIpaEntry entry = UploadIpaEntry.find(revision);
    	if ( entry == null ) {
    		entry = UploadIpaEntry.create(revision, sha);
    	} else {
    		entry.sha = sha;
    		entry.modified = new Date();
    		UploadIpaEntry.save(entry);
    	}
    	return true;
    }
	
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
    	String szHostname = request().host();
    	MultipartFormData<File> ipaBody = request().body().asMultipartFormData();
    	Map<String, String[]> params = ipaBody.asFormUrlEncoded();
    	FilePart<File> ipaFilepart = ipaBody.getFile("ipa");
    	if ( ipaFilepart != null ) {
    		String szFilename = ipaFilepart.getFilename();
    		String szContentType = ipaFilepart.getContentType();
    		String szFullpath = ipaFilepart.getFile().getAbsolutePath();
    		String szDebug = "File received: " + szFilename + " Path: " + szFullpath  + " ContentType: " + szContentType + " size: " + ipaFilepart.getFile().length();
    		Logger.debug(szDebug);
    		String[] revisionList = params.get("revision");
    		String[] shaList = params.get("sha");
    		String szRevision = "";
    		String szSha = "";
    		if ( revisionList != null ) {
    			szRevision = revisionList[0];
    			szSha = shaList[0];
    			boolean bSaveResult = saveUploadedIpa(szRevision, szSha, ipaFilepart.getFile());
    			if ( bSaveResult ) {
    				int iResult = generatePlist(szHostname, szRevision);
    				if ( iResult == 1 ) {
    					return ok("Saved " + szRevision + ".ipa");
    				} else {
    					return forbidden("Saved ipa file but unable to generate plist, you might want to generate it manually");
    				}
    			} else {
    				return internalServerError("Unable to save " + szRevision + ".ipa");
    			}
    		} else {
    			return badRequest("revision cannot be empty!");
    		}
    	} else {
    		return badRequest("ipa cannot be empty!");
    	}
    }
       
    /**
     * @param id - the base filename of the ipa file eg: for CasinoDeluxeByIGG.ipa, id is CasinoDeluxeByIGG
     * @return
     */
    public Result genplist(String id) {
    	String szHostname = request().host();
    	int iGenerated = generatePlist(szHostname, id);
    	if ( iGenerated > 0 ) {
    		return ok("generated: " + iGenerated + " files");
    	} else {
    		return ok("fail to generate");
    	}
    }

}
