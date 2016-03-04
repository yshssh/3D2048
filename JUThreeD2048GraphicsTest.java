package src;

import static org.junit.Assert.*;

import org.junit.Test;

public class JUThreeD2048GraphicsTest
{
    @Test
    public void ThreeD2048GraphicsConstructor()
    {
        ThreeD2048Graphics threeD = new ThreeD2048Graphics();
        assertNotNull(threeD);
    }
    @Test
    public void checkWin()
    {
        ThreeD2048Graphics threeD = new ThreeD2048Graphics();
        assertFalse("You prematurely won",threeD.checkWin());
    }
}
