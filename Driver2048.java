package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// import java.util.*;
//
// import com.sun.j3d.utils.*;
// import com.sun.j3d.utils.geometry.*;
// import com.sun.j3d.utils.universe.*;
//
// import javax.media.j3d.*;
import javax.swing.*;
// import javax.swing.JFrame;
// import javax.vecmath.*;


/**
 * Welcome screen, set's up the Player window. 
 * 
 * @author Sandeep Raghunandhan
 * @version May 6, 2014
 * @author Period: 2
 * @author Assignment: FinalProjectCS
 * 
 * @author Sources: http://www.java3d.org/introduction.html
 */
@SuppressWarnings("serial")
public class Driver2048 extends JFrame implements ActionListener
{
    JFrame choosingWindow = new JFrame("Welcome");
    /**
     * 
     */
    public Driver2048()
    {
        super( "2048" );
        Container c1 = choosingWindow.getContentPane();
        c1.setBackground( Color.BLACK);
        choosingWindow.setLayout( new FlowLayout());
        choosingWindow.setSize( 400,400 );
        
        
        ImageIcon image2 = new ImageIcon("2048_3D_icon.jpg");
        JButton button2 = new JButton( image2 );
        button2.addActionListener( this );
        button2.setActionCommand( "3D" );
        
        choosingWindow.getContentPane().add( button2);

        JLabel welcome = new JLabel("Welcome to 2048");
        welcome.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 36) );
        welcome.setForeground( Color.ORANGE );
        JLabel welcome2 = new JLabel("Click the tile to begin");
        welcome2.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 36) );
        welcome2.setForeground( Color.ORANGE );
        choosingWindow.getContentPane().add( welcome );
        choosingWindow.getContentPane().add( welcome2 );
        choosingWindow.setDefaultCloseOperation( EXIT_ON_CLOSE );
        choosingWindow.setVisible( true );

    }


    /**
     * TODO Write your method description here.
     * 
     * @param args
     */
    public static void main( String[] args )
    {
        new Driver2048();

    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        Container c = getContentPane();
        setSize(600,600);
        setResizable( false );
        setLayout( new BorderLayout() );
        if ( e.getActionCommand().equals( "3D" ) )
        {
            choosingWindow.dispose();
            ThreeD2048Graphics graph2 = new ThreeD2048Graphics();
            c.add( graph2, BorderLayout.CENTER  );
            setTitle("2048-3D");
        }
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setVisible( true );
    }

}
