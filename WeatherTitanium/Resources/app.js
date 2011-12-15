var win = Ti.UI.createWindow({
	fullscreen:false,  
    navBarHidden:true,
    exitOnClose: true,
    backgroundImage:"images/bg_gradient.png"
});
win.orientationModes=[Titanium.UI.PORTRAIT];

var splash_view = Titanium.UI.createView({
	layout:'vertical'
});

win.add(splash_view);

var label = Ti.UI.createLabel({
	text:"Weather\nForecast",
	textAlign:"center",
	color:"#FFFFFF",
	font:{
        fontWeight:'bold',  
        fontSize:50  
    },
    top: 20
});
splash_view.add(label);

var splash_logo = Ti.UI.createImageView({
	image:"images/splash_logo.png",
	height: 'auto',
	width: 'auto',
	top: 40
});
splash_view.add(splash_logo);

// load images
var images = [];
for (var i=1; i<9; i++){
	images.push('images/loading_' + i +'.png');
}

var loading = Titanium.UI.createImageView({
	images:images,
	duration:100, // in milliseconds, the time before next frame is shown
	repeatCount:0, // 0 means animation repeats indefinitely, use > 1 to control repeat count
	top: 40
});

// listen for load event (when all images are loaded)
loading.addEventListener('load', function(e){
	loading.loaded = true;
	loading.start();
});

splash_view.add(loading);

win.open();

if(Ti.Platform.name == 'android') {
   Ti.Android.currentActivity.addEventListener('resume',
        function(e) {
        	if(loading.loaded){
        		loading.start();
        	}
   });
}

var contents = null;
   
// listen for start event (when animation is started)
loading.addEventListener('start', function()
{
	var init_time = new Date().getTime();
	
	var file = Titanium.Filesystem.getFile(Titanium.Filesystem.applicationDataDirectory,'data.txt');
	//var file = Titanium.Filesystem.getFile(Titanium.Filesystem.externalStorageDirectory,'data.txt');

	if (file.exists())
    	 contents = file.read();
	
    var time = 4000 - (new Date().getTime() - init_time);
	setTimeout(function(){
			if(contents == null){
				win.remove(splash_view);
				win.add(help_view);
				win.add(toolbar);
			}
			else{
				ParseAndStore('', contents, false);
			}
		}, time);
});



/* --------------------------------------------------------------------------------
 * ---------------------------- Help screen ---------------------------------------
 * -------------------------------------------------------------------------------- */
var help_view = Titanium.UI.createView({
	layout: 'vertical',
});

var image_help_bg_url;
if(Ti.Platform.name == 'android') {
	image_help_bg_url = 'images/help_bg_android.png';
}
else{
	image_help_bg_url = 'images/help_bg.png';
}

var help_box = Titanium.UI.createView({
	backgroundImage: image_help_bg_url,
	backgroundLeftCap: 50,
	backgroundTopCap: 50,
});

var help_label = Ti.UI.createLabel({
	text:L("help"),
	textAlign:"center",
	color:"#000000",
	font:{
        fontSize:30  
   },
   textAlign: 'left',
   top: 70,
   left: 45,
   right: 45,
   bottom: 100
});

help_box.add(help_label);
help_view.add(help_box);

/* --------------------------------------------------------------------------------
 * ------------------------------ Weather View ------------------------------------
 * -------------------------------------------------------------------------------- */

var weather_view = Titanium.UI.createView({
	layout: 'vertical',
});

var weather_title = Ti.UI.createLabel({
	text: "Barcelona, Spain",
	color:"#FFFFFF",
	font:{
        fontSize:40  
	},
	top: 20,
	left: 0
});
weather_view.add(weather_title);

var weather_header = Titanium.UI.createView({
	top: 20
});

var weather_date_temp = Titanium.UI.createView({
	layout: 'vertical',
	width: '50%',
	font:{
        fontSize:30  
	},
	left:0
});
	
var weather_date = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: "03/10/2011",
	font:{
        fontSize:30  
	},
	left: 10
});

weather_date_temp.add(weather_date);

var weather_temp = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: "24ºC",
	left: 10
});

weather_date_temp.add(weather_temp);
weather_header.add(weather_date_temp);

var weather_icon = Ti.UI.createImageView({
	image:"http://ssl.gstatic.com/onebox/weather/35/partly_cloudy.png",
	height: 'auto',
	width: 'auto',
	right: 10,
});

