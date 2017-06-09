package com.playfairy.startup;

import com.google.inject.AbstractModule;

public class OnStartupModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MorphiaSingleton.class).asEagerSingleton();
	}

}
