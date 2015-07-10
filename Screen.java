/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: StdDraw (Princeton Cos126 download package), Character.java, Turret.java
  * 
  * Descrition: Background Screen for Tank Game Project
  * 
  * 2361 x 2055
  ****************************************************************************/

public class Screen {


  // Changes background in the x direction
  public static double movescreenx(Character thing, int move, double pos) {
    double rotation = thing.getrotation();
    return move * Math.cos(Math.toRadians(90 + rotation));
  }

  // Changes the background in the y direction
  public static double movescreeny(Character thing, int move, double pos) {
    double rotation = thing.getrotation();
    return move * Math.sin(Math.toRadians(90 + rotation));
  }

  // All game screen functions master while loops
  public static void main(String[] args) {
    
    // Sets gameboard and creates player
    StdDraw.setCanvasSize(750,750);
    StdDraw.setXscale(-100, 100);
    StdDraw.setYscale(-100, 100);
    Character tank = new Character(0, 0, "tank.png");
    Turret turret = new Turret(0, 2, "turret.png"); // (0,2) give a slightly better visual

    // origional coordinates of the screen background image
    double screenx = 75.0;
    double screeny = 75.0;

    // Gameplay while loop
    while (true) {

      // User imput for player movement
      if (StdDraw.hasNextKeyTyped()) {
        char nexttyped = StdDraw.nextKeyTyped();
        switch (nexttyped) {
          case 'a': tank.rotate(10.0); break;
          case 'd': tank.rotate(-10.0); break;
          case 'w':
            screenx += movescreenx(tank, -1, screenx);
            screeny += movescreeny(tank, -1, screeny);
            break;
          case 's': 
            screenx += movescreenx(tank, 1, screenx);
            screeny += movescreeny(tank, 1, screeny);
            break;
        }
      }

      // Redraws the gameboard and pauses
      turret.setdirectionmouse();
      StdDraw.picture(screenx, screeny, "background.png");
      tank.show();
      turret.showcompared(tank);
      StdDraw.show(10);
    }
  }
}