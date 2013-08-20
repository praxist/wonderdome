package org.playasophy.wonderdome;

import java.util.ArrayList;
import java.util.List;

import processing.core.*;

import org.playasophy.wonderdome.input.ButtonEvent;
import org.playasophy.wonderdome.input.InputEvent;
import org.playasophy.wonderdome.mode.ColorCycle;
import org.playasophy.wonderdome.mode.Mode;
import org.playasophy.wonderdome.mode.MovementTest;
import org.playasophy.wonderdome.mode.LanternMode;
import org.playasophy.wonderdome.mode.FlickerMode;

public class Wonderdome {

    ///// TYPES /////

    private enum State {
        PAUSED,
        RUNNING
    }



    ///// CONSTANTS /////

    private static final int NUM_STRIPS = 6;
    private static final int PIXELS_PER_STRIP = 240;



    ///// PROPERTIES /////

    private final PApplet parent;
    private int[][] pixels;
    private List<Mode> modes;
    private int currentModeIndex;
    private State state;
    private long lastUpdate;



    ///// INITIALIZATION /////

    public Wonderdome(PApplet parent) {
        this.parent = parent;
        parent.registerMethod("pre", this);
        pixels = new int[NUM_STRIPS][PIXELS_PER_STRIP];

        modes = new ArrayList<Mode>();

        //
        // List of Modes
        //
        modes.add(new ColorCycle(parent));        // Mode 0
        modes.add(new MovementTest(parent));      // Mode 1
        modes.add(new LanternMode(parent));       // Mode 2
        modes.add(new FlickerMode(parent));       // Mode 3

        // Initial Mode [Change for ease of use when testing new modes].
        switchToMode(3);

        state = State.RUNNING;
        lastUpdate = System.currentTimeMillis();
    }



    ///// PUBLIC METHODS /////

    public void pre() {
        if ( state == State.RUNNING ) {
            long dt = System.currentTimeMillis() - lastUpdate;
            try {
                getCurrentMode().update(pixels, dt);
            } catch ( Exception e ) {
                evictCurrentMode(e);
            }
        }
        lastUpdate = System.currentTimeMillis();
    }

    public int[][] getPixels() {
        return pixels;
    }

    public void handleEvent(InputEvent event) {
        boolean consumed = false;
        if ( event instanceof ButtonEvent ) {
            ButtonEvent be = (ButtonEvent) event;
            if ( be.getId() == ButtonEvent.Id.SELECT ) {
                handleSelectButton(be.getType());
                consumed = true;
            }
        }

        if ( !consumed ) {
            try {
                getCurrentMode().handleEvent(event);
            } catch ( Exception e ) {
                evictCurrentMode(e);
            }
        }
    }

    public void pause() {
        state = State.PAUSED;
    }

    public void resume() {
        state = State.RUNNING;
    }

    public void setModeList(List<Mode> modes) {
        // TODO: Implement this.
    }



    ///// PRIVATE METHODS /////

    private Mode getCurrentMode() {
        return modes.get(currentModeIndex);
    }

    private void evictCurrentMode(final Throwable cause) {

        System.err.println(
            "Mode '" + getCurrentMode().getClass() +
            "' threw exception '" + cause.getMessage() +
            "' and is being evicted from the mode cycle."
            );
        cause.printStackTrace();

        modes.remove(currentModeIndex);
        cycleModes();

    }

    private void handleSelectButton(final ButtonEvent.Type type) {
        if ( type == ButtonEvent.Type.PRESSED ) {
            cycleModes();
        }
    }

    private void switchToMode(int modeIndex)
    {
        if (modeIndex >= 0 && modeIndex < modes.size())
        {
            currentModeIndex = modeIndex;
            System.out.println("Now in mode " + currentModeIndex + ": " + modes.get(currentModeIndex).getClass());
        }
    }

    private void cycleModes()
    {
        int newMode = currentModeIndex + 1;
        if (newMode >= modes.size())
        {
            newMode = 0;
        }
        switchToMode(newMode);
    }

}
