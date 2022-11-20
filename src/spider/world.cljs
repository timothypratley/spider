(ns spider.world
  (:require [threeagent.core :as ta]))

(defonce world* (ta/atom {:t 0}))
(defonce t (js/performance.now))

(defn animate [ts]
  (let [dt (min (- ts t) 100)]
    (set! t ts)
    (swap! world* assoc :t ts)
    (doseq [child (:children @world*)]
      child
      #_(updateSpider child dt))
    (js/requestAnimationFrame animate)))

(defonce started
  (do (js/requestAnimationFrame animate) :done))
