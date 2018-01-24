package vehicle;

import application.ApplicationContext;
import grid.impl.LightDataLayer;
import javafx.geometry.Point2D;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * /**
 * Creatures have sensors (eyes), actors (wheels) and a brain to link them. Based on information (next position)
 * given by the brain a creature can move to a certain position on its world (map).
 * Every creature lives in its own thread
 */

public class Creature implements Runnable{

    private final List<PropertyChangeListener> listeners = new ArrayList<>();
    double rotate = 0;
    private Boolean isDead = false;

    private Location located;
    private Point2D currentVelocity = new Point2D(1, 1);
    private Point2D position;
    private Wheel leftWheel;
    private Wheel rightWheel;
    private ApplicationContext applicationContext;
    private Eye leftFrontEye;
    private Eye leftBackEye;
    private Eye rightFrontEye;
    private Eye rightBackEye;
    private LightDataLayer.reachedBorder border = LightDataLayer.reachedBorder.NONE;
    private Brain brain;
    private LightDataLayer lightMap;
    private int speedAmplification;

    public Creature(ApplicationContext applicationContext, int x, int y) {
        this.speedAmplification = applicationContext.getSettingsController().getVehicleSpeed();
        this.position  = new Point2D(x, y);
    	this.applicationContext=applicationContext;
        this.lightMap = applicationContext.getLightGrid();
        this.leftFrontEye = new Eye(Eye.sensorPosition.LEFTFRONT, lightMap);
        this.leftBackEye = new Eye(Eye.sensorPosition.LEFTBACK, lightMap);
        this.rightFrontEye = new Eye(Eye.sensorPosition.RIGHTFRONT, lightMap);
        this.rightBackEye = new Eye(Eye.sensorPosition.RIGHTBACK, lightMap);
        leftWheel = new Wheel(Wheel.WheelPosition.LEFT, speedAmplification);
        rightWheel = new Wheel(Wheel.WheelPosition.RIGHT, speedAmplification);

        this.brain = new Brain(this);
        this.brain.linkComponents(leftFrontEye, leftBackEye, leftWheel);
        this.brain.linkComponents(rightFrontEye, rightBackEye, rightWheel);
        applicationContext.getVehicleGrid().addVehicle(this, position);
        leftFrontEye.addPropertyChangeListener(evt -> {
            if(leftFrontEye.getBorder() == LightDataLayer.reachedBorder.TOP ||
                    leftFrontEye.getBorder() == LightDataLayer.reachedBorder.LEFT){
                border = leftFrontEye.getBorder();
            }
        });
        leftBackEye.addPropertyChangeListener(evt -> {
            if(leftBackEye.getBorder() == LightDataLayer.reachedBorder.BOTTOM){
                border = leftBackEye.getBorder();
            }
        });
        rightFrontEye.addPropertyChangeListener(evt -> {
            if(rightFrontEye.getBorder() == LightDataLayer.reachedBorder.TOP ||
                    rightFrontEye.getBorder() == LightDataLayer.reachedBorder.RIGHT){
                border = rightFrontEye.getBorder();
            }
        } );
        rightBackEye.addPropertyChangeListener(evt -> {
            if(rightBackEye.getBorder() == LightDataLayer.reachedBorder.BOTTOM){
                border = rightBackEye.getBorder();
            }
        });
    }

    public void update() {
    	//System.out.println("Creature.update()");
        emitPropertyChange("currentPosition", position.getX(), position.getY());


        if(border == LightDataLayer.reachedBorder.NONE){

            Point2D leftVelocity = leftWheel.getVelocity();
            Point2D rightVelocity = rightWheel.getVelocity();
            Point2D result = leftVelocity.add(rightVelocity);

            double x = currentVelocity.getX();
            double y = currentVelocity.getY();

            double currentAngle = angleBetween(currentVelocity);
            double resultAngle = angleBetween(result);

            double angle = resultAngle - currentAngle;


            if (angle > 90 || angle < -90) {
                switch (located) {
                    case TOPLEFT:
                        if (y > 0) {
                            rotate = -0.15;
                        } else {
                            rotate = +0.15;
                        }
                        break;
                    case TOPRIGHT:
                        if (y > 0) {
                            rotate = +0.15;
                        } else {
                            rotate = -0.15;
                        }
                        break;
                    case BOTTOMRIGHT:
                        if (y > 0) {
                            rotate = +0.15;
                        } else {
                            rotate = -0.15;
                        }
                        break;
                    case BOTTOMLEFT:
                        if (y > 0) {
                            rotate = -0.15;
                        } else {
                            rotate = +0.15;
                        }
                        break;
                }

                currentVelocity = new Point2D((x * Math.cos(rotate) - y * Math.sin(rotate)),( x * Math.sin(rotate) + y * Math.cos(rotate)));
            }

            position = position.add((result.getX() + currentVelocity.getX()), (result.getY() + currentVelocity.getY()));
            currentVelocity = result.add(currentVelocity);
        }
        else {
            System.out.println(border);
            if(border == LightDataLayer.reachedBorder.TOP || border == LightDataLayer.reachedBorder.BOTTOM){
                currentVelocity = new Point2D((currentVelocity.getX() * Math.cos(3.1) - currentVelocity.getY() * Math.sin(3.1)), (currentVelocity.getX() * Math.sin(3.1) + currentVelocity.getY() * Math.cos(3.1)));
                position = position.add(currentVelocity);
                border = LightDataLayer.reachedBorder.NONE;
            }
            else if(border == LightDataLayer.reachedBorder.LEFT || border == LightDataLayer.reachedBorder.RIGHT){
                currentVelocity = new Point2D((currentVelocity.getX() * Math.cos(3.1) - currentVelocity.getY() * Math.sin(3.1)), (currentVelocity.getX() * Math.sin(3.1) + currentVelocity.getY() * Math.cos(3.1)));
                position = position.add(currentVelocity);
                border = LightDataLayer.reachedBorder.NONE;
            }
        }
    }

    public Point2D getPosition(){
    	return position;
    }

    public double angleBetween(Point2D v) {

        Point2D reference = new Point2D(1, 0);

        //sector1
        if (v.getY() < 0 && v.getX() > 0) {
            located = Location.TOPLEFT;
            //System.out.print("go down right");
            return reference.angle(v);
        }
        //sector2
        else if (v.getY() < 0 && v.getX() < 0) {
            located = Location.TOPRIGHT;
            //System.out.print("go down left");
            return reference.angle(v);
        }
        //sector3
        else if (v.getY() > 0 && v.getX() < 0) {
            located = Location.BOTTOMRIGHT;
            //System.out.print("go up left");
            return reference.angle(v) + 90;
        }
        //sector4
        else if (v.getY() > 0 && v.getX() > 0) {
            located = Location.BOTTOMLEFT;
            //System.out.print("go up right");
            return reference.angle(v) + 180 + 90;
        }
        return 0;
    }

    /**
     * Adds listeners observed values in this class
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    private void emitPropertyChange(String property, double xPos, double yPos) {
        for (PropertyChangeListener l : listeners) {
            l.propertyChange(new PropertyChangeEvent(this, property, xPos, yPos));
        }
    }

    public void kill() {
        isDead = true;
    }

	@Override
	public void run() {
		try {
            while (!isDead) {
                update();
    			applicationContext.getVehicleGrid().setValue(position, this);

    		}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

    private enum Location {TOPRIGHT, TOPLEFT, BOTTOMLEFT, BOTTOMRIGHT}
}
