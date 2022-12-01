(ns spider.app-layout
  (:require [reagent.core :as reagent]
            [spider.scene :as sc]
            [spider.world :as sw]
            [spider.app-state :as app-state]))

(defn app-layout []
  (reagent/with-let [click (fn [ev]
                             (swap! sw/world* sw/add-spider))]
    [:div
     [:h2 "Hello world?"]
     [:div
      [:input]
      [:input]
      [:input]
      [:button {:onClick click} "Add"]]
     [:div [sc/canvas sw/world*] [sc/canvas sw/world*]]
     [:div [sc/canvas sw/world*] [sc/canvas sw/world*]]]))
