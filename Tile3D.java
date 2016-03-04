package src;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.Box;


/**
 * Represents a tile that will be drawn on a 3D canvas. Extends the abstract
 * tile class to account for 3-D dimensional rendering and animation.
 * 
 * @author Sandeep Raghunandhan
 * @version May 4, 2014 - June 2, 2014
 * @author Period: 2
 * @author Assignment: FinalProjectCS
 * 
 * @author Sources: http://lslwiki.net/lslwiki/wakka.php?wakka=color 
 * <== tool to determine floating point color code values for "pretty" colors
 * 
 */
public class Tile3D extends Tile
{
    // Number is a protected variable in Tile.

    /**
     * It's current color
     */
    private Color3f myColorf;

    /**
     * 
     */
    private Color3f[] colorsf = new Color3f[colors.length];

    /**
     * 
     */
    private Point3f point = new Point3f();

    /**
     * 
     */
    private float x;

    /**
     * 
     */
    private float y;

    /**
     * 
     */
    private float z;

    // To move tiles around
    // public static TransformGroup tg_Global = new TransformGroup();
    // public static ArrayList<TransformGroup> tg_Global = new
    // ArrayList<TransformGroup>();
    public static TransformGroup[][][] tg_Global = new TransformGroup[4][4][4];

    public static int tileCount = 0;


    // For animation purposes

    /**
     * Constructs a white tile with the number 2
     */
    public Tile3D()
    {
        num = 0;
        myColorf = new Color3f( 0f, 0f, 0f );
        x = 0;
        y = 0;
        z = 0;
        point = new Point3f( x, y, z );
    }


    /**
     * @param x
     * @param y
     * @param z
     */
    public Tile3D( float x, float y, float z )
    {
        num = 0;
        myColorf = new Color3f( 0f, 0f, 0f );
        this.x = x;
        this.y = y;
        this.z = z;
        point = new Point3f( x, y, z );
    }


    /**
     * Copy constructor
     * 
     * @param tile
     */
    public Tile3D( Tile3D tile )
    {
        // Don't copy the point, cause that's handled else where
        point = tile.getPoint();
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        num = tile.getNum();
        myColorf = tile.getColor();
    }


    /**
     * Create Blank templates for transform groups to be animated
     */
    private void initTransformArray()
    {
        for ( int x = 0; x < 4; x++ )
        {
            for ( int y = 0; y < 4; y++ )
            {
                for ( int z = 0; z < 4; z++ )
                {
                    tg_Global[x][y][z] = new TransformGroup();
                    tg_Global[x][y][z].setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
                    tg_Global[x][y][z].setCapability( TransformGroup.ALLOW_CHILDREN_EXTEND );
                    tg_Global[x][y][z].setCapability( TransformGroup.ALLOW_CHILDREN_WRITE );
                }
            }
        }
    }


    /**
     * Constructs a tile with a specified number and color
     * 
     * @param n
     *            - a number
     * @param c
     *            - a color
     */
    public Tile3D( int n )
    {
        createColors();
        num = n;
        // Index into the array of colors and pull out the correct one for a
        // given of number in the form 2^x
        myColorf = colorsf[indexIn( colorsf )];
    }


    /**
     * Converts all the colors in the array "colors" of the Tile abstract class
     * to Color3f objects for compatibility with Java3d.
     */
    private void createColors()
    {
        int i = 0;
        for ( Color c : colors )
        {
            float r = c.getRed() / 255.0f;
            float g = c.getGreen() / 255.0f;
            float b = c.getBlue() / 255.0f;
            Color3f color = new Color3f( r, g, b );
            colorsf[i] = color;
            i++;
        }
    }


    public static TransformGroup[][][] getTransformArray()
    {
        return tg_Global;
    }


    /**
     * Returns the current color
     * 
     * @return color - current color
     */
    public Color3f getColor()
    {
        return myColorf;
    }


