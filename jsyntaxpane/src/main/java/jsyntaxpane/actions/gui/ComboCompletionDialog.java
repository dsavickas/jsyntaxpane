/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsyntaxpane.actions.gui;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import jsyntaxpane.actions.ActionUtils;
import jsyntaxpane.util.StringUtils;
import jsyntaxpane.util.SwingUtils;

/**
 *
 * @author Ayman Al-Sairafi
 */
public class ComboCompletionDialog extends javax.swing.JDialog {

    /**
     * The result returned to the caller
     */
    private String result = null;
    /**
     * Our target component
     */
    private JTextComponent target;
    public String escapeChars = ";(= \t\n\r";
    public List<String> items;

    /**
     * Creates new form ComboCompletionDialog
     * @param target
     */
    public ComboCompletionDialog(JTextComponent target) {
        super(ActionUtils.getFrameFor(target), true);
        initComponents();
        jTxtItem.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                refilterList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refilterList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refilterList();
            }
        });
        // This will allow the textfield to receive TAB keys
        jTxtItem.setFocusTraversalKeysEnabled(false);
        this.target = target;
        SwingUtils.addEscapeListener(this);
    }

    /**
     * Display the Completion Dialog with initial abbrev and using the given items
     * The dialog is responsible for showing itself and for updating the target
     * with the text, depending on user actions.
     *
     * The dialog will be aligned to the selectionStart of the target component
     * and when a selection is made, replaceSelection will be called on dialog
     *
     * @param abbrev
     * @param items
     */
    public void displayFor(String abbrev, List<String> items) {
        this.items = items;
        try {
            Window window = SwingUtilities.getWindowAncestor(target);
            Rectangle rt = target.modelToView(target.getSelectionStart());
            Point loc = new Point(rt.x, rt.y);
            setLocationRelativeTo(window);
            // convert the location from Text Componet coordinates to
            // Frame coordinates...
            loc = SwingUtilities.convertPoint(target, loc, window);
            // and then to Screen coordinates
            SwingUtilities.convertPointToScreen(loc, window);
            setLocation(loc);
        } catch (BadLocationException ex) {
            Logger.getLogger(ComboCompletionDialog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Font font = target.getFont();
            jTxtItem.setFont(font);
            jLstItems.setFont(font);
            doLayout();
            jTxtItem.setText(abbrev);
            refilterList();
            setVisible(true);
        }
    }

    private void refilterList() {
        String prefix = jTxtItem.getText();
        Vector<String> filtered = new Vector<String>();
        Object selected = jLstItems.getSelectedValue();
        for (String s : items) {
            if (StringUtils.camelCaseMatch(s, prefix)) {
                filtered.add(s);
            }
        }
        jLstItems.setListData(filtered);
        if (selected != null && filtered.contains(selected)) {
            jLstItems.setSelectedValue(selected, true);
        } else {
            jLstItems.setSelectedIndex(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtItem = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLstItems = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        jTxtItem.setBorder(null);
        jTxtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTxtItemKeyPressed(evt);
            }
        });

        jLstItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jLstItems.setFocusable(false);
        jLstItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLstItemsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jLstItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTxtItem, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTxtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtItemKeyPressed

        int i = jLstItems.getSelectedIndex();
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                result = jTxtItem.getText();
                target.replaceSelection(result);
                setVisible(false);
                return;
            case KeyEvent.VK_DOWN:
                if (i < jLstItems.getModel().getSize() - 1) {
                    i++;
                }
                jLstItems.setSelectedIndex(i);
                jLstItems.ensureIndexIsVisible(i);
                break;
            case KeyEvent.VK_UP:
                if (i > 0) {
                    i--;
                }
                jLstItems.setSelectedIndex(i);
                jLstItems.ensureIndexIsVisible(i);
                break;
        }

        if (escapeChars.indexOf(evt.getKeyChar()) >= 0) {
            if (jLstItems.getSelectedIndex() >= 0) {
                result = jLstItems.getSelectedValue().toString();
            } else {
                result = jTxtItem.getText();
            }
            char pressed = evt.getKeyChar();
            // we need to just accept ENTER, and replace the tab with a single
            // space
            if (pressed != '\n') {
                result += (pressed == '\t') ? ' ' : pressed;
            }
            target.replaceSelection(result);
            setVisible(false);
        }
    }//GEN-LAST:event_jTxtItemKeyPressed

	private void jLstItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLstItemsMouseClicked
		if(evt.getClickCount() == 2) {
			String selected = jLstItems.getSelectedValue().toString();
			target.replaceSelection(selected);
			setVisible(false);
		}
	}//GEN-LAST:event_jLstItemsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jLstItems;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTxtItem;
    // End of variables declaration//GEN-END:variables
}
