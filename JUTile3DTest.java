package src;

import static org.junit.Assert.*;

import java.util.Random;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.junit.Test;

public class JUTile3DTest
{
    @Test
    public void Tile3DConstructor()
    {
        Tile3D tile = new Tile3D();
        assertNotNull("<<Construction Failed", tile);
        assertEquals("Number init failed" , tile.getNum(), 0);
        assertEquals("Color init failed", tile.getColor(), new Color3f(0.0f, 0.0f, 0.0f));
        
        for (int i = 0; i <= 12; i++)
        {
            Tile3D tile1 = new Tile3D((int)(Math.pow( 2.0, i )));
            assertNotNull("Construction Failed", tile1);
            assertEquals("Number init failed", tile1.getNum(), (int)(Math.pow( 2.0, i )));
            assertNotNull("Color init failed", tile1.getColor());
        }
        
        Random rand = new Random();
        int x = rand.nextInt();
        int y = rand.nextInt();
        int z = rand.nextInt();
        Tile3D tile2 = new Tile3D(x,y,z);
        assertNotNull("Construction Failed", tile2);
        assertEquals("Number init failed", tile2.getNum(), 0);
        assertEquals("Color init failed", tile2.getColor(), new Color3f(0.0f, 0.0f, 0.0f));
        assertEquals("Point init failed", tile2.getPoint(), new Point3f(x,y,z));
        
        Tile3D tile3 = new Tile3D(tile2);
        //Make sure they aren't the same object in memory
        assertNotNull("<<Construction Failed>>", tile3);
        assertNotSame(tile3, tile2);
        assertEquals("Number init failed", tile2.getNum(), tile3.getNum());
        assertEquals("Color init failed", tile2.getColor(), tile3.getColor());
        assertEquals("Point init failed", tile2.getPoint(), tile3.getPoint());
        
    }
    
    @Test
    public void testGetTransformArray()
    {
        Tile3D tile = new Tile3D();
        assertNotNull("Transformed Array null", tile.getTransformArray());       
    }
    
    @Test
    public void testSetPoint()
    {
        Tile3D tile = new Tile3D();
       // tile.setPoint( new Point3f );
    }
    
    @Test
    public void testMove()
    {
        Tile3D tile = new Tile3D();
        
        
    }
    
    @Test
    public void testDrawTile()
    {
        Tile3D tile = new Tile3D();
        tile.setPoint( new Point3f(0.25f, 0.25f, 0.25f) );
        tile.drawTile(new BranchGroup());
        int n = tile.pointToIndex(0.25f);
        
    }
    

}
