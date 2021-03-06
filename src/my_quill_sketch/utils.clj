(ns my-quill-sketch.utils
  (:require [clojure.math :as math]))

(defn btn-pressed?
  "Checks if a button is being pressed, receives a keyword button
  such as ':up' or ':left' and a vector of inputs
  (probably :input from global state)
  Returns the pressed button"
  [btn inputs]
  (some #{btn} inputs))

(defn conj-once
  "Adds a value to a collection if the value is not yet there.
  This function checks if the value already exists in the collection
  before adding.
  Returns new collection with item added (or not)"
  [coll val]
  (if (not (some #{val} coll))
    (conj coll val)
    coll))

(defn make-circle [x y radius]
  {:center [x y]
   :radius radius})

(defn collides?
  "Checks if two circles collides with each other
  Expects a circle map such as {:center [20 20] :radius 5}
  Center being a vector of x and y"
  [c1 c2]
  (let [r1 (:radius c1)
        r2 (:radius c2)
        c1-x (get (:center c1) 0)
        c1-y (get (:center c1) 1)
        c2-x (get (:center c2) 0)
        c2-y (get (:center c2) 1)
        dist (math/sqrt (+ (math/pow (- c2-x c1-x) 2.0)
                           (math/pow (- c2-y c1-y) 2.0)))]
    (< dist (+ r1 r2))))

(defn magnitude
  "Calculates the magnitude of a vector [x y]"
  [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalize
  "Normalize a vector [x y]"
  [[x y :as v]]
  (let [m (magnitude v)]
    [(/ x m) (/ y m)]))

(defn vec-subtraction
  "Subtraction of vectors (v1 - v2)"
  [v1 v2]
  (let [x1 (get v1 0)
        x2 (get v2 0)
        y1 (get v1 1)
        y2 (get v2 1)]
    [(- x1 x2) (- y1 y2)]))
