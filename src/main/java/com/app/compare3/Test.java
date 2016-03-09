package com.app.compare3;

import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] args) {
		Set<String> set =new HashSet<String>();
		set.add("a");
		set.add("a");
		set.add("b");
		System.out.println(set.size());
		for (String temp : set) {
			System.out.println(temp);
		}
	}

}
