package strategy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JFileChooser;

import mvc.DrawingFrame;

public class LogSave implements Save, Serializable {
	
	@Override
	public void save(Object o, File f) {
		
		DrawingFrame frame = (DrawingFrame) o;
		BufferedWriter bufferedWriter = null;

		try {
			bufferedWriter = new BufferedWriter((new FileWriter(f.getAbsolutePath())));
			frame.getTextArea().write(bufferedWriter);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}