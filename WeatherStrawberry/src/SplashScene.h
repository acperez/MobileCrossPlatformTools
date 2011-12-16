//
//  SplashScene.h
//  Weather
//
//  Made with Strawberry. The OpenGL + HTML + CSS cross-platform framework. More info: strawberry@no2.es
//  Copyright (c) 2011 NO2 Web and Mobile Applications, S.L. (http://www.no2.es) All rights reserved.
//

#include "NO2GL.h"
#include "Constants.h"
#include "MainScene.h"

#ifndef Weather_Splash_h
#define Weather_Splash_h

class SplashScene : public NO2Scene, NO2HTTPResponseDelegate {
private:
    void nextScene(NO2Dictionary *data);
    
public:
    SplashScene();
    ~SplashScene();

    // NO2Scene
    virtual void onEnterScene();
	virtual void onExitScene();
    virtual void onCommand(int command, int subcommand, NO2Object *obj);
    
    // NO2HTTPResponseDelegate
    void onRequestCompleted(NO2HTTPRequest *request,  NO2HTTPResponse *response);
    void onRequestFailed(NO2HTTPRequest *request, int errorCode);
    
};

#endif
