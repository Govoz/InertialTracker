package com.example.federico.inertialtracker;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Federico on 22-Nov-16.
 */

public class writeLogFile {
	public void write(Context c, String fileName, String data, int mode){
		try {
			FileOutputStream fos = c.openFileOutput(fileName, mode);
			fos.write(data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
