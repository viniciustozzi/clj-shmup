(ns my-quill-sketch.enemies
  (:require [my-quill-sketch.utils :as utils]
            [my-quill-sketch.player :as p]))

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32
   :hitbox 32})

(defn move-enemies [{:keys [enemies] :as state} speed]
  (assoc state :enemies (map #(assoc % :y (+ speed (:y %))) enemies)))

(defn collides-with-shot? [shots e]
  (seq (filter (fn [s]
                 (let [c1 (utils/make-circle (:x e) (:y e) (:hitbox e))
                       c2 (utils/make-circle (:x s) (:y s) (:hitbox s))]
                   (utils/collides? c1 c2))) shots)))

(defn check-enemies-collisions [{:keys [enemies shots] :as state}]
  (assoc state :enemies (let [e-hit (keep (fn [e] (collides-with-shot? shots e)) enemies)]
                            e-hit)))

(check-enemies-collisions {:enemies [(make-enemy 20 20)
                                     (make-enemy 200 300)]
                           :shots [(p/make-shot 20 20)
                                   (p/make-shot 50 50)]})
