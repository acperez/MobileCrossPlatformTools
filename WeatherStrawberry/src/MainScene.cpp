//
//  MainScene.cpp
//  Weather
//
//  Made with Strawberry. The OpenGL + HTML + CSS cross-platform framework. More info: strawberry@no2.es
//  Copyright (c) 2011 NO2 Web and Mobile Applications, S.L. (http://www.no2.es) All rights reserved.
//

#include "MainScene.h"

// defined in weather.html
enum kCommandEnumeration {
    kGPSButton = 1,
    kSelectCity = 2,
    kCancelButton = 100,
    kOkButton = 101
    };

MainScene::MainScene(NO2Dictionary *data):NO2Scene() {
    m_days = 0;
    m_data = NULL;

    // Load weather.html from resources (res_common/weather.html)
    loadHTML("weather.html");  
    
    if(data && data->getObjectByExpression("data.error") == NULL) {
        
        NO2Table *tbl = (NO2Table *)getElementById("forecast");
        tbl->setDelegate(this);
        
        NO2String * tmpString;
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].temp_C");
        ((NO2Label*)getElementById("degrees"))->setText(NO2String::stringWithFormat("%sºC", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].pressure");
        ((NO2Label*)getElementById("pressure_a"))->setText(NO2String::stringWithFormat("Pressure: %smb", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].humidity");
        ((NO2Label*)getElementById("humidity_a"))->setText(NO2String::stringWithFormat("Humidity: %s%%", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].cloudcover");
        ((NO2Label*)getElementById("cloud_a"))->setText(NO2String::stringWithFormat("Cloud cover: %s%%", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.weather[0].tempMinC");
        ((NO2Label*)getElementById("min_temp_a"))->setText(NO2String::stringWithFormat("Min temp: %sºC", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.weather[0].tempMaxC");
        ((NO2Label*)getElementById("max_temp_a"))->setText(NO2String::stringWithFormat("Max temp: %sºC", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].windspeedKmph");
        ((NO2Label*)getElementById("wind_a"))->setText(NO2String::stringWithFormat("Wind speed: %skm/h", tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].weatherDesc[0].value");
        ((NO2Label*)getElementById("partly"))->setText(tmpString);
        
        tmpString = (NO2String *)data->getObjectByExpression("data.current_condition[0].weatherIconUrl[0].value");
        ((NO2Sprite *)getElementById("image"))->setImage(loadImage(tmpString->c_str()));
        
        tmpString = (NO2String *)data->getObjectByExpression("data.request[0].query");
        ((NO2Label*)getElementById("title_city"))->setText(tmpString);
        
        tmpString = (NO2String *)data->getObjectByExpression("data.weather[0].date");
        ((NO2Label*)getElementById("date"))->setText(tmpString);
                
        m_days = ((NO2Array *)data->getObjectByExpression("data.weather"))->count();
        
        m_data = RETAIN(data);
        
    } else {
        // run interface action defined in the html
        runAction("show_help");  
    }
}

void MainScene::onEnterScene() {        
    
}

void MainScene::onExitScene() {
    
}

int MainScene::numberOfRowsInSection(NO2Table *table, int section) const{
    return m_days;
}

int MainScene::heightForRow(NO2Table *table, int section, int row) const {
    return 100;
}

NO2TableCell *MainScene::cellForRow(NO2Table *table, int section, int row) {
    NO2TableCell *cell = table->tableCellFromHTML("cell.html");
    
    NO2String * str;
    
    str = (NO2String *)m_data->getObjectByExpression(NO2String::stringWithFormat("data.weather[%d].weatherDesc[0].value", row));
    ((NO2Label*)cell->getChildWithId("title_cell"))->setText(str);
    str = (NO2String *)m_data->getObjectByExpression(NO2String::stringWithFormat("data.weather[%d].date", row));
    ((NO2Label*)cell->getChildWithId("date_cell"))->setText(str);
    str = (NO2String *)m_data->getObjectByExpression(NO2String::stringWithFormat("data.weather[%d].tempMinC", row));
    ((NO2Label*)cell->getChildWithId("num_temp_min"))->setText(NO2String::stringWithFormat("%sºC", str->c_str()));
    str = (NO2String *)m_data->getObjectByExpression(NO2String::stringWithFormat("data.weather[%d].tempMaxC", row));
    ((NO2Label*)cell->getChildWithId("num_temp_max"))->setText(NO2String::stringWithFormat("%sºC", str->c_str()));
    
    
    str = (NO2String *)m_data->getObjectByExpression(NO2String::stringWithFormat("data.weather[%d].weatherIconUrl[0].value", row));
    ((NO2Sprite*)cell->getChildWithId("image2"))->setImage(loadImage(str->c_str()));
    
    return cell;
}

bool MainScene::willSelectRow(NO2Table *table, int section, int row) {
    // Disable touch on cells
    return false;
}

void MainScene::onCommand(int command, int subcommand, NO2Object *obj) {
    switch (command) {
        case kGPSButton: // GPS Button
            g_sharedDirector->locationUpdate(this, kLocationAccuracyCoarse);
            break;
            
        case kSelectCity: // Select City button
            ((NO2TextField *)getElementById("text_city"))->clearText();
            runAction("select_city");
            break;
            
        case kCancelButton: // Cancel Button
            runAction("hide_select_city");
            break;
            
        case kOkButton: { // Ok Button 
            NO2String *location = ((NO2TextField *)getElementById("text_city"))->getText();
            
            g_sharedDirector->cancelAllConnections();
            
            NO2String *url = NO2String::stringWithFormat(WEATHER_URL, location->URLEncode()->c_str());
            NO2HTTPRequest *request = NO2HTTPRequest::requestWithURL(url);
            g_sharedDirector->addConnectionWithRequest(request, this);
            
            runAction("show_loading");
            break;
        }
        default:
            break;
    }
}

void MainScene::onRequestCompleted(NO2HTTPRequest *request,  NO2HTTPResponse *response) {
    if(response->isOk()) {
        NO2String *text = NO2String::stringWithData(response->getBody());
        
        NO2Dictionary *data = (NO2Dictionary *)NO2JSONParser::objectFromJSON(text);
        
        if(data && data->getObjectByExpression("data.error") ==  NULL) {
            
            NO2String *location = ((NO2TextField *)getElementById("text_city"))->getText();
            g_sharedDirector->saveStringValue("location", location);
            
            MainScene *scn = new MainScene(data);
            NO2Transition *trans = new NO2FadeTransition(1);
            g_sharedDirector->showScene(scn, trans);
            RELEASE(trans);
            RELEASE(scn);
        } else {
            // API error ( location not found, etc... )
            runAction("hide_loading");
            g_sharedDirector->showAlertDialog(0, $$("Error"), 
                                              ((NO2String *)data->getObjectByExpression("data.error[0].msg")), 
                                              $$("OK"), NULL);            
        }
    } else {
        // HTTP errors (404, etc...)
        runAction("hide_loading");
        g_sharedDirector->showAlertDialog(0, $$("Error"), 
                                          $$("Connection error"), 
                                          $$("OK"), NULL);
    }
}

void MainScene::onRequestFailed(NO2HTTPRequest *request, int errorCode) {
    // Connection error
    runAction("hide_loading");
    g_sharedDirector->showAlertDialog(0, $$("Error"), 
                                      $$("Connection error"), 
                                      $$("OK"), NULL);
}


void MainScene::onLocationUpdate(const NO2Location &location) {
    NO2String *strLocation = NO2String::stringWithFormat("%f,%f", location.latitude, location.longitude);
    
    ((NO2TextField *)getElementById("text_city"))->setText(strLocation);
    
    g_sharedDirector->cancelAllConnections();
    
    NO2String *url = NO2String::stringWithFormat(WEATHER_URL, strLocation->URLEncode()->c_str());
    NO2HTTPRequest *request = NO2HTTPRequest::requestWithURL(url);
    g_sharedDirector->addConnectionWithRequest(request, this);
    
    runAction("show_loading");
}

void MainScene::onLocationError(int errorCode, NO2String *errorDesc)  {
    g_sharedDirector->showAlertDialog(0, $$("Error"), 
                                      $$("Your location is temporarily unavailable"), 
                                      $$("OK"), NULL);    
}

MainScene::~MainScene() {
    RELEASE(m_data);
}
