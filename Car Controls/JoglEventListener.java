package helloOpenGL;






import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.awt.image.DataBufferByte; 










import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.awt.TextRenderer;





public class JoglEventListener implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	
	float[] vertices={5.97994f, -0.085086f, -0.010798f, 
			5.97994f, 10.0043f, -0.010798f, 
			7.99077f, 10.0043f, -0.010798f, 
			7.99077f, 11.3449f, -0.010798f, 
			-0.405339f, 11.3449f, -0.010798f, 
			-0.405339f, 9.98083f, -0.010798f, 
			1.65252f, 9.98083f, -0.010798f, 
			1.65252f, 0.549879f, -0.010798f, 
			-0.722839f, 0.549879f, -0.010798f, 
			-0.722839f, -1.69612f, -0.010798f, 
			2.6168f, -1.69612f, -0.010798f, 
			-7.24925f, 0.42055f, -0.010798f, 
			-9.35415f, 0.42055f, -0.010798f, 
			-9.35415f, 10.0043f, -0.010798f, 
			-7.37859f, 10.0043f, -0.010798f, 
			-7.37859f, 11.3802f, -0.010798f, 
			-15.8217f, 11.3802f, -0.010798f, 
			-15.8217f, 9.99258f, -0.010798f, 
			-13.8109f, 9.99258f, -0.010798f, 
			-13.8109f, -0.061591f, -0.010798f, 
			-10.2361f, -1.73139f, -0.010798f, 
			-7.26099f, -1.73139f, -0.010798f, 
			-6.1909f, 0.855631f, -0.010798f, 
			-8.11942f, 0.855631f, -0.010798f, 
			-8.11942f, 2.31379f, -0.010798f, 
			0.217914f, 2.31379f, -0.010798f, 
			0.217914f, 0.926204f, -0.010798f, 
			-1.73415f, 0.926204f, -0.010798f, 
			-1.73415f, -4.10675f, -0.010798f, 
			9.23724f, 0.937952f, -0.010798f, 
			7.26169f, 0.937952f, -0.010798f, 
			7.26169f, 2.38434f, -0.010798f, 
			15.6696f, 2.38434f, -0.010798f, 
			15.6696f, 1.00851f, -0.010798f, 
			14.964f, 1.00851f, -0.010798f, 
			7.75558f, -2.44873f, -0.010798f, 
			14.4231f, -9.36318f, -0.010798f, 
			16.0576f, -9.36318f, -0.010798f, 
			16.0576f, -10.6685f, -0.010798f, 
			7.62625f, -10.6685f, -0.010798f, 
			7.62625f, -9.33965f, -0.010798f, 
			9.67236f, -9.33965f, -0.010798f, 
			4.49827f, -3.90687f, -0.010798f, 
			-1.35784f, -6.59973f, -0.010798f, 
			-1.35784f, -9.3279f, -0.010798f, 
			0.217914f, -9.3279f, -0.010798f, 
			0.217914f, -10.6919f, -0.010798f, 
			-8.22526f, -10.6919f, -0.010798f, 
			-8.22526f, -9.32786f, -0.010798f, 
			-6.20266f, -9.32786f, -0.010798f};
	int[] indices={3, 2, 3, 1, 
			3, 1, 3, 6, 
			3, 1, 6, 10, 
			3, 10, 6, 7, 
			3, 10, 7, 8, 
			3, 4, 5, 6, 
			3, 4, 6, 3, 
			3, 10, 8, 9, 
			3, 1, 10, 0, 
			3, 13, 14, 15, 
			3, 13, 15, 18, 
			3, 13, 18, 20, 
			3, 13, 20, 12, 
			3, 16, 17, 18, 
			3, 16, 18, 15, 
			3, 12, 20, 21, 
			3, 12, 21, 11, 
			3, 20, 18, 19, 
			3, 49, 22, 44, 
			3, 44, 22, 28, 
			3, 44, 28, 43, 
			3, 43, 28, 29, 
			3, 43, 29, 42, 
			3, 42, 29, 35, 
			3, 42, 35, 41, 
			3, 41, 35, 36, 
			3, 41, 36, 38, 
			3, 38, 36, 37, 
			3, 39, 40, 41, 
			3, 39, 41, 38, 
			3, 29, 30, 32, 
			3, 29, 32, 34, 
			3, 29, 34, 35, 
			3, 46, 47, 49, 
			3, 46, 49, 44, 
			3, 46, 44, 45, 
			3, 22, 23, 25, 
			3, 22, 25, 27, 
			3, 22, 27, 28, 
			3, 25, 23, 24, 
			3, 27, 25, 26, 
			3, 49, 47, 48, 
			3, 32, 30, 31, 
			3, 34, 32, 33};
	float backrgb[] = new float[4]; 
	float rot; 
	float rotX; 
	float moveX;
	float moveXS = 1f;
	float moveY;
	
	/*
	 * Custom variables for mouse drag operations 
	 */
	int windowWidth, windowHeight;
	float orthoX=40;
	float tVal_x, tVal_y, rVal_x, rVal_y, rVal;
	double rtMat[] = new double[16];
	int mouseX0, mouseY0;
	int saveRTnow=0, mouseDragButton=0;
	GLUquadric earth;
	int texID[]  = new int[3]; 
	boolean move = false;
	boolean scaleIt = false;
	boolean animation = false;
	float velocityScale = 0;
	double time = 0;
	double y = 0f; //All cars - unless changed later to have slopes in race course
	
	//AI Cars
	double x_1 = 0f;
	double z_1 = 0f;
	double x_2 = 0f;
	double z_2 = 0f;
	double x_3 = 0f;
	double z_3 = 0f;
	double minz = 0;
	double velocity = 0;
	//myCar
	double velocityX = 0;
	double velocityZ = 0;
	double x = 0;
	double z = 0;
	boolean carMoveW = false;
	boolean carMoveS = false;
	boolean carMoveZ = false;
	boolean carMoveA = false;
	boolean carMoveD = false;
	//car1
	double velocityX1 = 0;
	double velocityZ1 = 0;
	
	//car2
	double velocityX2 = 0;
	double velocityZ2 = 0;
	
	//car3
	double velocityX3 = 0;
	double velocityZ3 = 0;
	
	double velocityScaleX = 0;
	double velocityScaleZ = 0;
	boolean firstPerson = false;
	boolean rotateIt = true;
	
	boolean midLight = false;
	boolean leftLight = false;
	boolean rightLight = false;
    
	 float[] lightPos = { 0,5,0,1 };        // light position at top of court
	 float[] lightPos1 = { -8.4f ,5,0,1 };        // light position
	 float[] lightPos2 = { 8.4f,5,0,1 };        // light position
	 //float[] lightPos3 = { 0,0,10,10 };        // light position

	 
    	private GLU glu = new GLU();

	
	 public void displayChanged(GLAutoDrawable gLDrawable, 
	            boolean modeChanged, boolean deviceChanged) {
	    }

	    /** Called by the drawable immediately after the OpenGL context is
	     * initialized for the first time. Can be used to perform one-time OpenGL
	     * initialization such as setup of lights and display lists.
	     * @param gLDrawable The GLAutoDrawable object.
	     */
	    public void init(GLAutoDrawable gLDrawable) {
	        GL2 gl = gLDrawable.getGL().getGL2();
	        //gl.glShadeModel(GL.GL_LINE_SMOOTH);              // Enable Smooth Shading
	        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
	        //gl.glClearDepth(1.0f);                      // Depth Buffer Setup
	        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
	        //gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
	        // Really Nice Perspective Calculations
	        //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

	        earth = glu.gluNewQuadric();
	        
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glLoadIdentity();
	        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, rtMat, 0);
	        
	        
	       
	        float[] noAmbient = { 0.2f, 0.2f, 0.2f, 1f };     // low ambient light
	        float[] diffuse = { 1.0f, 1.0f, 1.0f, 1f };        // full diffuse color

	        gl.glEnable(GL2.GL_LIGHTING);
	        //gl.glEnable(GL2.GL_LIGHT0);
	        //gl.glEnable(GL2.GL_LIGHT1);
	        //gl.glEnable(GL2.GL_LIGHT2);
	        //gl.glEnable(GL2.GL_LIGHT3);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, noAmbient, 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos, 0);
	        
	        
	        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, noAmbient, 0);
	        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
	        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION,lightPos1, 0);
	        
	        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, noAmbient, 0);
	        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuse, 0);
	        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION,lightPos2, 0);
	        
	        gl.glEnable(GL2.GL_RESCALE_NORMAL );

	        
	        
	        // load an image; 
	        try {
				BufferedImage aImage = ImageIO.read(new File("/Users/Julian/Documents/workspace/ruppoverheadwall.jpg"));
				//URL url = new URL("http://kentuckywallpapers.files.wordpress.com/2009/12/ruppoverheadwall.jpg");
				ByteBuffer buf = convertImageData(aImage);
				
				gl.glGenTextures(3, texID, 0);
				gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
				
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, aImage.getWidth(), 
	                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
				
				
				gl.glEnable(GL.GL_TEXTURE_2D);
				
				gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
				//gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
				
				aImage = ImageIO.read(new File("/Users/Julian/Downloads/basketball.jpg"));
				//URL url = new URL("http://kentuckywallpapers.files.wordpress.com/2009/12/ruppoverheadwall.jpg");
				buf = convertImageData(aImage);
				
				gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
				
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, aImage.getWidth(), 
	                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
				
				
				gl.glEnable(GL.GL_TEXTURE_2D);
				
				gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    }


	    
	    private ByteBuffer convertImageData(BufferedImage bufferedImage) {
	        ByteBuffer imageBuffer;
	        //WritableRaster raster;
	        //BufferedImage texImage;

	        /*
	        ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
	                .getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
	                true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

	        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
	                bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
	        texImage = new BufferedImage(glAlphaColorModel, raster, true,
	                new Hashtable());

	        // copy the source image into the produced image
	        Graphics g = texImage.getGraphics();
	        g.setColor(new Color(0f, 0f, 0f, 0f));
	        g.fillRect(0, 0, 256, 256);
	        g.drawImage(bufferedImage, 0, 0, null);
*/
	        // build a byte buffer from the temporary image
	        // that be used by OpenGL to produce a texture.
	        
	        DataBuffer buf = bufferedImage.getRaster().getDataBuffer(); 
	      
	        
	        final byte[] data = ((DataBufferByte) buf).getData();

	        //byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
	          //      .getData();

	        //System.out.printf("%d %d, %d\n", bufferedImage.getWidth(), bufferedImage.getHeight(), data.length); 
	        //imageBuffer = ByteBuffer.allocateDirect(data.length);
	        //imageBuffer.order(ByteOrder.nativeOrder());
	        //imageBuffer.put(data, 0, data.length);
	        //imageBuffer.flip();

	        //return imageBuffer;
	        
	        return (ByteBuffer.wrap(data)); 
	    }
	    
	    
	    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
	            int height) {
	    	windowWidth = width;
	    	windowHeight = height;
	        final GL2 gl = gLDrawable.getGL().getGL2();

	        if (height <= 0) // avoid a divide by zero error!
	            height = 1;
	        final float h = (float) width / (float) height;
	        gl.glViewport(0, 0, width, height);
	        gl.glMatrixMode(GL2.GL_PROJECTION);
	        gl.glLoadIdentity();
	        //gl.glOrtho(-orthoX*0.5, orthoX*0.5, -orthoX*0.5*height/width, orthoX*0.5*height/width, -100, 100);
	        glu.gluPerspective(45.0f, h, 1.0, 200.0);
	        
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glLoadIdentity();
	        glu.gluLookAt(0, 1, 6, 0, 0, 0, 0, 5, 0);
	    }

	    void drawSphere(GL2 gl){
			float radius = 0.1f;
			double rho = 1;
			double theta, lat_y, lat_x, lat_z;
			double phi, lon_x, lon_y, lon_z; 
		
			//longitude
			for (int i = 0; i < 360; i+=30) {
				gl.glPushMatrix();
				gl.glRotated(90, 1, 0, 0);
			    gl.glBegin(GL2.GL_POLYGON);
			    theta = i * (Math.PI / 180.0f);
			    for (int j = 0; j <= 180; j++) {
			        phi = j * (Math.PI / 180.0f);
			        double x = radius * Math.sin(phi) * Math.cos(theta);
			        double y = radius * Math.sin(phi) * Math.sin(theta);
			        double z = radius * Math.cos(phi);
			        gl.glVertex3d(x, y, z);
			    }
			    gl.glEnd();
			    gl.glPopMatrix();
			}
			//latitude
			for (int i = 0; i < 360; i+=30) {
				gl.glPushMatrix();
				gl.glRotated(90, 1, 0, 0);
			    gl.glBegin(GL2.GL_POLYGON);
			    phi = i * (Math.PI / 180.0f);
			    for (int j = 0; j <= 180; j++) {
			    	theta = j * (Math.PI / 180.0f); 
			        double x = radius * Math.sin(phi) * Math.cos(theta);
			        double y = radius * Math.sin(phi) * Math.sin(theta);
			        double z = radius * Math.cos(phi);
			        gl.glVertex3d(x, y, z);
			    }
			    gl.glEnd();
			    gl.glPopMatrix();
			}
	    }

	    public void drawBasketBall(final GL2 gl)
	    {
	    	gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
	    	glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
	    	glu.gluQuadricTexture(earth, true);
	    	glu.gluQuadricNormals(earth, GLU.GLU_SMOOTH);
	    	glu.gluSphere(earth, 0.1f, 16, 16);
	    }
	    
	    public void drawCube(final GL2 gl) {
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
	    
	    void drawLine(GL2 gl){
	    gl.glBegin( GL2.GL_LINES );
	    gl.glColor3f( 1, 0, 0 );
	    gl.glVertex3f( (float)velocityScaleX, 0, 0 );
	    gl.glVertex3f(0, (float)velocityScale, 0);
	    gl.glVertex3f(0,0,(float) velocityScaleZ);
	    //gl.glVertex3f( 20, 0, 0 );
	    gl.glEnd();
	    }
	    
	    public void run(){
	    	
	    }
	    
		@Override
		public void display(GLAutoDrawable gLDrawable) {
			// TODO Auto-generated method stub
			final GL2 gl = gLDrawable.getGL().getGL2();

			//gl.glClearColor(backrgb[0], 0, 1, 1);
			gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl.glEnable(GL.GL_TEXTURE_2D);			
			
			//backrgb[0]+=0.0005;
			//if (backrgb[0]> 1) backrgb[0] = 0; 

			double initialVelocity = velocityScale;
			double initialVelocityXCar1 = velocityScaleX + .04;
			double initialVelocityZCar1 = velocityScaleZ + .04;
			double initialVelocityXCar2 = velocityScaleX + .08;
			double initialVelocityZCar2 = velocityScaleZ + .08;
			double initialVelocityXCar3 = velocityScaleX + .01;
			double initialVelocityZCar3 = velocityScaleZ + .01;
			double carX = 0;
			double carZ = 0;
			double freq = 1/1000.0;
			double freq2 = 100/1000.0;
			double friction = 0.5;
			
			//Animation for Dummy AI Cars: 1,2,3
			//NOTE: collision detection not set for actual race course yet
			if (animation == true){
				//logic stuff
				if (time == 0){
					//car1
					//Commented out for y        //velocity += initialVelocity;
					velocityX1 += initialVelocityXCar1; 
					velocityZ1 += initialVelocityZCar1;	
					
					//car2
					velocityX2 += initialVelocityXCar2;
					velocityZ2 += initialVelocityZCar2;
					
					//car3
					velocityX3 += initialVelocityXCar3;
					velocityZ3 += initialVelocityZCar3;					
				}
				time += freq;
				//velocity = velocity - 9.8*freq; //for y - bouncing
				// update x and z positions
				x_1 += velocityX1;
				z_1 += velocityZ1;
				//car2
				x_2 += velocityX2;
				z_2 += velocityZ2;
				//car3
				x_3 += velocityX3;
				z_3 += velocityZ3;
				//myCar
				if (carMoveW == true){
					x = x + velocityX + 0.1;
				}
				if (carMoveS == true){
					x = x - velocityX - 0.1;
				}
				if (carMoveD == true){
					z = z + velocityZ + 0.1;
				}
				if (carMoveA == true){
					z = z - velocityZ - 0.1;
				}
				if(y+velocity < 0.1 || y+velocity > 50){
					//velocity *= -1; //y field
				}
				else{			
				//y += velocity; //y universal 
					y=0;
				}
				
				//car 1
				if(Math.abs(x_1 + velocityX1) >= 8.4){
					velocityX1 *= -1; //collide
				}	
				if(Math.abs(z_1 + velocityZ1) >= 5){
					velocityZ1 *= -1;
				}
				x_1 += velocityX1;
				z_1 += velocityZ1;	
				
				//car 2
				if(Math.abs(x_2 + velocityX2) >= 8.4){
					velocityX2 *= -1; //collide
				}	
				if(Math.abs(z_2 + velocityZ2 + 2) >= 5){
					velocityZ2 *= -1;
				}
				x_2 += velocityX2;
				z_2 += velocityZ2;
				
				//car 3
				if(Math.abs(x_3 + velocityX3) >= 8.4){
					velocityX3 *= -1; //collide
				}	
				if(Math.abs(z_3 + velocityZ3 - 2) >= 5){
					velocityZ3 *= -1;
				}
				x_3 += velocityX3;
				z_3 += velocityZ3;
				
				//myCar
				if(Math.abs(x + velocityX) >= 8.4){
					velocityX *= -1; //collide
				}	
				if(Math.abs(z + velocityZ) >= 5){
					velocityZ *= -1;
				}
				if (carMoveW == true || carMoveS == true){
					x += velocityX;
				}			
				if (carMoveD == true || carMoveA == true){
					z += velocityZ;
				}
			}
			
			
	         
	         gl.glPushMatrix();         
	         gl.glRotatef(rot, 0, 1, 0);
	         gl.glRotatef(rotX, 1, 0, 0);
	         
	         //translate stuff
	        	 gl.glTranslatef(moveX, 0, 0);
	         
	         //Scaling stuff
	        	gl.glScaled(moveXS, moveXS, moveXS);
	        	
	         //needs changing
	         if(firstPerson == true){
		 	   glu.gluLookAt(x_1, y, z_1, x_1, y- 1, z_1-1,  0, 1, -1);
	         }
	         //gl.glEnable(GL2.GL_LIGHT0);
	         //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos, 0);
	         //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos1, 0);
	         //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos2, 0);
	         //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos3, 0);
	         
		       
	         if (midLight == true)
	         {
	        	 gl.glEnable(GL2.GL_LIGHT0);
			     gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos, 0);
		     }
	         if (midLight == false){
	        	 gl.glDisable(GL2.GL_LIGHT0);
	         }
		         
		     if (leftLight == true)
		     {
			     gl.glEnable(GL2.GL_LIGHT1);
			     gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION,lightPos1, 0);   	 
		     }
		         
	         if (leftLight == false)
	         {
	        	 gl.glDisable(GL2.GL_LIGHT1);
	         }
		         
	         if (rightLight == true)
	         {
	        	 gl.glEnable(GL2.GL_LIGHT2);
	        	 gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION,lightPos2, 0);
	         }
	         if (rightLight == false)
	         {
	        	 gl.glDisable(GL2.GL_LIGHT2);
	         }
	         
	         //gl.glDisable(GL2.GL_LIGHT0);
	         gl.glPushMatrix(); 
	         
	         
	         
	         //gl.glTranslatef(-0.5f, -0.5f, -0.5f); 
	         
	       //The color of the sphere
	         float materialColor[] = {2.0f, 1.0f, 0.0f, 1.0f};
	         //The specular (shiny) component of the material
	         float  materialSpecular[] = {0,0,1,1};
	         //The color emitted by the material
	         float materialEmission[] = {1.0f,1.0f,0, 1.0f};

	         float shininess = 20;

	         //gl.glEnable(GL2.GL_COLOR_MATERIAL);
	         gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, materialColor, 0);
	         gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, materialSpecular, 0);
	         //gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, materialEmission, 0);
	         gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess); 
	         
	         
	         
	         gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
	         gl.glBegin(GL2.GL_QUADS);
	         gl.glNormal3f(0, 1, 0);
	         gl.glTexCoord2f(0.0f, 0.0f);
	         gl.glVertex3f(-8.4f, 0.0f, -5.0f);
	         
	         
	         gl.glTexCoord2f(1.0f, 0.0f);
	         gl.glVertex3f(8.4f, 0.0f, -5.0f);
	         
	         gl.glTexCoord2f(1.0f, 1.0f);
	         gl.glVertex3f(8.4f, 0.0f, 5.0f);
	         
	         gl.glTexCoord2f(0.0f, 1.0f);
	         gl.glVertex3f(-8.4f, 0.0f, 5.0f);
	         gl.glEnd();
	         gl.glDisable(GL2.GL_TEXTURE_2D);

	         
	         //drawCube(gl);
	         
	         gl.glPopMatrix(); 
	         
	         gl.glPushMatrix();
	         
	         
	         //gl.glTranslated(velocityScaleX, z, velocityScaleZ);
	         gl.glTranslated(x_1, y, z_1);
	         //gl.glColor3f(0.0f, 0.8f, 0.0f);
	        // gl.glColor3f(5,0,0);
	         
	         //drawSphere(gl);
	         //gl.glBindTexture(GL2.GL_TEXTURE_2D, texID[1]);
	         drawLine(gl);
	        drawCube(gl);
	        gl.glPopMatrix();
	         
	         //Car 2
	         gl.glPushMatrix();
	         gl.glTranslated(x_2,y,z_2+2); 
	         drawCube(gl);
	         gl.glPopMatrix();
	         
	       //Car 3
	         gl.glPushMatrix();
	         gl.glTranslated(x_3,y,z_3-2);
	         drawCube(gl);
	         gl.glPopMatrix();
	         
	         //myCar
	         gl.glPushMatrix();
	         
	         gl.glTranslated(x, y, z);
	         if (firstPerson == false){
		        	drawCube(gl);
		         }
	         
	         gl.glPopMatrix();
	         
	         //gl.glScalef(1.0f/2, 1, 1);
	         //gl.glTranslatef(0, -delta, 0);
	         
	         
	         int width = 100, height = 100; 
	         byte[] src = new byte[width*height];

	         for(int a=0; a<height; a++){
	        	 int color = (int)(a*1.0f/height*255); 
	             for(int b=0; b<width; b++){
	                 src[a*width+b]= (byte) color;
	             }
	         }

	         gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	         gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, 0);
	         gl.glPixelStorei(GL2.GL_UNPACK_SKIP_ROWS, 0);

	         
	         
	         gl.glPushMatrix(); 
	         gl.glLoadIdentity(); 
	         
	         gl.glMatrixMode(GL2.GL_PROJECTION);
	         
	         gl.glPushMatrix(); 
	         
	         
	         gl.glLoadIdentity(); 
	         
	         glu.gluOrtho2D(0, windowWidth, 0, windowHeight);
	         
	         gl.glRasterPos2f(windowWidth/2, windowHeight/2); // 0.0f);
	         
	         
	         /*gl.glDrawPixels(width, height,
	                 GL2.GL_RED, GL.GL_UNSIGNED_BYTE,
	                 ByteBuffer.wrap(src));
	         */
	         gl.glPopMatrix(); 
	         
	         gl.glMatrixMode(GL2.GL_MODELVIEW);
	         
	         gl.glPopMatrix();
	         
	         
	         gl.glPopMatrix();
	         
		}

		@Override
		public void dispose(GLAutoDrawable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
		    char key= e.getKeyChar();
			System.out.printf("Key typed: %c\n", key); 
			if (key == KeyEvent.VK_ESCAPE ){
				System.exit(0);
			}
			if (key == '1'){
				//lighting 1 Left Light
				leftLight = !leftLight;
			}
			if (key == '2'){
				//lighting 2 midLight
				midLight = !midLight;
			}
			if (key == '3'){
				//lighting 3 rightLight
				rightLight = !rightLight;
			}
			if (key == 't'){
				//toggle first person/free view mode
				firstPerson = !firstPerson;
			}
			if (key == ' '){
				animation = !animation;
				time = 0;
			}
			
			
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			char key= e.getKeyChar();
			if (key == 'm'){
				scaleIt = true;	
				rotateIt = false;
			}
			if (key == 'w'){
				//move forward
				carMoveW = true;
			}
			if (key == 's'){
				//move Backward
				carMoveS = true;
			}
			if (key == 'a'){
				//move left
				carMoveA = true;
			}
			if (key == 'd'){
				//move right
				carMoveD = true;
			}
			if (key == 'q'){
				//break
			}
			if (key == 'e'){
				//accelerate
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
				scaleIt = false;
				rotateIt = true;
				char key= e.getKeyChar();
				if (key == 'w'){
					//move forward
					carMoveW = false;
				}
				if (key == 's'){
					carMoveS = false;
				}
				if (key == 'a'){
					carMoveA = false;
				}
				if (key == 'd'){
					carMoveD = false;
				}
			}

		@Override
		public void mouseDragged(MouseEvent e) {
			
			float XX = (e.getX()-windowWidth*0.5f)*orthoX/windowWidth;
			float YY = -(e.getY()-windowHeight*0.5f)*orthoX/windowWidth;
			
			if(rotateIt == true){
			rot += (e.getX()-mouseX0)*0.5;		
			rotX += (e.getY() - mouseY0)*0.5; 
			}
			
			if (move == true){
			moveX += (e.getX()-mouseX0)*0.05;		
			moveY += (e.getY() - mouseY0)*0.05; 
			}
			
			if (scaleIt == true){
				moveXS += (e.getX()-mouseX0)*0.05;		 
				}
			
			mouseX0 = e.getX(); 
			mouseY0 = e.getY(); 
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			/*
			 * Coordinates printout
			 */
			float XX = (e.getX()-windowWidth*0.5f)*orthoX/windowWidth;
			float YY = -(e.getY()-windowHeight*0.5f)*orthoX/windowWidth;
			System.out.printf("Point clicked: (%.3f, %.3f)\n", XX, YY);
			
			mouseX0 = e.getX();
			mouseY0 = e.getY();
			if(e.getButton()==MouseEvent.BUTTON1) {	// Left button
				rotateIt = true;
				
			}
			else if(e.getButton()==MouseEvent.BUTTON3) {	// Right button
				move = true;
							}
			//else if (e.getButton() == MouseEvent.BUTTON1 + )
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			//if (e.getButton() == MouseEvent.BUTTON3){
				move = false;
				scaleIt = false;
				rotateIt = false;
			//}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		

	  /*  
	public void init(GLDrawable gLDrawable) {
		final GL gl = glDrawable.getGL();
        final GLU glu = glDrawable.getGLU();

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-1.0f, 1.0f, -1.0f, 1.0f); // drawing square
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }*/
	
}



