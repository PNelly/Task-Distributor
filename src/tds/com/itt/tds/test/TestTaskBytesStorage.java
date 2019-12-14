package com.itt.tds.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import java.io.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;

import com.itt.tds.coordinator.*;
import com.itt.tds.core.*;

public class TestTaskBytesStorage {
	
	private static Task taskHelper() {

		byte[] bytes = {1, 0, 5, 3, 7, 8};

		return new Task(bytes);		
	}

	private static String saveHelper(Task task) 
		throws IOException, NoSuchAlgorithmException {

		return TaskBytesStorage.storeTaskBytes(taskHelper());		
	}

	@Test
	public void testStoreTaskBytes() 
		throws IOException, NoSuchAlgorithmException {

		String path = saveHelper(taskHelper());

		File taskFile = new File(path);

		assertTrue(taskFile.exists());
	}

	@Test
	public void testGetTaskBytes() 
		throws IOException, NoSuchAlgorithmException {

		Task task = taskHelper();

		String path = saveHelper(task);

		task.setTaskExePath(path);

		byte[] bytesOut = TaskBytesStorage.getTaskBytes(task);

		assertTrue(Arrays.equals(task.getProgramBytes(), bytesOut));
	}
}