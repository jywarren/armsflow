import processing.core.*; import java.io.*; import processing.net.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class arcflow extends PApplet {
 

PFont font;
nanoxml.XMLElement xml;
int frame,lengthAnim = 50;
int colors;
Object[] nodes;
MapSystem ls;
PImage b;

public void setup(){
  frameRate(10);
  size(500,300,JAVA2D);
  background(0xffeeeeee);
  noFill();
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  fill(0xffcc4444);
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
	  lines=loadStrings("data/full-compressed.xml");
  }
  for(int i=0; i<lines.length; i++) xmlString+=lines[i];
  xml.parseString(xmlString);
  // retrieve all childnodes and store them in an array
  nodes = xml.getChildren().toArray();

  String title = (String)xml.getAttribute("title");
  String countries = title+": ";

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
	     	double value = location.getDoubleAttribute("v");
		    String agent = location.getStringAttribute("a");
		    String recipient = location.getStringAttribute("r");
		
		    double start_x = ((location.getDoubleAttribute("x")+140)*3)/2;
		    double start_y = (location.getDoubleAttribute("y")*-4+340)/2;
		    double end_x = ((location.getDoubleAttribute("x2")+140)*3)/2;
		    double end_y = (location.getDoubleAttribute("y2")*-4+340)/2;
	      ls.addLocation(nodeIndex,new Location(agent,recipient,start_x,start_y,end_x,end_y,value));
		}
	 nodeIndex = nodeIndex + 1;
   }
}

public void draw(){
	delay(20); 
  background(0xffeeeeee);
  tint(0xffeeeeee);
  strokeCap(SQUARE);
  image(b, -35, 10, 550, 270);
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  //text(frame+1950,10,20);

  fill(0xff333333,120);
  text("Exports",9,18);
  ls.renderPeriod(0); //each period will show for 10 frames
  frame = frame + 1;
  if (frame > (lengthAnim-1)) {
		frameRate(1);
	}
}

class Location {
 double start_x, start_y, end_x, end_y, loc_y, value;
 String agent,recipient;
 double scaleVal = 1000;

 Location (String _agent, String _recipient, double _start_x, double _start_y, double _end_x, double _end_y, double _value) {
	 value = _value;
	 agent = _agent;
	 recipient = _recipient;
	 if (_start_x < 10) {
	   _start_x = _start_x + 180;
	   }
	 if (_end_x < 10) {
	   _end_x = _end_x + 180;
	   }
	 start_x = _start_x;
	 start_y = _start_y;
	 end_x = _end_x;
	 end_y = _end_y;
 }

 public void render() {
   fill(0xffcc4444,210);
   stroke(0xffcc4444,210);
   double _diameter = log((float)(value/scaleVal)+1)*10;
   strokeWeight((float)_diameter+1);
   noFill();
	float startArc,endArc;
	double temp;
	float arc_x = (float)start_x,arc_y = (float)end_y,arc_w,arc_h;
	arc_w = (float)abs((int)(end_x-start_x))*2;
	arc_h = (float)abs((int)(end_y-start_y))*2;
	float a_0 = PI/-2, a_3 = 0, a_6 = PI/2, a_9 = PI, a_12 = TWO_PI-PI/2,a_15 = TWO_PI;
	
	// Arc angles
	if (end_x>start_x) { // Going E
		if (end_y>start_y) { // Going SE
			startArc = a_0;
			endArc = a_3;
		} else { // Going NE //CC
			startArc = a_9;
			endArc = a_12;
			arc_x = (float)end_x;
			arc_y = (float)start_y;
		}
	} else { // Going W	
		if (end_y>start_y) { // Going SW //CC
			startArc = a_9;
			endArc = a_12;
		} else { // Going NW
			startArc = a_6;
			endArc = a_9;
		}
	}
	
	//animate: 
	if (frame <= lengthAnim) {
		if (end_x>start_x) { // Going E
			if (end_y>start_y) { // Going SE
				endArc = (startArc+(PI/2)*((float)frame/(float)lengthAnim));
			} else { // Going NE //CC
				endArc = (startArc+(PI/2)*((float)frame/(float)lengthAnim));
			}
		} else { // Going W	
			if (end_y>start_y) { // Going SW //CC
				startArc = (endArc-(PI/2)*((float)frame/(float)lengthAnim));
			} else { // Going NW
				endArc = (startArc+(PI/2)*((float)frame/(float)lengthAnim));
			}
		}
	}

	arc(arc_x,arc_y,arc_w,arc_h,startArc,endArc);
	
   //line((float)start_x,(float)start_y,(float)end_x,(float)end_y);
   noStroke();
   fill(0xff333333,120);
   int label = (int) value;
   //text(agent,(float)(loc_x-6+_diameter/2),(float)(loc_y+6));
 }
 
}

class Period {
  int periodYr;
  ArrayList locations;  //Dead location storage
  
  Period (int _periodYr) {  
    locations = new ArrayList();
	periodYr = _periodYr;
  }
  
  public void addLocation(Location _l) {
    locations.add(_l);
    }

  public void render() {
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

  public void addPeriod(Period _p) {
	periods.add(_p);
	}
  
  public void addLocation(int _p,Location _l) {
      Period p = (Period) periods.get(_p);
	  p.addLocation(_l);
    }

  public void render() {
	background(0xffeeeeee);
    //Iterate through all periods
    for (int i = periods.size()-1; i>=0; i--) {
      Period b = (Period) periods.get(i);
      b.render();
    }
   }
  public void renderPeriod(int _p) {
	Period p = (Period) periods.get(_p);
	p.render();
	}
  public int periodCount() {
	return periods.size();
	}
}

  static public void main(String args[]) {     PApplet.main(new String[] { "arcflow" });  }}