(ns user
  (:require
    [clj-time.core :as time]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.repl :refer :all]
    [clojure.string :as str]
    [clojure.tools.namespace.repl :refer [refresh]]
    [environ.core :refer [env]]
    [com.stuartsierra.component :as component]
    (org.playasophy.wonderdome
      [core :as wonder]
      [layout :as layout]
      [util :refer [color]])
    (org.playasophy.wonderdome.display
      [core :as display]
      [processing :as processing])))


(def dimensions
  "Geodesic dome and pixel strip dimensions."
  {:radius 3.688         ; 12.1'
   :pixel-spacing 0.02   ; 2 cm
   :strip-pixels 240
   :strips 6})


; TODO: dynamically load modes?
(def config
  {:layout (layout/star dimensions)
   :display (processing/display [1000 600] (:radius dimensions))
   :modes []})


(def system
  (wonder/initialize config))


(defn start!
  "Initialize the wonderdome for local development."
  []
  (alter-var-root #'system component/start)
  (display/set-colors!
    (:display system)
    (repeat 6 (repeat 240 (color 255 255 0))))
  :started)


(defn stop!
  "Stops the wonderdome system and closes the display window."
  []
  (alter-var-root #'system component/stop)
  :stopped)


(defn reload!
  []
  (stop!)
  (refresh :after 'user/start!))
