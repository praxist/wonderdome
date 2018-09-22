package playasophy.wonderdome.util;

import clojure.lang.Keyword;
import clojure.java.api.Clojure;

public final class ClojureKeyword {
    public static Keyword TYPE = (Keyword) Clojure.read(":type");
    public static Keyword INPUT = (Keyword) Clojure.read(":input");
    public static Keyword TIME_TICK = (Keyword) Clojure.read(":time/tick");

    // Which strip of LEDs [0-5]
    public static Keyword STRIP = (Keyword) Clojure.read(":strip");

    // LED position of a single strip [0-239]
    public static Keyword PIXEL = (Keyword) Clojure.read(":pixel");

    // See graph.clj
    public static Keyword GRAPH_EDGE = (Keyword) Clojure.read(":graph/edge");
    public static Keyword GRAPH_OFFSET = (Keyword) Clojure.read(":graph/offset");
}
