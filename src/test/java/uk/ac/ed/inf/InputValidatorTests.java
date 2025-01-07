package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.dataTypes.*;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTests {

    InputValidator validator = new InputValidator();

    //validator tests
    @Test
    public void nullStringTest(){
        String test = null;

        boolean result = validator.inputStringValidator(test);
        assertTrue(result);
    }

    @Test
    public void emptyStringTest(){
        String test = "";

        boolean result = validator.inputStringValidator(test);
        assertTrue(result);
    }

    @Test
    public void validStringTest(){
        String test = "test";

        boolean result = validator.inputStringValidator(test);
        assertFalse(result);
    }

    @Test
    public void bothNull_LongLatTest(){
        LongLat test = new LongLat();

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void lngNull_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(45.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void latNull_LongLatTest(){
        LongLat test = new LongLat();
        test.setLng(45.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void latUpperBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(99.9);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void latLowerBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(-99.9);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void lngUpperBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(187.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void lngLowerBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(-190.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertTrue(result);
    }

    @Test
    public void latExactUpperBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(90.0);
        test.setLng(45.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertFalse(result);
    }

    @Test
    public void latExactLowerBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(-90.0);
        test.setLng(45.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertFalse(result);
    }

    @Test
    public void lngExactUpperBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(45.0);
        test.setLng(180.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertFalse(result);
    }

    @Test
    public void lngExactLowerBound_LongLatTest(){
        LongLat test = new LongLat();
        test.setLat(45.0);
        test.setLng(-180.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertFalse(result);
    }

    @Test
    public void validLongLatTest(){
        LongLat test = new LongLat();
        test.setLng(45.0);
        test.setLat(45.0);

        boolean result = validator.longLatValidator(test.getLng(), test.getLat());
        assertFalse(result);
    }
}