(ns my-quill-sketch.enemies
  (:require [my-quill-sketch.utils :as utils]
            [my-quill-sketch.player :as p]
            [clojure.set :refer [difference]]))

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32
   :hitbox 32})

(defn spawn-wave []
  (vec (map
        #(make-enemy (* 50 %) 10)
        (range 1 7))))

(defn spawn-enemies [{:keys [last-spawn-time enemies] :as state}
                     current-time
                     default-spawn-time]
  (if (< default-spawn-time (- current-time last-spawn-time))
    (-> state
        (assoc :enemies (into [] (concat enemies (spawn-wave))))
        (assoc :last-spawn-time current-time))
    state))

(defn move-enemies [{:keys [enemies] :as state} speed]
  (assoc state :enemies (map #(assoc % :y (+ speed (:y %))) enemies)))

(defn enemy-shot-collision [enemy shot]
  (when (utils/collides? (utils/make-circle (:x shot) (:y shot) (:hitbox shot))
                         (utils/make-circle (:x enemy) (:y enemy) (:hitbox enemy)))
    (vector enemy shot)))

(defn enemies-shots-collision [enemies shots]
  (remove nil? (distinct (flatten (for [s shots
                                        e enemies]
                                    (enemy-shot-collision s e))))))

(defn check-collision-enemies->shot [{:keys [enemies shots] :as state}]
  (let [collisions (enemies-shots-collision enemies shots)]
    (-> state
        (assoc :enemies (vec (difference (set enemies) (set collisions))))
        (assoc :shots (vec (difference (set shots) (set collisions)))))))
