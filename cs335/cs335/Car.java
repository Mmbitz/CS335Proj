package cs335;

import javax.media.opengl.GL2;

public abstract class Car {
	protected float x, y, z;
	protected double velocityX, velocityY, velocityZ;
	protected double accelX, accelY, accelZ;
	
	protected static double friction = 0.02;
	//protected static double acceleration = 0.05;
	protected static double breaking = 0; 
	protected static double mass = 10;

	
	protected static double maxSpeed = 20;
	private static final double collisionRadius = 1;
	
	private double time = 0;
	
	public Car(float x, float y, float z, double velocityX, double velocityY, double velocityZ){
		this.x = x;
		this.y = y;
		this.z = z;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
	}
	
	
	public void steering(){
		double degreesPerFrame = 180 / (2*30); //180 degrees over 30 fps in 2s
		//velocityX = velocityX * -1 * Math.cos(degreesPerFrame);
		//velocityY = velocityY * -1 * Math.sin(degreesPerFrame);
		double yChange = Math.sin(degreesPerFrame) * velocityY;
		double xChange = Math.cos(degreesPerFrame) * velocityX;
		x += xChange;
		y += yChange;
	}
	

	
	/**
	 * Updates position, etc
	 */
	public void update(){
		control();
		// only changes position and velocity here 
		double frequency = 10/1000.0;
		
		
		x += velocityX * frequency;
		y += velocityY * frequency;
		z += velocityZ * frequency;
		
		accelX = 0.5;
		accelY = 0.5;
		accelZ = 0;
		
		
		
		
		
		
		
		//other stuff
			if (time == 0){
			velocityX += accelX;
			velocityZ += accelZ;
			}
			time += frequency;
			if (velocityZ > maxSpeed){ //Z is forward vector
				velocityZ = maxSpeed;
			}
			velocityY = 0;
			y = 0;
			//if collides
			//velocityX -= friction;
			x += velocityX;
			z += velocityZ;
			
			if(Math.abs(x + velocityX) >= 8.4){
				velocityX *= -1; //collide with wall
				//velocityX -= friction;
			}	
			if(Math.abs(z + velocityZ) >= 5){
				velocityZ *= -1;
				//velocityZ -= friction;
			}
			x += velocityX;
			z += velocityZ;
	}
	
	
	/**
	 * Update acceleration, direction
	 */
	public abstract void control();
	
	/**
	 * Draws car
	 */
	public abstract void draw(GL2 gl);
}
