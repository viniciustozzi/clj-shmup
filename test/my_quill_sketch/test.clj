(ns my-quill-sketch.test
  (:require [my-quill-sketch.dynamic :as dyn]
            [my-quill-sketch.player :as p]
            [my-quill-sketch.enemies :as e]
            [my-quill-sketch.utils :as utils]
            [clojure.test :refer [deftest testing is]]))

(deftest make-enemy
  (let [e1 (e/make-enemy 50 50)
        e2 (e/make-enemy -10 20)]
    (testing "Create basic enemy"
      (is {:x 50 :y 50 :size 32 :hitbox 32} e1))
    (testing "Check if enemy has correct size"
      (is 32 (:size e1)))
    (testing "Check negative initial x position"
      (is (= -10 (:x e2))
          (= -10 (:y e2))))))

(deftest make-circle
  (is (= {:center [10 20]
          :radius 30}
         (utils/make-circle 10 20 30))))

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
      (is (= (count (:enemies s)) 0)))))

(deftest update-enemies-position
  (testing "Increase y position of each enemy based on the enemy speed"
    (is (= {:enemies [{:x 10 :y 12 :size 32}
                      {:x 30 :y 12 :size 32}]}
           (e/move-enemies {:enemies [{:x 10 :y 10 :size 32}
                                      {:x 30 :y 10 :size 32}]} 2)))))

(deftest check-borders
  (testing "Player on (10, 10) moving right and down"
    (is (true? (p/check-borders 10 10 30 30 1 1 350 500 3)))))

(deftest make-shot
  (is (= {:x 50 :y 40 :size 16 :hitbox 16}
         (p/make-shot 50 40))))

(deftest player-shot
  (is (= {:x 30 :y 30
          :shots [{:x 30 :y 30 :size 16 :hitbox 16}]
          :last-shot-time 30.5
          :input [:x :up]}
         (p/player-shot {:x 30 :y 30
                         :input [:x :up]
                         :shots []
                         :last-shot-time 0} 0.5 30.5))))

(deftest update-shots
  (is (= {:x 30 :y 30
          :shots [{:x 30 :y 28 :size 16}]}
         (p/move-shots {:x 30 :y 30
                        :shots [{:x 30
                                 :y 30
                                 :size 16}]} 2))))

(deftest can-shoot?
  (testing "Player shooting when not in cooldown"
    (is (true? (p/can-shoot? {:last-shot-time 10.0} 2 12.5))))
  (testing "Player shooting when in cooldown"
    (is (false? (p/can-shoot? {:last-shot-time 10.0} 2 5.0)))))

(deftest collides?
  (testing "Circles that collide"
    (is (true? (utils/collides? {:center [20 20] :radius 5}
                                {:center [15 20] :radius 10}))))
  (testing "Circles that do not collide"
    (is (false? (utils/collides? {:center [20 20] :radius 5}
                                 {:center [35 30] :radius 5})))))

(deftest test-enemy-shot-collision
  (testing "When enemies and shot collides"
    (is (= {:enemies [(e/make-enemy 150 150)]
            :shots [(p/make-shot 300 300)]}
           (e/check-collision-enemies->shot
            {:enemies [(e/make-enemy 10 10) (e/make-enemy 150 150)]
             :shots [(p/make-shot 10 10) (p/make-shot 300 300)]})))))
