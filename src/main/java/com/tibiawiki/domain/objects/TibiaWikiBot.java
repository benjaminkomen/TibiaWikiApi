package com.tibiawiki.domain.objects;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class TibiaWikiBot extends MediaWikiBot {

    private static final String DEFAULT_WIKI_URI = "https://tibia.wikia.com/";

    public TibiaWikiBot() {
        super(DEFAULT_WIKI_URI);
    }
}
