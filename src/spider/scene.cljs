(ns spider.scene
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [spider.world :as sw]
            [threeagent.core :as ta]))

(defn color-box [color size]
  [:box {:dims     [size size size]
         :material {:color color}}])

(defn growing-sphere []
  (let [s (r/atom 0)]
    (.setInterval js/window #(swap! s inc) 5000)
    (fn []
      [:sphere {:radius @s}])))

(defn foo []
  [:object
   [color-box "red" 1.0]
   [growing-sphere]])

(defn root []
  [:object {:position [1.0 0 -4.0]
            :rotation [0 (js/Math.sin (/ (:t @sw/world*) 1000)) 0]}
   [:ambient-light {:intensity 0.8}]
   [foo]])

(defn canvas []
  [:canvas {:width 400
            :height 300
            :ref (fn [el]
                   (when el
                     (ta/render root el)))}])
