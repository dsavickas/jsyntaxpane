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
package jsyntaxpane.components;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import jsyntaxpane.TokenType;
import jsyntaxpane.actions.*;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.util.Configuration;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * This class highlights any pairs of the given language.  Pairs are defined
 * with the Token.pairValue.
 *
 * @author Ayman Al-Sairafi
 */
public class PairsMarker implements CaretListener, SyntaxComponent, PropertyChangeListener {
    public static final String PROPERTY_COLOR = "PairMarker.Color";
    private static final Color DEFAULT_MARKER_COLOR = new Color(0xeeee33);
    private JTextComponent pane;
    private Markers.SimpleMarker marker = new Markers.SimpleMarker(DEFAULT_MARKER_COLOR);
    private Status status;

    public PairsMarker() {
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        removeMarkers();
        int pos = e.getDot();
        SyntaxDocument doc = ActionUtils.getSyntaxDocument(pane);
        if (doc != null) {
            List<Token> tokens = getPairTokens(doc, pos);
            if (tokens.size() == 2) {
                Markers.markToken(pane, tokens.get(0), marker);
                Markers.markToken(pane, tokens.get(1), marker);
            }
        }
    }

    List<Token> getPairTokens(SyntaxDocument doc, int pos) {
        List<Token> tokens = emptyList();
        Token token = this.getPairToken(doc, pos);
        if (token != null) {
            Token other = doc.getPairFor(token);
            if (other != null) {
                if (other.start > token.start) {
                    tokens = asList(token, other);
                } else {
                    tokens = asList(other, token);
                }
            }
        }

        return tokens;
    }

    private Token getPairToken(SyntaxDocument doc, int pos) {
        Token token = doc.getTokenAt(pos);
        if (token != null) {
            if (token.pairValue == 0 &&
                token.type != TokenType.IDENTIFIER &&
                pos != 0) {
                Token prevToken = doc.getTokenAt(pos - 1);
                if (prevToken != null && prevToken.start == pos - prevToken.length) {
                    token = prevToken;
                }
            }
            if (token.pairValue == 0) {
                token = null;
            }
        }

        return token;
    }

    /**
     * Remove all the highlights from the editor pane.  This should be called
     * when the editorkit is removed.
     */
    public void removeMarkers() {
        Markers.removeMarkers(pane, marker);
    }

    @Override
    public void config(Configuration config) {
        Color markerColor = config.getColor(PROPERTY_COLOR, DEFAULT_MARKER_COLOR);
        this.marker = new Markers.SimpleMarker(markerColor);
    }

    @Override
    public void install(JEditorPane editor) {
        pane = editor;
        pane.addCaretListener(this);
        status = Status.INSTALLING;
    }

    @Override
    public void deinstall(JEditorPane editor) {
        status = Status.DEINSTALLING;
        pane.removeCaretListener(this);
        removeMarkers();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("document")) {
                pane.removeCaretListener(this);
            if (status.equals(Status.INSTALLING)) {
                pane.addCaretListener(this);
                removeMarkers();
            }
        }
    }
}
