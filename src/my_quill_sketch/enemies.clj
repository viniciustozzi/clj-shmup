(ns my-quill-sketch.enemies
  (:require
   [clojure.set :refer [difference]]
   [my-quill-sketch.utils :as utils]
   [my-quill-sketch.enemies :as e]))

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32
   :hitbox 32})

(defn make-enemy-shot [x y target]
  {:x x
   :y y
   :size 16
   :hitbox 16
   :dir (utils/normalize
         (utils/vec-subtraction [(get target 0) (get target 1)]
                                [x y]))})

(defn spawn-shot [enemies player]
  (let [n (rand-int (count enemies))
        e (get (vec enemies) n)]
    (if (nil? e)
      (make-enemy-shot -400 0 player)
      (make-enemy-shot (:x e) (:y e) player))))

(defn spawn-shots [{:keys [last-enemies-shot-time
                           enemies-shots
                           enemies
                           x y] :as state}
                   current-time]
  (if (< 500 (- current-time last-enemies-shot-time))
    (-> state
        (assoc :enemies-shots (conj enemies-shots (spawn-shot enemies [x y])))
        (assoc :last-enemies-shot-time current-time))
    state))

(defn- move-shot [s speed]
  (let [d (:dir s)
        new-x (+ (:x s) (* speed (get d 0)))
        new-y (+ (:y s) (* speed (get d 1)))]
    (-> s
        (assoc :x new-x)
        (assoc :y new-y))))

(defn move-enemy-shots [{:keys [enemies-shots] :as state} speed]
  (assoc state :enemies-shots
         (map #(move-shot % speed) enemies-shots)))

(defn spawn-wave [scr-w scr-h]
  (mapv (fn [_] (let [x (rand (- scr-w 40))
                      y (- (rand scr-h) (- scr-h 32))]
                  (make-enemy x y)))
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
    (let [new-enemies (vec (difference (set enemies) (set collisions)))
          new-score (- (count enemies) (count new-enemies))]
      (-> state
          (assoc :score (if (< 0 new-score)
                          (inc (:score state))
                          (:score state)))
          (assoc :enemies new-enemies)
          (assoc :shots (vec (difference (set shots) (set collisions))))))))

(defn remove-enemies-out-of-screen [{:keys [enemies] :as state} scr-h]
  (assoc state :enemies (remove #(> (:y %) scr-h) enemies)))

(defn clean-shots [{:keys [enemies-shots] :as state} scr-w scr-h]
  (assoc state :enemies-shots (filterv
                               (fn [s]
                                 (let [x (:x s) y (:y s)]
                                   (and (< 0 x scr-w)
                                        (< 0 y scr-h))))
                               enemies-shots)))

(defn update-enemies [state scr-w scr-h
                      current-time enemy-spawn-time
                      enemy-shot-speed enemy-speed]
  (-> state
      (spawn-enemies current-time enemy-spawn-time scr-w scr-h)
      (move-enemies enemy-speed)
      (check-collision-enemies->shot)
      (remove-enemies-out-of-screen scr-h)
      (clean-shots scr-w scr-h)
      (spawn-shots current-time)
      (move-enemy-shots enemy-shot-speed)))
