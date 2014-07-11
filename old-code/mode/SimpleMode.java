package org.playasophy.wonderdome.mode;

import processing.core.PApplet;

import org.playasophy.wonderdome.input.ButtonEvent;
import org.playasophy.wonderdome.input.InputEvent;



//
// CLASS: SimpleMode
// Author: Zack
//
// Description:
//        Meant to be used as a base class for more complicated modes,
//         This class has 1 main method to derive from:
//
//            protected int getPixelColor(int x, int y, long dtMillis)
//
//         This will be called for every pixel in the graph and you
//        should return a color value derived from the method:
//
//            parent.color(r, g, b);
//            parent.color(h, s, b);
//
//        Subclasses may override the method getColorModeForThisMode to
//        set the color mode which will be applied before their
//        getPixelColor method is invoked.
//
//        This class also includes skeleton methods for all button presses
//        As we add more input methods, add more skeleton methods for those
//        below.
//

public abstract class SimpleMode implements Mode {

    ///// CONSTANTS /////
    protected static final int MAX_LED_STRIPS = 6;
    protected static final int MAX_LEDS_PER_STRIP = 240;



    ///// PROPERTIES /////
    protected final PApplet parent;
    protected int[] colors;



    ///// INITIALIZATION /////

    public SimpleMode(PApplet parent) {
        this.parent = parent;
        parent.colorMode(parent.RGB);

        colors = new int[] {
            parent.color(255, 0, 0),
            parent.color(0, 255, 0),
            parent.color(0, 0, 255)
        };
    }



    ///// ABSTRACT METHODS /////

    //
    // NOTE!!! Derived Classes should override this!
    // This gets called once for each pixel.
    //
    protected abstract int getPixelColor(int x, int y, long dtMillis);

    //
    // Derived Classes should override this if they wish to have a global state update.
    // This is called once per frame.
    //
    protected void updateState(long dtMillis) {}


    ///// Mode METHODS /////

    @Override
    public void onShow() { }

    @Override
    public void handleEvent(InputEvent event) {
        if ( event instanceof ButtonEvent ) {
            handleButtonEvent((ButtonEvent) event);
        }
    }

    @Override
    public void update(int[][] pixels, long dtMillis) {

        // Set the color mode.
        parent.colorMode(getColorModeForThisMode());
        
        // Update the State
        updateState(dtMillis);

        // Loop over all pixels and call the potentially overridden method
        // getPixelColor on all indices.
        for ( int i = 0; i < pixels.length; i++ )
        {
            for ( int j = 0; j < pixels[i].length; j++ )
            {
                setPixel(pixels, i, j, getPixelColor(i, j, dtMillis));
            }
        }
    }



    ///// PROTECTED METHODS /////

    /**
     * Determines what color mode this mode operates in. Subclasses should
     * override this method to change the color mode.
     *
     * @return The color mode to use for this mode.
     */
    protected int getColorModeForThisMode() {
        return parent.RGB;
    }



    ///// PRIVATE METHODS /////

    private void setPixel(int[][] pixels, int x, int y, int color) {
        // If the indices are in range, set the color.
        if (x >= 0 && x < MAX_LED_STRIPS         &&
            y >= 0 && y < MAX_LEDS_PER_STRIP)
        {
            pixels[x][y] = color;
        }
        else
        {
            System.out.println("SimpleMode.setPixel ERROR: Indices out of Range! [" +  x + ", " + y + "]");
        }
    }

    private void handleButtonEvent(ButtonEvent event) {
        boolean isPressed = event.getType() == ButtonEvent.Type.PRESSED;
        if ( isPressed ) {
            switch ( event.getId() ) {
                case UP:    UpButtonPressed();          break;
                case DOWN:  DownButtonPressed();        break;
                case LEFT:  LeftButtonPressed();        break;
                case RIGHT: RightButtonPressed();       break;
                case A:     AButtonPressed();           break;
                case B:     BButtonPressed();           break;
            }
        }
    }

    // Derived Classes should override accordingly.
    protected void UpButtonPressed() {}
    protected void DownButtonPressed() {}
    protected void LeftButtonPressed() {}
    protected void RightButtonPressed() {}
    protected void AButtonPressed() {}
    protected void BButtonPressed() {}

}