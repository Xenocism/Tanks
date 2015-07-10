/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * 
  * Dependancies: StdDraw, Picture
  * 
  * 
  * Descrition: Primary Interactable Object
  * 
  * 
  ****************************************************************************/

public class Character {
  private double xcord;
  private double ycord;
  private double rotation;
  private String pic;
  
  public Character(int x, int y, String pic) {
    xcord = x;
    ycord = y;
    this.pic = pic;
    rotation = 0.0;
  }

  public double rotation() {
    return rotation;
  }

  public void rotate(double degree) {
    rotation += degree;
  }

  public void setdirectionmouse() {
    double deltax = StdDraw.mouseX() - xcord;
    double deltay = StdDraw.mouseY() - ycord;
    double degree = Math.atan(deltay / deltax);
    
    degree = Math.toDegrees(degree);

    if (deltax > 0 && deltay > 0) degree += -90;
    if (deltax > 0 && deltay < 0) degree += 270;
    if (deltax < 0 && deltay < 0) degree += 90;
    if (deltax < 0 && deltay > 0) degree += 90;
    rotation = degree;
  }

  public void move(double distance) {
    xcord += distance * Math.cos(Math.toRadians(90 + rotation));
    ycord += distance * Math.sin(Math.toRadians(90 + rotation));
  }

  public void show() {
    StdDraw.picture(xcord, ycord, pic, rotation);
  }

  public void showcompared(Character related) {
    xcord = related.xcord;
    ycord = related.ycord;
    this.show();
  }
}

