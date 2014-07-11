(ns org.playasophy.wonderdome.display.processing
  (:require
    [com.stuartsierra.component :as component]
    [org.playasophy.wonderdome.display.core :as display]
    (org.playasophy.wonderdome.geometry
      [geodesic :as geodesic])
    (quil
      [applet :as applet]
      [core :as quil])))


;;;;; HELPER FUNCTIONS ;;;;;

(def scale
  "Scale up all coordinates to map meters to screen space."
  70.0)


(defn- scale-point
  "Scales up a point vector to screen space."
  [p]
  (vec (map (partial * scale) p)))



;;;;; PROCESSING SKETCH ;;;;;


(defn- setup-sketch
  []
  (quil/background 0)
  (quil/stroke 0))


(defn- draw-axes
  [length]
  (let [l (* scale length)]
    (quil/stroke-weight 1)
    ; x-axis red
    (quil/stroke (quil/color 255 0 0))
    (quil/line [0 0 0] [l 0 0])
    ; y-axis green
    (quil/stroke (quil/color 0 255 0))
    (quil/line [0 0 0] [0 l 0])
    ; z-axis blue
    (quil/stroke (quil/color 0 0 255))
    (quil/line [0 0 0] [0 0 l])))


(defn- draw-ground
  [radius]
  (let [c (quil/color 210 200 175)
        r (* scale radius)]
    (quil/fill c)
    (quil/stroke c)
    (quil/ellipse 0 0 r r)))


(defn- draw-dome
  [edges]
  (quil/stroke (quil/color 96 128))
  (quil/stroke-weight 3)
  (doseq [[a b] edges]
    (quil/line
      (scale-point a)
      (scale-point b))))


(defn- draw-strip
  "Draws a line representing the path of a pixel strip. Strip should be a vector
  of spherical coordinate maps."
  [strip]
  (quil/stroke-weight 1)
  (quil/stroke 0 64 196)
  (doseq [[a b] (partition 2 1 strip)]
    (quil/line
      (-> a :coord scale-point)
      (-> b :coord scale-point))))


(defn- draw-pixel
  "Draws a single pixel with the given color. The pixel should be a spherical
  coordinate map."
  [coordinate color]
  (quil/stroke-weight 3)
  (quil/stroke color)
  (->> coordinate
       :coord
       scale-point
       (apply quil/point)))


(defn- draw-pixel-strips
  [layout colors]
  (dorun (map draw-strip layout))
  (dorun (map #(dorun (map draw-pixel %1 %2)) layout colors)))


(defn- render
  [display]
  (quil/background 0)
  (quil/translate (* 0.50 (quil/width)) (* 0.60 (quil/height)) 0)
  (quil/rotate-x 1.2)
  (quil/rotate-z (* (quil/frame-count) 0.003))
  (draw-axes 0.5)
  ;(draw-ground 4.0)
  (draw-dome (:dome display))
  (draw-pixel-strips
    (:layout display)
    @(:colors display)))



;;;;; PROCESSING DISPLAY ;;;;;

(defrecord ProcessingDisplay
  [size dome layout colors])

(extend-type ProcessingDisplay
  component/Lifecycle

  (start
    [this]
    (assoc this :sketch
      (quil/sketch
        :title "Playasophy Wonderdome"
        :setup setup-sketch
        :draw #(render this)
        :size (:size this)
        :renderer :opengl)))


  (stop
    [this]
    (when-let [sketch (:sketch this)]
      (applet/applet-close sketch))
    (dissoc this :sketch))


  display/Display

  (set-colors!
    [this colors]
    (swap! (:colors this) (constantly colors))
    nil))


(defn display
  "Creates a new simulation display using Processing. Takes a vector giving the
  width and height in pixels, and a radius of geometric dome to draw. The pixel
  layout must be injected at runtime before starting the display."
  [size radius]
  (let [dome (-> radius (+ 0.05) (geodesic/edges 3) geodesic/ground-slice set)]
    (component/using
      (ProcessingDisplay. size dome nil (atom []))
      [:layout])))
