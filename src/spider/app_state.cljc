(ns spider.app-state
  #?(:cljs (:require [reagent.core :refer [atom]])))

(defonce app-state* (atom {}))
(defonce error* (atom nil))
(defonce info* (atom nil))

(defn format-error [err]
  (loop [msg ""
         ex err]
    (let [msg (str msg (or (ex-message ex) ex))
          c (ex-cause ex)]
      (if c
        (recur (str msg ":" \newline) c)
        msg))))

(defn on-error [err]
  #?(:cljs (js/console.error err)
     :clj (println err))
  (reset! error* (format-error err))
  nil)

(defn info [s]
  (reset! info* s)
  nil)
