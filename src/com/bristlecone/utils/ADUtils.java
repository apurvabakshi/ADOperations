package com.bristlecone.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class ADUtils {
	Properties properties = new Properties();

	public Properties readProperties() {

		File file = new File("resources/connection.properties");
		FileInputStream fileInput = null;

		try {
			fileInput = new FileInputStream(file);
			properties.load(fileInput);
			fileInput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public LdapContext establishConnection(Hashtable<String, String> env) throws NamingException {

		LdapContext ctx = new InitialLdapContext(env, null);
		return ctx;
	}

	public Hashtable<String, String> getEnvironment(Hashtable<String, String> env) {
		properties = readProperties();
		System.setProperty("javax.net.ssl.trustStore",properties.getProperty("KEYSTORE"));
		env.put(Context.INITIAL_CONTEXT_FACTORY, properties.getProperty("INITIAL_CONTEXT_FACTORY"));
		env.put(Context.PROVIDER_URL, properties.getProperty("PROVIDER_URL"));
		env.put(Context.SECURITY_AUTHENTICATION, properties.getProperty("SECURITY_AUTHENTICATION"));
		env.put(Context.SECURITY_PRINCIPAL, properties.getProperty("SECURITY_PRINCIPAL"));
		env.put(Context.SECURITY_CREDENTIALS, properties.getProperty("SECURITY_CREDENTIALS"));
		env.put(Context.SECURITY_PROTOCOL, properties.getProperty("SECURITY_PROTOCOL"));
		return env;
	}
}
