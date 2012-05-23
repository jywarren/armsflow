import java.io.*;
import processing.net.*; 

PFont font;
nanoxml.XMLElement xml;
int frame;
color colors;
Object[] nodes;
MapSystem ls;
PImage b;

void setup(){
  frameRate(10);
  size(1000,600,JAVA2D);
  background(#eeeeee);
  noFill();
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  fill(#cc4444);
  noStroke();
  smooth();
  frame = 0;

  ls = new MapSystem();
  b = loadImage("BlankMap-World.png");

  xml = new nanoxml.XMLElement();
  String xmlString="";
  String[] lines;
  if (online) {
	  lines=loadStrings(param("flowbase"));
  } else {
	  lines=loadStrings("data/data.xml");
  }
  for(int i=0; i<lines.length; i++) xmlString+=lines[i];
  xml.parseString(xmlString);
  // retrieve all childnodes and store them in an array
  nodes = xml.getChildren().toArray();

  String title = (String)xml.getAttribute("title");
  String countries = title+": ";

 /* 	// get all <bbb> tags inside <aaa> 
 	Vector bbbVector=xml.getChildrenForPath("timeseries/period/location"); 
 	// turn into an iterator 
 	Enumeration bbbEnum=bbbVector.elements();
 	while(bbbEnum.hasMoreElements()) {
 	  nanoxml.XMLElement currentNode=(nanoxml.XMLElement)bbbEnum.nextElement(); 
       countries = countries + "," + currentNode.getAttribute("name");
 	}*/

  //Iterate through periods
  Vector periodVector=xml.getChildrenForPath("timeseries/period"); 
  // turn into an iterator 
  Enumeration periodEnum=periodVector.elements();
  int nodeIndex = 0;
  while(periodEnum.hasMoreElements()) {
	nanoxml.XMLElement period=(nanoxml.XMLElement)periodEnum.nextElement(); 
	int periodYr = period.getIntAttribute("value");
	ls.addPeriod(new Period(periodYr));
		//Iterate through locations (nations)
	    Vector locVector=period.getChildren();
	    // turn into an iterator 
	    Enumeration locEnum=locVector.elements();
	    while(locEnum.hasMoreElements()) {
	      nanoxml.XMLElement location=(nanoxml.XMLElement)locEnum.nextElement(); 
	     	double value = location.getDoubleAttribute("value");
		    String name = location.getStringAttribute("name");
		    double loc_x = (location.getDoubleAttribute("lon")+140)*3;
		    double loc_y = (location.getDoubleAttribute("lat")*-4+340);
	      ls.addLocation(nodeIndex,new Location(name,loc_x,loc_y,value));
		}
	 nodeIndex = nodeIndex + 1;
   }
}

void draw(){
  background(#eeeeee);
  tint(#eeeeee);
  image(b, -70, 20, 1100, 540);
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  //text(frame+1950,10,20);

  //x-axis labels
  for (int i=1950;i<=2006;i+=5) {
	text(i,(8*(i-1950)) + 270,585);
  }

  //mark where we are
  fill(#cc4444,210);
  ellipse((float)(8*(frame)) + 270,560,14,14);

  ls.renderPeriod((int)frame); //each period will show for 10 frames
  frame = frame + 1;
  if (frame > ls.periodCount()-1) {
	frame = 0;
	}
}

class Location {
 double loc_x, loc_y, value;
 String name;
 double scaleVal = 1000;

 Location (String _name, double _loc_x, double _loc_y, double _value) {
	 value = _value;
	 name = _name;
	 if (_loc_x < 20) {
	   _loc_x = _loc_x + 180;
	   }
	 loc_x = _loc_x;
	 loc_y = _loc_y;
 }

 void render() {
   fill(#cc4444,210);
   noStroke();
   double _diameter = log((float)(value/scaleVal))*10;
   ellipse((float)loc_x,(float)loc_y,(float)_diameter,(float)_diameter);
   fill(#333333,120);
   int label = (int) value;
   text(name,(float)(loc_x-6+_diameter/2),(float)(loc_y+6));
 }
 
}

class Period {
  int periodYr;
  ArrayList locations;  //Dead location storage
  
  Period (int _periodYr) {  
    locations = new ArrayList();
	periodYr = _periodYr;
  }
  
  void addLocation(Location _l) {
    locations.add(_l);
    }

  void render() {
    //Iterate through all locations
    for (int i = locations.size()-1; i>=0; i--) {
      Location b = (Location) locations.get(i);
      b.render();
    }
   }
}

class MapSystem {
  ArrayList periods;  //Dead period storage
  
  MapSystem () {  
    periods = new ArrayList();
  }

  void addPeriod(Period _p) {
	periods.add(_p);
	}
  
  void addLocation(int _p,Location _l) {
      Period p = (Period) periods.get(_p);
	  p.addLocation(_l);
    }

  void render() {
	background(#eeeeee);
    //Iterate through all periods
    for (int i = periods.size()-1; i>=0; i--) {
      Period b = (Period) periods.get(i);
      b.render();
    }
   }
  void renderPeriod(int _p) {
	Period p = (Period) periods.get(_p);
	p.render();
	}
  int periodCount() {
	return periods.size();
	}
}
