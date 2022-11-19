(ns spider.main-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.math :as math]
            [spider.main :refer [deg2rad]]))

(deftest deg2rad-test
  (is (= math/PI (deg2rad 180))))
