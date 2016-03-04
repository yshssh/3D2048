package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
// import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * The graphics rendering, animation, and user interaction for the 3D version of
 * 2048. Handles collisions of tiles in 3-D space. 
 * 
 * @author Sandeep Raghunandhan
 * @version May 18, 2014 - June 2, 2014
 * @author Period: 2
 * @author Assignment: FinalProjectCS
 * 
 * @author Sources: http://www.java3d.org/ <--Has example code and tutorials for
 *         text and shapes in 3D.
 *         http://www.developer.com/java/data/article.php/
 *         3706721/Simple-Animation-with-the-Java-3D-API.htm
 *         http://www.macs.hw.ac
 *         .uk/~nkt/graphics/B%20Java3D%20Animation%20Slides.pdf
 *         http://www.tecgraf.puc-rio.br/~ismael/Cursos/Cidade_CG/labs/Java3D/
 *         Java3D_onlinebook_selman/Htmls/3DJava_Ch03.htm <- Slides for learning
 *         3D programming
 */
@SuppressWarnings("serial")
public class ThreeD2048Graphics extends JPanel
{
    /**
     * Needed for setting up background, lighting, and orbit controls.
     */
    BoundingSphere bounds = new BoundingSphere( new Point3d(), 100.0 );

    /**
     * Used to set up a canvas
     */
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    /**
     * Is what it sounds like. Can be added to a JPanel.
     */
    Canvas3D canvas = new Canvas3D( config );

    /**
     * The universe holds a canvas on which everything will be drawn
     */
    SimpleUniverse su = new SimpleUniverse( canvas );

    /**
     * Used to store shapes and objects to be added to the universe/canvas.
     */
    BranchGroup bg = new BranchGroup();

    private int score;

    private final int UP = 1;

    private final int DOWN = 2;

    private final int LEFT = 3;

    private final int RIGHT = 4;

    private final int IN = 5;

    private final int OUT = 6;

    BranchGroup[][][] graphicTiles = new BranchGroup[4][4][4];

    private Tile3D[][][] activeTiles;


    /**
     * Sets up the canvas to a defined size, sets the background to the 2048
     * theme color of orange.
     */
    public void init()
    {
        canvas.setSize( 600, 600 );
        add( "3d-2048", canvas );
        score = 0;
        Background background = new Background( new Color3f( 1.0f, 0.7f, 0.2f ) );
        background.setApplicationBounds( bounds );
        bg.setCapability( BranchGroup.ALLOW_CHILDREN_READ );
        bg.setCapability( BranchGroup.ALLOW_CHILDREN_WRITE );
        bg.setCapability( BranchGroup.ALLOW_CHILDREN_EXTEND );
        bg.setCapability( BranchGroup.ALLOW_DETACH );
        for ( int i = 0; i < graphicTiles.length; i++ )
        {
            for ( int j = 0; j < graphicTiles.length; j++ )
            {
                for ( int k = 0; k < graphicTiles.length; k++ )
                {
                    graphicTiles[i][j][k] = new BranchGroup();
                    BranchGroup gT = graphicTiles[i][j][k];
                    gT.setCapability( BranchGroup.ALLOW_DETACH );
                    gT.setCapability( BranchGroup.ALLOW_CHILDREN_READ );
                    gT.setCapability( BranchGroup.ALLOW_CHILDREN_WRITE );
                    gT.setCapability( BranchGroup.ALLOW_CHILDREN_EXTEND );
                }

            }

        }
        bg.addChild( background );
        // bg.compile();
        activeTiles = new Tile3D[4][4][4];
    }


    /**
     * Constructor to set up screen for initial board set up.
     */
    public ThreeD2048Graphics()
    {
        init();
        setLighting();
        addOrbit( canvas );
        su.getViewingPlatform().setNominalViewingTransform();
        su.addBranchGraph( bg );
        startGame();
        setUpMovement();
    }


    public void winMessage()
    {
        // Write the Game Over Message
        BranchGroup branch = new BranchGroup();
        Font3D font = new Font3D( new Font( Font.SANS_SERIF, Font.BOLD, 1 ),
            new FontExtrusion() );
        Text3D game = new Text3D( font,
            "You Win!" );
        Shape3D endText = new Shape3D( game );
        Appearance appear = new Appearance();
        Material mat = new Material( new Color3f( 1.0f, 0.6f, 0.0f ),
            new Color3f( 1.0f, 0.6f, 0.0f ),
            new Color3f( 1.0f, 0.6f, 0.0f ),
            new Color3f( 1.0f, 0.6f, 0.0f ),
            60 );
        appear.setMaterial( mat );
        endText.setAppearance( appear );
        TransformGroup trans = new TransformGroup();
        trans.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        Transform3D transform = new Transform3D();
        transform.setTranslation( new Vector3f( -0.75f, 0.0f, 0.50f ) );
        transform.setScale( 0.1 );
        trans.setTransform( transform );
        trans.addChild( endText );

        // Animation code
        Alpha alpha = new Alpha( 1, 5000 );
        TransformGroup rotator = new TransformGroup();
        rotator.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        rotator.addChild( trans );
        ScaleInterpolator rotate = new ScaleInterpolator( alpha, rotator );
        rotate.setSchedulingBounds( bounds );
        rotator.addChild( rotate );
        branch.addChild( rotator );
        su.addBranchGraph( branch );
    }
    private void gameOver()
    {
        // Write the Game Over Message
        BranchGroup branch = new BranchGroup();
        Font3D font = new Font3D( new Font( Font.SANS_SERIF, Font.BOLD, 1 ),
            new FontExtrusion() );
        Text3D game = new Text3D( font, "Game Over!" );
        Shape3D endText = new Shape3D( game );
        Appearance appear = new Appearance();
        Material mat = new Material( new Color3f( 0.6f, 0.6f, 0.6f ),
            new Color3f( 0.6f, 0.6f, 0.6f ),
            new Color3f( 0.6f, 0.6f, 0.6f ),
            new Color3f( 0.6f, 0.6f, 0.6f ),
            60 );
        appear.setMaterial( mat );
        endText.setAppearance( appear );
        TransformGroup trans = new TransformGroup();
        trans.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        Transform3D transform = new Transform3D();
        transform.setTranslation( new Vector3f( -0.75f, 0.0f, 0.50f ) );
        transform.setScale( 0.1 );
        trans.setTransform( transform );
        trans.addChild( endText );

        // Animation code
        Alpha alpha = new Alpha( -1, 5000 );
        TransformGroup rotator = new TransformGroup();
        rotator.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        rotator.addChild( trans );
        ScaleInterpolator rotate = new ScaleInterpolator( alpha, rotator );
        rotate.setSchedulingBounds( bounds );
        rotator.addChild( rotate );
        branch.addChild( rotator );
        su.addBranchGraph( branch );
    }

    private int numToN( int num )
    {
        return (int)( Math.log( num ) / Math.log( 2 ) );
    }


    private int calcPoints( int num )
    {
        return ( numToN( num ) - 1 ) * num;
    }


    private void setUpMovement()
    {
        InputMap inputMap = getInputMap();
        ActionMap actionMap = getActionMap();

        inputMap.put( KeyStroke.getKeyStroke( "UP" ), "moveUp" );
        actionMap.put( "moveUp", new Up() );

        inputMap.put( KeyStroke.getKeyStroke( "DOWN" ), "moveDown" );
        actionMap.put( "moveDown", new Down() );

        inputMap.put( KeyStroke.getKeyStroke( "LEFT" ), "moveLeft" );
        actionMap.put( "moveLeft", new Left() );

        inputMap.put( KeyStroke.getKeyStroke( "RIGHT" ), "moveRight" );
        actionMap.put( "moveRight", new Right() );

        inputMap.put( KeyStroke.getKeyStroke( "Q" ), "moveIn" );
        actionMap.put( "moveIn", new In() );

        inputMap.put( KeyStroke.getKeyStroke( "E" ), "moveOut" );
        actionMap.put( "moveOut", new Out() );
    }


