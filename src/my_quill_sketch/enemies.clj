(ns my-quill-sketch.enemies
  (:require [my-quill-sketch.utils :as utils]
            [my-quill-sketch.player :as p]))

(defn make-enemy [x y]
  {:id (utils/gen-id)
   :x x
   :y y
   :size 32
   :hitbox 32})

(defn move-enemies [{:keys [enemies] :as state} speed]
  (assoc state :enemies (map #(assoc % :y (+ speed (:y %))) enemies)))

(def state {:enemies [(make-enemy 10 10) (make-enemy 150 150)]
            :shots [(p/make-shot 10 10)]})

(defn enemy-shot-collision [enemy shot]
  (when (utils/collides? (utils/make-circle (:x shot) (:y shot) (:hitbox shot))
                         (utils/make-circle (:x enemy) (:y enemy) (:hitbox enemy)))
    (vector enemy shot)))

(defn enemies-shots-collision [{:keys [shots enemies] :as state}]
  (flatten (for [s shots
                 e enemies]
             (enemy-shot-collision s e))))

(enemies-shots-collision state)

(defn check-collision-enemies->shot [{:keys [enemies shots] :as state}]
  ;(assoc state :enemies (vec (remove #(collides-with-shot? shots %) enemies)))
      ;(assoc :shots (remove (fn [s] (collides-with-enemy? enemies s) shots)))
  )

(vec (distinct (flatten [[1 2] [2 3]])))
