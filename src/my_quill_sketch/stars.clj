(ns my-quill-sketch.stars)

(defn make-star [x y speed]
  {:x x
   :y y
   :speed speed})

(defn spawn-star-group [scr-w scr-h]
  (map (fn [_] (make-star (rand scr-w)
                          (- 100 (rand (/ scr-h 2)))
                          (inc (rand 3))))
       (range 1 10)))

(defn spawn-stars [{:keys [last-star-time stars] :as state}
                   current-time scr-w scr-h]
  (if (< 1800 (- current-time last-star-time))
    (-> state
        (assoc :stars (into [] (concat stars (spawn-star-group scr-w scr-h))))
        (assoc :last-star-time current-time))
    state))

(defn move-stars [{:keys [stars] :as state}]
  (assoc state :stars (mapv (fn [s] {:x (:x s)
                                     :y (inc (:y s))})
                            stars)))