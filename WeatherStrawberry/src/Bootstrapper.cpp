//
//  Bootstrapper.cpp
//  Weather
//
//  Made with Strawberry. The OpenGL + HTML + CSS cross-platform framework. More info: strawberry@no2.es
//  Copyright (c) 2011 NO2 Web and Mobile Applications, S.L. (http://www.no2.es) All rights reserved.
//

#include "Bootstrapper.h"
#include "no2gl.h"
#include "SplashScene.h"

bool NO2GLBootstrapper(NO2Director *director) {

    LOG("Bootstrapping...");

    director->setScalingPolicy(kFillWidth);
    director->setOrientation(ORIENTATION_0);
    director->setScreenSize(320, 480);

    director->loadLocalizationFiles();
    
    return true;
}

NO2Scene *NO2GLFirstScene() {
    return AUTORELEASE(new SplashScene());
}