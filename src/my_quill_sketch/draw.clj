(ns my-quill-sketch.draw
  (:require [quil.core :as q]))

(defn draw-spaceship [x y w h img]
  (q/image img x y w h))

(defn draw-element [x y size img]
  (q/image img x y size size))

(defn draw-star [x y]
  (q/stroke 255)
  (q/point x y))

(defn draw-game-over [scr-w scr-h score]
  (q/fill 255 0 0)
  (q/text "YOU DIED" (- (/ scr-w 2) 50) (- (/ scr-h 2) 40))
  (q/fill 255 255 255)
  (q/text (str "Your score is: " score) (- (/ scr-w 2) 75) (/ scr-h 2)))

(defn draw-game-score [score]
  (q/fill 255 255 255)
  (q/text (str "SCORE: " score) 10 30))
