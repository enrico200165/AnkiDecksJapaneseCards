package com.enrico_viali.gtranslate;

import org.apache.log4j.Logger;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class GTranslate {

	public GTranslate() {
		super();
		// Set the HTTP referrer to your website address.
		GoogleAPI.setHttpReferrer("http://kissako.it");

		// Set the Google Translate API key
		// See:
		// http://code.google.com/apis/language/translate/v2/getting_started.html
		GoogleAPI.setKey("AIzaSyB_txqfYgwZi9Om3T3lo0lTDVUP8wB9sJ0");
	}

	public RespTransl translate(String toTranslate) {
		return translate(toTranslate, Language.ENGLISH, Language.ITALIAN);
	}

	public RespTransl translate(String toTranslate, Language source,
			Language dest) {
		RespTransl ret = new RespTransl();
		try {
			ret.translation = Translate.DEFAULT.execute(toTranslate, source,
					dest);
			log.info("translated with GOOGLE: " + toTranslate + " -> "
					+ ret.translation);
			ret.found = true;
		} catch (GoogleAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret.found = false;
		}
		return ret;
	}

	private static org.apache.log4j.Logger log = Logger
			.getLogger(GTranslate.class);
}
