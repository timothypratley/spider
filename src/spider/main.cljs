(ns ^:figwheel-hooks spider.main
  (:require [goog.dom :as gdom]
            [reagent.core :as r]
            ["react-dom/client" :as rc]
            [spider.app-layout :as al]))

(defonce root
  (some-> (gdom/getElement "app")
          (rc/createRoot)))

(defn mount-app-element []
  (when root
    (.render root (r/as-element [al/app-layout]))))

(defonce app
  (do (mount-app-element) :done))

(defn ^:after-load on-reload []
  (mount-app-element))
