package grid;
/**
 * This Interface is used to generate the image which is shown in the GUI
 * an internal clock, will start a update process of this Data, which also
 * frees the different Threads from the creatures.
 * Therefore need this interface the ApplicationContext.
 * @author Joel Zimmerli
 *
 */
public interface PictureGenerator {
	
	
	/**
	 * This method will start the process to render all the Grids in different
	 * ways. Should only be called, when the background is changing
	 */
	void makePictures();
	
	/**
	 * This method creates the new image of the Creatures grid.
	 */
	void makeMovement();
	/**
	 * This method is called in a specific frequency by the clock
	 * crates a new image to show
	 */
	void update();
	
	

}
