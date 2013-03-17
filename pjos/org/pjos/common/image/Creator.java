/*
Copyright 2002 Simon Daniel
email: simon@pjos.org

This file is part of PJOS.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.pjos.common.image;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.ByteOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.pjos.common.runtime.Type;

/**
 * Creates an executable memory image for a java virtual machine.
 */
public class Creator {

    /** The configuration document */
    private Document doc;
    
    /** The path used to load the class files */
    private String classpath;
    
    /** The name of the image file to be generated */
    private String imageFile;
    
    /** The name of the log file to be generated */
    private String logFile;
    
    /** The type manager */
    private TypeManager types;
    
    /** The instance manager */
    private InstanceManager instances;
    
    /** Contains the frames, indexed by name */
    private Map frameMap = new HashMap();
    
    /** The web used to create the actual binary image */
    private Web web;
    
    /** The array containing all strings */
    private String[] strings;

    /** The map containing all the peer objects */
    private Map peerMap = new HashMap();
    
    /**
     * Create an image object with the given document
     */
    private Creator(
            Document doc,
            String classpath,
            String imageFile,
            String logFile)
    {
        this.doc = doc;
        this.classpath = classpath;
        this.imageFile = imageFile;
        this.logFile = logFile;
        types = new TypeManager(this);
        instances = new InstanceManager(this);
    }
    
    /**
     * Generate the image file
     */
    private void generate() throws IOException {
        // check there's only one image node
        getUnique("image");
        
        // read settings
        int offset = getIntValue("offset");
        String root = getValue("root");
        ByteOrder byteOrder = getByteOrder(getValue("byteorder"));
        
        // create web
        PrintStream log = new PrintStream(
                new BufferedOutputStream(new FileOutputStream(logFile)));
        web = new Web(byteOrder, offset, createInitialisers(), log);
        
        // load types, frames and instances
        types.loadTypes(doc, classpath);
        loadFrames();
        instances.loadInstances(doc);
        
        // Create the executable image and allocate objects
        Instance rootInstance = getInstance(root);
        web.allocate(rootInstance);
        byte[] data = web.generate();
        
        // save to file
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(imageFile));
        out.write(data);
        out.close();
        log.close();
        System.out.println("Memory image generated: " + imageFile);
    }
    
    /**
     * Create an array containing all the initialisers
     */
    private Initialiser[] createInitialisers() {
        return new Initialiser[] {
            new StringInitialiser(this),
            new ByteArrayInitialiser(this),
            new ShortArrayInitialiser(this),
            new CharArrayInitialiser(this),
            new ObjectArrayInitialiser(this),
            new StringConstantInitialiser(this),
            new IntegerConstantInitialiser(this),
            new LongConstantInitialiser(this),
            new TypeInitialiser(this),
            new InstanceInitialiser(this),
            new FrameInitialiser(this),
            new MethodInitialiser(this),
            new FieldInitialiser(this),
            new UnresolvedFieldInitialiser(this),
            new UnresolvedMethodInitialiser(this),
            new UnresolvedTypeInitialiser(this),
            new StaticsInitialiser(this),
            new PeerInitialiser(this),
            new BooleanArrayInitialiser(this)
        };
    }
    
    /**
     * Return a byte order object for the given string
     */
    private ByteOrder getByteOrder(String s) {
        if (s.equals("big endian")) {
            return ByteOrder.BIG_ENDIAN;
        } else if (s.equals("little endian")) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        throw new IllegalArgumentException("Invalid byte order: " + s
                + " ('big endian' or 'little endian')");
    }
    
    /**
     * Read the frame entries and initialise them
     */
    private void loadFrames() {
        NodeList frameNodes = doc.getElementsByTagName("frame");
        for (int i = 0, n = frameNodes.getLength(); i < n; i++) {
            Frame frame = new Frame(frameNodes.item(i), this);
            String name = frame.getName();
            if (frameMap.containsKey(name)) {
                throw new IllegalStateException("Frame already defined: "
                        + name);
            }
            frameMap.put(name, frame);
        }
    }
    
    /**
     * Load the given object into the web as a key
     * @param key the key
     * @return the node
     */
    Node load(Object key) {
        if (key == null) { throw new NullPointerException(); }
        return web.get(key);
    }
    
    /**
     * @param key the key
     * @return a pointer slot for the given object
     */
    PointerSlot getPointerTo(Object key) {
        return new PointerSlot(key, web);
    }
    
    /**
     * @param name the name
     * @return the type with the given name
     */
    Type getType(String name) {
        return types.get(name);
    }

    /**
     * @param type the type
     * @return the peer for the given type
     */
    Peer getPeer(Type type) {
        if (type == null) { return null; }
        synchronized (peerMap) {
            Peer result = (Peer) peerMap.get(type);
            if (result == null) {
                result = new Peer(type);
                peerMap.put(type, result);
            }
            return result;
        }
    }
    
    /**
     * @param name the name
     * @return the frame with the given name
     */
    Frame getFrame(String name) {
        Frame result = (Frame) frameMap.get(name);
        if (result == null) {
            throw new IllegalStateException("No frame defined: " + name);
        }
        return result;
    }
    
    /**
     * @param name the name
     * @return the instance with the given name
     */
    Instance getInstance(String name) {
        return instances.get(name);
    }
    
    /**
     * @return the address following this object 
     */
    int next() {
        return web.next();
    }
    
    /**
     * @return an array containing all loaded strings
     */
    String[] getStrings() {
        if (strings == null) {
            Set stringSet = web.getMatchingKeys(String.class);
            String[] s = new String[stringSet.size()];
            strings = (String[]) stringSet.toArray(s);
        }
        return strings;
    }
    
    /**
     * @return an array containing all loaded types
     */
    Type[] getTypes() {
        return types.getAll();
    }
    
    /**
     * Retrieve the value attribute from the node with the given name
     * as an integer.
     */
    private int getIntValue(String name) {
        return Integer.decode(getValue(name)).intValue();
    }
    
    /**
     * Retrieve the value attribute from the node with the given name
     */
    private String getValue(String name) {
        org.w3c.dom.Node node = getUnique(name);
        return getAttribute("value", node);
    }
    
    /**
     * @return the named attribute from the given node
     * @param name the name
     * @param node the node
     */
    static String getAttribute(String name, org.w3c.dom.Node node) {
        org.w3c.dom.Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            throw new IllegalStateException("missing " + name
                    + " attribute for <" + node.getNodeName() + ">");
        }
        return attr.getNodeValue();
    }
    
    /**
     * Retrieve the unique node with the given name
     */
    private org.w3c.dom.Node getUnique(String name) {
        NodeList nodes = doc.getElementsByTagName(name);
        int length = nodes.getLength();
        if (length < 1) {
            throw new IllegalStateException("<" + name + "> node missing");
        } else if (length > 1) {
            throw new IllegalStateException("multiple <" + name + ">"
                    + " nodes not allowed");
        }
        return nodes.item(0);
    }
    
    /**
     * Create an image using the specified configuration file. The supplied
     * classpath must be a list of directories separated by the ';' character.
     * @param args the command line args
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new Exception("usage: java org.pjos.common.image.Creator "
                    + "<configuration> <classpath> <image> <log>");
        }

        // Load xml configuration
        DocumentBuilder builder
                = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(args[0]);
        
        // generate image file
        Creator creator = new Creator(document, args[1], args[2], args[3]);
        creator.generate();
    }

}
