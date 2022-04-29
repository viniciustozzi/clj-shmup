(ns my-quill-sketch.enemies
  (:require
   [clojure.set :refer [difference]]
   [my-quill-sketch.utils :as utils]))

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32
   :hitbox 32})

(defn spawn-wave [scr-w scr-h]
  (mapv (fn [_] (make-enemy (rand scr-w) (- (rand scr-h) scr-h)))
        (range 0 3)))

(defn spawn-enemies [{:keys [last-spawn-time enemies] :as state}
                     current-time default-spawn-time
                     scr-w scr-h]
  (if (< default-spawn-time (- current-time last-spawn-time))
    (-> state
        (assoc :enemies (into [] (concat enemies
                                         (spawn-wave scr-w scr-h))))
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
