(ns user
  (:require
    [clojure.core.async :as async :refer [<! <!! >! >!!]]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.repl :refer :all]
    [clojure.stacktrace :refer [print-cause-trace]]
    [clojure.string :as str]
    [clojure.tools.namespace.repl :refer [refresh]]
    [com.stuartsierra.component :as component]
    (playasophy.wonderdome
      [config :as config]
      [system :as system])
    (playasophy.wonderdome.display
      [processing :as processing])
    (playasophy.wonderdome.geometry
      [layout :as layout]
      [layout-harness :as layout-harness])
    (playasophy.wonderdome.input
      [audio-harness :as audio-harness]
      [gamepad-harness :as gamepad-harness])
    (playasophy.wonderdome.util
      [color-harness :as color-harness])))


(def dome-radius
  "Geodesic dome radius in meters (~12.1')."
  3.688)


(def system nil)


(defn init!
  "Initialize the wonderdome system for local development."
  [config-path]
  (config/clear!)
  (-> config-path
      (config/read-file)
      (system/initialize)
      (assoc :display
        (component/using
          (processing/display [1000 600] dome-radius)
          [:layout :event-channel]))
      (constantly)
      (->> (alter-var-root #'system)))
  :init)


(defn start!
  "Starts the wonderdome system running."
  []
  (when system
    (alter-var-root #'system component/start))
  :start)


(defn go!
  "Initializes with the default config and starts the wonderdome system."
  []
  (init! "config.clj")
  (start!))


(defn stop!
  "Stops the wonderdome system and closes the display window."
  []
  (when system
    (alter-var-root #'system component/stop))
  :stop)


(defn reload!
  "Reloads all changed namespaces to update code, then re-launches the system."
  []
  (stop!)
  (refresh :after 'user/go!))
