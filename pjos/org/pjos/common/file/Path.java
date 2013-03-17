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
package org.pjos.common.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Immutable representation of a file path which uses
 * the '/' character as the separator. A path is absolute
 * if it starts with the separator character, relative otherwise.
 */
public final class Path implements Comparable {

    /** The separator character as a string */
    public static final String SEPARATOR = "/";
    
    /** Relative reference to the current directory */
    public static final String CURRENT = ".";
    
    /** Relative reference to the parent directory */
    public static final String PARENT = "..";

    /** The empty path (empty and relative) */
    public static final Path EMPTY = new Path(Collections.EMPTY_LIST, false);
    
    /** The root path (empty and absolute) */
    public static final Path ROOT = new Path(Collections.EMPTY_LIST, true);
    
    /** The number of elements in this path */
    private int size;

    /** The elements making up this path */
    private List elements;

    /** The absolute flag */
    private boolean absolute;

    /** The string description */
    private String description;
    
    /**
     * Create a path object consisting of the elements in the given path
     * descriptor. If the given path is null throw a NullPointerException.
     * The only separator recognised by this method is the '/' character.
     * @param descriptor the path descriptor 
     * @return a path object representing the given descriptor
     */
    public static Path create(String descriptor) {
        if (descriptor == null) { throw new NullPointerException(); }
        StringTokenizer st = new StringTokenizer(descriptor, SEPARATOR);
        List list = new ArrayList(st.countTokens());
        while (st.hasMoreTokens()) { list.add(st.nextToken()); }
        return createInternal(list, descriptor.startsWith(SEPARATOR));
    }
    
    /**
     * Create a path object consisting of the elements in the given
     * list. Each element must be a string object with length > 0
     * and cannot contain the '/' character.
     * @param elements the list of names making up the path
     * @param absolute true if the path should be absolute
     * @return the path object representing the given elements
     */
    public static Path create(List elements, boolean absolute) {
        // check that the list elements are valid
        for (Iterator it = elements.iterator(); it.hasNext();) {
            String next = (String) it.next();
            if (next.indexOf(SEPARATOR) != -1 || next.length() == 0) {
                throw new IllegalArgumentException();
            }
        }
        return createInternal(new ArrayList(elements), absolute);
    }
    
    /**
     * Create a path object using the elements in the given list
     */
    private static Path createInternal(List names, boolean absolute) {
        if (names.isEmpty()) {
            return (absolute) ? ROOT : EMPTY;
        } else {
            return new Path(Collections.unmodifiableList(names), absolute);
        }
    }

    /**
     * Create a path consisting of the given elements.
     */
    private Path(List elements, boolean absolute) {
        this.elements = elements;
        this.absolute = absolute;
        size = elements.size();
    }

    /**
     * @return the number of elements in this path
     */
    public int size() {
        return size;
    }

    /**
     * This method returns true for the EMPTY and ROOT instances,
     * and false for all other instances.
     *
     * @return true if this path has no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Return the element at the given index
     *
     * @param index the index of the desired element
     * @return the element
     */
    public String get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException();
        }
        return (String) elements.get(index);
    }

    /**
     * Return the first element
     *
     * @return the first element, or the empty string "" if this path is empty
     */
    public String first() {
        return (size > 0) ? get(0) : "";
    }

    /**
     * Return the last element
     *
     * @return the last element, or the empty string "" if this path is empty
     */
    public String last() {
        return (size > 0) ? get(size - 1) : "";
    }

    /**
     * Return the description. If this path is absolute, the
     * separator character '/' will be the first character.
     *
     * @return the string representation of this path
     */
    public String toString() {
        if (description == null) {
            StringBuffer buf = new StringBuffer();
            if (absolute) { buf.append(SEPARATOR); }
            Iterator it = elements.iterator();
            if (it.hasNext()) { buf.append(it.next()); }
            while (it.hasNext()) {
                buf.append(SEPARATOR).append(it.next());
            }
            description = buf.toString();
        }
        return description;
    }

    /**
     * Return a new path object that represents a subset of the elements
     * of this path.
     *
     * @param from the index of the first element to include
     * @return the path containing all the elements from the specified index
     */
    public Path subPath(int from) {
        return subPath(from, size);
    }

    /**
     * Return a new path that represents a subset of the elements of this path.
     * The subpath begins with the element at the specified 'from' index and
     * includes all the entries following it up to and including the element
     * at the 'to' index.
     *
     * @param from the index of the first element (inclusive)
     * @param to the index of the last element (exclusive)
     * @return a path object representing the desired subset of elements
     */
    public Path subPath(int from, int to) {
        if (from < 0 || to > size || from > to) {
            throw new IllegalArgumentException();
        } else if (from == 0 && to == size) {
            return this;
        } else if (to < 1) {
            return (absolute) ? ROOT : EMPTY;
        } else {
            boolean abs = (from == 0) ? absolute : false;
            return new Path(elements.subList(from, to), abs);
        }
    }

    /**
     * Return a new path that contains all the entries of this path followed by
     * all the entries of the given path.
     *
     * @param path the path to append
     * @return the concatenation of this path with the given path
     */
    public Path append(Path path) {        
        int length = size + path.size;
        List all = new ArrayList(length);
        all.addAll(elements);
        all.addAll(path.elements);
        return createInternal(all, absolute);
    }

    /**
     * Return a path representing the parent of this path. This is simply a path
     * containing all entries in this path except the last. The parent path of
     * the empty path or a path with only one entry is the empty path.
     *
     * @return the parent path
     */
    public Path parent() {
        return (size < 2) ? EMPTY : subPath(0, size - 1);
    }

    /**
     * Return a path equal to this one but with all references
     * to "." and ".." resolved.
     *
     * @return the resolved path
     * @exception ResolveException thrown when the path cannot be resolved
     */
    public Path resolve() throws ResolveException {
        List list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            String element = get(i);
            if (element.equals(PARENT)) {
                if (list.isEmpty()) { throw new ResolveException(); }
                list.remove(list.size() - 1);
            } else if (!element.equals(CURRENT)) {
                list.add(element);
            }
        }
        return (list.size() == size) ? this : createInternal(list, absolute);
    }

    /**
     * Test for equality.
     *
     * @param o the object to test
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Path)) { return false; }
        Path other = (Path) o;
        return elements.equals(other.elements);
    }

    /**
     * Return a hash code based on the description
     *
     * @return the hash code value
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Compare to another object using the string description.
     *
     * @param o the object to be compared to
     * @return int value indicating ordering
     */
    public int compareTo(Object o) {
        return compareTo((Path) o);
    }

    /**
     * Compare to another path using the string description.
     *
     * @param p the path to be compared to
     * @return int value indicating ordering
     */
    public int compareTo(Path p) {
        return toString().compareTo(p.toString());
    }

    /**
     * Return true if this path represents an absolute file path. This path is
     * defined as absolute if the first entry is a valid drive specifier.
     *
     * @return true if this path is absolute, false otherwise
     */
    public boolean isAbsolute() {
        return absolute;
    }
    
    /**
     * Return a path object containing the same elements as this path
     * but which is absolute.
     *
     * @return the absolute version of this path
     */
    public Path makeAbsolute() {
        if (absolute) { return this; }
        if (size == 0) { return ROOT; }
        return new Path(elements, true);
    }

}






