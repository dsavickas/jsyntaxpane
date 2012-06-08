package jsyntaxpane;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import jsyntaxpane.util.Configuration;

/**
 * Minimal syntax-highlighting {@link EditorKit}.
 * 
 * E.g.
 * JEditorPane codeEditor = new JEditorPane();
 * codeEditor.setEditorKitForContentType("text/java", new MinimalSyntaxEditorKit(new JavaLexer()));
 * 
 * @author domas
 *
 */
public class MinimalSyntaxEditorKit extends DefaultEditorKit implements ViewFactory {
    private final Lexer lexer;
    private final Configuration configuration;

    public MinimalSyntaxEditorKit(Lexer lexer) {
        this(lexer, createDefaultConfiguration());
    }
    
    public MinimalSyntaxEditorKit(Lexer lexer, Configuration configuration) {
        super();
        if (lexer == null) {
            throw new IllegalArgumentException("lexer cannot be null");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }
        this.lexer = lexer;
        this.configuration = configuration; 
    }
    
    @Override
    public Document createDefaultDocument() {
        return new SyntaxDocument(this.lexer);
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    @Override
    public View create(Element element) {
        return new SyntaxView(element, this.configuration);
    }

    private static Configuration createDefaultConfiguration() {
        Configuration configuration = new Configuration(MinimalSyntaxEditorKit.class);
        Map<String, String> values = new HashMap<String, String>();
        values.put("Style.OPERATOR", "0x000000, 0");
        values.put("Style.DELIMITER", "0x000000, 1");
        values.put("Style.KEYWORD", "0x3333ee, 0");
        values.put("Style.KEYWORD2", "0x3333ee, 3");
        values.put("Style.TYPE", "0x000000, 2");
        values.put("Style.TYPE2", "0x000000, 1");
        values.put("Style.TYPE3", "0x000000, 3");
        values.put("Style.STRING", "0xcc6600, 0");
        values.put("Style.STRING2", "0xcc6600, 1");
        values.put("Style.NUMBER", "0x999933, 1");
        values.put("Style.REGEX", "0xcc6600, 0");
        values.put("Style.IDENTIFIER", "0x000000, 0");
        values.put("Style.COMMENT", "0x339933, 2");
        values.put("Style.COMMENT2", "0x339933, 3");
        values.put("Style.DEFAULT", "0x000000, 0");
        values.put("Style.WARNING", "0xCC0000, 0");
        values.put("Style.ERROR", "0xCC0000, 3");
        configuration.putAll(values);
        
        return configuration;
    }
    
}
