package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Manages all registered Box instances. Can save and load the registered boxes information
 * from a given file.
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class BoxPool {

	private final DynamicRemoteControlPool remoteControlPool;

	public BoxPool(DynamicRemoteControlPool remoteControlPool) {
		this.remoteControlPool = remoteControlPool;
	}
	private List<Box> boxes = new ArrayList<Box>();

	public void register(Box box) {
		this.boxes.add(box);
	}
	
	public void saveTo(File file) {
		XStream stream = new XStream();
		stream.alias("box", Box.class);
		stream.alias("boxes", List.class);
		String xml = stream.toXML(boxes);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
			writer.println(xml);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File not found", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	public void loadFrom(File file) {
		XStream stream = new XStream(new DomDriver());
		stream.alias("box", Box.class);
		stream.alias("boxes", List.class);
		try {
			boxes = (List<Box>) stream.fromXML(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("file not found", e);
		}
	}
	
	public List<Box> getBoxes() {
		return boxes;
	}

	public Box load(Box box) {
		for (Box b : boxes) {
			if (b.host().equals(box.host()) && b.port() == box.port()) {
				return b;
			}
		}
		throw new IllegalArgumentException("Attempt to start box that was not already registered");
	}

	public void unregister(Box box) {
		Box loaded = load(box);
		loaded.setDown(remoteControlPool);
		boxes.remove(loaded);
	}

}
