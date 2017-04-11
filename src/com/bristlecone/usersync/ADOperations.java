package com.bristlecone.usersync;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.bristlecone.pojoobjects.Person;
import com.bristlecone.utils.ADUtils;

public class ADOperations {

	private Hashtable<String, String> env = new Hashtable<String, String>();
	Scanner scan = new Scanner(System.in);
	ADUtils utils = new ADUtils();

	public ADOperations() {
		env = utils.getEnvironment(env);
	}

	/**
	 * Method to add user to AD
	 * 
	 * @return
	 */
	public void insert() {
		DirContext ctx = null;
		Properties properties = utils.readProperties();
		Person person = new Person();
		System.out.println("Enter person name");
		person.setName(scan.nextLine());

		System.out.println("Enter person last name");
		person.setLastName(scan.nextLine());

		System.out.println("Enter person address");
		person.setAddress(scan.nextLine());

		System.out.println("Enter person password");
		person.setPassword(scan.nextLine());

		String username = person.getName();
		String lastname = person.getLastName();
		String entryDN = "CN=" + username + "," + properties.getProperty("USER_BIND");
		Attribute oc = new BasicAttribute("objectClass");
		oc.add("top");
		oc.add("person");
		oc.add("organizationalPerson");
		oc.add("user");
		try {
			ctx = new InitialDirContext(env);
			Attributes entry = new BasicAttributes();
			entry.put(new BasicAttribute("cn", username));
			entry.put(new BasicAttribute("sn", lastname));
			entry.put(new BasicAttribute("uid", username));
			entry.put(new BasicAttribute("userpassword", person.getPassword()));
			entry.put(new BasicAttribute("street", person.getAddress()));
			entry.put(oc);
			ctx.createSubcontext(entryDN, entry);
			System.out.println("Added user " + entryDN + ".");
		} catch (NamingException e) {
			System.err.println("error adding entry." + e);
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to search user from AD
	 * 
	 * @param person
	 * @return
	 */
	public HashMap<String, String> search(Person person) {
		Properties properties = utils.readProperties();
		try {
			HashMap<String, String> userAttributes = new HashMap<String, String>();
			LdapContext dctx = utils.establishConnection(env);
			String base = properties.getProperty("USER_BIND");
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(objectclass=person)(cn=" + person.getName() + "))";
			NamingEnumeration<SearchResult> results = dctx.search(base, filter, sc);
			if (results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				Attributes attrs = sr.getAttributes();
				Attribute attr = attrs.get("cn");
				if (attr != null) {
					userAttributes.put("cn", attr.get().toString());
					attr = attrs.get("cn");
					attr = attrs.get("sn");
					userAttributes.put("sn", attr.get().toString());
					attr = attrs.get("street");
					userAttributes.put("street", attr.get().toString());
				}
			} else
				System.out.println("Record not found");
			dctx.close();
			return userAttributes;

		} catch (NamingException e) {
			System.out.println("Search exception : " + e);
			return null;
		}
	}

	/**
	 * Method to update user from AD
	 * 
	 * @param person
	 */
	public void update(Person person) {
		String answer = "y", name = "", value = "";
		int i = 1;
		Properties properties = utils.readProperties();
		try {
			DirContext ctx = utils.establishConnection(env);
			HashMap<String, String> editAttributes = new HashMap<String, String>();
			editAttributes = search(person);

			for (Entry<String, String> entry : editAttributes.entrySet()) {
				System.out.println(i + ". " + entry.getKey() + " : " + entry.getValue());
				i++;
			}
			i = 0;
			editAttributes.clear();
			while (answer.equalsIgnoreCase("y")) {
				System.out.println("Enter name of attribute to edit:");
				name = scan.next();
				System.out.println("Enter modified value:");
				value = scan.next();
				editAttributes.put(name, value);
				System.out.println("Do you want to edit more?(y/n)");
				answer = scan.next();
			}
			ModificationItem[] mods = new ModificationItem[editAttributes.size()];
			for (Entry<String, String> entry : editAttributes.entrySet()) {
				mods[i] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute(entry.getKey(), entry.getValue()));
				i++;
			}
			String entryDN = "CN=" + person.getName() + "," + properties.getProperty("USER_BIND");
			ctx.modifyAttributes(entryDN, mods);
			System.out.println("Modified successfully");
		} catch (NamingException e) {
			e.printStackTrace();
			System.err.println("Error while editing");
		}
	}

	/**
	 * Method to delete user from AD
	 * 
	 * @param person
	 */
	public void deleteUser(Person person) {
		Context ctx = null;
		Object obj = null;
		Properties properties = utils.readProperties();
		try {
			ctx = utils.establishConnection(env);

			String baseDN = "cn=" + person.getName() + "," + properties.getProperty("USER_BIND");
			ctx.unbind(baseDN);
			try {
				obj = ctx.lookup(baseDN);
			} catch (NameNotFoundException ne) {
				System.out.println("unbind successful");
				return;
			}
			System.out.println("unbind failed; object still there : " + obj);
			ctx.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

}