    /**
     * TODO Write your method description here.
     * 
     * @return
     */
    public Point3f getPoint()
    {
        return new Point3f( x, y, z );
    }


//    public void remove()
//    {
//        tg_Global[pointToIndex( x )][pointToIndex( y )][pointToIndex( z )] = new TransformGroup();
//    }


    /**
     * Mutator method that changes the point around which the tile is centered
     * (Only in the background data, not visually)
     * 
     * @param newPoint - the point you want to change the current point to
     */
    public void setPoint( Point3f newPoint )
    {
        point = newPoint;
        x = newPoint.x;
        y = newPoint.y;
        z = newPoint.z;
    }
    

    /**
     * Sets the color to a specified value
     * 
     * @param c
     *            - a color
     */
    private void setColor( Color3f c )
    {
        myColorf = c;
    }


    /**
     * Given an integer input, returns a branchgroup to draw the tile in a new
     * location.
     * 
     * @param n
     *            - the direction to move 1 = UP, 2 = DOWN, 3 = LEFT, 4 = RIGHT,
     *            5 = IN, 6 = OUT
     * @param mag
     *            - the magnitude
     * @return BranchGroup with moved tiles drawn in
     */
    public BranchGroup move( int n, int mag )
    {
        int x1 = pointToIndex(x);
        int y1 = pointToIndex(y);
        int z1 = pointToIndex(z);
        BranchGroup bg = new BranchGroup();
        bg.setCapability( BranchGroup.ALLOW_DETACH );
        // // TransformGroup tg = tg_Global;
        BoundingSphere bounds = new BoundingSphere( new Point3d( x, y, z ),
            1.0d );
        // Transform3D subTrans = new Transform3D();
        // subTrans.setTranslation( new Vector3f(x,y,z) );
        // // tg.setTransform( subTrans );
        // tg_Global[pointToIndex(x)][pointToIndex(y)][pointToIndex(z)].setTransform(
        // subTrans );
        Transform3D transform = new Transform3D();

        switch ( n )
        {
            case 1:
                if ( y >= 0.25f )
                {
                    return bg;
                }
                transform.rotZ( Math.PI / 2 );
                break;
            case 2:
                // point.add( new Point3f(0f,-0.25f,0f) );
                if ( y <= -0.5f )
                {
                    return bg;
                }
                transform.rotZ( -Math.PI / 2 );
                break;
            case 3:
                // point.add( new Point3f(-0.25f,0f,0f) );
                if ( x <= -0.5f )
                {
                    return bg;
                }
                transform.rotY( Math.PI );
                break;
            case 4:
                // point.add( new Point3f(0.25f,0f,0f) );
                if ( x >= 0.25f )
                {
                    return bg;
                }
                break; // No change needed
            case 5:
                // point.add( new Point3f(0f,0f,-0.25f) );
                if ( z <= -0.5f )
                {
                    return bg;
                }
                transform.rotY( Math.PI / 2 );
                break;
            case 6:
                // point.add( new Point3f(0f,0f,0.25f) );
                if ( z >= 0.25f )
                {
                    return bg;
                }
                transform.rotY( -Math.PI / 2 );
                break;
            default:
                break; // No change if some other parameter
        }
        Alpha alpha = new Alpha( 1, 2000 );
        PositionInterpolator inter = new PositionInterpolator( alpha,
            tg_Global[x1][y1][z1],
            transform,
            0.0f,
            mag * 0.25f );
        inter.setSchedulingBounds( bounds );
        tg_Global[x1][y1][z1].addChild( inter );
        bg.addChild( tg_Global[x1][y1][z1] );
        return bg;

    }


    public static int pointToIndex( float n )
    {
        return (int)( n / 0.25f + 2 );
    }


