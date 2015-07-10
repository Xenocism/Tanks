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
  public Character(int x, int y, String pic) {
    xcord = x;
    ycord = y;
    this.pic = pic;
    rotation = 0.0;
  }

  // Returns current rotation value
  public double getrotation() {
    return rotation;
  }

  // Changes orientation by imputted degrees
  public void rotate(double degree) {
    rotation += degree;
  }

  // changes the Characters in game coordinates in orientation direction
  public void move(double distance) {
    xcord += distance * Math.cos(Math.toRadians(90 + rotation));
    ycord += distance * Math.sin(Math.toRadians(90 + rotation));
  }

  // Displays Character in StdDraw window
  public void show() {
    StdDraw.picture(xcord, ycord, pic, rotation);
  }
}

