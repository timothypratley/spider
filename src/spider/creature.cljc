(ns spider.creature
  (:require ["mathjs" :as m]
            [clojure.math :as math]))

(defn deg2rad [x]
  (/ (* math/PI x) 180))

(def spiderLegSockets
  [[0 -2] [3 -1]
   [0.5 -1.5] [1 -1.5]
   [0.5 1.5] [1 1.5]
   [0 2] [3 1]])

(defn spiderLeg [socket]
  {:tr     [0 0]
   :socket socket
   :down   true})

(defn spider []
  {:tr      [0 0]
   :rot     [0]
   :rotGoal [0]
   :urgency 0.5
   :size    (+ 0.5 (rand 0.5))
   :legs    (doall (for [socket spiderLegSockets]
                     (spiderLeg socket)))})

(defn updateSpider [s dt]
  #_(let [dt (* dt (.-urgency s))]
    ;; stay in the world
    (cond (< (aget (.-tr s) 0) -60)
          (doto s (-> .-rotGoal (set! #js[0]))
                  (-> .-rot (set! #js[0])))
          (< (aget (.-tr s) 1) -60)
          (doto s (-> .-rotGoal (set! #js[90]))
                  (-> .-rot (set! #js[90])))
          (> (aget (.-tr s) 0) 60)
          (doto s (-> .-rotGoal (set! #js[180]))
                  (-> .-rot (set! #js[180])))
          (> (aget (.-tr s) 1) 60)
          (doto s (-> .-rotGoal (set! #js[270]))
                  (-> .-rot (set! #js[270])))

          ;; randomization
          (< (rand) 0.01)
          (set! (.-rotGoal s) #js[(rand-int 360)])
          (< (rand) 0.01)
          (set! (.-urgency s) (rand)))

    ;; rotate toward a goal
    (let [drot (cond (> (aget (.-rotGoal s) 0) (+ (aget (.-rot s) 0) 5)) 5
                     (< (aget (.-rotGoal s) 0) (- (aget (.-rot s) 0) 5)) -5
                     :else 0)
          dt (if (= 0 drot) dt (* 5 dt))]
      (aset (.-rot s) 0 (+ (aget (.-rot s) 0) drot))
      (set! (.-tr s) (m/add (.-tr s)
                            (m/rotate #js[dt 0] (deg2rad (aget (.-rot s) 0)))))
      (.setAttribute s "transform" (str "translate(" (.-tr s) ") rotate(" (.-rot s) ") scale(" (.-size s) ")"))
      ;; TODO: feet shouldn't rotate when turning!
      (doseq [leg (.-children s)
              :when (.-tr leg)]
        (set! (.-tr leg) (m/add (.-tr leg)
                                (if (.-down leg)
                                  #js[(- dt) 0]
                                  #js[(* 4 dt) 0])))
        (cond (> (aget (.-tr leg) 0) (if (> (aget (.-socket leg) 0) 0.5) 9 -5))
              (set! (.-down leg) true)
              (< (aget (.-tr leg) 0) (if (> (aget (.-socket leg) 0) 0.5) 5 -9))
              (set! (.-down leg) false))
        (.setAttribute leg "d" (str "M" (.-socket leg) " L" (.-tr leg)))))))
