import processing.core.*; import java.io.*; import processing.net.*; import netscape.javascript.JSObject; import nanoxml.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class timeline extends PApplet {



PFont font;
nanoxml.XMLElement xml;
int frame;
int colors;
Object[] nodes;
MapSystem ls;
PImage b;

public void setup(){
  frameRate(10);
  size(1000,160,JAVA2D);
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
	  lines=loadStrings("data/data.xml");
  }
  for(int i=0; i<lines.length; i++) xmlString+=lines[i];
  xml.parseString(xmlString);
  // retrieve all childnodes and store them in an array
  nodes = xml.getChildren().toArray();

  String title = (String)xml.getAttribute("title");
  String countries = title+": ";

  //Iterate through periods
  Vector timeseriesVector=xml.getChildrenForPath("timeseries/period"); 
  // turn into an iterator 
  Enumeration timeseriesEnum=timeseriesVector.elements();
  int nodeIndex = 0;
  while(timeseriesEnum.hasMoreElements()) {
	nanoxml.XMLElement period=(nanoxml.XMLElement)timeseriesEnum.nextElement(); 
	int periodYr = period.getIntAttribute("value");
	double value = period.getDoubleAttribute("sum");
	ls.addPeriod(new Period(periodYr,value));
	nodeIndex = nodeIndex + 1;
   }
}

public void draw(){
	delay(20);
  background(0xffeeeeee);
  tint(0xffeeeeee);
  image(b, -70, 40, 1100, 540);
  font = createFont("LucidaGrande-10.vlw",10); 
  textFont(font, 10);
  //render dots
  ls.render();
  text("Exports",9,18);
  //x-axis labels
  fill(0xff888888,160);
  for (int i=1950;i<=2006;i+=5) {
	text(i,(16.42f*(i-1950)) + 27,140);
  }
}

public void mousePressed() {
	if ((mouseY > 0) && (160 > mouseY)) {
		if ((mouseX > 23) && (977 > mouseX)) {
			int periodYr = (int)(mouseX/16.42f + 1948);
			link(param("urlbase")+"/flow/country/"+param("country")+"."+periodYr);
			//JSObject.getWindow(this).eval( "alert(\""+periodYr+"\")" ); 		
		}
	}
}

class Period {
  double scaleVal = 1;
  double value, yearPos;
  int periodYr;
  ArrayList locations;  //Dead location storage
  
  Period (int _periodYr,double _value) {  
    locations = new ArrayList();
	periodYr = _periodYr;
	value = _value;
  }

  public void render() {
	yearPos = (16.42f*(periodYr-1950)) + 40;
    
	//hovers
	fill(0xff888888,80);
	noStroke();
    
	if ((mouseY > 0) && (160 > mouseY)) {
		if ((yearPos+8 > mouseX) && (mouseX > yearPos-8)) {
			rectMode(CENTER);
		    rect((float)yearPos,80,30,160);
			fill(0xff888888,160);
			text((int)periodYr,(int)yearPos-13,60);
			fill(0xffcc4444,250);
		} else {
			fill(0xffcc4444,210);
		}
	} else {
		fill(0xffcc4444,210);
	}
	
	//value-circle
    double _diameter = log((float)(value))*4;
    ellipse((float)yearPos,80,(float)_diameter,(float)_diameter);
    fill(0xff333333,120);
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

  static public void main(String args[]) {     PApplet.main(new String[] { "timeline" });  }}