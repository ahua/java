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
package java.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.LinkedList;
import java.util.List;

import org.pjos.common.file.Path;
import org.pjos.common.file.ResolveException;

/**
 * Implementation of java.io.File based on Sun specification.
 *
 * A drivename consists of a name ending with the ":" character.
 * Any path starting with a drivename is considered to be absolute.
 *
 * The names "." and ".." represent the current and parent directories
 * respectively.
 */
public class File {

    /** The default file separator string */
    public static final String separator
            = System.getProperty("file.separator");

    /** The default file separator character */
    public static final char separatorChar = separator.charAt(0);

    /** The default path separator string */
    public static final String pathSeparator
            = System.getProperty("path.separator");

    /** The default path separator character */
    public static final char pathSeparatorChar = pathSeparator.charAt(0);

    /** The file descriptor */
    private FileDescriptor fd;

    /** The path */
    private Path path;

    /** The absolute path */
    private Path absolutePath;

    /** The canonical path */
    private Path canonicalPath;

    /**
     * Create a new file
     * @param path the path to the file
     */
    public File(String path) {
        init(null, getPath(path));
    }

    /**
     * Create a new file
     * @param parent the path to the parent directory
     * @param child the path to the file
     */
    public File(String parent, String child) {
        init(getPath(parent), getPath(child));
    }

    /**
     * Create a new file
     * @param parent the parent directory
     * @param child the path to the file
     */
    public File(File parent, String child) {
        init(getPath(parent), getPath(child));
    }

    /**
     * Create a new file
     * @param uri the URI specifying the file location
     */
    public File(URI uri) {
throw new UnsupportedOperationException();
    }

    /**
     * Connect this file to a system resource.
     * Essentially sets the file descriptor field.
     * @throws IOException if unable to connect to the underlying resource
     */
    void connect() throws IOException {
        if (fd == null) {
            try {
                Path absolute = getAbsolutePathname();
                Path resolved = absolute.resolve();
                fd = new FileDescriptor(resolved);
                canonicalPath = fd.getBroker().path();
            } catch (ResolveException e) {
                throw (IOException) new IOException().initCause(e);
            }
        }
    }

    /**
     * Initialise this file object using the given paths
     */
    private void init(Path parent, Path child) {
        if (child == null) { throw new NullPointerException(); }
        path = (parent != null) ? parent.append(child) : child;
    }

    /**
     * Return a path object for the given string
     */
    private Path getPath(String s) {
        return (s != null) ? Path.create(s) : null;
    }

    /**
     * Return a path object for the given file
     */
    private Path getPath(File f) {
        return (f != null) ? f.path : null;
    }

    /**
     * @return the file name
     */
    public String getName() {
        return path.last();
    }

    /**
     * @return the parent path
     */
    public String getParent() {
        Path parent = path.parent();
        return (parent.isEmpty()) ? null : parent.toString();
    }

    /**
     * @return the parent file
     */
    public File getParentFile() {
        String parent = getParent();
        return (parent != null) ? new File(parent) : null;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path.toString();
    }

    /**
     * @return true if this file represents an absolute path, false otherwise
     */
    public boolean isAbsolute() {
        return path.isAbsolute();
    }

    /**
     * @return the default user path
     */
    private Path getUserPath() {
        Path userDir = Path.create(System.getProperty("user.dir"));
        return userDir;
    }

    /**
     * Return the absolute pathname as a path object
     */
    private Path getAbsolutePathname() {
        if (absolutePath == null) {
            absolutePath = (isAbsolute()) ? path : getUserPath().append(path);
        }
        return absolutePath;
    }

    /**
     * @return the absolute path
     */
    public String getAbsolutePath() {
        return getAbsolutePathname().toString();
    }

    /**
     * @return absolute form
     */
    public File getAbsoluteFile() {
        return new File(getAbsolutePath());
    }

    /**
     * Return the canonical path as path object
     */
    private Path getCanonicalPathname() throws IOException {
        try {
            return (exists())
                    ? canonicalPath
                    : getAbsolutePathname().resolve();
        } catch (ResolveException e) {
            throw (IOException) new IOException().initCause(e);
        }
    }

    /**
     * This is the file-system dependent absolute path with all instances
     * of "." and ".." resolved.
     * @return the canonical path
     * @throws IOException if an error occurs
     */
    public String getCanonicalPath() throws IOException {
        return getCanonicalPathname().toString();
    }

    /**
     * Return canonical file
     * @return the file object representing the canonical path
     * @throws IOException if an error occurs
     */
    public File getCanonicalFile() throws IOException {
        return new File(getCanonicalPath());
    }

    /**
     * Convert to a url
     * @return a URL object representing this file
     * @throws MalformedURLException if the url is not valid
     */
    public URL toURL() throws MalformedURLException {
throw new UnsupportedOperationException();
    }

    /**
     * Convert to a uri
     * @return a URI object representing this file
     */
    public URI toURI() {
throw new UnsupportedOperationException();
    }

    /**
     * Determine if the application can read from this file
     * @return true if this file is readable, false otherwise
     */
    public boolean canRead() {
throw new UnsupportedOperationException();
    }

    /**
     * Determine if the application can write to this file
     * @return true if this file is writeable, false otherwise
     */
    public boolean canWrite() {
throw new UnsupportedOperationException();
    }

