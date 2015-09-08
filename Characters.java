/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: Tankframe.java, Picture
  * 
  * Descrition: Primary Interactable Object Master Class
  * 
  ****************************************************************************/

public class Characters {
  public double xcord;
  public double ycord;
  public double rotation;
  public String pic;
  
  // Constructor
  public Characters(double x, double y, String pic) {
    xcord = x;
    ycord = y;
    this.pic = pic;
    rotation = 0.0; // Starts with upward orientation
  }

  // Returns current rotation value
  public double getrotation() {
    return rotation;
  }

  // Changes orientation by imputted degrees
  public void rotate(double degree) {
    rotation += degree;

    // Resets in order to not work with huge numbers

    //if (rotation > 360.0) rotation += -360.0;
    //if (rotation < -360.0) rotation += 360.0;
  }

  // Displays Character in StdDraw window
  public void show(int x, int y) {
    Tankframe.picture(x, y, pic, rotation);
  }

  // Moves character
  public void move(double x, double y) {
    xcord += x;
    ycord += y;
  }
}

