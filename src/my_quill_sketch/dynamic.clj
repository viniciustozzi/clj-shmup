(ns my-quill-sketch.dynamic
  (:require [quil.core :as q]
            [quil.applet :as qa]
            [my-quill-sketch.utils :as utils]))

(def player-speed 3)
(def shot-speed 2)
(def enemy-speed 1)
(def screen-width 350)
(def screen-height 500)

(def assets (atom nil))

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32})

(defn make-shot [x y]
  {:x x
   :y y
   :size 16})

(defn initial-state [scr-w scr-h]
  {:x (- (/ scr-w 2) 16) :y (- scr-h 50)
   :w 32 :h 32
   :dirx 0 :diry 0
   :shots []
   :enemies (vec (map
                  #(make-enemy (* 50 %) 10)
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

(defn player-shot [{:keys [input x y shots] :as state}]
  (if (utils/btn-pressed? :x input)
    (assoc state :shots (conj shots (make-shot x y)))
    state))

(defn move-shots [{:keys [x y shots] :as state} speed]
  (assoc state :shots (map #(assoc % :y (- (:y %) speed)) shots)))

(defn proccess-inputs
  "Process every keyword in :input from 'state'
  and update 'state' accordingly"
  [state]
  (-> state
      (update-dir-x)
      (update-dir-y)
      (player-shot)))

(defn check-borders [x y w h dirx diry scr-w scr-h]
  (let [new-x (+ x (* player-speed dirx))
        new-y (+ y (* player-speed diry))]
    (and (> new-x 0)
         (< (+ w new-x) scr-w)
         (> new-y 0)
         (< new-y (+ h new-y) scr-h))))

(defn update-player-pos [{:keys [x y w h dirx diry] :as state}]
  (-> state
      (assoc-in [:x] (if (check-borders x y w h dirx diry screen-width screen-height)
                       (+ x (* player-speed dirx))
                       x))
      (assoc-in [:y] (if (check-borders x y w h dirx diry screen-height screen-height)
                       (+ y (* player-speed diry))
                       y))))

(defn move-enemies [{:keys [enemies] :as state} speed]
  (assoc state :enemies (map #(assoc % :y (+ speed (:y %))) enemies)))

(defn on-update
  "Called every frame, receives global state as argument
  Returns a new updated state at the end of the frame"
  [state]
  (-> state
      (proccess-inputs)
      (update-player-pos)
      (move-shots shot-speed)
      (move-enemies enemy-speed)))

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
    (q/text (pr-str (:input state)) 40 40)
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