    /**
     * Determine if this file exists
     * @return true if this file exists, false otherwise
     */
    public boolean exists() {
        try {
            connect();
            return (fd != null) ? fd.getBroker().isValid() : false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return true if this is a directory, false otherwise
     */
    public boolean isDirectory() {
        return exists() && fd.getBroker().isDirectory();
    }

    /**
     * @return true if this object is a file, false otherwise
     */
    public boolean isFile() {
        return exists() && fd.getBroker().isFile();
    }

    /**
     * @return true if this file is hidden, false otherwise
     */
    public boolean isHidden() {
throw new UnsupportedOperationException();
    }

    /**
     * @return the date the file was last modified
     */
    public long lastModified() {
throw new UnsupportedOperationException();
    }

    /**
     * @return the length of the file
     */
    public long length() {
throw new UnsupportedOperationException();
    }

    /**
     * Create a new empty file for this path
     * @return true if successful, false otherwise
     * @throws IOException if an error occurs
     */
    public boolean createNewFile() throws IOException {
throw new UnsupportedOperationException();
    }

    /**
     * Delete this file or directory (directory must be empty)
     * @return true if successful, false otherwise
     */
    public boolean delete() {
throw new UnsupportedOperationException();
    }

    /**
     * Delete when virtual machine terminates
     */
    public void deleteOnExit() {
throw new UnsupportedOperationException();
    }

    /**
     * @return a list of file names in this directory
     */
    public String[] list() {
        try {
            connect();
            return (fd != null) ? fd.getBroker().list() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param filter the filename filter to use
     * @return a list of file names that satisfy the specified filter
     */
    public String[] list(FilenameFilter filter) {
        String[] all = list();
        if (all == null) { return null; }
        List filenames = new LinkedList();
        for (int i = 0, n = all.length; i < n; i++) {
            String filename = all[i];
            if (filter.accept(this, filename)) { filenames.add(filename); }
        }
        int size = filenames.size();
        return (String[]) filenames.toArray(new String[size]);
    }

    /**
     * @return a list of files in this directory
     */
    public File[] listFiles() {
        return toFileList(list());
    }

    /**
     * @param filter the filename filter to use
     * @return a list of files in this directory
     *         that satisfy the specified filter
     */
    public File[] listFiles(FilenameFilter filter) {
        return toFileList(list(filter));
    }

    /**
     * Return the list of files represented by the given list of strings
     */
    private static File[] toFileList(String[] names) {
        if (names == null) { return null; }
        int length = names.length;
        File[] result = new File[length];
        for (int i = 0; i < length; i++) {
            result[i] = new File(names[i]);
        }
        return result;
    }

    /**
     * @param filter the filter to use
     * @return the list of files in this directory that
     *         satisfy the specified filter
     */
    public File[] listFiles(FileFilter filter) {
        File[] all = listFiles();
        if (all == null) { return null; } 
        List files = new LinkedList();
        for (int i = 0, n = all.length; i < n; i++) {
            File file = all[i];
            if (filter.accept(file)) { files.add(file); }
        }
        int size = files.size();
        return (File[]) files.toArray(new File[size]);
    }

    /**
     * Create the directory represented by this file object
     * @return true if successful, false otherwise
     */
    public boolean mkdir() {
throw new UnsupportedOperationException();
    }

    /**
     * Create the directory represented by this file object,
     * including parent directories
     * @return true if successful, false otherwise
     */
    public boolean mkdirs() {
throw new UnsupportedOperationException();
    }

    /**
     * Rename this file
     * @param file the new location for the file
     * @return true if successful, false otherwise
     */
    public boolean renameTo(File file) {
throw new UnsupportedOperationException();
    }

    /**
     * Set the last modified time
     * @param time the new timestamp to be set
     * @return true if successful, false otherwise
     */
    public boolean setLastModified(long time) {
        if (time < 0) { throw new IllegalArgumentException(); }
throw new UnsupportedOperationException();
    }

    /**
     * Set the read-only flag
     * @return true if successful, false otherwise
     */
    public boolean setReadOnly() {
throw new UnsupportedOperationException();
    }

    /**
     * @return the list of available file system roots
     */
    public static File[] listRoots() {
throw new UnsupportedOperationException();
    }

    /**
     * Create a temp file
     * @param prefix the filename prefix
     * @param suffix the filename suffix
     * @param directory the directory to use
     * @return the temp file object
     * @throws IOException if an error occurs
     */
    public static File createTempFile(
            String prefix,
            String suffix,
            File directory)
            throws IOException
    {
throw new UnsupportedOperationException();
    }

    /**
     * Create a temp file
     * @param prefix the filename prefix
     * @param suffix the filename suffix
     * @return the temp file object
     * @throws IOException if an error occurs
     */
    public static File createTempFile(String prefix, String suffix)
            throws IOException
    {
throw new UnsupportedOperationException();
    }

    /**
     * Compare two abstract pathnames
     * @param other the file to be compared to
     * @return int indication of ordering
     */
    public int compareTo(File other) {
        return path.compareTo(other.path);
    }

    /**
     * Compare to another object
     * @param o the object to be compared to
     * @return an int value indicating the comparison result
     */
    public int compareTo(Object o) {
        return compareTo((File) o);
    }

    /**
     * Test for equality
     * @param o the object to test
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof File)) { return false; }
        File other = (File) o;
        return path.equals(other.path);
    }

    /**
     * @return a hash code for this file object
     */
    public int hashCode() {
        return path.hashCode() ^ 1234321;
    }

    /**
     * @return a string description of this file's path
     */
    public String toString() {
        return path.toString();
    }
    
    /**
     * Return the descriptor
     */
    FileDescriptor getFileDescriptor() {
        return fd;
    }

}