weather_header.add(weather_icon);
weather_view.add(weather_header);
	
var weather_description = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Partly cloudy',
	top: 20,
	left: 10
});

weather_view.add(weather_description);

var weather_prop_view_1 = Titanium.UI.createView({
	top: 20
});

var weather_tempMin = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Min temp: 17ºC',
	left: 10
});
weather_prop_view_1.add(weather_tempMin);

var weather_tempMax = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Max temp: 17ºC',
	left: '50%'
});
weather_prop_view_1.add(weather_tempMax);

weather_view.add(weather_prop_view_1);

var weather_prop_view_2 = Titanium.UI.createView({
	top: 10
});

var weather_pressure = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Pressure: 1016mb',
	left: 10
});
weather_prop_view_2.add(weather_pressure);

var weather_humidity = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Humidity: 88%',
	left: '50%'
});
weather_prop_view_2.add(weather_humidity);

weather_view.add(weather_prop_view_2);

var weather_prop_view_3 = Titanium.UI.createView({
	top: 10
});

var weather_cloud = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Cloud cover: 25%',
	left: 10
});
weather_prop_view_3.add(weather_cloud);

var weather_wind = Ti.UI.createLabel({
	color:"#FFFFFF",
	text: 'Wind speed: 11km/h',
	left: '50%'
});
weather_prop_view_3.add(weather_wind);

weather_view.add(weather_prop_view_3);

var scroll_list = Ti.UI.createScrollView({
	contentWidth:'auto',
    contentHeight:'auto',
    top:0,
    showVerticalScrollIndicator:true,
    showHorizontalScrollIndicator:true,
    height: 300
});

var CustomData = [
{ date:'24/09/2011', image:"http://ssl.gstatic.com/onebox/weather/35/partly_cloudy.png", condition:'Partly cloudy', minTemp:'17ºC' , maxTemp: '24ºC', hasChild:true },
{ date:'24/09/2011', image:"http://ssl.gstatic.com/onebox/weather/35/partly_cloudy.png", condition:'Partly cloudy', minTemp:'17ºC' , maxTemp: '24ºC', hasChild:true },
{ date:'24/09/2011', image:"http://ssl.gstatic.com/onebox/weather/35/partly_cloudy.png", condition:'Partly cloudy', minTemp:'17ºC' , maxTemp: '24ºC', hasChild:true },
{ date:'24/09/2011', image:"http://ssl.gstatic.com/onebox/weather/35/partly_cloudy.png", condition:'Partly cloudy', minTemp:'17ºC' , maxTemp: '24ºC', hasChild:true },
];

function GenerateRow(date, image, condition, minTemp, maxTemp){
	var row = Titanium.UI.createTableViewRow();
	
	var image_row_bg;
	if(Ti.Platform.name == 'android') {
		image_row_bg = 'images/list_bg_android.png';
	}
	else{
		image_row_bg = 'images/list_bg.png';
	}
	
	var row_view = Titanium.UI.createView({
		backgroundImage: image_row_bg,
		backgroundLeftCap: 7,
		backgroundTopCap: 7,
		layout: 'vertical'
	});
	
	var row_header = Titanium.UI.createView({
	});
	
	var row_date = Ti.UI.createLabel({
		color:"#000000",
		text: date,
		left: 20,
		top: 20,
		
		height: 30
	});
	row_header.add(row_date);
	
	var row_icon = Ti.UI.createImageView({
		url:image,
		height: 'auto',
		width: 'auto',
		right: 20,
		top:10,
		
		height: 40,
		width: 40
	});
	row_header.add(row_icon);
	row_view.add(row_header);
	
	var row_condition = Ti.UI.createLabel({
		color:"#000000",
		text: condition,
		left: 20,
		top: 5
	});
	row_view.add(row_condition);
	
	var row_temp = Titanium.UI.createView({
		top: 5
	});
	
	var row_minTemp = Ti.UI.createLabel({
		color:"#000000",
		text: minTemp,
		left: 20
	});
	row_temp.add(row_minTemp);
	
	var row_maxTemp = Ti.UI.createLabel({
		color:"#000000",
		text: maxTemp,
		right: 20,
	});
	row_temp.add(row_maxTemp);
	row_view.add(row_temp);
	
	row.add(row_view);
	return row;
}

