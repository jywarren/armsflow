import processing.core.*; import proxml.*; import processing.net.*; import proxml.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class armsflow extends PApplet {
 

PFont font;
XMLInOut xmlIO;
int frame;
int colors;
LocationSystem myLocations;

public void setup(){
  size(850,600);
  background(0xffeeeeee,100);
  noFill();
  smooth();
  font = createFont("Univers65Bold-10.vlw",10); 
  textFont(font, 10);
  fill(0xffcc4444);
  noStroke();
  
  myLocations = new LocationSystem();
    
  xmlIO = new XMLInOut(this);
  xmlIO.loadElement("data.xml");
  frame = 0;

}

public void xmlEvent(XMLElement element){
  int[] colors = new int[3];
  colors[0] = 0xff333333;
  colors[1] = 0xff990000;
  colors[2] = 0xff888888;

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
      fill(0xffcc4444,210);
      float scaleVal = 1000.00f;
      float value = element.getChild(1).getChild(i).getChild(j).getFloatAttribute("value");
      String name = element.getChild(1).getChild(i).getChild(j).getAttribute("name");
      float loc_x = (element.getChild(1).getChild(i).getChild(j).getFloatAttribute("lon")+120.00f)*3;
      float loc_y = (element.getChild(1).getChild(i).getChild(j).getFloatAttribute("lat")*-4+360.00f);
      
	  if (loc_x < 20) {
		loc_x = loc_x + 180;
	  }
	  float _diameter = log(value/scaleVal)*10;
      ellipse(loc_x,loc_y,_diameter,_diameter);
      
      fill(0xff333333,120);
	  int label = (int) value;
      text(name,loc_x-6+_diameter/2,loc_y+6);
    }
  }
  
}


public void draw(){
  
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
  
  public void addLocation(Location b) {
      locations.add(b);
    }
}

  static public void main(String args[]) {     PApplet.main(new String[] { "armsflow" });  }}