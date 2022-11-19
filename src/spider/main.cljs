(ns spider.main
  (:require ["mathjs" :as m]
            [clojure.math :as math]))

(def spiderLegSockets
  (clj->js [[0 -2] [3 -1]
            [0.5 -1.5] [1 -1.5]
            [0.5 1.5] [1 1.5]
            [0 2] [3 1]]))

(defn hiccup [tag attributes]
  (let [e (js/document.createElementNS "http://www.w3.org/2000/svg", tag)]
    (doseq [[k v] attributes]
      (.setAttribute e (name k) v))
    e))

(defn spiderLeg [socket]
  (let [tr (m/multiply socket 3)]
    (if (> (aget socket 1) 0)
      tr (m/add tr, #js[-5 0]))
    (doto (hiccup "path", {:d (str "M" socket " L" tr)})
      (-> .-socket (set! socket))
      (-> .-down (set! true))
      (-> .-tr (set! tr)))))

(defn spider []
  (let [s (doto (hiccup "g" {:stroke "black", :stroke-width 0.5, :stroke-linecap "round"})
            (.appendChild (hiccup "ellipse" {:cx 2, :rx 2, :ry 1}))
            (.appendChild (hiccup "ellipse" {:cx -2, :rx 3, :ry 2}))
            (-> .-tr (set! #js[0 0]))
            (-> .-rotGoal (set! #js[0]))
            (-> .-rot (set! #js[0]))
            (-> .-urgency (set! 0.5))
            (-> .-size (set! (+ 0.5 (/ (rand) 2)))))]
    (doseq [socket spiderLegSockets]
      (.appendChild s (spiderLeg socket)))
    s))

(defn deg2rad [x]
  (/ (* math/PI x) 180))

(defn updateSpider [s dt]
  (let [dt (* dt (.-urgency s))]
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

(def world (js/document.getElementById "world"))

(def lastTs (js/performance.now))

(defn animate [ts]
  (let [dt (/ (- ts lastTs) 50)]
    (set! lastTs ts)
    (doseq [child (.-children world)]
      (updateSpider child dt))
    (js/requestAnimationFrame animate)))

(defn init []
  (dotimes [i 50]
    (.appendChild world (spider)))
  (js/requestAnimationFrame animate))

(when world
  (init))
