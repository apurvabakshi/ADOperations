package com.bristlecone.usersync;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import com.bristlecone.pojoobjects.Person;

public class Main {

	public static void main(String[] args) {
		ADOperations ad = new ADOperations();
		Person person = new Person();

		try {
			Scanner scan = new Scanner(System.in);
			System.out.println("1. Create\n2. Read\n3. Update\n4. Delete\n5. Exit");
			System.out.print("Enter your choice : ");
			int choice = Integer.parseInt(scan.nextLine());
			switch (choice) {
			case 1:
				ad.insert();
				break;
			case 2:
				System.out.print("Enter uid to search : ");
				person.setName(scan.nextLine());
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes = ad.search(person);
				int i = 1;
				for (Entry<String, String> entry : attributes.entrySet()) {
					System.out.println(i + ". " + entry.getKey() + " : " + entry.getValue());
					i++;
				}
				break;
			case 3:
				System.out.print("Enter uid to edit : ");
				person.setName(scan.nextLine());
				ad.update(person);
				break;

			case 4:
				System.out.print("Enter uid to delete : ");
				person.setName(scan.nextLine());
				ad.deleteUser(person);
				break;
			default:
				System.out.println("Please enter correct number");
				break;
			}
			scan.close();
		} catch (NumberFormatException ex) {
			System.out.println("Please enter number");
		}
	}
}
