(ns my-quill-sketch.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [my-quill-sketch.dynamic :as dyn]))

(defn -main []
  (q/defsketch my-game
    :host "my-gamek"
    :size [800 600]
    :setup dyn/setup
    :draw dyn/on-draw
    :update dyn/on-update
    :key-pressed dyn/on-key-pressed
    :key-released dyn/on-key-released
    :features [:keep-on-top]
    :middleware [m/fun-mode]))

(-main)