    public void printActiveTiles()
    {
        for ( int x = 0; x < activeTiles.length; x++ )
        {
            for ( int y = 0; y < activeTiles.length; y++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    if ( activeTiles[x][y][z] != null )
                    {
                        System.out.println( activeTiles[x][y][z] );
                    }
                }
            }
        }
    }


    public boolean checkWin()
    {
        for ( int x = 0; x < activeTiles.length; x++ )
        {
            for ( int y = 0; y < activeTiles.length; y++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    if ( activeTiles[x][y][z] != null )
                    {
                        int num = activeTiles[x][y][z].getNum();
                        if ( num == 2048 )
                        {
                            System.out.println( "YOU WON! Congratulations" );
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private void removeTile( int x, int y, int z )
    {
        Tile3D.getTransformArray()[x][y][z] = new TransformGroup();
        graphicTiles[x][y][z].detach();
        graphicTiles[x][y][z].removeAllChildren();
        activeTiles[x][y][z] = null;
        su.addBranchGraph( graphicTiles[x][y][z] );
    }


    /**
     * Give my the reference to the tile to be moved. Tell me where I am in the
     * array and where I need to go. Only for rectalinear movement with no
     * collisions. Displays changes graphically.
     * 
     * @param tile
     *            - a reference to a Tile3D object
     * @param x
     *            - its x position in the array
     * @param y
     *            - its y position in the array
     * @param z
     *            - its z position in the array
     * @param x1
     *            - x-coordinate of where on the array to move it to
     * @param y1
     *            - y-coordinate of where on the array to move it to
     * @param z1
     *            - z-coordinate of where on the array to move it to
     */
    public void moveTile(
        Tile3D tile,
        int x,
        int y,
        int z,
        int x1,
        int y1,
        int z1 )
    {
        int diffX = x1 - x;
        int diffY = y1 - y;
        int diffZ = z1 - z;
        // 1 = up, 2 = down, 3 = left, 4 = right, 5 = in, 6 = out
        int direction = -1;
        int magnitude = 0;
        if ( diffY == 0 && diffZ == 0 )
        {
            if ( diffX > 0 )
            {
                direction = RIGHT;
            }
            else if ( diffX < 0 )
            {
                direction = LEFT;
            }
            magnitude = Math.abs( diffX );
        }
        else if ( diffX == 0 && diffZ == 0 )
        {
            if ( diffY > 0 )
            {
                direction = UP;
            }
            else if ( diffY < 0 )
            {
                direction = DOWN;
            }
            magnitude = Math.abs( diffY );
        }
        else if ( diffX == 0 && diffY == 0 )
        {
            if ( diffZ > 0 )
            {
                direction = OUT;
            }
            else if ( diffZ < 0 )
            {
                direction = IN;
            }
            magnitude = Math.abs( diffZ );
        }
        if ( magnitude == 0 )
        {
            // don't do anything for no movement
            return;
        }
        Point3f point = new Point3f( indexToPoint( x1 ),
            indexToPoint( y1 ),
            indexToPoint( z1 ) );
        BranchGroup temp = tile.move( direction, magnitude );
        su.addBranchGraph( temp );
        removeTile( x, y, z );
        BranchGroup temp2 = new BranchGroup();
        activeTiles[x + diffX][y + diffY][z + diffZ] = new Tile3D( tile );
        activeTiles[x + diffX][y + diffY][z + diffZ].setPoint( point );
        temp.detach();
        activeTiles[x + diffX][y + diffY][z + diffZ].drawTile( temp2 );
        graphicTiles[x + diffX][y + diffY][z + diffZ].detach();
        graphicTiles[x + diffX][y + diffY][z + diffZ].addChild( temp2 );
        su.addBranchGraph( graphicTiles[x + diffX][y + diffY][z + diffZ] );
        activeTiles[x][y][z] = null;
    }


    /**
     * Handles combining two tiles into one and doubling the number.
     * Precondition: Assumes tile1 and tile2 contain the same number and lie on
     * a common axis consistent with the direction of collision.
     * 
     * @param tile1
     * @param tile2
     * @param p1
     * @param p2
     * @param directionOfCollision
     *            (ie: UP, DOWN, LEFT, RIGHT, IN, OUT)
     */
    public void twoTileCollisionHandler(
        Tile3D tile1,
        Tile3D tile2,
        Point3i p1,
        Point3i p2,
        int directionOfCollision )
    {
        int x1 = p1.x;
        int y1 = p1.y;
        int z1 = p1.z;
        int x2 = p2.x;
        int y2 = p2.y;
        int z2 = p2.z;

        int num = tile1.getNum();
        Tile3D tile = new Tile3D( num * 2 );
        score += calcPoints( num * 2 );
        BranchGroup branch = new BranchGroup();
        if ( directionOfCollision == UP )
        {
            tile.setPoint( new Point3f( indexToPoint( x2 ),
                indexToPoint( 3 ),
                indexToPoint( z2 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, x2, 3, z2 );
            removeTile( x2, 3, z2 );
            tile.drawTile( branch );
            activeTiles[x2][3][z2] = tile;
            graphicTiles[x2][3][z2].detach();
            graphicTiles[x2][3][z2].addChild( branch );
            su.addBranchGraph( graphicTiles[x2][3][z2] );
        }
        else if ( directionOfCollision == DOWN )
        {
            tile.setPoint( new Point3f( indexToPoint( x2 ),
                indexToPoint( 0 ),
                indexToPoint( z2 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, x2, 0, z2 );
            removeTile( x2, 0, z2 );
            tile.drawTile( branch );
            activeTiles[x2][0][z2] = tile;
            graphicTiles[x2][0][z2].detach();
            graphicTiles[x2][0][z2].addChild( branch );
            su.addBranchGraph( graphicTiles[x2][0][z2] );
        }
        else if ( directionOfCollision == LEFT )
        {
            tile.setPoint( new Point3f( indexToPoint( 0 ),
                indexToPoint( y2 ),
                indexToPoint( z2 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, 0, y2, z2 );
            removeTile( 0, y2, z2 );
            tile.drawTile( branch );
            activeTiles[0][y2][z2] = tile;
            graphicTiles[0][y2][z2].detach();
            graphicTiles[0][y2][z2].addChild( branch );
            su.addBranchGraph( graphicTiles[0][y2][z2] );
        }
        else if ( directionOfCollision == RIGHT )
        {
            tile.setPoint( new Point3f( indexToPoint( 3 ),
                indexToPoint( y2 ),
                indexToPoint( z2 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, 3, y2, z2 );
            removeTile( 3, y2, z2 );
            tile.drawTile( branch );
            activeTiles[3][y2][z2] = tile;
            graphicTiles[3][y2][z2].detach();
            graphicTiles[3][y2][z2].addChild( branch );
            su.addBranchGraph( graphicTiles[3][y2][z2] );
        }
        else if ( directionOfCollision == IN )
        {
            tile.setPoint( new Point3f( indexToPoint( x2 ),
                indexToPoint( y2 ),
                indexToPoint( 0 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, x2, y2, 0 );
            removeTile( x2, y2, 0 );
            tile.drawTile( branch );
            activeTiles[x2][y2][0] = tile;
            graphicTiles[x2][y2][0].detach();
            graphicTiles[x2][y2][0].addChild( branch );
            su.addBranchGraph( graphicTiles[x2][y2][0] );
        }
        else if ( directionOfCollision == OUT )
        {
            tile.setPoint( new Point3f( indexToPoint( x2 ),
                indexToPoint( y2 ),
                indexToPoint( 3 ) ) );
            removeTile( x2, y2, z2 );
            moveTile( tile1, x1, y1, z1, x2, y2, 3 );
            removeTile( x2, y2, 3 );
            tile.drawTile( branch );
            activeTiles[x2][y2][3] = tile;
            graphicTiles[x2][y2][3].detach();
            graphicTiles[x2][y2][3].addChild( branch );
            su.addBranchGraph( graphicTiles[x2][y2][3] );
        }
    }


    public void threeTileCollisionHandler(
        Tile3D tile1,
        Tile3D tile2,
        Tile3D tile3,
        Point3i p1,
        Point3i p2,
        Point3i p3,
        int directionOfCollision )
    {
        int x1 = p1.x;
        int y1 = p1.y;
        int z1 = p1.z;

        int x2 = p2.x;
        int y2 = p2.y;
        int z2 = p2.z;

        int x3 = p3.x;
        int y3 = p3.y;
        int z3 = p3.z;

        int num1 = tile1.getNum();
        int num2 = tile2.getNum();
        int num3 = tile3.getNum();

        BranchGroup branch = new BranchGroup();

        if ( directionOfCollision == UP )
        {
            if ( num1 == num2 )
            {
                int num = tile1.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( 2 ),
                    indexToPoint( z1 ) ) );
                removeTile( x1, y1, z1 );
                moveTile( tile2, x2, y2, z2, x2, 2, z2 );
                removeTile( x2, 2, z2 );
                tile.drawTile( branch );
                activeTiles[x2][2][z2] = tile;
                graphicTiles[x2][2][z2].detach();
                graphicTiles[x2][2][z2].addChild( branch );
                su.addBranchGraph( graphicTiles[2][y2][z2] );
                moveTile( tile3, x3, y3, z3, x3, 3, z3 );
            }
            else if ( num2 == num3 )
            {
                twoTileCollisionHandler( tile2,
                    tile3,
                    new Point3i( x2, y2, z2 ),
                    new Point3i( x3, y3, z3 ),
                    UP );
                moveTile( tile1, x1, y1, z1, x1, 2, z1 );
            }
        }
        else if ( directionOfCollision == DOWN )
        {
            if ( num1 == num2 )
            {
                twoTileCollisionHandler( tile1,
                    tile2,
                    new Point3i( x1, y1, z1 ),
                    new Point3i( x2, y2, z2 ),
                    DOWN );
                moveTile( tile3, x3, y3, z3, x3, 1, z3 );
            }
            else if ( num2 == num3 )
            {
                int num = tile2.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( 1 ),
                    indexToPoint( z3 ) ) );
                removeTile( x3, y3, z3 );
                moveTile( tile1, x2, y2, z2, x2, 1, z2 );
                removeTile( x2, 1, z2 );
                tile.drawTile( branch );
                activeTiles[x2][1][z2] = tile;
                graphicTiles[x2][1][z2].detach();
                graphicTiles[x2][1][z2].addChild( branch );
                su.addBranchGraph( graphicTiles[x2][1][z2] );
                moveTile( tile1, x1, y1, z1, x1, 0, z1 );
            }
        }
        else if ( directionOfCollision == LEFT )
        {
            if ( num1 == num2 )
            {
                twoTileCollisionHandler( tile1,
                    tile2,
                    new Point3i( x1, y1, z1 ),
                    new Point3i( x2, y2, z2 ),
                    LEFT );
                moveTile( tile3, x3, y3, z3, 1, y3, z3 );
            }
            else if ( num2 == num3 )
            {
                int num = tile2.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( 1 ),
                    indexToPoint( y3 ),
                    indexToPoint( z3 ) ) );
                removeTile( x3, y3, z3 );
                moveTile( tile1, x2, y2, z2, 1, y2, z2 );
                removeTile( 1, y2, z2 );
                tile.drawTile( branch );
                activeTiles[1][y2][z2] = tile;
                graphicTiles[1][y2][z2].detach();
                graphicTiles[1][y2][z2].addChild( branch );
                su.addBranchGraph( graphicTiles[1][y2][z2] );
                moveTile( tile1, x1, y1, z1, 0, y1, z1 );
            }
        }
        else if ( directionOfCollision == RIGHT )
        {
            if ( num1 == num2 )
            {
                int num = tile1.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( 2 ),
                    indexToPoint( y1 ),
                    indexToPoint( z1 ) ) );
                removeTile( x1, y1, z1 );
                moveTile( tile2, x2, y2, z2, 2, y2, z2 );
                removeTile( 2, y2, z2 );
                tile.drawTile( branch );
                activeTiles[2][y2][z2] = tile;
                graphicTiles[2][y2][z2].detach();
                graphicTiles[2][y2][z2].addChild( branch );
                su.addBranchGraph( graphicTiles[2][y2][z2] );
                moveTile( tile3, x3, y3, z3, 3, y3, z3 );
            }
            else if ( num2 == num3 )
            {
                twoTileCollisionHandler( tile2,
                    tile3,
                    new Point3i( x2, y2, z2 ),
                    new Point3i( x3, y3, z3 ),
                    RIGHT );
                moveTile( tile1, x1, y1, z1, 2, y1, z1 );
            }
        }
        else if ( directionOfCollision == IN )
        {
            if ( num1 == num2 )
            {
                twoTileCollisionHandler( tile1,
                    tile2,
                    new Point3i( x1, y1, z1 ),
                    new Point3i( x2, y2, z2 ),
                    DOWN );
                moveTile( tile3, x3, y3, z3, x3, y3, 1 );
            }
            else if ( num2 == num3 )
            {
                int num = tile2.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( y3 ),
                    indexToPoint( 1 ) ) );
                removeTile( x3, y3, z3 );
                moveTile( tile1, x2, y2, z2, x2, y2, 1 );
                removeTile( x2, y2, 1 );
                tile.drawTile( branch );
                activeTiles[x2][y2][1] = tile;
                graphicTiles[x2][y2][1].detach();
                graphicTiles[x2][y2][1].addChild( branch );
                su.addBranchGraph( graphicTiles[x2][y2][1] );
                moveTile( tile1, x1, y1, z1, x1, y1, 0 );
            }
        }
        else if ( directionOfCollision == OUT )
        {
            if ( num1 == num2 )
            {
                int num = tile1.getNum();
                Tile3D tile = new Tile3D( num * 2 );
                score += calcPoints( num * 2 );
                tile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( y1 ),
                    indexToPoint( 2 ) ) );
                removeTile( x1, y1, z1 );
                moveTile( tile2, x2, y2, z2, x2, y2, 2 );
                removeTile( x2, y2, 2 );
                tile.drawTile( branch );
                activeTiles[x2][y2][2] = tile;
                graphicTiles[x2][y2][2].detach();
                graphicTiles[x2][y2][2].addChild( branch );
                su.addBranchGraph( graphicTiles[2][y2][z2] );
                moveTile( tile3, x3, y3, z3, x3, y3, 3 );
            }
            else if ( num2 == num3 )
            {
                twoTileCollisionHandler( tile2,
                    tile3,
                    new Point3i( x2, y2, z2 ),
                    new Point3i( x3, y3, z3 ),
                    UP );
                moveTile( tile1, x1, y1, z1, x1, y1, 2 );
            }
        }
    }


    public void fourTileCollisionHandler(
        Tile3D tile0,
        Tile3D tile1,
        Tile3D tile2,
        Tile3D tile3,
        Point3i p0,
        Point3i p1,
        Point3i p2,
        Point3i p3,
        int directionOfMovement )
    {
        // Made the numbers following the tiles the same as the array index for
        // easy comprehension
        int x0 = p0.x;
        int y0 = p0.y;
        int z0 = p0.z;

        int x1 = p1.x;
        int y1 = p1.y;
        int z1 = p1.z;

        int x2 = p2.x;
        int y2 = p2.y;
        int z2 = p2.z;

        int x3 = p3.x;
        int y3 = p3.y;
        int z3 = p3.z;

        int num0 = tile0.getNum();
        int num1 = tile1.getNum();
        int num2 = tile2.getNum();
        int num3 = tile3.getNum();

        if ( directionOfMovement == UP )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( x3, 3, z3 );
                moveTile( tile2, x2, y2, z2, x3, 3, z3 );
                removeTile( x3, 3, z3 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( 3 ),
                    indexToPoint( z3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x3][3][z3].detach();
                graphicTiles[x3][3][z3].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x3][3][z3] );
                activeTiles[x3][3][z3] = newTile;
                removeTile( x2, 2, z2 );
                moveTile( tile1, x1, y1, z1, x2, 2, z2 );
                removeTile( x2, 2, z2 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( x2 ),
                    indexToPoint( 2 ),
                    indexToPoint( z2 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[x2][2][z2].detach();
                graphicTiles[x2][2][z2].addChild( bg2 );
                su.addBranchGraph( graphicTiles[x2][2][z2] );
                activeTiles[x2][2][z2] = newTile1;
            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x1, 1, z1 );
                moveTile( tile0, x0, y0, z0, x1, 1, z1 );
                removeTile( x1, 1, z1 );
                Tile3D newTile = new Tile3D( num0 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( 1 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg );
                graphicTiles[x1][1][z1].detach();
                graphicTiles[x1][1][z1].addChild( bg );
                su.addBranchGraph( graphicTiles[x1][1][z1] );
                activeTiles[x1][1][z1] = newTile;
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x2, 2, z2 );
                moveTile( tile1, x1, y1, z1, x2, 2, z2 );
                removeTile( x2, 2, z2 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x2 ),
                    indexToPoint( 2 ),
                    indexToPoint( z2 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x2][2][z2].detach();
                graphicTiles[x2][2][z2].addChild( bg1 );
                activeTiles[x2][2][z2] = newTile;
                su.addBranchGraph( graphicTiles[x2][2][z2] );
                moveTile( tile0, x0, y0, z0, x1, 1, z1 );
            }
            else if ( num2 == num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x3, 3, z3 );
                moveTile( tile2, x2, y2, z2, x3, 3, z3 );
                removeTile( x3, 3, z3 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( 3 ),
                    indexToPoint( z3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x3][3][z3].detach();
                graphicTiles[x3][3][z3].addChild( bg1 );
                activeTiles[x3][3][z3] = newTile;
                moveTile( tile1, x1, y1, z1, x1, 2, z1 );
                moveTile( tile0, x0, y0, z0, x1, 1, z0 );
            }
        }
        else if ( directionOfMovement == DOWN )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( x0, 0, z0 );
                moveTile( tile1, x1, y1, z1, x0, 0, z0 );
                removeTile( x0, 0, z0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x0 ),
                    indexToPoint( 0 ),
                    indexToPoint( z0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x0][0][z0].detach();
                graphicTiles[x0][0][z0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x0][0][z0] );
                activeTiles[x0][0][z0] = newTile;
                removeTile( x2, 1, z2 );
                moveTile( tile2, x2, y2, z2, x2, 1, z2 );
                removeTile( x2, 1, z2 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( 1 ),
                    indexToPoint( z1 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[x1][1][z1].detach();
                graphicTiles[x1][1][z1].addChild( bg2 );
                su.addBranchGraph( graphicTiles[x1][1][z1] );
                activeTiles[x1][1][z1] = newTile1;

            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num0 * 2 );
                removeTile( x0, 0, z0 );
                moveTile( tile1, x1, y1, z1, x0, 0, z0 );
                removeTile( x0, 0, z0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x0 ),
                    indexToPoint( 0 ),
                    indexToPoint( z0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x0][0][z0].detach();
                graphicTiles[x0][0][z0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x0][0][z0] );
                removeTile( x2, 1, z2 );
                activeTiles[x0][0][z0] = newTile;
                moveTile( tile2, x2, y2, z2, x2, 1, z2 );
                moveTile( tile3, x3, y3, z3, x3, 2, z3 );
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x1, 1, z1 );
                moveTile( tile2, x2, y2, z2, x2, 1, z2 );
                removeTile( x1, 1, z1 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( 1 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x1][1][z1].detach();
                graphicTiles[x1][1][z1].addChild( bg1 );
                activeTiles[x1][1][z1] = newTile;
                su.addBranchGraph( graphicTiles[x1][1][z1] );
                moveTile( tile3, x3, y3, z3, x3, 2, z3 );
            }
            else if ( num2 == num3 && num0 != num1 )
            {
                score += calcPoints( num2 * 2 );
                removeTile( x2, 2, z2 );
                moveTile( tile3, x3, y3, z3, x2, 2, z2 );
                removeTile( x2, 2, z2 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( 2 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x2][2][z2].detach();
                graphicTiles[x2][2][z2].addChild( bg1 );
                activeTiles[x2][2][z2] = newTile;
                su.addBranchGraph( graphicTiles[x2][2][z2] );

            }
        }
        else if ( directionOfMovement == LEFT )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( 0, y0, z0 );
                moveTile( tile1, x1, y1, z1, 0, y0, z0 );
                removeTile( 0, y0, z0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( 0 ),
                    indexToPoint( y0 ),
                    indexToPoint( z0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[0][y0][z0].detach();
                graphicTiles[0][y0][z0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[0][y0][z0] );
                activeTiles[0][y0][z0] = newTile;
                removeTile( 1, y2, z2 );
                moveTile( tile2, x2, y2, z2, 1, y2, z2 );
                removeTile( 1, y2, z2 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( 1 ),
                    indexToPoint( y1 ),
                    indexToPoint( z1 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[1][y1][z1].detach();
                graphicTiles[1][y1][z1].addChild( bg2 );
                su.addBranchGraph( graphicTiles[1][y1][z1] );
                activeTiles[1][y1][z1] = newTile1;

            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num0 * 2 );
                removeTile( 0, y0, z0 );
                moveTile( tile1, x1, y1, z1, 0, y0, z0 );
                removeTile( 0, y0, z0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( 0 ),
                    indexToPoint( y0 ),
                    indexToPoint( z0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[0][y0][z0].detach();
                graphicTiles[0][y0][z0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[0][y0][z0] );
                removeTile( 1, y2, z2 );
                activeTiles[0][y0][z0] = newTile;
                moveTile( tile2, x2, y2, z2, 1, y2, z2 );
                moveTile( tile3, x3, y3, z3, 2, y3, z3 );
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( 1, y1, z1 );
                moveTile( tile2, x2, y2, z2, 1, y2, z2 );
                removeTile( 1, y1, z1 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( 1 ),
                    indexToPoint( y1 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[1][y1][z1].detach();
                graphicTiles[1][y1][z1].addChild( bg1 );
                activeTiles[1][y1][z1] = newTile;
                su.addBranchGraph( graphicTiles[1][y1][z1] );
                moveTile( tile3, x3, y3, z3, 2, y3, z3 );
            }
            else if ( num2 == num3 && num0 != num1 )
            {
                score += calcPoints( num2 * 2 );
                removeTile( 2, y2, z2 );
                moveTile( tile3, x3, y3, z3, 2, y2, z2 );
                removeTile( 2, y2, z2 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( 2 ),
                    indexToPoint( y1 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[2][y2][z2].detach();
                graphicTiles[2][y2][z2].addChild( bg1 );
                activeTiles[2][y2][z2] = newTile;
                su.addBranchGraph( graphicTiles[2][y2][z2] );

            }
        }
        else if ( directionOfMovement == RIGHT )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( 3, y3, z3 );
                moveTile( tile2, x2, y2, z2, 3, y3, z3 );
                removeTile( 3, y3, z3 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( 3 ),
                    indexToPoint( y3 ),
                    indexToPoint( z3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[3][y3][z3].detach();
                graphicTiles[3][y3][z3].addChild( bg1 );
                su.addBranchGraph( graphicTiles[3][y3][z3] );
                activeTiles[3][y3][z3] = newTile;
                removeTile( 2, y2, z2 );
                moveTile( tile1, x1, y1, z1, 2, y2, z2 );
                removeTile( 2, y2, z2 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( 2 ),
                    indexToPoint( y2 ),
                    indexToPoint( z2 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[2][y2][z2].detach();
                graphicTiles[2][y2][z2].addChild( bg2 );
                su.addBranchGraph( graphicTiles[2][y2][z2] );
                activeTiles[2][y2][z2] = newTile1;
            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( 1, y1, z1 );
                moveTile( tile0, x0, y0, x0, 1, y1, z1 );
                removeTile( 1, y1, z1 );
                Tile3D newTile = new Tile3D( num0 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( 1 ),
                    indexToPoint( y1 ),
                    indexToPoint( z1 ) ) );
                newTile.drawTile( bg );
                graphicTiles[1][y1][z1].detach();
                graphicTiles[1][y1][z1].addChild( bg );
                su.addBranchGraph( graphicTiles[1][y1][z1] );
                activeTiles[1][y1][z1] = newTile;
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( 2, y2, z2 );
                moveTile( tile1, x1, y1, z1, 2, y2, z2 );
                removeTile( 2, y2, z2 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( 2 ),
                    indexToPoint( y2 ),
                    indexToPoint( z2 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[2][y2][z2].detach();
                graphicTiles[2][y2][z2].addChild( bg1 );
                activeTiles[2][y2][z2] = newTile;
                su.addBranchGraph( graphicTiles[2][y2][z2] );
                moveTile( tile0, x0, y0, z0, 1, y1, z1 );
            }
            else if ( num2 == num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( 3, y3, z3 );
                moveTile( tile2, x2, y2, z2, 3, y3, z3 );
                removeTile( 3, y3, z3 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( 3 ),
                    indexToPoint( y3 ),
                    indexToPoint( z3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[3][y3][z3].detach();
                graphicTiles[3][y3][z3].addChild( bg1 );
                activeTiles[3][y3][z3] = newTile;
                moveTile( tile1, x1, y1, z1, 2, y1, z1 );
                moveTile( tile0, x0, y0, z0, 1, y0, z0 );
            }
        }
        else if ( directionOfMovement == IN )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( x0, y0, 0 );
                moveTile( tile1, x1, y1, z1, x0, y0, 0 );
                removeTile( x0, y0, 0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x0 ),
                    indexToPoint( y0 ),
                    indexToPoint( 0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x0][y0][0].detach();
                graphicTiles[x0][y0][0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x0][y0][0] );
                activeTiles[x0][y0][0] = newTile;
                removeTile( x2, y2, 1 );
                moveTile( tile2, x2, y2, z2, x2, y2, 1 );
                removeTile( x2, y2, 1 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( y1 ),
                    indexToPoint( 1 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[x1][y1][1].detach();
                graphicTiles[x1][y1][1].addChild( bg2 );
                su.addBranchGraph( graphicTiles[x1][y1][1] );
                activeTiles[x1][y1][1] = newTile1;

            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num0 * 2 );
                removeTile( x0, y0, 0 );
                moveTile( tile1, x1, y1, z1, x0, y0, 0 );
                removeTile( x0, y0, 0 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x0 ),
                    indexToPoint( y0 ),
                    indexToPoint( 0 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x0][y0][0].detach();
                graphicTiles[x0][y0][0].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x0][y0][0] );
                removeTile( x2, y2, 1 );
                activeTiles[x0][0][z0] = newTile;
                moveTile( tile2, x2, y2, z2, x2, y2, 1 );
                moveTile( tile3, x3, y3, z3, x3, y3, 2 );
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x1, y1, 1 );
                moveTile( tile2, x2, y2, z2, x2, y2, 1 );
                removeTile( x1, y1, 1 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( y1 ),
                    indexToPoint( 1 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x1][y1][1].detach();
                graphicTiles[x1][y1][1].addChild( bg1 );
                activeTiles[x1][y1][1] = newTile;
                su.addBranchGraph( graphicTiles[x1][y1][1] );
                moveTile( tile3, x3, y3, z3, x3, y3, 2 );
            }
            else if ( num2 == num3 && num0 != num1 )
            {
                score += calcPoints( num2 * 2 );
                removeTile( x2, x2, 2 );
                moveTile( tile3, x3, y3, z3, x2, y2, 2 );
                removeTile( x2, y2, 2 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( y1 ),
                    indexToPoint( 2 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x2][y2][2].detach();
                graphicTiles[x2][y2][2].addChild( bg1 );
                activeTiles[x2][y2][2] = newTile;
                su.addBranchGraph( graphicTiles[x2][y2][2] );

            }
        }
        else if ( directionOfMovement == OUT )
        {
            BranchGroup bg1 = new BranchGroup();
            BranchGroup bg2 = new BranchGroup();
            if ( num0 == num1 && num2 == num3 )
            {
                score += calcPoints( num0 * 2 );
                score += calcPoints( num2 * 2 );
                removeTile( x3, y3, 3 );
                moveTile( tile2, x2, y2, z2, x3, y3, 3 );
                removeTile( x3, y3, 3 );
                Tile3D newTile = new Tile3D( num0 * 2 ); // (0, y, z)
                newTile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( y3 ),
                    indexToPoint( 3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x3][y3][3].detach();
                graphicTiles[x3][y3][3].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x3][y3][3] );
                activeTiles[x3][y3][3] = newTile;
                removeTile( x2, y2, 2 );
                moveTile( tile1, x1, y1, z1, x2, y2, 2 );
                removeTile( x2, y2, 2 );
                Tile3D newTile1 = new Tile3D( num2 * 2 );
                newTile1.setPoint( new Point3f( indexToPoint( x2 ),
                    indexToPoint( y2 ),
                    indexToPoint( 2 ) ) );
                newTile1.drawTile( bg2 );
                graphicTiles[x2][y2][2].detach();
                graphicTiles[x2][y2][2].addChild( bg2 );
                su.addBranchGraph( graphicTiles[x2][y2][2] );
                activeTiles[x2][y2][y2] = newTile1;
            }
            else if ( num0 == num1 && num2 != num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x1, y1, 1 );
                moveTile( tile0, x0, y0, x0, x1, y1, 1 );
                removeTile( x1, y1, 1 );
                Tile3D newTile = new Tile3D( num0 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x1 ),
                    indexToPoint( y1 ),
                    indexToPoint( 1 ) ) );
                newTile.drawTile( bg );
                graphicTiles[x1][y1][1].detach();
                graphicTiles[x1][y1][1].addChild( bg );
                su.addBranchGraph( graphicTiles[x1][y1][1] );
                activeTiles[x1][y1][1] = newTile;
            }
            else if ( num1 == num2 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x2, y2, 2 );
                moveTile( tile1, x1, y1, z1, x2, y2, 2 );
                removeTile( x2, y2, 2 );
                Tile3D newTile = new Tile3D( num1 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x2 ),
                    indexToPoint( y2 ),
                    indexToPoint( 2 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x2][y2][2].detach();
                graphicTiles[x2][y2][2].addChild( bg1 );
                activeTiles[x2][y2][2] = newTile;
                su.addBranchGraph( graphicTiles[x2][y2][2] );
                moveTile( tile0, x0, y0, z0, x1, y1, 1 );
            }
            else if ( num2 == num3 )
            {
                score += calcPoints( num1 * 2 );
                removeTile( x3, y3, 3 );
                moveTile( tile2, x2, y2, z2, x3, y3, 3 );
                removeTile( x3, y3, 3 );
                Tile3D newTile = new Tile3D( num2 * 2 );
                newTile.setPoint( new Point3f( indexToPoint( x3 ),
                    indexToPoint( y3 ),
                    indexToPoint( 3 ) ) );
                newTile.drawTile( bg1 );
                graphicTiles[x3][y3][3].detach();
                graphicTiles[x3][y3][3].addChild( bg1 );
                activeTiles[x3][y3][3] = newTile;
                moveTile( tile1, x1, y1, z1, x1, y1, 2 );
                moveTile( tile0, x0, y0, z0, x1, y1, 0 );
            }
        }
    }


    private abstract class Movement extends AbstractAction
    {
        protected int x;

        protected int y;

        protected int z;

        // Don't allow any movement initially
        protected boolean okUp = false;

        protected boolean okDown = false;

        protected boolean okLeft = false;

        protected boolean okRight = false;

        protected boolean okIn = false;

        protected boolean okOut = false;


        public Movement()
        {
            x = 0;
            y = 0;
            z = 0;
        }
    }


    private class Up extends Movement
    {

        public Up()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int x = 0; x < activeTiles.length; x++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    // How far to move the tile
                    // Cases for 1 null and 3 non - null (No collisions)
                    if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        moveTile( activeTiles[x][0][z], x, 0, z, x, 3, z );
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        moveTile( activeTiles[x][1][z], x, 1, z, x, 3, z );
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        moveTile( activeTiles[x][2][z], x, 2, z, x, 3, z );
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        // Do nothing
                    }
                    // Cases for 2 null and 2 non-null (Collision cases)
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            // Move Tile
                            moveTile( activeTiles[x][0][z], x, 0, z, x, 2, z );
                        }

                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][1][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 1, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            // Move tile
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 2, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][2][z].getNum();
                        int num2 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num1 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][2][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 2, z ),
                                UP );
                        }
                        else
                        {
                            // Move Tiles
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 3, z );
                            moveTile( activeTiles[x][0][z], x, 0, z, x, 2, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                UP );
                        }
                        else
                        {
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 3, z );
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 2, z );
                        }

                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        int num1 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][1][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                UP );
                        }
                        else
                        {
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 3, z );
                            moveTile( activeTiles[x][0][z], x, 0, z, x, 2, z );
                        }

                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        if ( num0 == num1 || num1 == num2 )
                        {
                            threeTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                UP );

                        }
                        else
                        {
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 3, z );
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 2, z );
                            moveTile( activeTiles[x][0][z], x, 0, z, x, 1, z );
                        }

                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            activeTiles[x][1][z] = new Tile3D( activeTiles[x][0][z] );
                            activeTiles[x][0][z] = null;
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num1 || num1 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                new Point3i( x, 3, z ),
                                UP );
                        }
                        else
                        {
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 2, z );
                            moveTile( activeTiles[x][0][z], x, 0, z, x, 1, z );
                        }
                    }

                    // Case for 4 non-nulls
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num1 || num1 == num2 || num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                DOWN );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }
                }
            }
            System.out.println( "You moved up" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();
        }
    }


    private class Down extends Movement
    {
        public Down()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int x = 0; x < activeTiles.length; x++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    // Cases for 1 null and 3 non - null
                    if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        moveTile( activeTiles[x][3][z], x, 3, z, x, 0, z );
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        moveTile( activeTiles[x][2][z], x, 2, z, x, 0, z );
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        moveTile( activeTiles[x][1][z], x, 1, z, x, 0, z );
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        // Do nothing
                    }

                    // Cases for 2 null and 2 non-null
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 3, z ),
                                DOWN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 1, z );
                        }

                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num1 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[x][1][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 1, z ),
                                new Point3i( x, 3, z ),
                                DOWN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 0, z );
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 1, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num2 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                DOWN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 0, z );
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 1, z );

                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][z][z].getNum();
                        if ( num0 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][2][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 2, z ),
                                DOWN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 1, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                DOWN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 0, z );
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 1, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] == null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        if ( num0 == num1 )
                        {
                            twoTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                DOWN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] == null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        if ( num0 == num1 )
                        {
                            activeTiles[x][0][z] = new Tile3D( num0 * 2 );
                            score += calcPoints( num0 * 2 );
                            activeTiles[x][1][z] = null;
                            activeTiles[x][2][z] = null;
                        }
                        else if ( num1 == num2 )
                        {
                            activeTiles[x][1][z] = new Tile3D( num1 * 2 );
                            score += calcPoints( num0 * 2 );
                            activeTiles[x][2][z] = null;
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[x][0][z] == null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num1 == num2 )
                        {
                            activeTiles[x][0][z] = new Tile3D( num1 * 2 );
                            activeTiles[x][1][z] = new Tile3D( activeTiles[x][3][z] );
                            score += calcPoints( num1 * 2 );
                            activeTiles[x][2][z] = null;
                            activeTiles[x][3][z] = null;

                        }
                        else if ( num2 == num3 )
                        {
                            activeTiles[x][0][z] = new Tile3D( activeTiles[x][1][z] );
                            activeTiles[x][1][z] = new Tile3D( num2 * 2 );
                            score += calcPoints( num2 * 2 );
                            activeTiles[x][2][z] = null;
                            activeTiles[x][3][z] = null;
                        }
                        else
                        {
                            moveTile( activeTiles[x][1][z], x, 1, z, x, 0, z );
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 1, z );
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 2, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] == null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num2 )
                        {
                            activeTiles[x][0][z] = new Tile3D( num0 * 2 );
                            activeTiles[x][1][z] = new Tile3D( activeTiles[x][3][z] );
                            score += calcPoints( num0 * 2 );
                            activeTiles[x][2][z] = null;
                            activeTiles[x][3][z] = null;
                        }
                        else if ( num2 == num3 )
                        {
                            activeTiles[x][1][z] = new Tile3D( num2 * 2 );
                            score += calcPoints( num2 * 2 );
                            activeTiles[x][2][z] = null;
                            activeTiles[x][3][z] = null;
                        }
                        else
                        {
                            moveTile( activeTiles[x][2][z], x, 2, z, x, 1, z );
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 2, z );
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num1 )
                        {
                            activeTiles[x][0][z] = new Tile3D( num0 * 2 );
                            activeTiles[x][1][z] = new Tile3D( activeTiles[x][3][z] );
                            activeTiles[x][3][z] = null;
                        }
                        else if ( num1 == num3 )
                        {
                            activeTiles[x][1][z] = new Tile3D( num1 * 2 );
                            activeTiles[x][3][z] = null;
                        }
                        else
                        {
                            moveTile( activeTiles[x][3][z], x, 3, z, x, 2, z );
                        }
                    }

                    // Case of 4 non-nulls
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] != null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num2 = activeTiles[x][2][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num1 || num1 == num2 || num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[x][0][z],
                                activeTiles[x][1][z],
                                activeTiles[x][2][z],
                                activeTiles[x][3][z],
                                new Point3i( x, 0, z ),
                                new Point3i( x, 1, z ),
                                new Point3i( x, 2, z ),
                                new Point3i( x, 3, z ),
                                DOWN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                }
            }
            System.out.println( "You moved down" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();
            if ( checkWin() )
            {
                winMessage();
            }
        }
    }


    private class Left extends Movement
    {
        public Left()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int y = 0; y < activeTiles.length; y++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    // Cases for 1 null and 3 non - null
                    if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        moveTile( activeTiles[3][y][z], 3, y, z, 0, y, z );
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        moveTile( activeTiles[2][y][z], 2, y, z, 0, y, z );
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        moveTile( activeTiles[1][y][z], 1, y, z, 0, y, z );
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        // Do nothing
                    }

                    // Cases for 2 null and 2 non-null
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[3][y][z], 3, y, z, 1, y, z );
                        }

                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num1 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 0, y, z );
                            moveTile( activeTiles[3][y][z], 3, y, z, 1, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num2 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 0, y, z );
                            moveTile( activeTiles[3][y][z], 3, y, z, 1, y, z );

                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num0 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 2, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 1, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 0, y, z );
                            moveTile( activeTiles[2][y][z], 2, y, z, 1, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        if ( num0 == num1 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                LEFT );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num0 == num1 || num1 == num2 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                LEFT );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 0, y, z );
                            moveTile( activeTiles[2][y][z], 2, y, z, 1, y, z );
                            moveTile( activeTiles[3][y][z], 3, y, z, 2, y, z );

                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 3, y, z );
                            moveTile( activeTiles[3][y][z], 3, y, z, 2, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num1 || num1 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                        else
                        {
                            moveTile( activeTiles[3][y][z], 3, y, z, 2, y, z );
                        }
                    }

                    // Case of 4 non-nulls
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num1 || num1 == num2 || num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                LEFT );
                        }
                    }

                }
            }
            System.out.println( "You moved left" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();
            if ( checkWin() )
            {
                winMessage();
            }
        }
    }


    private class Right extends Movement
    {
        public Right()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int y = 0; y < activeTiles.length; y++ )
            {
                for ( int z = 0; z < activeTiles.length; z++ )
                {
                    // Cases for 1 null and 3 non - null (No collisions)
                    if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        moveTile( activeTiles[0][y][z], 0, y, z, 3, y, z );
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        moveTile( activeTiles[1][y][z], 1, y, z, 3, y, z );
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        moveTile( activeTiles[2][y][z], 2, y, z, 3, y, z );
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        // Do nothing
                    }

                    // Cases for 2 null and 2 non-null (Collision cases)
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[3][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[0][y][z], 0, y, z, 2, y, z );
                        }

                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[3][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 3, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[2][y][z].getNum();
                        int num2 = activeTiles[3][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num1 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 2, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 3, y, z );
                            moveTile( activeTiles[0][y][z], 0, y, z, 2, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 3, y, z );
                            moveTile( activeTiles[1][y][z], 1, y, z, 2, y, z );
                        }

                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] == null )
                    {
                        int num1 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[1][y][z].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 3, y, z );
                            moveTile( activeTiles[0][y][z], 0, y, z, 2, y, z );
                        }

                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[0][y][z] == null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] == null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        if ( num0 == num1 || num1 == num2 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                RIGHT );

                        }
                        else
                        {
                            moveTile( activeTiles[2][y][z], 2, y, z, 3, y, z );
                            moveTile( activeTiles[1][y][z], 1, y, z, 2, y, z );
                            moveTile( activeTiles[0][y][z], 0, y, z, 1, y, z );
                        }

                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] == null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[0][y][z], 0, y, z, 1, y, z );
                        }
                    }
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] == null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num1 || num1 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            moveTile( activeTiles[1][y][z], 1, y, z, 2, y, z );
                            moveTile( activeTiles[0][y][z], 0, y, z, 1, y, z );
                        }
                    }

                    // Case for 4 non-nulls
                    else if ( activeTiles[0][y][z] != null
                        && activeTiles[1][y][z] != null
                        && activeTiles[2][y][z] != null
                        && activeTiles[3][y][z] != null )
                    {
                        int num0 = activeTiles[0][y][z].getNum();
                        int num1 = activeTiles[1][y][z].getNum();
                        int num2 = activeTiles[2][y][z].getNum();
                        int num3 = activeTiles[3][y][z].getNum();
                        if ( num0 == num1 || num1 == num2 || num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[0][y][z],
                                activeTiles[1][y][z],
                                activeTiles[2][y][z],
                                activeTiles[3][y][z],
                                new Point3i( 0, y, z ),
                                new Point3i( 1, y, z ),
                                new Point3i( 2, y, z ),
                                new Point3i( 3, y, z ),
                                RIGHT );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }
                }
            }
            System.out.println( "You moved right" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();
            if ( checkWin() )
            {
                winMessage();
            }
        }
    }


    private class In extends Movement
    {
        public In()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int x = 0; x < activeTiles.length; x++ )
            {
                for ( int y = 0; y < activeTiles.length; y++ )
                {
                    // How far to move the tile
                    // Cases for 1 null and 3 non - null
                    if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        moveTile( activeTiles[x][y][3], x, y, 3, x, y, 0 );
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        moveTile( activeTiles[x][y][2], x, y, 2, x, y, 0 );
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        moveTile( activeTiles[x][y][1], x, y, 1, x, y, 0 );
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        // Do nothing
                    }
                    // Cases for 2 null and 2 non-null
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 3 ),
                                IN );

                        }
                        else
                        {
                            moveTile( activeTiles[x][y][3], x, y, 3, x, y, 1 );
                        }

                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num1 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 0 );
                            moveTile( activeTiles[x][y][3], x, y, 3, x, y, 1 );
                        }
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num2 == num3 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 0 );
                            moveTile( activeTiles[x][y][3], x, y, 3, x, y, 1 );

                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        if ( num0 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 2 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 1 );
                        }
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 0 );
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 1 );
                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        if ( num0 == num1 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                IN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        if ( num0 == num1 || num1 == num2 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                IN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 0 );
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 1 );
                            moveTile( activeTiles[x][y][3], x, y, 3, x, y, 2 );
                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num0 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[x][0][z] != null
                        && activeTiles[x][1][z] != null
                        && activeTiles[x][2][z] == null
                        && activeTiles[x][3][z] != null )
                    {
                        int num0 = activeTiles[x][0][z].getNum();
                        int num1 = activeTiles[x][1][z].getNum();
                        int num3 = activeTiles[x][3][z].getNum();
                        if ( num0 == num1 || num1 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][3], x, y, 3, x, y, 2 );
                        }
                    }

                    // Case of 4 non-nulls
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num0 == num1 || num1 == num2 || num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                IN );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }

                }
            }
            if ( checkWin() )
            {
                winMessage();
            }
            System.out.println( "You moved in" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();

        }
    }


    private class Out extends Movement
    {
        public Out()
        {
            super();
        }


        public void actionPerformed( ActionEvent arg0 )
        {
            for ( int x = 0; x < activeTiles.length; x++ )
            {
                for ( int y = 0; y < activeTiles.length; y++ )
                {
                    // Cases for 1 null and 3 non - null (No collisions)
                    if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        moveTile( activeTiles[x][y][0], x, y, 0, x, y, 3 );
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        moveTile( activeTiles[x][y][1], x, y, 1, x, y, 3 );
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        moveTile( activeTiles[x][y][2], x, y, 2, x, y, 3 );
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        // Do nothing
                    }

                    // Cases for 2 null and 2 non-null (Collision cases)
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 2 );
                        }

                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 2 );
                        }
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][2].getNum();
                        int num2 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        if ( num0 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 2 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 3 );
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 2 );
                        }
                    }
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        if ( num1 == num2 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 3 );
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 2 );
                        }

                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] == null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        if ( num0 == num1 )
                        {
                            twoTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 3 );
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 2 );
                        }

                    }

                    // Cases for 1 null and 3 non-null
                    else if ( activeTiles[x][y][0] == null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            // Do nothing
                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] == null )
                    {
                        int num1 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][1].getNum();
                        int num3 = activeTiles[x][y][2].getNum();
                        if ( num1 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                OUT );

                        }
                        else
                        {
                            moveTile( activeTiles[x][y][2], x, y, 2, x, y, 3 );
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 2 );
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 1 );
                        }

                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] == null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num0 == num2 || num2 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 1 );
                        }
                    }
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] == null
                        && activeTiles[x][y][3] != null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num0 == num1 || num1 == num3 )
                        {
                            threeTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            moveTile( activeTiles[x][y][1], x, y, 1, x, y, 2 );
                            moveTile( activeTiles[x][y][0], x, y, 0, x, y, 1 );
                        }
                    }

                    // Case for 4 non-nulls
                    else if ( activeTiles[x][y][0] != null
                        && activeTiles[x][y][1] != null
                        && activeTiles[x][y][2] != null
                        && activeTiles[x][y][3] != null )
                    {
                        int num0 = activeTiles[x][y][0].getNum();
                        int num1 = activeTiles[x][y][1].getNum();
                        int num2 = activeTiles[x][y][2].getNum();
                        int num3 = activeTiles[x][y][3].getNum();
                        if ( num0 == num1 && num2 == num3 )
                        {
                            fourTileCollisionHandler( activeTiles[x][y][0],
                                activeTiles[x][y][1],
                                activeTiles[x][y][2],
                                activeTiles[x][y][3],
                                new Point3i( x, y, 0 ),
                                new Point3i( x, y, 1 ),
                                new Point3i( x, y, 2 ),
                                new Point3i( x, y, 3 ),
                                OUT );
                        }
                        else
                        {
                            // Do Nothing
                        }
                    }

                }

            }
            if ( checkWin() )
            {
                winMessage();
            }
            System.out.println( "You moved out" );
            System.out.println( "Score: " + score );
            printActiveTiles();
            spawnTile();
        }
    }



    /**
     * The game board which consists of 4 planes of 16 empty tiles stacked one
     * upon another. The playing tiles will float a little bit above a plane on
     * the game board.
     */
    private void drawBoard()
    {
        BranchGroup bg2 = new BranchGroup();
        for ( float x = -0.5f; x < 0.5f; x += 0.25f )
        {
            for ( float y = -0.5f; y < 0.5f; y += 0.25f )
            {
                for ( float z = -0.5f; z < 0.5f; z += 0.25f )
                {

                    Tile3D tile = new Tile3D( x, y, z - 0.1f );
                    tile.drawTile( bg2 );
                }
            }
        }
        su.addBranchGraph( bg2 );
    }


    /**
     * Uses both directional and ambient lighting to make the playing surface
     * well lit.
     */
    public void setLighting()
    {
        Color3f lightingColor = new Color3f( 1.0f, 1.0f, 1.0f );
        Vector3f lightDirection = new Vector3f( 1.0f, -7.0f, -12.0f );
        DirectionalLight light = new DirectionalLight( lightingColor,
            lightDirection );
        light.setInfluencingBounds( bounds );
        AmbientLight amb = new AmbientLight( lightingColor );
        amb.setInfluencingBounds( bounds );

        bg.addChild( light );
        bg.addChild( amb );
    }


    /**
     * Converts the integer index of the an array to a floating point number
     * which will help to specify a location on the 3-D plane
     * 
     * @param n
     *            - an integer index in an array
     * @return a floating point coordinate value on an axis
     */
    private float indexToPoint( int n )
    {
        return n * 0.25f - 0.5f;
    }


    public void startGame()
    {
        drawBoard();

        int x = (int)( Math.random() * 4 );
        int y = (int)( Math.random() * 4 );
        int z = (int)( Math.random() * 4 );
        int x1 = (int)( Math.random() * 4 );
        int y1 = (int)( Math.random() * 4 );
        int z1 = (int)( Math.random() * 4 );

        // Clear up board
        activeTiles = new Tile3D[4][4][4];
        activeTiles[x][y][z] = new Tile3D( 2 );
        Random rand = new Random();
        int prob = rand.nextInt( 2 );

        if ( x1 != x || y1 != y || z1 != z )
        {
            activeTiles[x1][y1][z1] = new Tile3D( 2 + 2 * prob );
        }
        else
        {
            while ( x == x1 && y == y1 && z == z1 )
            {
                x1 = (int)( Math.random() * 4 );
                y1 = (int)( Math.random() * 4 );
                z1 = (int)( Math.random() * 4 );
            }
            activeTiles[x1][y1][z1] = new Tile3D( 2 );
        }

        BranchGroup bg1 = new BranchGroup();
        bg1.setCapability( BranchGroup.ALLOW_DETACH );
        activeTiles[x][y][z].setPoint( new Point3f( indexToPoint( x ),
            indexToPoint( y ),
            indexToPoint( z ) ) );
        activeTiles[x][y][z].drawTile( bg1 );

        BranchGroup bg2 = new BranchGroup();
        activeTiles[x1][y1][z1].setPoint( new Point3f( indexToPoint( x1 ),
            indexToPoint( y1 ),
            indexToPoint( z1 ) ) );
        activeTiles[x1][y1][z1].drawTile( bg2 );


        graphicTiles[x][y][z].addChild( bg1 );
        graphicTiles[x1][y1][z1].addChild( bg2 );

        su.addBranchGraph( graphicTiles[x][y][z] );
        su.addBranchGraph( graphicTiles[x1][y1][z1] );

    }


    public boolean isFull()
    {
        int count = 0;
        for ( int x = 0; x < activeTiles.length; x++ )
        {
            for ( int y = 0; y < activeTiles[x].length; y++ )
            {
                for ( int z = 0; z < activeTiles[x][y].length; z++ )
                {
                    if ( activeTiles[x][y][z] != null )
                    {
                        count++;
                    }
                }
            }
        }
        if ( count == 64 )
        {
            return true;
        }
        return false;
    }


    public void spawnTile()
    {
        Random rand = new Random();
        int x = rand.nextInt( 4 );
        int y = rand.nextInt( 4 );
        int z = rand.nextInt( 4 );
        if ( activeTiles[x][y][z] != null && !isFull() )
        {
            // Keep generating locations until a empty tile is chosen
            spawnTile();
        }
        else
        {
            if ( isFull() )
            {
                gameOver();
            }
            else
            {
                BranchGroup bg1 = new BranchGroup();
                bg1.setCapability( BranchGroup.ALLOW_DETACH );
                activeTiles[x][y][z] = new Tile3D( 2 * rand.nextInt( 2 ) + 2 );
                activeTiles[x][y][z].setPoint( new Point3f( x * 0.25f - 0.5f,
                    y * 0.25f - 0.5f,
                    z * 0.25f - 0.5f ) );
                activeTiles[x][y][z].drawTile( bg1 );
                graphicTiles[x][y][z].detach();
                graphicTiles[x][y][z].addChild( bg1 );
                su.addBranchGraph( graphicTiles[x][y][z] );
            }
        }
    }


    /**
     * Allows the user to move around the screen with the mouse.
     * 
     * @param canvas
     *            - a Canvas3D object to which this behavior will be
     *            implemented.
     */
    public void addOrbit( Canvas3D canvas )
    {
        OrbitBehavior orbit = new OrbitBehavior( canvas );
        orbit.setEnable( true );
        orbit.setSchedulingBounds( bounds );
        su.getViewingPlatform().setViewPlatformBehavior( orbit );
    }
}