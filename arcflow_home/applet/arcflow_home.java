import processing.core.*; import java.io.*; import processing.net.*; import netscape.javascript.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class arcflow_home extends PApplet {
 


PFont font;
nanoxml.XMLElement xml,xmlt;
int frame,lengthAnim = 50;
int colors;
Object[] nodes;
MapSystem ls;
PImage b;

public void setup(){
  frameRate(20);
  size(1000,640,JAVA2D);
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
		    double start_x = ((location.getDoubleAttribute("x")+140)*3);
		    double start_y = (location.getDoubleAttribute("y")*-4+340);
		    double end_x = ((location.getDoubleAttribute("x2")+140)*3);
		    double end_y = (location.getDoubleAttribute("y2")*-4+340);
			
			ls.addLabel((float)start_x,(float)start_y,agent);
			ls.addLabel((float)end_x,(float)end_y,recipient);
			ls.addLocation(nodeIndex,new Location(agent,recipient,start_x,start_y,end_x,end_y,value));
		}
	 nodeIndex = nodeIndex + 1;
   }


  xmlt = new nanoxml.XMLElement();
  String xmlStringt="";
  String[] linest;
  if (online) {
	  linest=loadStrings(param("flowsums"));
  } else {
	  linest=loadStrings("data/full-sums.xml");
  }
  for(int i=0; i<linest.length; i++) xmlStringt+=linest[i];
  xml.parseString(xmlStringt);
  // retrieve all childnodes and store them in an array
  nodes = xml.getChildren().toArray();

  //Iterate through periods
  Vector timeseriesVector=xml.getChildrenForPath("timeseries/period"); 
  // turn into an iterator 
  Enumeration timeseriesEnum=timeseriesVector.elements();
  nodeIndex = 0;
  while(timeseriesEnum.hasMoreElements()) {
	nanoxml.XMLElement period=(nanoxml.XMLElement)timeseriesEnum.nextElement();
	int periodYr = period.getIntAttribute("value");
	double value = period.getDoubleAttribute("sum");
	ls.addTimelineCircle(new TimelineCircle(periodYr,value));
	nodeIndex = nodeIndex + 1;
   }
   //JS.callJS("$('spinner').hide();$('armsflow_container').show();", this);
	JSObject.getWindow(this).eval( "$('spinner').hide();$('armsflow_container').show();" ); 		
}

public void draw(){
  background(0xffeeeeee);
  tint(0xffeeeeee);
  strokeCap(SQUARE);
  image(b, -70, 20, 1100, 540);
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  //text(frame+1950,10,20);

  
  fill(0xff888888,160);
  if (online) {
	text("Exports, "+param("year"),9,18);
  } else {
	text("Exports",9,18);
  }
  //x-axis labels
  for (int i=1950;i<=2006;i+=5) {
	text(i,(16.42f*(i-1950)) + 27,620);
  }


  //mark where we are
  //fill(#cc4444,210);
  //ellipse((float)(8*(frame)) + 135,290,7,7);

  ls.renderPeriod(0); //each period will show for 10 frames
  frame = frame + 1;
  if (frame > (lengthAnim-1)) {
	//frame = 0;
	frameRate(12);
	}
}

public void mousePressed() {
	ls.click();
	if ((mouseY > 560) && (640 > mouseY)) {
		if ((mouseX > 23) && (977 > mouseX)) {
			int periodYr = (int)(mouseX/16.42f + 1948);
			link(param("urlbase")+"/flow/year/"+periodYr);
			//JSObject.getWindow(this).eval( "alert(\""+periodYr+"\")" ); 		
		}
	}
}

class TimelineCircle {
  double scaleVal = 1000;
  double value, yearPos;
  int periodYr;
  
  TimelineCircle (int _periodYr,double _value) {  
	periodYr = _periodYr;
	value = _value;
  }

  public void render() {
	yearPos = (16.42f*(periodYr-1950)) + 40;
    
	//hovers
	fill(0xff888888,80);
	noStroke();
    
	if ((mouseY > 560) && (640 > mouseY)) {
		if ((yearPos+8 > mouseX) && (mouseX > yearPos-8)) {
			rectMode(CENTER);
			fill(0xff888888,160);
			text((int)periodYr,(int)yearPos-13,570);
			fill(0xffcc4444,250);
		} else {
			fill(0xffcc4444,210);
		}
	} else {
		fill(0xffcc4444,210);
	}
	
	//value-circle
    double _diameter = log((float)(value/scaleVal)+1)*20;
    ellipse((float)yearPos,590,(float)_diameter,(float)_diameter);
    fill(0xff333333,120);
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
   double _diameter = log((float)(value/scaleVal)+1)*20;
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
/*		//east or west?
		if (end_x>start_x) { // Going E
			endArc = (startArc+(PI/2)*((float)frame/(float)lengthAnim));
		} else { // Going W
			startArc = (endArc-(PI/2)*((float)frame/(float)lengthAnim));
		}
*/	}

