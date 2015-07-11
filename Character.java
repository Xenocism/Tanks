/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: StdDraw, Picture
  * 
  * Descrition: Primary Interactable Object Master Class
  * 
  ****************************************************************************/

public class Character {
  public double xcord;
  public double ycord;
  public double rotation;
  public String pic;
  
  // Constructor
  public Character(double x, double y, String pic) {
    xcord = x;
    ycord = y;
    this.pic = pic;
    rotation = 0.0; // starts with upward orientation
  }

  // Returns current rotation value
  public double getrotation() {
    return rotation;
  }

  // Changes orientation by imputted degrees
  public void rotate(double degree) {
    rotation += degree;
  }

  // Displays Character in StdDraw window
  public void show() {
    StdDraw.picture(xcord, ycord, pic, rotation);
  }
}

