(ns my-quill-sketch.enemies)

(defn make-enemy [x y]
  {:x x
   :y y
   :size 32})

(defn move-enemies [{:keys [enemies] :as state} speed]
  (assoc state :enemies (map #(assoc % :y (+ speed (:y %))) enemies)))
