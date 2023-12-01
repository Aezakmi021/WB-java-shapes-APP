package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public interface Save {

	void save(Object o, File f);
	
}