var data=[];

for (var i = 0; i < CustomData.length; i++){
	var row = GenerateRow(CustomData[i].date, CustomData[i].image, CustomData[i].condition, CustomData[i].minTemp, CustomData[i].maxTemp);
	data.push(row);
}

var list = Titanium.UI.createTableView({
	data: data,
	top: 20
});

var list_container = Titanium.UI.createView({
	height: 324
});

list_container.add(list);

weather_view.add(list_container);

/* --------------------------------------------------------------------------------
 * ------------------------------ ToolBar -----------------------------------------
 * -------------------------------------------------------------------------------- */

var toolbar = Ti.UI.createView();

var button_gps = Ti.UI.createView({
	left: 0,
	width: '50%',
	height: 110,
	backgroundColor: '#444444',
	layout: 'vertical',
	bottom: -10,
	borderColor: '#555555',
	borderWidth: 4,
	borderRadius: 10
});

var gps_img = Ti.UI.createImageView({
	top: 10,
	image: 'images/gps_location.png',
	height: 'auto',
	width: 'auto',
});
button_gps.add(gps_img);

var gps_txt = Ti.UI.createLabel({
	top: 5,
	text: 'GPS location',
	color: '#99AABB',
});
button_gps.add(gps_txt);

var gps_border = Ti.UI.createView({
	top: 4,
	height: 4,
	backgroundColor: '#555555'
});
button_gps.add(gps_border);
toolbar.add(button_gps);

var button_city = Ti.UI.createView({
	left: '50%',
	width: '50%',
	height: 110,
	backgroundColor: '#444444',
	layout: 'vertical',
	bottom: -10,
	borderColor: '#555555',
	borderWidth: 4,
	borderRadius: 10
});

var city_img = Ti.UI.createImageView({
	top: 10,
	image: 'images/city.png',
	height: 'auto',
	width: 'auto',
});
button_city.add(city_img);

var city_txt = Ti.UI.createLabel({
	top: 5,
	text: 'Select city',
	color: '#99AABB'
});
button_city.add(city_txt);

var city_border = Ti.UI.createView({
	top: 4,
	height: 4,
	backgroundColor: '#555555'
});
button_city.add(city_border);
toolbar.add(button_city);

var gpsEnable = false;

button_gps.addEventListener('click', function(e) {
	var notification = Ti.UI.createNotification({message: 'Waiting for location'});
	notification.duration = Ti.UI.NOTIFICATION_DURATION_LONG;
	notification.show();
	
	Ti.Geolocation.preferredProvider = "gps";

	Titanium.Geolocation.getCurrentPosition(function(e)
	{
		if (!e.success || e.error)
		{
			return;
		}
		
		Titanium.Geolocation.removeEventListener('location', locationCallback);
		gpsEnable = false;
				
		HttpGetLocation(e.coords.latitude + "," + e.coords.longitude);
	});


	var locationCallback = function(e){
		if (e.error)
		{
			alert(e.error.message);
			return;
		}

 		Titanium.Geolocation.removeEventListener('location', locationCallback);
 		gpsEnable = false;
        
        HttpGetLocation(e.coords.latitude + "," + e.coords.longitude);
	};

	Titanium.Geolocation.addEventListener('location', locationCallback);
	gpsEnable = true;
	setTimeout(GpsTimeout, 20000, [locationCallback]);
	
	if (Titanium.Platform.name == 'android'){
		Ti.Android.currentActivity.addEventListener('pause', function(e) {
			if(gpsEnable)
				Titanium.Geolocation.removeEventListener('location', locationCallback);
			gpsEnable = false;
		});

		Ti.Android.currentActivity.addEventListener('destroy', function(e) {
			if(gpsEnable)
				Titanium.Geolocation.removeEventListener('location', locationCallback);
			gpsEnable = false;
		});
	}
});

function HttpGetLocation(coordinates){
	var xhr = Titanium.Network.createHTTPClient();
	
	xhr.onload = function() {
		var location = eval('(' + this.responseText + ')');
		UpdateForecast(location.results[0].address_components[2].long_name);
    };
    
    xhr.onerror = function() {
    	alert(this.status + ' - ' + this.statusText);
	};
	
	var url="http://maps.google.com/maps/api/geocode/json?latlng=" + coordinates + "&sensor=true";

    xhr.open( 'GET', url);
  
    xhr.send(); 
};

