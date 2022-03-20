(ns my-quill-sketch.dynamic
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as qa]
            [my-quill-sketch.utils :as utils]))

(defonce speed 3)
(defonce screen-width 350)
(defonce screen-height 500)

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32})

(defn initial-state []
  {:x 30 :y 30
   :w 32 :h 32
   :dirx 0 :diry 0
   :assets {:player (q/load-image "ship.png")
            :enemy (q/load-image "enemy.png")}
   :enemies (vec (map
                  #(make-enemy (* 50 %) 10)
                  (range 1 7)))
   :input []})

(defn setup
  "Initial setup of the game, called once in the begining.
  Returns the initial state of the game"
  []
  (q/text-font (q/create-font "Arial" 24 true))
  (initial-state))

(defn on-key-pressed
  "Called when any key is pressed.
  Adds to the :input vector only the relevant inputs to this game"
  [{:keys [input] :as state} event]
  (let [key (:key event)]
    (case key
      :left (assoc state :input (utils/conj-once input key))
      :right (assoc state :input (utils/conj-once input key))
      :up (assoc state :input (utils/conj-once input key))
      :down (assoc state :input (utils/conj-once input key))
      state)))

(defn on-key-released [{:keys [input] :as state} event]
  "Called when any key is released.
  Removes to the :input vector only the relevant inputs to this game"
  (let [key (:key event)]
    (case key
      :left (assoc state :input (vec (remove #(= % key) input)))
      :right (assoc state :input (vec (remove #(= % key) input)))
      :up (assoc state :input (vec (remove #(= % key) input)))
      :down (assoc state :input (vec (remove #(= % key) input)))
      state)))

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

(defn proccess-inputs
  "Process every keyword in :input from 'state'
  and update 'state' accordingly"
  [state]
  (-> state
      (update-dir-x)
      (update-dir-y)))

(defn check-borders [x y w h dirx diry]
  (let [new-x (+ x (* speed dirx))
        new-y (+ y (* speed diry))]
    (and (> new-x 0)
         (< (+ w new-x) screen-width)
         (> new-y 0)
         (< new-y (+ h new-y) screen-height))))

(defn update-pos [{:keys [x y w h dirx diry] :as state}]
  (-> state
      (assoc-in [:x] (if (check-borders x y w h dirx diry)
                       (+ x (* speed dirx))
                       x))
      (assoc-in [:y] (if (check-borders x y w h dirx diry)
                       (+ y (* speed diry))
                       y))))

(defn on-update
  "Called every frame, receives global state as argument
  Returns a new updated state at the end of the frame"
  [state]
  (-> state
      (proccess-inputs)
      (update-pos)))

(defn settings []
  (q/smooth 0))

(defn on-draw
  "Receives global state of the game and draws it to the screen"
  [state]
  (q/background 0)
  (q/fill 255)
  (q/stroke 255)
  (let [player (-> state :assets :player)
        enemy (-> state :assets :enemy)]
    (when (and (q/loaded? player) (q/loaded? enemy))
      (q/image player (:x state) (:y state) (:w state) (:w state))
      (doseq [e (:enemies state)]
        (q/image enemy (:x e) (:y e) (:size e) (:size e))))))

(comment
  (use 'my-quill-sketch.dynamic :reload)
  (qa/with-applet my-quill-sketch.core/my-game (q/no-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/start-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/random 10)))
