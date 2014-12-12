package com.owens.oobjloader.lwjgl;

// Written by Sean R. Owens, sean at guild dot net, released to the
// public domain. Share and enjoy. Since some people argue that it is
// impossible to release software to the public domain, you are also free
// to use this code under any version of the GPL, LPGL, Apache, or BSD
// licenses, or contact me for use of another license.

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.util.glu.GLU; 
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.Material;
import com.owens.oobjloader.parser.Parse;

// Based on tutorial code from http://lwjgl.org/wiki/doku.php/lwjgl/tutorials/opengl/basicopengl
public class Test {

    public static final String WINDOW_TITLE = "Test OBJ loader";
    /** Desired frame time */
    private static final int FRAMERATE = 60;
    private static boolean finished;

    /**
     * Application init
     * @param args Commandline args
     */
    public static void main(String[] args) {

//        String filename = "/Users/sean/sample_obj_files/wallTest.obj";
//        String filename = "/Users/sean/src/owens/oobjloader/test/procerus_complete.obj";
//        String filename = "/Users/sean/newobjfiles/mini_obj.obj";
//        String filename = "/Users/sean/newobjfiles/thompson.obj";
//        String filename = "/Users/sean/newobjfiles/extincteur_obj.obj";
        String filename = "/Users/sean/uvmapperclassicsamples/torus.obj";
        String defaultTextureMaterial = "/Users/sean/default_texture.png";

        boolean fullscreen = false;

        for (int loopi = 0; loopi < args.length; loopi++) {
            if (null == args[loopi]) {
                continue;
            }
            if (args[loopi].equals("-fullscreen")) {
                fullscreen = true;
            } else if (args[loopi].equals("-defaulttexture") && args.length >= (loopi + 1)) {
                defaultTextureMaterial = args[++loopi];
            } else {
                filename = args[loopi];
            }
        }

        try {
            init(fullscreen);
            run(filename, defaultTextureMaterial);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Sys.alert(WINDOW_TITLE, "An error occured and the program will exit.");
        } finally {
            cleanup();
        }
        System.exit(0);
    }

    // iterate over face list from builder, and break it up into a set of face lists by material, i.e. each for each face list, all faces in that specific list use the same material
    private static ArrayList<ArrayList<Face>> createFaceListsByMaterial(Build builder) {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<ArrayList<Face>>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<Face>();
        for (Face face : builder.faces) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    System.err.println("Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
                    facesByTextureList.add(currentFaceList);
                }
                System.err.println("Creating new list of faces for material " + face.material);
                currentMaterial = face.material;
                currentFaceList = new ArrayList<Face>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            System.err.println("Adding list of " + currentFaceList.size() + " triangle faces with material " + currentMaterial + "  to our list of lists of faces.");
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }

    // @TODO: This is a crappy way to calculate vertex normals if we are missing said normals.  I just wanted 
    // something that would add normals since my simple VBO creation code expects them.  There are better ways
    // to generate normals,  especially given that the .obj file allows specification of "smoothing groups".
    private static void calcMissingVertexNormals(ArrayList<Face> triangleList) {
        for (Face face : triangleList) {
            face.calculateTriangleNormal();
            for (int loopv = 0; loopv < face.vertices.size(); loopv++) {
                FaceVertex fv = face.vertices.get(loopv);
                if (face.vertices.get(0).n == null) {
                    FaceVertex newFv = new FaceVertex();
                    newFv.v = fv.v;
                    newFv.t = fv.t;
                    newFv.n = face.faceNormal;
                    face.vertices.set(loopv, newFv);
                }
            }
        }
    }

    // load and bind the texture we will be using as a default texture for any missing textures, unspecified textures, and/or 
    // any materials that are not textures, since we are pretty much ignoring/not using those non-texture materials.
    //
    // In general in this simple test code we are only using textures, not 'colors' or (so far) any of the other multitude of things that
    // can be specified via 'materials'. 
    private static int setUpDefaultTexture(TextureLoader textureLoader, String defaultTextureMaterial) {
        int defaultTextureID = -1;
        try {
            defaultTextureID = textureLoader.load(defaultTextureMaterial, true);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ERROR: Got an exception trying to load default texture material = " + defaultTextureMaterial + " , ex=" + ex);
            ex.printStackTrace();
        }
        System.err.println("INFO:  default texture ID = " + defaultTextureID);
        return defaultTextureID;
    }

