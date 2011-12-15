/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.tid.weather.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import es.tid.weather.common.WeatherApplication;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

/**
 * Render a pair of tumbling cubes.
 */

class CubeRenderer implements GLSurfaceView.Renderer {
	private float xrot;				//X Rotation ( NEW )
	private float yrot;				//Y Rotation ( NEW )
	private float zrot;				//Z Rotation ( NEW )
	
    public CubeRenderer(boolean useTranslucentBackground) {
        mCube = new Cube();
    }

    public void onDrawFrame(GL10 gl) {
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		//Drawing
		gl.glTranslatef(0.0f, 0.0f, -5.0f);		//Move 5 units into the screen
		gl.glScalef(0.8f, 0.8f, 0.8f); 			//Scale the Cube to 80 percent, otherwise it would be too large for the screen
		
		//Rotate around the axis based on the rotation matrix (rotation, x, y, z)
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);	//X
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);	//Y
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);	//Z
				
		mCube.draw(gl);							//Draw the Cube	
		
		//Change rotation factors (nice rotation)
		xrot += 0.4f;
		yrot += 0.3f;
		zrot += 0.5f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//Load the texture for the cube once during Surface creation
		mCube.loadGLTexture(gl, WeatherApplication.texture);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
    }
    private Cube mCube;
}
