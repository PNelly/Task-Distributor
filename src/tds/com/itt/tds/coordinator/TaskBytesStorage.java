package com.itt.tds.coordinator;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import com.itt.tds.core.*;
import com.itt.tds.cfg.*;

public class TaskBytesStorage {

	private static String filenameBase64(byte[] bytes){

		String str = Base64.getEncoder().encodeToString(bytes);

		str = str.replace("+","_");
		str = str.replace("\\","$");
		str = str.replace("/","@");
		str = str.replace("=","#");

		return str;
	}

	public static String storeTaskBytes(Task task) 
		throws IOException, NoSuchAlgorithmException {

		// ensure directory exists
		String home 	= System.getProperty("user.home") + TDSConfiguration.getPathSeparator();
		String dirName 	= home + TDSConfiguration.getCoordinatorTaskDir();

		File   taskDir  = new File(dirName);

		if(!taskDir.exists())
			taskDir.mkdir();

		// filename as hash of time and rng
		String base = Long.toString(new Random().nextLong()) 
					+ Long.toString(System.currentTimeMillis());

		byte[] hash = MessageDigest.getInstance("SHA-256").digest(
			base.getBytes(StandardCharsets.UTF_8)
		);

		String name = filenameBase64(hash);

		// save and return file path
		String path 	= dirName + TDSConfiguration.getPathSeparator() + name;
		File   program 	= new File(path);

		FileOutputStream fos = new FileOutputStream(program.getAbsoluteFile());

		fos.write(task.getProgramBytes());
		fos.flush();
		fos.close();

		return path;
	}

	public static byte[] getTaskBytes(Task task) throws IOException {

		File file = new File(task.getTaskExePath());

		FileInputStream fis = new FileInputStream(file);

		byte[] bytes = new byte[(int) file.length()];

		fis.read(bytes);

		fis.close();

		return bytes;
	}
}