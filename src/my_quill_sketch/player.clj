(ns my-quill-sketch.player
  (:require [my-quill-sketch.utils :as utils]
            [quil.core :as q]))

(defn make-shot [x y]
  {:x x
   :y y
   :size 16
   :hitbox 16})

(defn update-dir-x [{:keys [input] :as state}]
  (cond
    (utils/btn-pressed? :left input) (assoc state :dirx -1)
    (utils/btn-pressed? :right input) (assoc state :dirx 1)
    :else (assoc state :dirx 0)))

(defn update-dir-y [{:keys [input] :as state}]
  (cond
    (utils/btn-pressed? :up input) (assoc state :diry -1)
    (utils/btn-pressed? :down input) (assoc state :diry 1)
    :else (assoc state :diry 0)))

(defn check-borders [x y w h dirx diry scr-w scr-h speed]
  (let [new-x (+ x (* speed dirx))
        new-y (+ y (* speed diry))]
    (and (> new-x 0)
         (< (+ w new-x) scr-w)
         (> new-y 0)
         (< new-y (+ h new-y) scr-h))))

(defn update-player-pos [{:keys [x y w h dirx diry] :as state}
                         speed
                         scr-w scr-h]
  (-> state
      (assoc-in [:x] (if (check-borders x y w h
                                        dirx diry
                                        scr-w scr-h
                                        speed)
                       (+ x (* speed dirx))
                       x))
      (assoc-in [:y] (if (check-borders x y w h
                                        dirx diry
                                        scr-w scr-h
                                        speed)
                       (+ y (* speed diry))
                       y))))

(defn can-shoot? [{:keys [last-shot-time]}
                  cooldown-time
                  current-time]
  (< cooldown-time (- current-time last-shot-time)))

(defn player-shot [{:keys [input x y shots] :as state}
                   cooldown-time
                   current-time]
  (if (and (utils/btn-pressed? :x input)
           (can-shoot? state cooldown-time current-time))
    (-> state
        (assoc :shots (conj shots (make-shot x y)))
        (assoc :last-shot-time current-time))
    state))

(defn move-shots [{:keys [shots] :as state} speed]
  (assoc state :shots (map #(assoc % :y (- (:y %) speed)) shots)))

(defn player-collision [{:keys [x y
                                hitbox
                                enemies
                                enemies-shots] :as state}]
  (assoc state :dead (or
                      (seq (filter #(utils/collides?
                                     (utils/make-circle x y hitbox)
                                     (utils/make-circle (:x %) (:y %) (:hitbox %))) enemies-shots))
                      (seq (filter #(utils/collides?
                                     (utils/make-circle x y hitbox)
                                     (utils/make-circle (:x %) (:y %) (:hitbox %))) enemies)))))

(defn check-death [{:keys [level dead] :as state}]
  (assoc state :level (if dead
                        "game-over"
                        level)))
