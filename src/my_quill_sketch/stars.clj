(ns my-quill-sketch.stars)

(defn make-star [x y]
  {:x x
   :y y})

(def state {:stars []})

(defn spawn-stars [state scr-w scr-h]
  (assoc state :stars  (map (fn [_] (make-star (rand scr-w)
                                               (rand scr-h)))
                            (range 1 200))))

(defn move-stars [{:keys [stars] :as state}]
  (assoc state :stars (mapv (fn [s] {:x (:x s)
                                     :y (inc (:y s))})
                            stars)))
