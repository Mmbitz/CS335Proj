package cs335;

import java.awt.event.KeyEvent;

import com.owens.oobjloader.*;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;



public class JoglEventListener implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private int windowWidth, windowHeight;
	
	private TextureLoader texture_loader = null;
	private Skybox current_skybox = null;
	private final float skybox_size = 1000.0f;
	private boolean skybox_follow_camera = true;
	private final String[] skybox_names = {
		"ThickCloudsWater", "DarkStormy",
		"FullMoon", "SunSet",
		"CloudyLightRays", "TropicalSunnyDay"
	};
	// Making this larger will allocate more skybox textures to start, giving a
	// super slow startup, but allowing you to switch between them quickly.
	// Best to use a value of 1 for production code.
	private final int skybox_max_textures = 1;
	private Skybox[] skyboxes = new Skybox[ skybox_names.length ];
	private boolean psychedelic_mode = false;
	private int paper_cube_size = 1;
	int texID[]  = new int[3];
	
	private float scene_eye_x = 0.0f;
	private float scene_eye_y = 0.0f;
	private float scene_eye_z = 0.0f;
	private float scene_look_x = 1.0f;
	private float scene_look_y = 0.0f;
	private float scene_look_z = 0.0f;
	
	//Positions
	double posX = 0;
	double posY = 0;
	double posZ = 0;
	//Acceleration
	double accelX = 0;
	double accelY = 0;
	double facingDirection =0;
	double facingVelocity = 0;
	double collisionRadius = 1;
	//Velocity
	double velocityX = 0;
	double velocityY = 0;
	//Render Car
	private Build my_model;
	//Set max
	double maxAngle = 60;
	double maxAccelX = 10;
	double maxAccelY = 10;
	
	private int mouse_x0 = 0;
	private int mouse_y0 = 0;
	private int mouse_mode = 0;
	
	private final int MOUSE_MODE_NONE = 0;
	private final int MOUSE_MODE_ROTATE = 1;
	
	boolean animation = false;
	
	public enum Turns{noTurn, turnLeft, turnRight};
	Turns turning = Turns.noTurn;
	
	private boolean[] keys = new boolean[256];
	GLUquadric quadric;
	private GLU glu = new GLU();
	
	public void displayChanged( GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) { }

	@Override
	public void init( GLAutoDrawable gLDrawable ) {
		
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
		gl.glColor3f( 1.0f, 1.0f, 1.0f );
		gl.glClearDepth( 1.0f );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glDepthFunc( GL.GL_LEQUAL );
		gl.glEnable( GL.GL_TEXTURE_2D );
		//gl.glEnable(GL2.GL_LIGHTING);
		
		quadric = glu.gluNewQuadric();
		
		// Initialize the texture loader and skybox.
		texture_loader = new TextureLoader( gl );
		
		for ( int i = 0; i < skybox_max_textures; ++i )
			skyboxes[ i ] = new Skybox( texture_loader, skybox_names[ i ] );
		
		current_skybox = skyboxes[ 0 ];
		try {
			gl.glGenTextures(texID.length, texID, 0);
			texture_loader.loadTexture(texID[0], "/Users/Julian/Documents/workspace/skybox/CS335Textures/grass.jpg");
			texture_loader.loadTexture(texID[1], "/Users/Julian/Documents/workspace/skybox/CS335Textures/asphalt.jpg");
			/*texture_loader.loadTexture(texID[2], "/Users/Julian/Documents/workspace/skybox/CS335Textures/brick.jpg");
			texture_loader.loadTexture(texID[3], "/Users/Julian/Documents/workspace/skybox/CS335Textures/crowd.jpg");
			texture_loader.loadTexture(texID[4], "/Users/Julian/Documents/workspace/skybox/CS335Textures/bluth.jpg");
			texture_loader.loadTexture(texID[5], "/Users/Julian/Documents/workspace/skybox/CS335Textures/garagefloor.jpg");
			texture_loader.loadTexture(texID[6], "/Users/Julian/Documents/workspace/skybox/CS335Textures/garagedoor.jpg");
			texture_loader.loadTexture(texID[7], "/Users/Julian/Documents/workspace/skybox/CS335Textures/garagesign.jpg");
			texture_loader.loadTexture(texID[8], "/Users/Julian/Documents/workspace/skybox/CS335Textures/speedway.jpg"); */
	} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		my_model = new Build();
		try{
			new Parse(my_model, "/Users/Julian/Documents/workspace/skybox/models/car/car.obj");
			//new Parse(my_model, "/Users/Julian/Documents/workspace/skybox/models/Lambo/Avent.obj");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		// Initialize the keys.
		for ( int i = 0; i < keys.length; ++i )
			keys[i] = false;
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
	}
	
	public double distanceFormula(double x1, double x2, double y1, double y2){
		double distance = Math.sqrt( Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) );
		return distance;
	}
	
	void drawCar(GL2 gl){
		gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		for (int i = 0; i < my_model.faces.size(); ++i){
			ArrayList<FaceVertex> vertices = my_model.faces.get(i).vertices;
			//TODO: check and bind texture for this face here
			gl.glBegin(GL2.GL_TRIANGLES);
			for (int j = 0; j< vertices.size(); ++j){
				FaceVertex vertex = vertices.get(j);
				//TODO: add texture coordinates here
				gl.glVertex3f(vertex.v.x, vertex.v.y, vertex.v.z);
			}
			gl.glEnd();
		}
	}
	
	@Override
	public void reshape( GLAutoDrawable gLDrawable, int x, int y, int width, int height ) {
		windowWidth = width;
		windowHeight = height > 0 ? height : 1;
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glViewport( 0, 0, width, height );
		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 60.0f, (float) windowWidth / windowHeight, 0.1f, skybox_size * (float) Math.sqrt( 3.0 ) / 2.0f );
	}
	
	double velX = 0.1;
	double velY =0.1;
	void updateAIPositions(GL2 gl){
		if (animation == true){
			//At posX = -15, 14, 19, and 48 hits walls on right side
			//At posY = -60, and 60 rotation starts
			//At posY = +- 60.5 and posX = +- 16.5 at the radius for turning
			
			posX += velX;
			posY += velY;
			
			if(posX + velX >= 14 || posX + velX <= -15){
				//velX *= -1; //collide with wall
				//velocityX -= friction;
			}	
			if(Math.abs(posY + velY) >= 60){
				velY *= -1; 
				//velocityZ -= friction;
			}
			
			posX += velX;
			posY += velY;
			
		}
	}
	
	double friction(int mass){
		double frict = .5 * mass * velocityX * velocityX * 9.8;
		return frict;
	}
	
	public void moveCar(){
		double frequency = 10/1000;
		double velocityDirection = Math.toDegrees(Math.atan2(-velocityY, velocityX));
		double turnAmount = 0;
		
		if (velocityDirection < 0){
			velocityDirection += 360;
		}
		if (turning == Turns.noTurn){
			if (facingDirection > velocityDirection){
				turnAmount = 1 / (-1 * 60);
			}
			if (facingDirection < velocityDirection){
				turnAmount = 1 / (1 * 60);
			}
		}
		if (turning == Turns.turnRight){
			if (facingDirection -  velocityDirection < -maxAngle){
				turning = Turns.noTurn;
				turnAmount = 0;
				facingVelocity = 0;
			}
			else{
				turnAmount = facingVelocity;
			}
		}
		if (turning == Turns.turnLeft){
			if (facingDirection - velocityDirection > maxAngle){
				turning = Turns.noTurn;
				turnAmount = 0;
				facingVelocity = 0;
			}
			else{
				turnAmount = -1 * facingVelocity;
			}
		}
		if (facingDirection > 360){
			facingDirection = 0;
		}
		if (facingDirection < 0){
			facingDirection = 360;
		}
		
		velocityX += accelX * 0.2 * frequency;
		velocityY += -1 * accelY * 0.2 * frequency;
		posX += velocityX * frequency;
		posY += velocityY * frequency;
		posZ = 0;
		facingVelocity += turnAmount * frequency;
		facingDirection += facingVelocity * frequency;
		//System.out.println(velocityX);
	}

	@Override
	public void display( GLAutoDrawable gLDrawable ) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glPushMatrix();
		
		final float throttle_pan = 0.25f;
		
		//AI Path
		//updateAIPositions(gl);
		
		
		// Update the camera state.
		if ( keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S] ) {
			float normxy = (float) Math.sqrt( scene_look_x * scene_look_x + scene_look_y * scene_look_y );
			float multiplier = keys[KeyEvent.VK_W] ? 1.0f : -1.0f;
			scene_eye_x += scene_look_x / normxy * throttle_pan * multiplier;
			scene_eye_y += scene_look_y / normxy * throttle_pan * multiplier;
		}
		
		if ( keys[KeyEvent.VK_R] ) {
			scene_eye_z += throttle_pan;
		} else if ( keys[KeyEvent.VK_F] ) {
			scene_eye_z -= throttle_pan;
		}
		
		if ( keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D] ) {
			float theta = (float) Math.atan2( scene_look_y, scene_look_x );
			float phi = (float) Math.acos( scene_look_z );
			
			if ( keys[KeyEvent.VK_A] )
				theta += Math.PI / 2.0;
			else if ( keys[KeyEvent.VK_D] )
				theta -= Math.PI / 2.0;
			
			float strafe_x = (float)( Math.cos( theta ) * Math.sin( phi ) );
			float strafe_y = (float)( Math.sin( theta ) * Math.sin( phi ) );
			float normxy = (float) Math.sqrt( strafe_x * strafe_x + strafe_y * strafe_y );
			
			scene_eye_x += strafe_x / normxy * throttle_pan;
			scene_eye_y += strafe_y / normxy * throttle_pan;
		}
		
		glu.gluLookAt( scene_eye_x, scene_eye_y, scene_eye_z,
				scene_eye_x + scene_look_x, scene_eye_y + scene_look_y, scene_eye_z + scene_look_z,
				0.0f, 0.0f, 1.0f );
		
		gl.glPushMatrix();
		if ( skybox_follow_camera )
			gl.glTranslatef( scene_eye_x, scene_eye_y, scene_eye_z );
		
		current_skybox.draw( gl, skybox_size );
		gl.glPopMatrix();
		
		Random random = new Random();
		
		gl.glPushMatrix();
		gl.glRotated(90, 1, 0, 0);
		gl.glTranslated(posX, posY, posZ);
		//System.out.println(posX);
		gl.glRotated(-90, 0, 1, 0);
		moveCar();
		drawCar(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslated(posX-4, posY, posZ);
		//drawCube(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslated(posX, posY, posZ);
		//drawCube(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f, -0.5f);
		drawPlane( gl, 750.0f );
		gl.glTranslatef(0f, 0f, 0.1f);
		drawTrack(gl, 15f);
		gl.glTranslatef(27f, 0f, 0f);
		drawGrand(gl, 15.0f);
		gl.glTranslatef(-88f, 0f, 0f);
		gl.glRotatef(-180f, 0f, 0f, 1f);
		drawGrand(gl, 15.0f);
		gl.glRotatef(180f, 0f, 0f, 1f);
		gl.glTranslatef(44f, 110f, 0f);
		//drawGarage( gl, 30.0f );
		gl.glTranslatef(-44f, -110f, 0f);
		gl.glTranslatef(27f, 0f, 0f);
		//drawBanner( gl, 15.0f );
		gl.glTranslatef(-27f, 0f, 0f);
		gl.glPopMatrix();
		
		
		/*for ( int i = 0; i < paper_cube_size; ++i ) {
			gl.glTranslatef( 10.0f, 0.0f, 0.0f );
			gl.glPushMatrix();
			
			for ( int j = 0; j < paper_cube_size; ++j ) {
				gl.glTranslatef( 0.0f, 10.0f, 0.0f );
				gl.glPushMatrix();
				
				for ( int k = 0; k < paper_cube_size; ++k ) {
					gl.glTranslatef( 0.0f, 0.0f, 10.0f );
					
					if ( psychedelic_mode)
						gl.glColor3f( random.nextFloat(), random.nextFloat(), random.nextFloat() );
					drawPlane( gl, 5.0f );
				}
				
				gl.glPopMatrix();
			}
			
			gl.glPopMatrix();
		}*/
		
		gl.glPopMatrix();
	}
	
	
	public static void drawCube(final GL2 gl) {
    	gl.glBegin(GL2.GL_QUADS);
         
    	// on the XY plane
    	// front plane
         gl.glNormal3f(0,  0, 1);
         //gl.glColor3f(1, 0, 0);
         
         gl.glTexCoord2f(0.0f, 1.0f);
         gl.glVertex3f(0, 0, 1); 
        
         
         
         gl.glTexCoord2f(1.0f, 1.0f);
         gl.glVertex3f(1, 0, 1);
         
         gl.glTexCoord2f(1.0f, 0.0f);
         gl.glVertex3f(1, 1, 1); 
         
         gl.glTexCoord2f(0.0f, 0.0f);
         gl.glVertex3f(0, 1, 1);
        
         gl.glTexCoord2f(98.0f/255, 136.0f/255);
         // back plane
         gl.glNormal3f(0,  0, -1);
         gl.glColor3f(1, 0, 0);
         gl.glVertex3f(0, 0, 0); 
         gl.glVertex3f(1, 0, 0);
         gl.glVertex3f(1, 1, 0); 
         gl.glVertex3f(0, 1, 0);
         
         // on the YZ plane
         // left plane 
         gl.glNormal3f(-1,  0, 0);
         gl.glColor3f(0, 1, 0);
         gl.glVertex3f(0, 0, 0); 
         gl.glVertex3f(0, 1, 0);
         gl.glVertex3f(0, 1, 1); 
         gl.glVertex3f(0, 0, 1);
         
         // right plane
         gl.glNormal3f(1,  0, 0);
         gl.glColor3f(0, 1, 0);
         gl.glVertex3f(1, 0, 0); 
         gl.glVertex3f(1, 1, 0);
         gl.glVertex3f(1, 1, 1); 
         gl.glVertex3f(1, 0, 1);
         
         
         // on the XZ plane,  
         // up plane; 
         gl.glNormal3f(0,  1, 0);
         gl.glColor3f(0, 0, 1);
         gl.glTexCoord2f(0+0.2f, 1-(1-0.2f));gl.glVertex3f(0, 1, 0); 
         gl.glTexCoord2f(1-0.2f, 1-(1-0.2f));gl.glVertex3f(1, 1, 0);
         gl.glTexCoord2f(1-0.2f, 1-(0 + 0.2f));gl.glVertex3f(1, 1, 1); 
         gl.glTexCoord2f(0+0.2f, 1-(0 + 0.2f));gl.glVertex3f(0, 1, 1);
         
         // down plane; 
         gl.glNormal3f(0,  -1, 0);
         gl.glColor3f(0, 0, 1);
         gl.glVertex3f(0, 0, 0); 
         gl.glVertex3f(1, 0, 0);
         gl.glVertex3f(1, 0, 1); 
         gl.glVertex3f(0, 0, 1);
        
         gl.glEnd(); 
    }
	
	void drawTrack(GL2 gl, float size ) {
		final float length = size * 4f;
		final float width = size;
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
		//Draw the first straight track
		gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(2.0f, 2.0f);
			gl.glVertex3f(width, length, 0f);
			gl.glTexCoord2f(-1f, 2.0f);
			gl.glVertex3f(-width, length, 0f);
			gl.glTexCoord2f(-1f, -1f);
			gl.glVertex3f(-width, -length, 0f);
			gl.glTexCoord2f(2.0f, -1f);
			gl.glVertex3f(width, -length, 0f);
		gl.glEnd();
		
		//Draw the first inside wall
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(width, length, 0f);
			gl.glVertex3f(width, length, 3f);
			gl.glVertex3f(width, -length, 3f);
			gl.glVertex3f(width, -length, 0f);
		gl.glEnd();
		
		//Draw the first outside wall
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(-width, length, 0f);
			gl.glVertex3f(-width, length, 3f);
			gl.glVertex3f(-width, -length, 3f);
			gl.glVertex3f(-width, -length, 0f);
		gl.glEnd();
		
		gl.glTranslatef(17f, 60f, 0f);
		//Drawing the first curved track piece
		Torus2d(gl, 2f, 32f, 20);
		//Drawing the second curved inner wall
		gl.glBegin(GL2.GL_QUAD_STRIP);
		for(int i = 0; i <= 20; ++i)
		{
			float angle = ( i / 20f ) * 3.14159f * 1.0f;
			
			gl.glVertex3f((float) (32 * Math.cos( angle )),(float) (32 * Math.sin( angle )), 0f);
			gl.glVertex3f((float) (32 * Math.cos( angle )),(float) (32 * Math.sin( angle )), 3f);
		}
		gl.glEnd();
		//Drawing the first curved inner wall
		gl.glBegin(GL2.GL_QUAD_STRIP);
		for(int i = 0; i <= 20; ++i)
		{
			float angle = ( i / 20f ) * 3.14159f * 1.0f;
			gl.glVertex3f((float) (2 * Math.cos( angle )),(float) (2 * Math.sin( angle )), 0f);
			gl.glVertex3f((float) (2 * Math.cos( angle )),(float) (2 * Math.sin( angle )), 3f);
		}
		gl.glEnd();
		
		gl.glTranslatef(17f, -60f, 0f);
		//Drawing the second straight track part
		gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(2.0f, 2.0f);
			gl.glVertex3f(width, length, 0f);
			gl.glTexCoord2f(-1f, 2.0f);
			gl.glVertex3f(-width, length, 0f);
			gl.glTexCoord2f(-1f, -1f);
			gl.glVertex3f(-width, -length, 0f);
			gl.glTexCoord2f(2.0f, -1f);
			gl.glVertex3f(width, -length, 0f);
		gl.glEnd();
		//Draw the second inside wall
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(width, length, 0f);
			gl.glVertex3f(width, length, 3f);
			gl.glVertex3f(width, -length, 3f);
			gl.glVertex3f(width, -length, 0f);
		gl.glEnd();
		
		//Draw the second outside wall
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(-width, length, 0f);
			gl.glVertex3f(-width, length, 3f);
			gl.glVertex3f(-width, -length, 3f);
			gl.glVertex3f(-width, -length, 0f);
		gl.glEnd();
		
		gl.glTranslatef(-17f, -60f, 0f);
		gl.glRotatef(-180f, 0f, 0f, 1f);
		//Drawing the second curve
		Torus2d(gl, 2f, 32f, 20);
		//Drawing the second outer wall
		gl.glBegin(GL2.GL_QUAD_STRIP);
		for(int i = 0; i <= 20; ++i)
		{
			float angle = ( i / 20f ) * 3.14159f * 1.0f;
			gl.glVertex3f((float) (32 * Math.cos( angle )),(float) (32 * Math.sin( angle )), 0f);
			gl.glVertex3f((float) (32 * Math.cos( angle )),(float) (32 * Math.sin( angle )), 3f);
		}
		gl.glEnd();
		//Drawing the second inner wall
		gl.glBegin(GL2.GL_QUAD_STRIP);
		for(int i = 0; i <= 20; ++i)
		{
			float angle = ( i / 20f ) * 3.14159f * 1.0f;
			gl.glVertex3f((float) (2 * Math.cos( angle )),(float) (2 * Math.sin( angle )), 0f);
			gl.glVertex3f((float) (2 * Math.cos( angle )),(float) (2 * Math.sin( angle )), 3f);
		}
		gl.glEnd();
		
		gl.glRotatef(180f, 0f, 0f, 1f);
		gl.glTranslatef(17f, 60f, 0f);
	}
	
	void Torus2d(GL2 gl, float inner, float outer, int pts )
	{
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
	    gl.glBegin(GL2.GL_QUAD_STRIP);
	    for(int i = 0; i <= pts; ++i )
	    {
	        float angle = ( i / (float)pts ) * 3.14159f * 1.0f;
	        gl.glTexCoord2f(0.5f * i, 0.5f * i);
	        gl.glVertex2f((float) (inner * Math.cos( angle )),(float) (inner * Math.sin( angle )));
	        gl.glTexCoord2f(0.5f * i, 1.0f * i);
	        gl.glVertex2f((float) (outer * Math.cos( angle )),(float) (outer * Math.sin( angle )));
	    }
	    gl.glEnd();
	}
	
	
	void drawGarage(GL2 gl, float size){
		final float width = size;
		final float length = size / 3.0f;
		
		//Floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[5]);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, -1.0f);
			gl.glVertex3f(width, length, 0.0f);
			gl.glTexCoord2f(-1.0f, 3.0f);
			gl.glVertex3f(-width, length, 0.0f);
			gl.glTexCoord2f(3.0f, 3.0f);
			gl.glVertex3f(-width, -length, 0.0f);
			gl.glTexCoord2f(3.0f, -1.0f);
			gl.glVertex3f(width, -length, 0.0f);
		gl.glEnd();
		//Roof
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[2]);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, -1.0f);
			gl.glVertex3f(width, length, 7.5f);
			gl.glTexCoord2f(1.0f, -1.0f);
			gl.glVertex3f(-width, length, 7.5f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(-width, -length, 7.5f);
			gl.glTexCoord2f(-1.0f, 1.0f);
			gl.glVertex3f(width, -length, 7.5f);
		gl.glEnd();
		//Back wall
		gl.glTranslatef(0f, 20f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, 0.0f);
			gl.glVertex3f(width, -length, 0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(-width, -length, 0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(-width, -length, 7.5f);
			gl.glTexCoord2f(-1.0f, 1.0f);
			gl.glVertex3f(width, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(0f, -20f, 0f);
		//Left wall
		gl.glTranslatef(-20f, 0f, 0f);
		gl.glRotatef(-90f, 0f, 0f, 1f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, 0.0f);
			gl.glVertex3f(width/3f, -length, 0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(-width/3f, -length, 0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(-width/3f, -length, 7.5f);
			gl.glTexCoord2f(-1.0f, 1.0f);
			gl.glVertex3f(width/3f, -length, 7.5f);
		gl.glEnd();
		gl.glRotatef(90f, 0f, 0f, 1f);
		gl.glTranslatef(20f, 0f, 0f);
		//Right wall
		gl.glTranslatef(40f, 0f, 0f);
		gl.glRotatef(-90f, 0f, 0f, 1f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, 0.0f);
			gl.glVertex3f(width/3f, -length, 0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(-width/3f, -length, 0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(-width/3f, -length, 7.5f);
			gl.glTexCoord2f(-1.0f, 1.0f);
			gl.glVertex3f(width/3f, -length, 7.5f);
		gl.glEnd();
		gl.glRotatef(90f, 0f, 0f, 1f);
		gl.glTranslatef(-40f, 0f, 0f);
		
		//The three
		//The right sep
		gl.glTranslatef(15f, 0f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 0.0f);
			gl.glVertex3f(-width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 1.0f);
			gl.glVertex3f(-width/12f, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/12f, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(-15f, 0f, 0f);
		
		//The middle sep
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 0.0f);
			gl.glVertex3f(-width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 1.0f);
			gl.glVertex3f(-width/12f, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/12f, -length, 7.5f);
		gl.glEnd();
		
		//The left sep
		gl.glTranslatef(-15f, 0f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 0.0f);
			gl.glVertex3f(-width/12f, -length, 0f);
			gl.glTexCoord2f(0.333f, 1.0f);
			gl.glVertex3f(-width/12f, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/12f, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(15f, 0f, 0f);
		
		//Far left
		gl.glTranslatef(-28.75f, 0f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/24f, -length, 0f);
			gl.glTexCoord2f(0.333f, 0.0f);
			gl.glVertex3f(-width/24f, -length, 0f);
			gl.glTexCoord2f(0.333f, 1.0f);
			gl.glVertex3f(-width/24f, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/24f, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(28.75f, 0f, 0f);
		
		//Far right
		gl.glTranslatef(28.75f, 0f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/24f, -length, 0f);
			gl.glTexCoord2f(0.333f, 0.0f);
			gl.glVertex3f(-width/24f, -length, 0f);
			gl.glTexCoord2f(0.333f, 1.0f);
			gl.glVertex3f(-width/24f, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/24f, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(-28.75f, 0f, 0f);
		
		//Top bar
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(-1.0f, 0.0f);
			gl.glVertex3f(width, -length, 6.5f);
			gl.glTexCoord2f(3f, 0.0f);
			gl.glVertex3f(-width, -length, 6.5f);
			gl.glTexCoord2f(3f, 0.2f);
			gl.glVertex3f(-width, -length, 7.5f);
			gl.glTexCoord2f(-1.0f, 0.2f);
			gl.glVertex3f(width, -length, 7.5f);
		gl.glEnd();
		
		//Garage door
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[6]);
		gl.glTranslatef(-15f, 2.5f, 0f);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(width/2, -length, 0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(-width/2, -length, 0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(-width/2, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(width/2, -length, 7.5f);
		gl.glEnd();
		gl.glTranslatef(15f, -2.5f, 0f);
		
		//Billboard
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[7]);
		gl.glBegin(GL2.GL_QUADS );
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(width/2, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-width/2, -length, 7.5f);
			gl.glTexCoord2f(0.0f, 0.8f);
			gl.glVertex3f(-width/2, -length, 15f);
			gl.glTexCoord2f(1.0f, 0.8f);
			gl.glVertex3f(width/2, -length, 15f);
		gl.glEnd();
	}
	
	
	void drawPlane( GL2 gl, float size ) {
		final float d = size / 2.0f;
		
		//gl.glDisable( GL2.GL_TEXTURE_2D );
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
		gl.glBegin( GL2.GL_QUADS );
		
			gl.glTexCoord2f(10.0f, 10.0f);
			gl.glVertex3f( d, d, 0.0f );
			gl.glTexCoord2f(-1f, 10.0f);
			gl.glVertex3f( -d, d, 0.0f );
			gl.glTexCoord2f(-1f, -1f);
			gl.glVertex3f( -d, -d, 0.0f );
			gl.glTexCoord2f(10.0f, -1f);
			gl.glVertex3f( d, -d, 0.0f );
		
		gl.glEnd();
		//gl.glEnable( GL2.GL_TEXTURE_2D );
	}
	
	void drawGrand(GL2 gl, float size){
		final float width = size -5f;
		final float length = size * 4f;
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(-width, -length, 0f);
			gl.glVertex3f(-width, -length, 5f);
			gl.glVertex3f(-width, length, 5f);
			gl.glVertex3f(-width, length, 0f);
		gl.glEnd();
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(-width, -length, 5f);
			gl.glVertex3f(-width, length, 5f);
			gl.glVertex3f(width, length, 10f);
			gl.glVertex3f(width, -length, 10f);
		gl.glEnd();
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(width, -length, 10f);
			gl.glVertex3f(-width, -length, 5f);
			gl.glVertex3f(-width, -length, 0f);
			gl.glVertex3f(width, -length, 0f);
		gl.glEnd();
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(width, length, 10f);
			gl.glVertex3f(-width, length, 5f);
			gl.glVertex3f(-width, length, 0f);
			gl.glVertex3f(width, length, 0f);
		gl.glEnd();
		gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(width, length, 10f);
			gl.glVertex3f(width, -length, 10f);
			gl.glVertex3f(width, -length, 0f);
			gl.glVertex3f(width, length, 0f);
		gl.glEnd();
		
	}
	
	
	void drawBanner(GL2 gl, float size){
		final float width = size;
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[8]);
		gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(width, -width, 6.0f );
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-width, -width, 6.0f );
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(-width, -width, 10.0f );
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(width, -width, 10.0f );
		gl.glEnd();
		
		gl.glTranslatef(15.955f, 0f, 0f);
		gl.glBegin( GL2.GL_QUADS );
			gl.glVertex3f(width/16, -width, 0.0f );
			gl.glVertex3f(-width/16, -width, 0.0f );
			gl.glVertex3f(-width/16, -width, 10.0f );
			gl.glVertex3f(width/16, -width, 10.0f );
		gl.glEnd();
		gl.glTranslatef(-15.955f, 0f, 0f);
		
		gl.glTranslatef(-15.955f, 0f, 0f);
		gl.glBegin( GL2.GL_QUADS );
			gl.glVertex3f(width/16, -width, 0.0f );
			gl.glVertex3f(-width/16, -width, 0.0f );
			gl.glVertex3f(-width/16, -width, 10.0f );
			gl.glVertex3f(width/16, -width, 10.0f );
		gl.glEnd();
		gl.glTranslatef(15.955f, 0f, 0f);
	}
	
	
	@Override
	public void dispose( GLAutoDrawable arg0 ) {
	}

	@Override
	public void keyTyped( KeyEvent e ) {
		char key = e.getKeyChar();
		
		switch ( key ) {
			case KeyEvent.VK_P:
				psychedelic_mode = ! psychedelic_mode;
				break;
			
			case KeyEvent.VK_F:
				skybox_follow_camera = ! skybox_follow_camera;
				break;
			
			case KeyEvent.VK_OPEN_BRACKET:
				if ( paper_cube_size > 0 )
					paper_cube_size--;
				break;
				
			case KeyEvent.VK_CLOSE_BRACKET:
				paper_cube_size++;
				break;
		}
		
		if (key == ' '){
			animation = !animation;
			System.out.println(animation);
		}
		
		if (key == 't'){
			//toggle first person
		}
		
		
		// Change the skybox dynamically.
		if ( key >= '1' && key <= '1' + Math.min( skybox_names.length, skybox_max_textures ) - 1 )
			current_skybox = skyboxes[ key - 0x30 - 1 ];
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		keys[ e.getKeyCode() ] = true;
		char key = e.getKeyChar();
		
		
		if (key == 'i'){
			//move forward
			accelX += 0.3 * Math.cos(Math.toRadians(facingDirection));
			accelY += 0.3 * Math.sin(Math.toRadians(facingDirection));
			System.out.println(accelX);
		}
		
		if (key == 'k'){
			//move backwards
			accelX += -1 * 0.3 * Math.cos(Math.toRadians(facingDirection));
			accelY += -1 * 0.3 * Math.sin(Math.toRadians(facingDirection));
			System.out.println(accelX);
		}
		if (key == 'j'){
			//move left
			turning(1);
			System.out.println(posX);
		}
		if (key == 'l'){
			//move right
			turning(2);
		}
		if (key == 'o'){
			//accelerate
			accelX += 0.3 * Math.cos(Math.toRadians(facingDirection));
			accelY += 0.3 * Math.sin(Math.toRadians(facingDirection));
		}
		if (key == 'u'){
			//break
			accelX += -1 * 0.3 * Math.cos(Math.toRadians(facingDirection));
			accelY += -1 * 0.3 * Math.sin(Math.toRadians(facingDirection));
		}
	}

	@Override
	public void keyReleased( KeyEvent e ) {
		keys[ e.getKeyCode() ] = false;
		char key = e.getKeyChar();
		if (key == 'i'){
			//move forward
			accelX = 0;
			accelY = 0;
		}
		if (key == 'k'){
			//move backwards
			accelX = 0;
			accelY = 0;
		}
		if (key == 'j'){
			//move left
			turning(0);
		}
		if (key == 'l'){
			//move right
			turning(0);
		}
		if (key == 'o'){
			//accelerate
			accelX = 0;
			accelY = 0;
		}
		if (key == 'u'){
			//break
			accelX = 0;
			accelY = 0;
		}
	}
	
	private void turning(int direction){
		switch(direction){
		case 1:
			turning = Turns.turnLeft;
			break;
		case 2:
			turning = Turns.turnRight;
			break;
		default:
			turning = Turns.noTurn;
		}
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		int x = e.getX();
		int y = e.getY();
		
		final float throttle_rot = 128.0f;
		
		float dx = ( x - mouse_x0 );
		float dy = ( y - mouse_y0 );
		
		if ( MOUSE_MODE_ROTATE == mouse_mode ) {
			float phi = (float) Math.acos( scene_look_z );
			float theta = (float) Math.atan2( scene_look_y, scene_look_x );
			
			theta -= dx / throttle_rot;
			phi += dy / throttle_rot;
			
			if ( theta >= Math.PI * 2.0 )
				theta -= Math.PI * 2.0;
			else if ( theta < 0 )
				theta += Math.PI * 2.0;
			
			if ( phi > Math.PI - 0.1 )
				phi = (float)( Math.PI - 0.1 );
			else if ( phi < 0.1f )
				phi = 0.1f;
			
			scene_look_x = (float)( Math.cos( theta ) * Math.sin( phi ) );
			scene_look_y = (float)( Math.sin( theta ) * Math.sin( phi ) );
			scene_look_z = (float)( Math.cos( phi ) );
		}
		
		mouse_x0 = x;
		mouse_y0 = y;
	}
	
	@Override
	public void mouseMoved( MouseEvent e ) {
	}

	@Override
	public void mouseClicked( MouseEvent e ) {
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		mouse_x0 = e.getX();
		mouse_y0 = e.getY();
		
		if ( MouseEvent.BUTTON1 == e.getButton() ) {
			mouse_mode = MOUSE_MODE_ROTATE;
		} else {
			mouse_mode = MOUSE_MODE_NONE;
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
	}

	@Override
	public void mouseEntered( MouseEvent e ) {
	}

	@Override
	public void mouseExited( MouseEvent e ) {
	}
}