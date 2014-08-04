(ns org.playasophy.wonderdome.geometry.layout-harness
  (:require
    (org.playasophy.wonderdome.geometry
      [cartesian :as cartesian]
      [geodesic :as geodesic])
    (org.playasophy.wonderdome.util
      [color :as color]
      [quil :refer [scale-point draw-axes]])
    [quil.core :as quil]))


;;;;; DRAWING FUNCTIONS ;;;;;

(def struts
  (-> 3.787
      (geodesic/edges 3)
      geodesic/ground-slice
      set))


(defn- draw-dome
  [edges]
  (quil/stroke (quil/color 96 128))
  (quil/stroke-weight 3)
  (let [sorted-edges (map vector (range) (cartesian/sort-edges edges))]
    (doseq [[i [a b]] sorted-edges]
      (quil/line
        (scale-point a)
        (scale-point b))
      (apply quil/text (str i)
        (scale-point (cartesian/midpoint 1/2 a b))))))



;;;;; SKETCH FUNCTIONS ;;;;;

(defn- setup
  []
  (quil/frame-rate 30)
  (quil/text-font (quil/create-font "Courier" 18 true))
  (quil/background 0))


(defn- render
  []
  (quil/background 0)
  (quil/translate (* 0.50 (quil/width)) (* 0.50 (quil/height)) 0)
  (quil/rotate-x 0.0 #_ 1.2)
  ;(quil/rotate-z (* (quil/frame-count) 0.003))
  (draw-axes 0.5)
  (draw-dome struts))


(defn start!
  "Creates and starts a sketch to demonstrate the audio input functionality."
  []
 (quil/sketch
    :title "Wonderdome Geodesic Layout Harness"
    :features [:resizable]
    :setup #'setup
    :draw #'render
    :size [1000 600]
    :renderer :opengl))