    // Get the specified Material, bind it as a texture, and return the OpenGL ID.  Returns he default texture ID if we can't
    // load the new texture, or if the material is a non texture and hence we ignore it.  
    private static int getMaterialID(Material material, int defaultTextureID, Build builder, TextureLoader textureLoader) {
        int currentTextureID;
        if (material == null) {
            currentTextureID = defaultTextureID;
        } else if (material.mapKdFilename == null) {
            currentTextureID = defaultTextureID;
        } else {
            try {
                File objFile = new File(builder.objFilename);
                File mapKdFile = new File(objFile.getParent(), material.mapKdFilename);
                System.err.println("Trying to load  " + mapKdFile.getAbsolutePath());
                currentTextureID = textureLoader.load(mapKdFile.getAbsolutePath(), true);
            } catch (IOException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("ERROR: Got an exception trying to load  texture material = " + material.mapKdFilename + " , ex=" + ex);
                ex.printStackTrace();
                System.err.println("ERROR: Using default texture ID = " + defaultTextureID);
                currentTextureID = defaultTextureID;
            }
        }
        return currentTextureID;
    }

    // VBOFactory can only handle triangles, not faces with more than 3 vertices.  There are much better ways to 'triangulate' polygons, that
    // can be used on polygons with more than 4 sides, but for this simple test code justsplit quads into two triangles 
    // and drop all polygons with more than 4 vertices.  (I was originally just dropping quads as well but then I kept ending up with nothing
    // left to display. :-)  Or at least, not much. )
    private static ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<Face>();
        int countTriangles = 0;
        int countQuads = 0;
        int countNGons = 0;
        for (Face face : faceList) {
            if (face.vertices.size() == 3) {
                countTriangles++;
                triangleList.add(face);
            } else if (face.vertices.size() == 4) {
                countQuads++;
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.add(v1);
                f1.add(v2);
                f1.add(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.add(v1);
                f2.add(v3);
                f2.add(v4);
                triangleList.add(f2);
            } else {
                countNGons++;
            }
        }
        int texturedCount = 0;
        int normalCount = 0;
        for (Face face : triangleList) {
            if ((face.vertices.get(0).n != null)
                    && (face.vertices.get(1).n != null)
                    && (face.vertices.get(2).n != null)) {
                normalCount++;
            }
            if ((face.vertices.get(0).t != null)
                    && (face.vertices.get(1).t != null)
                    && (face.vertices.get(2).t != null)) {
                texturedCount++;
            }
        }
        System.err.println("Building VBO, originally " + faceList.size() + " faces, of which originally " + countTriangles + " triangles, " + countQuads + " quads,  and  " + countNGons + " n-polygons with more than 4 vertices that were dropped.");
        System.err.println("Triangle list has " + triangleList.size() + " rendered triangles of which " + normalCount + " have normals for all vertices and " + texturedCount + " have texture coords for all vertices.");
        return triangleList;
    }

