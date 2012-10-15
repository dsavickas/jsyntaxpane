package jsyntaxpane.components;

import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.lexers.JavaLexer;
import jsyntaxpane.syntaxkits.JavaSyntaxKit;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.BadLocationException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PairsMarkerTest {
    private SyntaxDocument doc;
    private PairsMarker pairsMarker;

    @Before
    public void initialize() {
        JavaSyntaxKit syntaxKit = new JavaSyntaxKit();
        JavaLexer lexer = new JavaLexer();
        this.doc = new SyntaxDocument(lexer);
        this.pairsMarker = new PairsMarker();
    }

    @Test
    public void noParentheses() {
        assertEquals("a", this.getMarkerPositions("|a"));
    }

    @Test
    public void singlePairOfParenthesesBeforeFirst() {
        assertEquals("[(a)]", this.getMarkerPositions("|(a)"));
    }

    // Test helper for building string with marker position indicated
    // using '[' and ']' symbols
    // "|(A)" -> "[(A)]"
    private String getMarkerPositions(String expression) {
        final int position = expression.indexOf('|');
        String updatedExpression = expression.replaceAll("[\\|]", "");
        try {
            this.doc.replace(0, this.doc.getLength(), updatedExpression, null);
        } catch (BadLocationException e) {
            throw new RuntimeException("Error updating document text", e);
        }

        List<Token> pairTokens = this.pairsMarker.getPairTokens(doc, position);
        return this.markTokenPositions(updatedExpression, pairTokens);
    }

    // generate string for comparison by inserting token markers
    private String markTokenPositions(String expression, List<Token> tokens) {
        Iterator<Token> it = tokens.iterator();
        StringBuilder sb = new StringBuilder();
        if (it.hasNext()) {
            Token firstToken = it.next();
            if (firstToken.start != 0) {
                sb.append(expression.substring(0, firstToken.start));
            }
            sb.append("[");
            if (it.hasNext()) {
                Token secondToken = it.next();
                sb.append(expression.substring(firstToken.start, secondToken.start + 1));
                sb.append("]");
                if (secondToken.start != expression.length()) {
                    sb.append(expression.substring(secondToken.start + 1));
                }
            } else {
                sb.append(expression.substring(firstToken.start));
            }
        } else {
            sb.append(expression);
        }

        return sb.toString();
    }

    @Test
    public void singlePairOfParenthesesAfterFirst() {
        assertEquals("(a)", this.getMarkerPositions("(|a)"));
    }

    @Test
    public void singlePairOfParenthesesBeforeSecond() {
        assertEquals("[(a)]", this.getMarkerPositions("(a|)"));
    }

    @Test
    public void singlePairOfParenthesesAfterSecond() {
        assertEquals("[(a)]", this.getMarkerPositions("(a)|"));
    }

    @Test
    public void twoPairsOfParenthesesBeforeFirst() {
        assertEquals("([(a)])", this.getMarkerPositions("(|(a))"));
    }

    @Test
    public void twoPairsOfParenthesesAfterFirst() {
        assertEquals("((a))", this.getMarkerPositions("((|a))"));
    }

    @Test
    public void nonMatchingParenthesesAfterSecond() {
        assertEquals("([(a)], b))", this.getMarkerPositions("((a)|, b))"));
    }

    @Test
    public void nonMatchingParenthesesCursor() {
        assertEquals("((a) , b))", this.getMarkerPositions("((a) |, b))"));
    }

}