(ns playasophy.wonderdome.mode.lantern
  (:require
    [playasophy.wonderdome.util.color :as color])
  (:import
    playasophy.wonderdome.mode.Mode))


(def ^:private ^:const adjustment-rate
  "Rate at which the brightness level changes per millisecond."
  0.0005)


(defrecord LanternMode
  [brightness]

  Mode

  (update
    [this event]
    (condp = [(:type event) (:input event)]
      [:button/press :L]
      (assoc this :brightness 0.0)

      [:button/press :R]
      (assoc this :brightness 1.0)

      [:axis/direction :y-axis]
      (let [delta (* (or (:value event) 0)
                     (or (:elapsed event) 0)
                     adjustment-rate)
            level (-> brightness (+ delta) (min 1.0) (max 0.0))]
        (assoc this :brightness level))

      this))


  (render
    [this pixel]
    (color/gray brightness)))


(defn init
  "Creates a new lantern mode with starting brightness."
  [brightness]
  (->LanternMode brightness))
