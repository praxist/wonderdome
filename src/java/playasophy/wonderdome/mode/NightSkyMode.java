package playasophy.wonderdome.mode;

import java.util.*;
import clojure.lang.Associative;
import playasophy.wonderdome.util.Color;
import playasophy.wonderdome.util.ClojureKeyword;

/**
 * A night sky with twinkling stars
 */
public class NightSkyMode implements Mode<NightSkyMode> {

  /****************
    / INTERNAL VARS /
   ****************/
  // Adjusts how bright neighboring pixels are lit when next to a star
  private final float neighborBrightness = (float) 0.2;
  // Percentage brightness variance among generated stars
  private final float starBrightnessVariance = (float) 0.1;
  // Stores all of the currently active stars
  private final float starChance = (float) 0.2;
  HashMap<Integer, Star> stars;

  /****************
    / EXTERNAL VARS /
   ****************/
  //
  private final int offset;
  // Adjusts how close stars can potentially appear next to each other
  private final int starSpacing;
  // Maximum amount of stars that can be visually present
  private final int maxStars;

  public NightSkyMode() {
    this(0, 60, new HashMap<Integer, Star>(), 10);
  }

  private NightSkyMode(int offset, int starSpacing, HashMap<Integer, Star> stars, int maxStars) {
    this.offset = offset;
    this.starSpacing = starSpacing;

    this.maxStars = maxStars;
    this.stars = stars;
  }

  @Override
  public NightSkyMode update(Associative event) {
    if (ClojureKeyword.TIME_TICK.equals(event.valAt(ClojureKeyword.TYPE)) && event.valAt(ClojureKeyword.INPUT) == null) {
      // Age current stars and populate next frame's stars
      HashMap<Integer, Star> nextFrameStars = new HashMap<Integer, Star>();
      Iterator<Map.Entry<Integer, Star>> iter = stars.entrySet().iterator();

      while (iter.hasNext()) {
        Map.Entry<Integer, Star> pair = iter.next();
        int starPos = pair.getKey();
        Star star = pair.getValue();
        if (star.age()) {
          // Star has faded away, so we don't add it
        } else {
          nextFrameStars.put(starPos, star);
        }
      }

      // Add new stars
      if (Math.random() < starChance)
      {
        int potentialPos = (int) (Math.random() * 5 * 240);
        boolean validPos = true;
        for (int i = potentialPos - starSpacing; i < potentialPos + starSpacing; i++) {
          if (stars.containsKey(i)) {
            // I didn't want a new star anyways.
            validPos = false;
            break;
          }
        }
        if (validPos)
          nextFrameStars.put(potentialPos, new Star((float) (1.0 - Math.random() * starBrightnessVariance)));
      }

      if (offset >= (5 * 240) - 1)
        return new NightSkyMode(1, starSpacing, nextFrameStars, maxStars);
      else
        return new NightSkyMode(offset + 1, starSpacing, nextFrameStars, maxStars);
    } else {
      return new NightSkyMode(offset, starSpacing, stars, maxStars);
    }
  }

  @Override
  public int render(Associative pixel) {
    Integer pos = 240
      * Math.toIntExact((long) pixel.valAt(ClojureKeyword.STRIP))
      + Math.toIntExact((long) pixel.valAt(ClojureKeyword.PIXEL));

    // color 0.5  0.8
    float hue = (float) (0.55 + Math.sin(Math.PI * offset / 240.0) * 0.25);
    if (stars.containsKey(pos)) {
      Star star = stars.get(pos);
      return Color.hsv(hue, (float) 0.0, (float) star.getBrightness());
    } else if (stars.containsKey(pos + 1)) {
      Star star = stars.get(pos + 1);
      return Color.hsv(hue, (float) 0.0, (float) star.getBrightness() * neighborBrightness);
    } else if (stars.containsKey(pos - 1)) {
      Star star = stars.get(pos - 1);
      return Color.hsv(hue, (float) 0.0, (float) star.getBrightness() * neighborBrightness);

    } else {
      return Color.hsv(hue, (float) 1, (float) 0.05);
    }
  }


  private boolean star(long pos) {
    if (pos > offset &&
        (pos - offset) % starSpacing == 0) {
      if (Math.random() < 0.05) {
        return true;
      }
        }
    return false;
  }

  /*
   * Returns the which edge the pixel is located on following this diagram:
   *     3 /\ 2
   *      /__\
   *   0  \  1
   *       \
   */
  public int edge(int pixelPos) {
    if (pixelPos < 60) {
      return 0;
    } else if (pixelPos < 120) {
      return 1;
    } else if (pixelPos < 180) {
      return 2;
    } else {
      return 3;
    }
  }
}

class Star {
  // Number of frames a star lives
  private final int lifespan = 40;
  // Percentage brightness of this particular star
  private float brightnessFactor;
  private int age;

  public Star (float f) {
    this.age = 1;
    this.brightnessFactor = f;
  }

  public boolean age() {
    age++;
    return age >= lifespan;
  }

  // Return a fraction representing the star's maximum brightness
  public float getBrightness() {
    // Stars reach maximum brightness at half their maximum age
    return (float) Math.sin(Math.PI * age / (double) lifespan) * brightnessFactor;
  }
}

// x todo: use hash instead of ll
// background colors
// x space stars apart
// x stars fade to bgnd color
// x stars light up nearby leds
// x stars have variable brightnesses
// shooting stars on button press
