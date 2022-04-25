(ns my-quill-sketch.enemies
  (:require [my-quill-sketch.utils :as utils]
            [my-quill-sketch.player :as p]
            [clojure.set :refer [difference]]))

(defn make-enemy [x y]
  {:x x
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

(defn enemies-shots-collision [enemies shots]
  (remove nil? (distinct (flatten (for [s shots
                                        e enemies]
                                    (enemy-shot-collision s e))))))


;; (def state {:enemies [(make-enemy 10 10) (make-enemy 150 150)]
;;             :shots [(p/make-shot 10 10) (p/make-shot 300 300)]})

(defn check-collision-enemies->shot [{:keys [enemies shots] :as state}]
  (let [collisions (enemies-shots-collision enemies shots)]
    (-> state
        (assoc :enemies (vec (difference (set enemies) (set collisions))))
        (assoc :shots (vec (difference (set shots) (set collisions)))))))
