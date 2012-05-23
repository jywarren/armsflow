import proxml.*;
import processing.net.*; 

PFont font;
XMLInOut xmlIO;
int frame;
color colors;
LocationSystem myLocations;

void setup(){
  size(850,600);
  background(#eeeeee,100);
  noFill();
  smooth();
  font = createFont("Univers65Bold-10.vlw",10); 
  textFont(font, 10);
  fill(#cc4444);
  noStroke();
  
  myLocations = new LocationSystem();
    
  xmlIO = new XMLInOut(this);
  xmlIO.loadElement("data.xml");
  frame = 0;

}

void xmlEvent(XMLElement element){
  color[] colors = new color[3];
  colors[0] = #333333;
  colors[1] = #990000;
  colors[2] = #888888;

  String title = element.getAttribute("title");

  String countries = title+": ", comma = "";
  
  for (int i=0;i<element.getChild(1).firstChild().countChildren();i=i+1) {
    if (i > 0) {
      comma = ", ";
    } else {
      comma = ""; 
    }
    countries = countries + comma + element.getChild(1).firstChild().getChild(i).getAttribute("name");
  }
  text(title,10,20);
  //text(countries,10,20,820,150);

  // element.2-timeseries.period.location

  //Iterate through periods
  for (int i=0;i<element.getChild(1).countChildren();i=i+1) {
    //Iterate through locations (nations)
    for (int j=0;j<element.getChild(1).firstChild().countChildren();j=j+1) {
      fill(#cc4444,210);
      float scaleVal = 1000.00;
      float value = element.getChild(1).getChild(i).getChild(j).getFloatAttribute("value");
      String name = element.getChild(1).getChild(i).getChild(j).getAttribute("name");
      float loc_x = (element.getChild(1).getChild(i).getChild(j).getFloatAttribute("lon")+120.00)*3;
      float loc_y = (element.getChild(1).getChild(i).getChild(j).getFloatAttribute("lat")*-4+360.00);
      
	  if (loc_x < 20) {
		loc_x = loc_x + 180;
	  }
	  float _diameter = log(value/scaleVal)*10;
      ellipse(loc_x,loc_y,_diameter,_diameter);
      
      fill(#333333,120);
	  int label = (int) value;
      text(name,loc_x-6+_diameter/2,loc_y+6);
    }
  }
  
}


void draw(){
  
}

class Location {
 float lon, lat; 
 
 Location (String description) {

 }
 
}

class LocationSystem {
  ArrayList locations;  //Dead location storage
  
  LocationSystem () {  
    locations = new ArrayList();
    Client myClient;
  }
  
  void addLocation(Location b) {
      locations.add(b);
    }
}
