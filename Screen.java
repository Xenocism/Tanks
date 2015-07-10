/******************************************************************************
  * Creator: Samuel Ferguson
  *
  * 
  * Dependancies: StdDraw (Princeton Cos126 download package)
  * 
  * 
  * Descrition: Background Screen for GameProject
  * 
  * 2361 x 2055
  ****************************************************************************/

public class Screen {

 public static double movescreenx(Character thing, int move, double pos) {
    double rotation = thing.rotation();
    return move * Math.cos(Math.toRadians(90 + rotation));
  }

  public static double movescreeny(Character thing, int move, double pos) {
    double rotation = thing.rotation();
    return move * Math.sin(Math.toRadians(90 + rotation));
  }

  public static void main(String[] args) {
    StdDraw.setCanvasSize(750,750);
    StdDraw.setXscale(-100, 100);
    StdDraw.setYscale(-100, 100);

    Character tank = new Character(0, 0, "tank.png");
    Character turret = new Character(0, 2, "turret.png");

    double screeny = 75.0;
    double screenx = 75.0;

    while (true) {
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
      turret.setdirectionmouse();
      StdDraw.picture(screenx, screeny, "background.png");
      tank.show();
      turret.showcompared(tank);
      StdDraw.show(10);
    }
  }
}