function GpsTimeout(locationCallback){
	if(gpsEnable){
		Titanium.Geolocation.removeEventListener('location', locationCallback);
		gpsEnable = false;
		var notification = Ti.UI.createNotification({message: 'Your current location is temporarily unavailable'});
  		notification.duration = Ti.UI.NOTIFICATION_DURATION_LONG;
  		notification.show();
	}
}

button_city.addEventListener('click', function(e) {
	var alert_view = Titanium.UI.createView({
		layout: 'vertical',
		backgroundColor: '#000000',
		opacity: 0.60,
	});

	var image_alert_bg_url;
	if(Ti.Platform.name == 'android') {
		image_alert_bg_url = 'images/popup_full_bright_android.png';
	}
	else{
		image_alert_bg_url = 'images/popup_full_bright.png';
	}

	var alert_box = Titanium.UI.createView({
		backgroundImage: image_alert_bg_url,
		backgroundLeftCap: 21,
		backgroundTopCap: 18,
		layout: 'vertical',
	});

	var alert_title = Ti.UI.createLabel({
		text: 'Select city',
		textAlign:"center",
		color:"#000000",
		font:{
	        fontSize:40  
	   },
	   top: 20,
	   left: 45,
	   right: 45,
	});
	
	var alert_line = Ti.UI.createLabel({
		textAlign:"center",
		backgroundColor:"#c5c5c5",
	   	height: 1,
	   	width: '80%',
	   	top: 10
	});
	
	var alert_message = Ti.UI.createLabel({
		text: 'Type a city name to get the forecast',
		textAlign:"center",
		color:"#767676",
		font:{
	        fontSize:30  
	   },
	   textAlign: 'left',
	   top: 10,
	   left: 45,
	   right: 45,
	});
	
	var alert_input = Ti.UI.createTextField({
		color:'#336699',
    	width: '80%',
    	top: 5,
    	borderStyle:Titanium.UI.INPUT_BORDERSTYLE_ROUNDED, 
	});

	var image_alert_button_url;
	if(Ti.Platform.name == 'android') {
		image_alert_button_url = 'images/alert_button_android.png';
	}
	else{
		image_alert_button_url = 'images/alert_button.png';
	}
	
	var alert_buttons = Titanium.UI.createView({
		top: 5,
	});

	var alert_cancel = Ti.UI.createLabel({
		backgroundImage: image_alert_button_url,
		width:'35%',
		left: 45,
		height: 55,
		font:{
	        fontSize:25  
	    },
		text: 'Cancel',
		textAlign:'center',
		color: '#767676',
		backgroundLeftCap: 7,
		backgroundTopCap: 9,
	});
	
	var alert_ok = Ti.UI.createLabel({
		backgroundImage: image_alert_button_url,
		width:'35%',
		right:45,
		height: 55,
		font:{
	        fontSize:25  
	    },
		text: 'Ok',
		textAlign:'center',
		color: '#767676',
		backgroundLeftCap: 7,
		backgroundTopCap: 9,
	});
	
	alert_buttons.add(alert_cancel);
	alert_buttons.add(alert_ok);

	alert_box.add(alert_title);
	alert_box.add(alert_line);
	alert_box.add(alert_message);
	alert_box.add(alert_input);
	alert_box.add(alert_buttons);
	alert_view.add(alert_box);
	
	alert_cancel.addEventListener('click', function(e) {
		alert_input.blur();
		win.remove(alert_view);
	});
	
	alert_ok.addEventListener('click', function(e) {
		var city = alert_input.value;
		if(city == ''){
			var notification = Ti.UI.createNotification({message: 'You must type a city, field is blank'});
	  		notification.duration = Ti.UI.NOTIFICATION_DURATION_LONG;
	  		notification.show();
	  		return;
		}

		UpdateForecast(city);
		alert_input.blur();
		win.remove(alert_view);
	});
	
	win.add(alert_view);
});

