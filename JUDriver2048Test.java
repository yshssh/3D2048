package src;

import static org.junit.Assert.*;

import org.junit.Test;

public class JUDriver2048Test
{
    @Test
    public void testWindow()
    {
        Driver2048 driver = new Driver2048();
        assertNotNull("<<Construction Failed>>", driver);
    }
}
