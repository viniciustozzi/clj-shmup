(ns my-quill-sketch.core
  (:require
   [my-quill-sketch.dynamic :as dyn]
   [quil.core :as q]
   [quil.middleware :as m])
  (:gen-class))

(defn -main []
  (q/defsketch my-game
    :host "my-game"
    :size [dyn/screen-width dyn/screen-height]
    :setup dyn/setup
    :draw dyn/on-draw
    :update dyn/on-update
    :settings dyn/settings
    :key-pressed dyn/on-key-pressed
    :key-released dyn/on-key-released
    :features [:keep-on-top :exit-on-close]
    :middleware [m/fun-mode]))
