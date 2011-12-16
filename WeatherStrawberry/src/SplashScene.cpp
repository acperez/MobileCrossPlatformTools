//
//  SplashScene.cpp
//  Weather
//
//  Made with Strawberry. The OpenGL + HTML + CSS cross-platform framework. More info: strawberry@no2.es
//  Copyright (c) 2011 NO2 Web and Mobile Applications, S.L. (http://www.no2.es) All rights reserved.
//

#include "SplashScene.h"

#define COMMAND_TIMER 1

SplashScene::SplashScene():NO2Scene() {
    // Load splash.html from resources (res_common/splash.html)
    loadHTML("splash.html");
}

void SplashScene::onEnterScene() {   
    
    NO2String *savedLocation = g_sharedDirector->readStringValue("location");
    if(savedLocation) {
        NO2String *url = NO2String::stringWithFormat(WEATHER_URL, savedLocation->URLEncode()->c_str());
        NO2HTTPRequest *request = NO2HTTPRequest::requestWithURL(url);
        g_sharedDirector->addConnectionWithRequest(request, this);
    } else {
        g_sharedDirector->scheduleTimer(COMMAND_TIMER, 4, false);
    }
    
}

void SplashScene::onExitScene() {
    
}

void SplashScene::onRequestCompleted(NO2HTTPRequest *request,  NO2HTTPResponse *response) {
    if(response->isOk()) {
        NO2String *text = NO2String::stringWithData(response->getBody());
        
        NO2Dictionary *data = (NO2Dictionary *)NO2JSONParser::objectFromJSON(text);
        
        nextScene(data);
    } else {
        nextScene(NULL);
    }
}

void SplashScene::onRequestFailed(NO2HTTPRequest *request, int errorCode) {
    nextScene(NULL);
}

void SplashScene::nextScene(NO2Dictionary *data) {
    MainScene *scn = new MainScene(data);
    NO2Transition *trans = new NO2FadeTransition(1);
    g_sharedDirector->showScene(scn, trans);
    RELEASE(trans);
    RELEASE(scn);
}

void SplashScene::onCommand(int command, int subcommand, NO2Object *obj) {
    if(command == COMMAND_TIMER) {
        nextScene(NULL);
    }
}

SplashScene::~SplashScene() {
    
}

