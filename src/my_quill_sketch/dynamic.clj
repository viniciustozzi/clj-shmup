(ns my-quill-sketch.dynamic
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as qa]
            [my-quill-sketch.utils :as utils]))

(defonce speed 3)

(defn setup
  "Initial setup of the game, called once in the begining.
  Returns the initial state of the game"
  []
  (q/text-font (q/create-font "Arial" 24 true))
  {:x 0 :y 0
   :dirx 0 :diry 0
   :image (q/load-image "mage.png")
   :input []})

(defn on-key-pressed
  "Called when any key is pressed.
  Adds to the :input vector only the relevant inputs to this game"
  [{:keys [input] :as state} event]
  (let [key (:key event)]
    (case key
      :left (assoc state :input (utils/conj-once input key))
      :right (assoc state :input (utils/conj-once input key))
      :up (assoc state :input (utils/conj-once input key))
      :down (assoc state :input (utils/conj-once input key)))))

(defn on-key-released [{:keys [input] :as state} event]
  "Called when any key is released.
  Removes to the :input vector only the relevant inputs to this game"
  (let [key (:key event)]
    (case key
      :left (assoc state :input (vec (remove #(= % key) input)))
      :right (assoc state :input (vec (remove #(= % key) input)))
      :up (assoc state :input (vec (remove #(= % key) input)))
      :down (assoc state :input (vec (remove #(= % key) input))))))

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

(defn update-pos [{:keys [x y dirx diry] :as state}]
  (-> state
      (assoc-in [:x] (+ x (* speed dirx)))
      (assoc-in [:y] (+ y (* speed diry)))))

(defn on-update
  "Called every frame, receives global state as argument
  Returns a new updated state at the end of the frame"
  [state]
  (-> state
      (proccess-inputs)
      (update-pos)))

(defn on-draw
  "Receives global state of the game and draws it to the screen"
  [state]
  (q/background 0)
  (let [im (:image state)]
    (when (q/loaded? im)
      (q/image im (:x state) (:y state) 130 120)
      (q/text (pr-str (:input state)) 100 100)
      (q/fill 255))))

(comment
  (use 'my-quill-sketch.dynamic :reload)
  (qa/with-applet my-quill-sketch.core/my-game (q/no-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/start-loop))
  (qa/with-applet my-quill-sketch.core/my-game (q/random 10)))
