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
(def shot-speed 5)
(def enemy-speed 2)
(def enemy-shot-speed 5)
(def enemy-spawn-time 2500)
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
   :last-enemies-shot-time 0
   :enemies []
   :enemies-shots []
   :stars (s/spawn-initial-stars screen-width screen-height)
   :score 0
   :input []})

(defn setup
  "Initial setup of the game, called once in the begining.
  Returns the initial state of the game"
  []
  (q/text-font (q/create-font "Arial" 24 true))
  (reset! assets {:player (q/load-image "ship.png")
                  :enemy (q/load-image "enemy.png")
                  :shot (q/load-image "shot.png")
                  :enemy-shot (q/load-image "enemy_shot.png")})
  (initial-state screen-width screen-height))

(defn assets-loaded? []
  (and
   (q/loaded? (:player @assets))
   (q/loaded? (:enemy @assets))
   (q/loaded? (:shot @assets))
   (q/loaded? (:enemy-shot @assets))))

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
  (case (:level state)
    "play" (-> state
               (p/update-dir-x)
               (p/update-dir-y)
               (p/player-shot shot-cooldown (q/millis)))
    "game-over" (if (utils/btn-pressed? :x (:input state))
                  (initial-state screen-width screen-height)
                  state)))

(defn on-update
  "Called every frame, receives global state as argument
  Returns a new updated state at the end of the frame"
  [state]
  (case (:level state)
    "play" (-> state
               (proccess-inputs)
               (s/update-stars screen-width screen-height (q/millis))
               (p/update-player player-speed shot-speed  screen-width screen-height)
               (e/update-enemies screen-width screen-height (q/millis)
                                 enemy-spawn-time enemy-shot-speed enemy-speed))
    "game-over" (proccess-inputs state)))

(defn settings []
  (q/smooth 0))
(defn on-draw
  "Receives global state of the game and draws it to the screen"
  [{:keys [x y w h
           enemies
           shots
           stars
           level
           score
           enemies-shots]}]
  (q/background 0)
  (when (assets-loaded?)
    (case level
      "play" (do (doseq [s stars]
                   (d/draw-star (:x s) (:y s)))
                 (doseq [e enemies]
                   (d/draw-element (:x e) (:y e) (:size e) (:enemy @assets)))
                 (doseq [es enemies-shots]
                   (let [x (:x es)
                         y (:y es)]
                     (when (not (and (nil? x) (nil? y)))
                       (d/draw-element (:x es) (:y es) 16 (:enemy-shot @assets)))))
                 (doseq [s shots]
                   (d/draw-element (:x s) (:y s) (:size s)  (:shot @assets)))
                 (d/draw-spaceship x y w h (:player @assets))
                 (d/draw-game-score score))
      "game-over" (d/draw-game-over screen-width screen-height score))))

(comment
  (use 'my-quill-sketch.dynamic :reload)
  (qa/with-applet my-quill-sketch.core/my-game
    (q/state))

  (qa/with-applet my-quill-sketch.core/my-game (q/no-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/start-loop)))
;; => nil
