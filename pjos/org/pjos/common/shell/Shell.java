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
package org.pjos.common.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A simple shell that uses the system input and
 * output streams.
 */
public final class Shell implements Runnable {

    /** The backspace character */
    private static final char BACKSPACE = '\u0008';

    /** The prompt character */
    private static final char PROMPT = '>';
    
    /** The cursor (only used if echo is on) */
    private static final char CURSOR = '_';

    /** The current working directory path */
    private String working;
    
    /** The echo flag */
    private boolean echo;

    /**
     * Create a shell
     * @param echo set to true for local echo, false otherwise 
     */
    public Shell(boolean echo) {
        this.echo = echo;
        working = System.getProperty("user.dir");
    }

    /**
     * Process commands
     */
    public void run() {
        // use system streams
        InputStream in = System.in;
        PrintStream out = System.out;

        // welcome message
        out.println();
        out.println();
        out.println("PJOS Test Shell");
        if (echo) {
            out.println("[echo on]");
        } else {
            out.println("[echo off]");
        }

        // process commands
        try {
            StringBuffer buf = new StringBuffer();
            while (true) {
                // Write prompt and cursor
                out.print(PROMPT);
                if (echo) { out.print(CURSOR); }

                // read the next command
                for (int k = in.read(); k != '\n'; k = in.read()) {
                    if (echo) {
                        out.print(BACKSPACE); // delete cursor
                        if (k == BACKSPACE) {
                            if (buf.length() > 0) {
                                buf.setLength(buf.length() - 1);
                                out.print(BACKSPACE);
                            }
                        } else {
                            buf.append((char) k);
                            out.print((char) k);
                            out.flush();
                        }
                        out.print(CURSOR);
                    } else {
                        buf.append((char) k);
                    }
                }
                
                // clean up cursor
                if (echo) {
                    out.print(BACKSPACE); // delete cursor
                    out.print('\n');
                }

                // process the command
                execute(buf.toString().trim());
                buf.setLength(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute the given command
     */
    private void execute(String command) {
        if (command.equals("help")) {
            // print available commands
            System.out.println("Commands available: "
                    + "cat <file>, cd <dir>, help, ls, pwd");
            
        } else if (command.startsWith("cat ")) {
            // print file contents
            String path = command.substring(4);
            try {
                File file = new File(path);
                if (!file.isAbsolute()) { file = new File(working, path); }
                FileInputStream fis = new FileInputStream(file);
                int k = fis.read();
                while (k != -1) {
                    char c = (char) k;
                    if (Character.isISOControl(c)) { c = '.'; }
                    System.out.print(c);
                    k = fis.read();
                }
                fis.close();
                System.out.println();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + path);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else if (command.equals("pwd")) {
            // print working directory
            System.out.println(working);

        } else if (command.equals("ls")) {
            // print list of filenames in current directory
            File dir = new File(working);
            String[] names = dir.list();
            if (names == null) {
                System.out.println("Directory '" + working + "' not found");
            } else {
                for (int i = 0, n = names.length; i < n; i++) {
                    System.out.println(names[i]);
                }
            }

        } else if (command.startsWith("cd ")) {
            // change working directory
            String path = command.substring(3);
            try {
                File dir = new File(path);
                if (!dir.isAbsolute()) { dir = new File(working, path); }
                if (dir.isDirectory()) {
                    working = dir.getCanonicalPath();
                } else {
                    System.out.println("Directory '" + path + "' not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Command not understood");
        }
    }

}