	arc(arc_x,arc_y,arc_w,arc_h,startArc,endArc);
	
   //line((float)start_x,(float)start_y,(float)end_x,(float)end_y);
   /*noStroke();
      fill(#333333,120);
      int label = (int) value;
      //text(agent,(float)(loc_x-6+_diameter/2),(float)(loc_y+6));
   	if ((mouseY > end_y-20) && (end_y+20 > mouseY)) {
   		if ((end_x+20 > mouseX) && (mouseX > end_x-20)) {
   			fill(#888888,100);
   			ellipse((float)end_x,(float)end_y,(float)_diameter*10,(float)_diameter*10);
   			fill(#444444,200);
   			text(recipient,(float)end_x,(float)end_y);
   			fill(#cc4444,250);
   		} else {
   			fill(#cc4444,210);
   		}
   	}*/
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

class Label {
 double x, y;
 String label,yearPos;

 Label (double _x, double _y, String _label) {
	 label = _label;
	 x = _x;
	 y = _y;
	 if (online) {
	   yearPos = param("year");
	 } else {
	   yearPos = "2006";
	 }
 }

 public String label() {
	return label;
 }

  public boolean prox() {
	if ((mouseX+10 > x)&&(mouseX-10 < x)) {
		if ((mouseY+10 > y)&&(mouseY-10 < y)) {
			return true;
		} else {
			return false;
		}
	} else {
		return false;
	}
  }

 public void render() {
    font = loadFont("Georgia-16.vlw"); 
    textFont(font, 16);
	float textwidth = textWidth(label)+10;
	fill(0xff111111,40);
	rect((float)x-textwidth/2+2,(float)y-36+2,textwidth,26);
	triangle((float)x+2,(float)y,(float)x-8+2,(float)y-8,(float)x+8+2,(float)y-8);
	fill(0xffd3dde7);
	rect((float)(int)(x-textwidth/2),(float)(int)(y-36),textwidth,26);
	triangle((float)x,(float)y,(float)x-8,(float)y-12,(float)x+8,(float)y-12);
	fill(0xff333333);
	textAlign(CENTER);
	text(label,(float)x,(float)y-17);
	noStroke();
	textAlign(LEFT);
	}
}

class MapSystem {
  ArrayList periods,timelineCircles,labels;  //Dead period storage
  
  MapSystem () {  
    periods = new ArrayList();
    timelineCircles = new ArrayList();
    labels = new ArrayList();
  }

  public void addPeriod(Period _p) {
	periods.add(_p);
	}
	
  public void addTimelineCircle(TimelineCircle _p) {
	timelineCircles.add(_p);
  }
  
  public void addLabel(float _x, float _y,String _label) {
	boolean isUnique = true;
	for (int i = labels.size()-1; i>=0; i--) {
	      Label l = (Label) labels.get(i);
		  if (l.label().equals(_label)) {
			isUnique = false;
		  }
	    }
	
	//Add logic = if it's still unique...
	if (isUnique == true) {
		labels.add(new Label(_x,_y,_label));
	}
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
	for (int i = timelineCircles.size()-1; i>=0; i--) {
      TimelineCircle b = (TimelineCircle) timelineCircles.get(i);
      b.render();
    }
	//Base this on x,y... 
	//This should eventually just be based on which ones are close to mousex, mousey
	for (int i = labels.size()-1; i>=0; i--) {
	      Label l = (Label) labels.get(i);
	      if (l.prox()) { 
			l.render();
			}
	    }    
  }
  public void click() {
	for (int i = labels.size()-1; i>=0; i--) {
	      Label l = (Label) labels.get(i);
	      if (l.prox()) { 
			link(param("urlbase")+"/flow/country/"+l.label+"."+param("year"));			
			}
	    }
  }
  public int periodCount() {
		return periods.size();
  }
}

  static public void main(String args[]) {     PApplet.main(new String[] { "arcflow_home" });  }}