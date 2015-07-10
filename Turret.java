/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: StdDraw, Picture
  * 
  * Descrition: Turret Character Extension
  * 
  ****************************************************************************/

public class Turret extends Character {

  // Constructor
  public Turret(int x, int y, String pic) {
    super(x, y, pic);
  }

  // Sets the rotation to math that of the mouse position
  public void setdirectionmouse() {

    // Finds position of mouse
    double deltax = StdDraw.mouseX() - xcord;
    double deltay = StdDraw.mouseY() - ycord;
    double degree = Math.toDegrees(Math.atan(deltay / deltax));

    // Adjusts for limitations of trig in the math library
    if (deltax > 0 && deltay > 0) degree += -90;
    if (deltax > 0 && deltay < 0) degree += 270;
    if (deltax < 0 && deltay < 0) degree += 90;
    if (deltax < 0 && deltay > 0) degree += 90;
    rotation = degree;
  }

   // Places Turret on top of another character object
   public void showcompared(Character related) {
    xcord = related.xcord;
    ycord = related.ycord;
    this.show();
  }
}