    /**
     * Given the x,y,z around which to center the tile, this method will add the
     * tile to a given branch group, which will be utilized by the graphics
     * class to actually draw the tile on the 3D-canvas.
     * 
     * @param x
     *            - x-coordinate of center
     * @param y
     *            - y-coordinate of center
     * @param z
     *            - z-coordinate of center
     * @param branch
     *            - a given branch group to which to add the tile.
     */
    public TransformGroup drawTile( BranchGroup branch )
    {
        if ( tileCount == 0 )
        {
            initTransformArray();
        }
        TransformGroup Tile = new TransformGroup();
        // Only draw in positive numbers 2 or greater
        if ( num > 1 )
        {
            Tile.addChild( drawNumber( x, y, z, branch ).cloneTree() );
        }
        Appearance appear = new Appearance();
        if ( num > 2048 || num < 2 )
        {
            TransparencyAttributes trans = new TransparencyAttributes( TransparencyAttributes.NICEST,
                0.6f );
            appear.setTransparencyAttributes( trans );
        }
        Material mat = new Material();
        changeColor( mat, myColorf );
        appear.setMaterial( mat );
        Box box = new Box( 0.1f, 0.1f, 0.005f, appear );
        box.setAppearance( appear );
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();
        Vector3f vector = new Vector3f( x, y, z );
        transform.setTranslation( vector );
        tg.setTransform( transform );
        tg.addChild( box );
        branch.addChild( tg );
        Tile.addChild( tg.cloneTree() );
        // NO NON-NUMERICAL TILES ALLOWED
        if ( num > 1 )
        {
            tg_Global[pointToIndex( x )][pointToIndex( y )][pointToIndex( z )].addChild( Tile );
        }
        tileCount++;
        return Tile;

    }


    /**
     * Helper method to draw the number in the box.
     * 
     * @param x
     *            - x-coordinate to center the number around
     * @param y
     *            - y-coordinate to center the number around
     * @param z
     *            - z-coordinate to center the number around
     * @param branch
     *            - the given branch group passed from drawTile
     */
    private TransformGroup drawNumber(
        float x,
        float y,
        float z,
        BranchGroup branch )
    {

        Font3D font = new Font3D( new Font( Font.MONOSPACED, Font.BOLD, 1 ),
            new FontExtrusion() );
        Text3D number = new Text3D( font, "" + num );
        Shape3D text1 = new Shape3D();
        text1.addGeometry( number );
        LineAttributes lineAt = new LineAttributes();
        lineAt.setLineAntialiasingEnable( true );
        Appearance appear = new Appearance();
        appear.setLineAttributes( lineAt );
        Material mat = new Material();

        // Make the number black if on a tile with a 2 or 4, else make it white
        if ( num <= 4 && num > 0 )
        {
            changeColor( mat, new Color3f( 0f, 0f, 0f ) );
        }
        else
        {
            changeColor( mat, new Color3f( 1.0f, 1.0f, 1.0f ) );
        }
        appear.setMaterial( mat );
        text1.setAppearance( appear );
        float length = ( num + "" ).length();
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();
        // Vector calibrated for alignment
        Vector3f vector = new Vector3f( x - 0.03f,
            y - 0.036f / length,
            z + 0.01f );
        transform.setTranslation( vector );
        transform.setScale( 0.12 / (double)( length ) );
        tg.setTransform( transform );
        tg.addChild( text1 );
        branch.addChild( tg );
        return tg;
    }


    // private void checkAlign()
    // {
    // // Uncomment to check alignment
    // // Box line = new Box(5f, 0.005f,0.005f, appear);
    // // Box line2 = new Box (0.005f , 5f ,0.005f, appear);
    // // Box line3 = new Box (0.005f , 0.005f ,5f, appear);
    // // Uncommment to check alignment
    // // tg.addChild( line );
    // // tg.addChild( line2 );
    // // tg.addChild(line3);
    // }

    /**
     * Used to change the color of a cube when it is moved or initialized.
     * 
     * @param mat
     * @param color
     */
    public void changeColor( Material mat, Color3f color )
    {
        mat.setAmbientColor( color );
        mat.setSpecularColor( color );
        mat.setEmissiveColor( color );
        mat.setDiffuseColor( color );
    }


    /**
     * Prints out the tile number and location
     * 
     * @return the string representation of a tile
     */
    public String toString()
    {
        return "Tile:  " + num + " at " + point;
    }

}
