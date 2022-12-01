(ns spider.scene
  (:require [reagent.core :as reagent]
            [threeagent.core :as ta]
            ["three" :as three]))

(defn color-box [color size]
  [:box {:dims     [size size size]
         :material {:color color}}])

(defn growing-sphere []
  (let [s (reagent/atom 0)]
    (.setInterval js/window #(swap! s inc) 5000)
    (fn []
      [:sphere {:radius @s}])))

(defn foo []
  [:object
   [color-box "red" 1.0]
   [growing-sphere]])

(defn body [{:keys [tr]}]
  [:object {:position tr}
   [:sphere {:radius 1}]
   [:sphere {:radius 2}]])

(defn root [world]
  (into
    [:object {:position   [1.0 0 -4.0]
              :rotation   [0 (js/Math.sin (/ (:t world) 1000)) 0]
              :background {:color "red"}}
     #_[:perspective-camera {:fov    45
                           :aspect 1
                           :near   1
                           :far    1000}]
     [:ambient-light {:intensity 0.8}]]
    (map (fn [child]
           (body child)))
    (:children world)))

(defn canvas [world*]
  (reagent/with-let [render (fn [] [root @world*])]
    [:canvas {:width  400
              :height 300
              :style  {:border "solid 1px lightgrey"}
              :ref    (fn [el]
                        (when el
                          (doto (ta/render render el)
                            (-> :threejs-scene (.-background) (set! (three/Color. 0xffffff)))
                            (-> :threejs-scene (.-fog) (set! (three/FogExp2. 0xffffff 0.1))))))}]))
