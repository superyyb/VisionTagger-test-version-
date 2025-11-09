package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Label model class.
 */
public class LabelTest {

    private Label label;
    private String name;
    private double confidence;

    @BeforeEach
    void setUp() {
        name = "Otter";
        confidence = 96.3;
        label = new Label(name, confidence);
    }

    @Test
    void testConstructor() {
        assertNotNull(label);
        assertEquals(name, label.getName());
        assertEquals(confidence, label.getConfidence(), 0.001);
    }

    @Test
    void testConstructorWithMarginConfidence() {
        Label lbl1 = new Label("Object", 0.0);
        assertEquals(0.0, lbl1.getConfidence(), 0.001);
    
        Label lbl2 = new Label("Object", 100.0);
        assertEquals(100.0, lbl2.getConfidence(), 0.001);
    }

    @Test
    void testGetters() {
        assertEquals(name, label.getName());
        assertEquals(confidence, label.getConfidence(), 0.001);
    }

    @Test
    void testEquals() {
        Label label1 = new Label(name, confidence);
        Label label2 = new Label(name, confidence);
        assertEquals(label1, label2);
        assertEquals(label1.hashCode(), label2.hashCode());
    }

    @Test
    void testNotEqualsWithDifferentName() {
        Label label1 = new Label("Cat", confidence);
        Label label2 = new Label("Car", confidence);
        assertNotEquals(label1, label2);
        assertNotEquals(label1.hashCode(), label2.hashCode());
    }

    @Test
    void testEqualsWithDifferentConfidence() {
        Label label1 = new Label(name, 98.8);
        Label label2 = new Label(name, 90.0);
        assertNotEquals(label1, label2);
        assertNotEquals(label1.hashCode(), label2.hashCode());
    }

    @Test
    void testEqualsWithInvalidObject() {
        assertFalse(label.equals(null));
        assertFalse(label.equals("not a label"));
    }

    @Test
    void testToString() {
        String result = label.toString();
        assertEquals(result, name + " (" + confidence + "%)");
    }
}
