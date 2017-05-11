package com.playfairy.models;

import java.util.Date;
import java.util.List;

//import java.util.Date;
//import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;

import com.mongodb.DBCollection;

import com.playfairy.datasources.MorphiaObject;
import play.Logger;
import play.data.format.Formats;

public class UploadIpaEntry {
	
	@Id
	public ObjectId id;

	@Indexed(unique = true)
	public String revision;
	
	public String sha;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	public static UploadIpaEntry create(final String revision, final String sha) {
		UploadIpaEntry entry = new UploadIpaEntry();
		entry.revision = revision;
		entry.sha = sha;
		entry.created = new Date();
		MorphiaObject.datastore.save(entry);
		return entry;
	}
	
	public static void save(final UploadIpaEntry entry) {
		MorphiaObject.datastore.save(entry);
	}
	
	public static UploadIpaEntry find(String revision) {
		UploadIpaEntry entry = MorphiaObject.datastore.createQuery(UploadIpaEntry.class)
				.filter("revision", revision)
				.get();
		if ( entry == null ) {
			Logger.error("UploadIpaEntry find: unable to find " + revision);
		} else {
			Logger.error("UploadIpaEntry find: revision = " + revision + " sha: " + entry.sha + " date: " + entry.created);
		}
		return entry;
	}
	
//	public static List<String> getAllToken() {
//		DBCollection collections = MorphiaObject.datastore.getCollection(CasinoApnsUser.class);
//		List allToken = collections.distinct("token");
//		List<String> allTokenString = (List<String>)allToken;
//		for ( String szToken : allTokenString ) {
//			Logger.error("CasinoApnsUser getAllToken: " + szToken);
//		}
//		return allTokenString;
//	}
//	
//	public static List<CasinoApnsUser> getAllUsers() {
//		Query<CasinoApnsUser> qUser = MorphiaObject.datastore.createQuery(CasinoApnsUser.class);
//		List<CasinoApnsUser> users = qUser.asList();
//		return users;
//	}
//	
//	public static CasinoApnsUser find(final String token) {
//		CasinoApnsUser user = MorphiaObject.datastore.createQuery(CasinoApnsUser.class)
//				.filter("token", token)
//				.get();
//		if ( user == null ) {
//			Logger.error("CasinoApnsUser cannot find: " + token);
//		} else {
//			Logger.error("CasinoApnsUser found: " + token);
//		}
//		return user;
//	}
//	
//	public static void save(final CasinoApnsUser user) {
//		MorphiaObject.datastore.save(user);
//	}
//
//	public static CasinoApnsUser create(final String token) {
//		CasinoApnsUser user = new CasinoApnsUser();
//		user.token = token;
//		user.created = new Date();
//		MorphiaObject.datastore.save(user);
//		return user;
//	}

}
