package strategy;

import java.io.File;

public class SavingManager implements Save {

	private Save save;
	
	public SavingManager(Save save) {
		this.save = save;
	}
	
	@Override
	public void save(Object o, File f) {
		save.save(o, f);
		
	}
	
	public void setFileSave(Save save) {
		this.save = save;
	}

}