    /**
     * @throws Exception if init fails
     */
    private static void init(boolean fullscreen) throws Exception {
        // Create a fullscreen window with 1:1 orthographic 2D projection (default)
        Display.setTitle(WINDOW_TITLE);
        Display.setFullscreen(fullscreen);

        // Enable vsync if we can (due to how OpenGL works, it cannot be guarenteed to always work)
        Display.setVSyncEnabled(true);

        // Create default display of 640x480
        Display.create();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float fAspect = (float) Display.getDisplayMode().getWidth() / (float) Display.getDisplayMode().getHeight();
        GLU.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, Display.getDisplayMode().getWidth() - 100, Display.getDisplayMode().getHeight() - 100);
    }

    /**
     * Runs the program (the "main loop")
     */
    private static void run(String filename, String defaultTextureMaterial) {
        Scene scene = null;

        scene = new Scene();

        System.err.println("Parsing WaveFront OBJ file");
        Build builder = new Build();
        Parse obj = null;
        try {
            obj = new Parse(builder, filename);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Exception loading object!  e=" + e);
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.err.println("Exception loading object!  e=" + e);
            e.printStackTrace();
        }
        System.err.println("Done parsing WaveFront OBJ file");

        System.err.println("Splitting OBJ file faces into list of faces per material");
        ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(builder);
        System.err.println("Done splitting OBJ file faces into list of faces per material, ended up with " + facesByTextureList.size() + " lists of faces.");

        System.err.println("Loading default texture =" + defaultTextureMaterial);
        TextureLoader textureLoader = new TextureLoader();
        int defaultTextureID = setUpDefaultTexture(textureLoader, defaultTextureMaterial);
        System.err.println("Done loading default texture =" + defaultTextureMaterial);

        int currentTextureID = -1;
        for (ArrayList<Face> faceList : facesByTextureList) {
            if (faceList.isEmpty()) {
                System.err.println("ERROR: got an empty face list.  That shouldn't be possible.");
                continue;
            }
            System.err.println("Getting material " + faceList.get(0).material);
            currentTextureID = getMaterialID(faceList.get(0).material, defaultTextureID, builder, textureLoader);
            System.err.println("Splitting any quads and throwing any faces with > 4 vertices.");
            ArrayList<Face> triangleList = splitQuads(faceList);
            System.err.println("Calculating any missing vertex normals.");
            calcMissingVertexNormals(triangleList);
            System.err.println("Ready to build VBO of " + triangleList.size() + " triangles");;

            if (triangleList.size() <= 0) {
                continue;
            }
            System.err.println("Building VBO");

            VBO vbo = VBOFactory.build(currentTextureID, triangleList);

            System.err.println("Adding VBO with text id " + currentTextureID + ", with " + triangleList.size() + " triangles to scene.");
            scene.addVBO(vbo);
        }
        System.err.println("Finally ready to draw things.");

        float anglex = 0;
        float angley = 0;
        float anglez = 0;
        float anglexInc = .25f;
        float angleyInc = .005f;
        float anglezInc = .25f;
        float translatex = 0;
        float translatey = 0f;
        float translatez = -200f;
        float incrementx = 0;
        float incrementy = 0;
        float incrementz = -0.1f;
        float zmax = .10f;
        float zmin = -300f;

        while (!finished) {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            // add some arbitrary rotation and translation of the viewpoint just to make things less boring
            anglex = (anglex + anglexInc) % 360;
            angley = (angley + angleyInc) % 360;
            anglez = (anglez + anglezInc) % 360;
            if (anglez > 360) {
                anglez = 0;
            }
            translatex += incrementx;
            translatey += incrementy;
            translatez += incrementz;
            if (translatez <= zmin || translatez >= zmax) {
                incrementz = -incrementz;
            }
//            System.err.println("positioning at  " + translatex + ", " + translatey + ", " + translatez + " rotation " + anglex + ", " + angley + ", " + anglez);
            GL11.glTranslated(0, 0, translatez);
            GL11.glTranslated(0, translatey, 0);
            GL11.glTranslated(translatex, 0, 0);
            GL11.glRotatef(anglez, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(angley, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(anglex, 1.0f, 0.0f, 0.0f);

            // Always call Window.update(), all the time - it does some behind the
            // scenes work, and also displays the rendered output
            Display.update();

            // Check for close requests
            if (Display.isCloseRequested()) {
                finished = true;
            } // The window is in the foreground, so render!
            else if (Display.isActive()) {
                logic();
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
                scene.render();
                Display.sync(FRAMERATE);
            } // The window is not in the foreground, so we can allow other stuff to run and infrequently update
            else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                logic();

                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty()) {
                    System.err.print(".");
                    scene.render();
                }
            }
        }
    }

    /**
     * Do any cleanup
     */
    private static void cleanup() {
        // Close the window
        Display.destroy();
    }

    /**
     * Do all calculations, handle input, etc.
     */
    private static void logic() {
        // Example input handler: we'll check for the ESC key and exit if it is pressed
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }

    }
}