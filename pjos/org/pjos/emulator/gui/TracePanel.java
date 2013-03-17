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
package org.pjos.emulator.gui;

import org.pjos.common.runtime.Constants;

import org.pjos.emulator.engine.Engine;

import java.awt.BorderLayout;
import java.awt.Graphics;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A panel displaying a stack trace of the current processor state.
 */
class TracePanel extends JPanel implements Constants {

    /** The engine */
    private Engine engine;

    /** The text area */
    private JTextArea area = new MonospacedArea(10, 80);

    /**
     * Create a trace panel
     * @param engine the engine
     */
    TracePanel(Engine engine) {
        this.engine = engine;
        initLayout();
    }

    /**
     * Set the values of the internal components
     */
    private void setValues() {
        try {
            area.setText(getTrace());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            area.setText("Unable to assemble stack trace: " + sw);
        } finally {
            area.setCaretPosition(0);
        }
    }

    /**
     * Layout internal components
     */
    private void initLayout() {
        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    /**
     * Paint this panel. This is overridden to first
     * set the trace content correctly before display the panel.
     * @param g the graphics
     */
    public void paint(Graphics g) {
        setValues();
        super.paint(g);
    }

    /**
     * Assemble the current stack trace
     */
    private String getTrace() {
        StringBuffer result = new StringBuffer();
        int frame = engine.getFrame();
        while (frame != NULL) {
            // get method info
            int method = engine.load(frame + 4 * FRAME_METHOD);
            int methodNameAddress = engine.load(method + 4 * ENTRY_NAME);
            String methodName = Util.readString(methodNameAddress, engine);
            int type = engine.load(method + 4 * ENTRY_OWNER);
            int classNameAddress = engine.load(type + 4 * TYPE_NAME);
            String className = Util.readString(classNameAddress, engine)
                    .replace('/', '.');
            int sourceAddress = engine.load(type + 4 * TYPE_SOURCE);
            String source = (sourceAddress == NULL)
                    ? "Unknown Source"
                    : Util.readString(sourceAddress, engine);

            // get line number info
            String lineNumber = "";
            int pc = engine.load(frame + 4 * FRAME_PC);
            int lineNumberTable = engine.load(method + 4 * METHOD_LINE_NUMBERS);
            if (lineNumberTable != NULL) {
                int length = engine.load(lineNumberTable + 4 * ARRAY_LENGTH);
                int numEntries = length / 2;
                int line = -1;
                for (int i = 0; i < numEntries; i++) {
                    int entry = engine.load(
                            lineNumberTable + 4 * ARRAY_DATA + 4 * i);
                    int startEntry = entry >>> 16;
                    int lineEntry = entry & 0x0000ffff;
                    if (pc >= startEntry) {
                        line = lineEntry;
                    } else  if (pc < startEntry) {
                        break;
                    }
                }
                lineNumber = (line >= 0) ? ":" + line : "";
            }

            // create next line in stack trace
            if (result.length() != 0) { result.append("\tat "); }
            result.append(className)
                .append(".")
                .append(methodName)
                .append("(")
                .append(source)
                .append(lineNumber)
                .append(")\n");
            frame = engine.load(frame + 4 * FRAME_RETURN_FRAME);
        }
        return result.toString();
    }

}












