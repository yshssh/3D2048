package src;

import java.awt.Color;
import java.awt.Font;
/**
 *  TODO Write a one-sentence summary of your class here.
 *  TODO Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 *
 *  @author  Sandeep Raghunandhan
 *  @version May 20, 2014
 *  @author  Period: TODO
 *  @author  Assignment: FinalProjectCS
 *
 *  @author  Sources: TODO
 */
public abstract class Tile
{


        /**
         * The number the tile contains
         */
        /**
         * 
         */
        protected int num;

        /**
         * 
         */
        protected static final Color[] colors = { Color.BLACK,
            new Color( 255, 255, 230 ), new Color( 255, 230, 210 ),
            new Color( 244, 164, 96 ), new Color( 255, 127, 80 ),
            new Color( 255, 99, 71 ), new Color( 255, 69, 0 ),
            new Color( 255, 255, 130 ), new Color( 245, 222, 150 ),
            new Color( 250, 218, 140 ), new Color( 240, 220, 0 ),
            new Color( 255, 215, 0 ) };

        /**
         * 
         */
        protected Color myColor;


        /**
         * 
         */
        protected Font font;


        /**
         * 
         */
        public Tile()
        {
            num = 0;
            myColor = Color.lightGray;
            font = new Font( Font.SERIF, Font.BOLD, 36 );
        }


        /**
         * @param number
         */
        public Tile( int number )
        {
            num = number;
            // Index into the array of colors and pull out the correct one for a
            // given of number in the form 2^x
            myColor = colors[indexIn(colors)];
            font = new Font( Font.SERIF, Font.BOLD, 36 );
        }


        /**
         * TODO Write your method description here.
         * 
         * @return
         */
        public int getNum()
        {
            return num;
        }


        /**
         * TODO Write your method description here.
         * 
         * @param num
         */
        public void setNum( int num )
        {
            this.num = num;
        }


        /**
         * TODO Write your method description here.
         * 
         * @param font
         */
        public void setFont( Font font )
        {
            this.font = font;
        }
        
        /**
         * TODO Write your method description here.
         * @param a
         * @return
         */
        public int indexIn(Object[] a)
        {
            int colorIndex = 0;
            if ( num > 0 )
            {
                colorIndex = (int)( Math.log( num ) / Math.log( 2 ) );
            }
            if ( colorIndex <= 11 )
            {
                return colorIndex;
            }
            else
            {
                return 0;
            }
        }

    }


