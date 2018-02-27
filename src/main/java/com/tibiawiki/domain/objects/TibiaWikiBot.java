package com.tibiawiki.domain.objects;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class TibiaWikiBot extends MediaWikiBot {

    private static final Logger log = LoggerFactory.getLogger(TibiaWikiBot.class);

    private static final String DEFAULT_WIKI_URI = "https://tibia.wikia.com/";

    public TibiaWikiBot() {
        super(DEFAULT_WIKI_URI);
    }

    public void login() {
        String username = this.getProperty("username");
        String password = this.getProperty("password");
        super.login(username, password);
    }

    private String getProperty(String propertyName) {
        String output = "";
        try {
            Properties props = new Properties();
            InputStream is = props.getClass().getResourceAsStream("/credentials.properties");
            props.load(is);
            return props.getProperty(propertyName);
        } catch(Exception ex) {
            log.error(String.valueOf(ex));
        }
        return output;
    }
}
