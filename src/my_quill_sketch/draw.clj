(ns my-quill-sketch.draw
  (:require [quil.core :as q]))

(defn draw-spaceship [x y w h img]
  (q/image img x y w h))

(defn draw-element [x y size img]
  (q/image img x y size size))

(defn draw-star [x y]
  (q/line x y x (+ y 15)))
