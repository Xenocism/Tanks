/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * Dependancies: Tankframe Tankframe.java, Characters.java, Turret.java
  * 
  * Descrition: Tank game executer (contains the game while loops)
  * 
  ****************************************************************************/

public class Screen {


  // Changes background in the x direction
  public static double movescreenx(Characters thing, int move) {
    double rotation = thing.getrotation();
    return move * Math.cos(Math.toRadians(90 + rotation)); // zero orientation is upwards
  } 

  // Changes the background in the y direction
  public static double movescreeny(Characters thing, int move) {
    double rotation = thing.getrotation();
    return move * Math.sin(Math.toRadians(90 + rotation)); // zero orientation is upwards
  }

  /*
  // Restart functionality
  public static void restart() {
    try {
      Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
    } catch (Exception e) {
        e.printStackTrace();
      }
  }
  */

  // All game screen functions master while loops
  public static void main(String[] args) {
    
    // Sets gameboard and creates player
    Tankframe.setCanvasSize(800,800); // popup screen size
    Tankframe.setXscale(-100, 100);
    Tankframe.setYscale(-100, 100);
    Characters tank = new Characters(0.0, 0.0, "tank.png");
    Turret turret = new Turret(0.0, 0.0, "turret.png");

    // Origional coordinates of the screen background image
    double screenx = 0.0;
    double screeny = 0.0;

    // Gameplay while loop
    while (true) {
 
      // User imput for player movement
      if (Tankframe.hasNextKeyTyped()) {
        char nexttyped = Tankframe.nextKeyTyped();
        switch (nexttyped) {

          case 'a': tank.rotate(10.0);
          
          case 'd': tank.rotate(-10.0);

          case 'w':
            if ((tank.xcord > -193 && tank.xcord < 193) && (tank.ycord > -155 && tank.ycord < 155)) { //Effective background edges
              tank.move(movescreenx(tank, 1), movescreeny(tank, 1));
              // StdOut.println ("X: " + tank.xcord + " " + "Y: " + tank.ycord); // Tester method
              screenx += movescreenx(tank, -1);
              screeny += movescreeny(tank, -1);
            } else {
              if (tank.xcord < -193) {
                if (movescreenx(tank, 1) > 0) {
                  tank.move(movescreenx(tank, 1), 0.0);
                  screenx += movescreenx(tank, -1);
                }
                screeny += movescreeny(tank, -1);
              }
              if (tank.xcord > 193 && movescreenx(tank, 1) < 0) {
                tank.move(movescreenx(tank, 1), 0.0);
                screenx += movescreenx(tank, -1);
              }
              if (tank.ycord < -155 && movescreeny(tank, 1) > 0) {
                tank.move(0.0, movescreeny(tank, 1));
                screeny += movescreeny(tank, -1);
              }
              if (tank.ycord > 155 && movescreeny(tank, 1) < 0) {
                tank.move(0.0, movescreeny(tank, 1));
                screeny += movescreeny(tank, -1);
              }
            }
            break;

          case 's': 
            if ((tank.xcord > -193 && tank.xcord < 193) && (tank.ycord > -155 && tank.ycord < 155)) { //Effective background edges
              tank.move(movescreenx(tank, -1), movescreeny(tank, -1));
              // StdOut.println ("X: " + tank.xcord + " " + "Y: " + tank.ycord); // Tester method
              screenx += movescreenx(tank, 1);
              screeny += movescreeny(tank, 1);
            } else {
              if (tank.xcord < -193 && movescreenx(tank, -1) > 0) {
               tank.move(movescreenx(tank, -1), 0.0);
               screenx += movescreenx(tank, 1);
             }
              if (tank.xcord > 193 && movescreenx(tank, -1) < 0) {
                tank.move(movescreenx(tank, -1), 0.0);
                screenx += movescreenx(tank, 1);
              }
              if (tank.ycord < -155 && movescreeny(tank, -1) > 0) {
                tank.move(0.0, movescreeny(tank, -1));
                screeny += movescreeny(tank, 1);
              }
              if (tank.ycord > 155 && movescreeny(tank, -1) < 0) {
                tank.move(0.0, movescreeny(tank, -1));
                screeny += movescreeny(tank, 1);
              }
            }
            break;
        }
      } 

      // Redraws the gameboard and pauses
      Tankframe.picture(screenx, screeny, "background.png");
      tank.show(0, 0);
      turret.setdirectionmouse();
      turret.show(0, 0);
      Tankframe.show(10); // 10 miliseconds
    }
  }
}