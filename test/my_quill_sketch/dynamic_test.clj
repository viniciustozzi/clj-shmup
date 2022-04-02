(ns my-quill-sketch.dynamic-test
  (:require [my-quill-sketch.dynamic :as dyn]
            [clojure.test :refer [deftest testing is]]))

(deftest make-enemy
  (let [e1 (dyn/make-enemy 50 50)
        e2 (dyn/make-enemy -10 20)]
    (testing "Create basic enemy"
      (is e1 {:x 50 :y 50 :size 32}))
    (testing "Check if enemy has correct size"
      (is (:size e1) 32))
    (testing "Check negative initial x position"
      (is (= (:x e2) -10)
          (= (:y e2) -20)))))

(deftest check-initial-state
  (let [s (dyn/initial-state 300 300)]
    (testing "Check initial position of the player"
      (is (= (:x s) (- 150 (/ 32 2)))
          (= (:y s) (- 150 (/ 32 2)))))
    (testing "Player should start with direction 0 (no movement)"
      (is (= (:dirx s) 0)
          (= (:diry s) 0)))
    (testing "Game should start with no inputs"
      (is (empty? (:input s))))
    (testing "Check initial enemies"
      (is (= (count (:enemies s)) 6)))))

(deftest update-enemies-position
  (testing "Increase y position of each enemy"
    (is (= (dyn/update-enemies {:enemies [{:x 10 :y 10 :size 32}
                                          {:x 30 :y 10 :size 32}]})
           [{:x 10 :y 12 :size 32}
            {:x 30 :y 12 :size 32}]))))

(deftest check-borders
  (testing "Player on (10, 10) moving right and down"
    (is (true? (dyn/check-borders 10 10 30 30 1 1 350 500)))))
