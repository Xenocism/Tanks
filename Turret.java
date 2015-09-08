/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: Tankframe.java, Picture, Character.java
  * 
  * Descrition: Turret Character Extension
  * 
  ****************************************************************************/

public class Turret extends Characters {

  // Constructor
  public Turret(double x, double y, String pic) {
    super(x, y, pic);
  }

  // Sets the rotation to match that of the mouse position
  public void setdirectionmouse() {

    // Finds position of mouse
    double xpos = Tankframe.mouseX();
    double ypos = Tankframe.mouseY();
    double degree = 0.0;

    // Along axes or at center (to avoid zero value glitches in using Math library)
    if (xpos == 0.0 || ypos == 0.0) {
      if (xpos == 0.0 && ypos > 0.0) degree = 0.0;
      if (xpos == 0.0 && ypos < 0.0) degree = 180.0;
      if (xpos > 0.0 && ypos == 0.0) degree = -90.0;
      if (xpos < 0.0 && ypos == 0.0) degree = 90.0;
      if (xpos == 0.0 && ypos == 0.0) degree = 0.0;
    } else {
        degree = Math.toDegrees(Math.atan(ypos / xpos));
        if (xpos > 0 && ypos > 0) degree += -90;
        if (xpos > 0 && ypos < 0) degree += 270;
        if (xpos < 0 && ypos < 0) degree += 90;
        if (xpos < 0 && ypos > 0) degree += 90;
      }
    rotation = degree;
  }
}