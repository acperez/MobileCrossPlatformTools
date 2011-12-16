//
//  MainScene.h
//  Weather
//
//  Made with Strawberry. The OpenGL + HTML + CSS cross-platform framework. More info: strawberry@no2.es
//  Copyright (c) 2011 NO2 Web and Mobile Applications, S.L. (http://www.no2.es) All rights reserved.
//

#include "NO2GL.h"
#include "Constants.h"

#ifndef Weather_Main_h
#define Weather_Main_h

class MainScene : public NO2Scene, NO2TableDelegate, NO2HTTPResponseDelegate, NO2LocationDelegate {
private:
    
    NO2Dictionary *m_data;
    int m_days;

public:
    
    MainScene(NO2Dictionary *data);
    ~MainScene();
    
    // NO2Scene
    virtual void onEnterScene();
	virtual void onExitScene();
    virtual void onCommand(int command, int subcommand, NO2Object *obj);    
    
    // NO2TableDelegate
    int numberOfRowsInSection(NO2Table *table, int section) const;
    int heightForRow(NO2Table *table, int section, int row) const;
	NO2TableCell *cellForRow(NO2Table *table, int section, int row);
    bool willSelectRow(NO2Table *table, int section, int row);

    // NO2HTTPResponseDelegate
    void onRequestCompleted(NO2HTTPRequest *request,  NO2HTTPResponse *response);
    void onRequestFailed(NO2HTTPRequest *request, int errorCode);
    
    // NO2LocationDelegate
    virtual void onLocationUpdate(const NO2Location &location);
    virtual void onLocationError(int errorCode, NO2String *errorDesc);
 
};

#endif
