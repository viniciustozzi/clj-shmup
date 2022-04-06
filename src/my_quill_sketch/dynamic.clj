(ns my-quill-sketch.dynamic
  (:require [quil.core :as q]
            [quil.applet :as qa]
            [my-quill-sketch.utils :as utils]
            [my-quill-sketch.player :as p]
            [my-quill-sketch.enemies :as e]))

(def player-speed 3)
(def shot-cooldown 500) ;;in ms
(def shot-speed 3)
(def enemy-speed 1)
(def screen-width 350)
(def screen-height 500)

(def assets (atom nil))

(defn initial-state [scr-w scr-h]
  {:x (- (/ scr-w 2) 16) :y (- scr-h 50)
   :w 32 :h 32
   :dirx 0 :diry 0
   :shots []
   :last-shot-time 0
   :enemies (vec (map
                  #(e/make-enemy (* 50 %) 10)
                  (range 1 7)))
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
      (p/update-player-pos player-speed screen-width screen-height)
      (p/move-shots shot-speed)
      (e/move-enemies enemy-speed)))

(defn settings []
  (q/smooth 0))

(defn on-draw
  "Receives global state of the game and draws it to the screen"
  [state]
  (q/background 0)
  (q/fill 255)
  (q/stroke 255)
  (when (assets-loaded?)
    (q/image (:player @assets) (:x state) (:y state) (:w state) (:w state))
    (q/text (pr-str (:last-shot-time state)) 40 40)
    (q/text (pr-str (q/millis)) 40 60)
    (doseq [e (:enemies state)]
      (q/image (:enemy @assets) (:x e) (:y e) (:size e) (:size e)))
    (doseq [s (:shots state)]
      (q/image (:shot @assets) (:x s) (:y s) (:size s) (:size s)))))

(comment
  (use 'my-quill-sketch.dynamic :reload)
  (qa/with-applet my-quill-sketch.core/my-game
    (q/state))

  (qa/with-applet my-quill-sketch.core/my-game (q/no-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/start-loop)))
