(ns my-quill-sketch.dynamic
  (:require
   [my-quill-sketch.draw :as d]
   [my-quill-sketch.enemies :as e]
   [my-quill-sketch.player :as p]
   [my-quill-sketch.stars :as s]
   [my-quill-sketch.utils :as utils]
   [quil.applet :as qa]
   [quil.core :as q]))

(def player-speed 3)
(def shot-cooldown 500)
(def shot-speed 3)
(def enemy-speed 2)
(def enemy-spawn-time 3000)
(def screen-width 350)
(def screen-height 500)

(def assets (atom nil))

(defn initial-state [scr-w scr-h]
  {:x (- (/ scr-w 2) 16) :y (- scr-h 50)
   :w 32 :h 32
   :hitbox 8
   :dead false
   :level "play"
   :dirx 0 :diry 0
   :shots []
   :last-shot-time 0
   :last-spawn-time 0
   :last-star-time 0
   :enemies []
   :enemies-shots []
   :stars (s/spawn-star-group scr-w scr-h)
   :input []})

(defn setup
  "Initial setup of the game, called once in the begining.
  Returns the initial state of the game"
  []
  (q/text-font (q/create-font "Arial" 24 true))
  (reset! assets {:player (q/load-image "ship.png")
                  :enemy (q/load-image "enemy.png")
                  :shot (q/load-image "shot.png")})
  (initial-state screen-width screen-height))

(defn assets-loaded? []
  (and
   (q/loaded? (:player @assets))
   (q/loaded? (:enemy @assets))
   (q/loaded? (:shot @assets))))

(defn on-key-pressed
  "Called when any key is pressed.
  Adds to the :input vector if the key was pressed"
  [{:keys [input] :as state} event]
  (let [key (:key event)]
    (assoc state :input (utils/conj-once input key))))

(defn on-key-released
  "Called when any key is released.
  Removes from the :input vector if the key was relesed"
  [{:keys [input] :as state} event]
  (let [key (:key event)]
    (assoc state :input (vec (remove #(= % key) input)))))

(defn proccess-inputs
  "Process every keyword in :input from 'state'
  and update 'state' accordingly"
  [state]
  (-> state
      (p/update-dir-x)
      (p/update-dir-y)
      (p/player-shot shot-cooldown (q/millis))))

(defn on-update
  "Called every frame, receives global state as argument
  Returns a new updated state at the end of the frame"
  [state]
  (-> state
      (proccess-inputs)
      (s/spawn-stars (q/millis) screen-width screen-height)
      (s/move-stars)
      (p/update-player-pos player-speed screen-width screen-height)
      (p/move-shots shot-speed)
      (p/player-collision)
      (p/check-death)
      (e/spawn-enemies (q/millis) enemy-spawn-time screen-width screen-height)
      (e/move-enemies enemy-speed)
      (e/check-collision-enemies->shot)))

(defn settings []
  (q/smooth 0))

(defn on-draw
  "Receives global state of the game and draws it to the screen"
  [{:keys [x y w h
           enemies shots stars level] :as state}]
  (q/background 0)
  (when (assets-loaded?)
    (case level
      "play" (do (doseq [s stars]
                   (d/draw-star (:x s) (:y s)))
                 (doseq [e enemies]
                   (d/draw-element (:x e) (:y e) (:size e) (:enemy @assets)))
                 (doseq [s shots]
                   (d/draw-element (:x s) (:y s) (:size s)  (:shot @assets)))
                 (d/draw-spaceship x y w h (:player @assets)))
      "game-over" (d/draw-game-over screen-width screen-height))))

(comment
  (use 'my-quill-sketch.dynamic :reload)
  (qa/with-applet my-quill-sketch.core/my-game
    (q/state))

  (qa/with-applet my-quill-sketch.core/my-game (q/no-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/start-loop)))