function UpdateForecast(city){
	var xhr = Titanium.Network.createHTTPClient();
	
	xhr.onload = function() {
		ParseAndStore(city, this.responseText, true);
    };
    
    xhr.onerror = function() {
    	alert(this.status + ' - ' + this.statusText);
	};
	
	var API_KEY = "6affebacbb125602110709";
	var FORECAST_DAYS = "5";
	var DATA_FORMAT = "json";
	
	var url = "http://free.worldweatheronline.com/feed/weather.ashx?key=" + API_KEY + "&q=" + city + "&num_of_days=" + FORECAST_DAYS + "&format=" + DATA_FORMAT;

    xhr.open( 'GET', url);
  
    xhr.send();  
}

function ParseAndStore(city, rawdata, save){
	var data = eval("(" + rawdata + ")");
	data = data.data;
	if(data.error){
		var msg = "Unable to find any matching weather location to the query '" + city + "'";
		alert(msg);
		return;
	}

	var weatherLocal =new Object();
	weatherLocal.city = data.request[0].query;
	var tmp = data.weather;
	weatherLocal.forecast = new Array();
	for(i=0; i<tmp.length; i++){
		weatherLocal.forecast[i] = new Object();
		weatherLocal.forecast[i].date = tmp[i].date.split("-");
		weatherLocal.forecast[i].date = weatherLocal.forecast[i].date[2] + "/" + weatherLocal.forecast[i].date[1] + "/" + weatherLocal.forecast[i].date[0];					
		weatherLocal.forecast[i].iconUrl = tmp[i].weatherIconUrl[0].value;
		weatherLocal.forecast[i].tempMin = tmp[i].tempMinC;
		weatherLocal.forecast[i].tempMax = tmp[i].tempMaxC;
		weatherLocal.forecast[i].condition = tmp[i].weatherDesc[0].value;
	}

	var tmp = data.current_condition[0];
	weatherLocal.currentWeather = new Object();
	weatherLocal.currentWeather.date = weatherLocal.forecast[0].date;
	weatherLocal.currentWeather.cloudCover = tmp.cloudcover;
	weatherLocal.currentWeather.pressure = tmp.pressure;
	weatherLocal.currentWeather.temp = tmp.temp_C;
	weatherLocal.currentWeather.humidity = tmp.humidity;
	weatherLocal.currentWeather.iconUrl = tmp.weatherIconUrl[0].value;
	weatherLocal.currentWeather.windspeed = tmp.windspeedKmph;
	weatherLocal.currentWeather.condition = tmp.weatherDesc[0].value;
	weatherLocal.currentWeather.tempMin = weatherLocal.forecast[0].tempMin;
	weatherLocal.currentWeather.tempMax = weatherLocal.forecast[0].tempMax;

	weather = weatherLocal;

	if(save){
		var file = Titanium.Filesystem.getFile(Titanium.Filesystem.applicationDataDirectory,'data.txt');
		//var file = Titanium.Filesystem.getFile(Titanium.Filesystem.externalStorageDirectory,'data.txt');    
	 	file.write(rawdata);
	 }

    InitFromPreferences(weather);
}

function InitFromPreferences(weather){
	weather_title.text = weather.city;
	weather_date.text = weather.currentWeather.date;
	weather_temp.text = weather.currentWeather.temp + "ºC";
	weather_icon.image = weather.currentWeather.iconUrl;
	weather_description.text = weather.currentWeather.condition;
	weather_tempMin.text = "Min temp: " + weather.currentWeather.tempMin + "ºC";
	weather_tempMax.text = "Max temp: " + weather.currentWeather.tempMax + "ºC";
	weather_pressure.text = "Pressure: " + weather.currentWeather.pressure + "mb";
	weather_humidity.text = "Humidity: " + weather.currentWeather.humidity + "%";
	weather_cloud.text = "Cloud cover: " + weather.currentWeather.cloudCover + "%";
	weather_wind.text = "Wind speed: " + weather.currentWeather.windspeed + "km/h";

	for(i=1; i<weather.forecast.length; i++){
		var row = GenerateRow(weather.forecast[i].date, weather.forecast[i].iconUrl, weather.forecast[i].condition, "Min temp: " + weather.forecast[i].tempMin + "ºC", "Max temp: " + weather.forecast[i].tempMax + "ºC");
		list.deleteRow(0);
		list.appendRow(row);
	}
	
	win.remove(splash_view);
	win.remove(help_view);
	win.remove(toolbar);
	
	weather_view.add(toolbar);
	win.add(weather_view);
}