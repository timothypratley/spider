(ns spider.app-layout
  (:require [spider.scene :as sc]))

(defn app-layout []
  [:div
   [:h2 "Hello world?"]
   [:form
    [:input]
    [:input]
    [:input]]
   [:div [sc/canvas] [sc/canvas]]
   [:div [sc/canvas] [sc/canvas]]])
