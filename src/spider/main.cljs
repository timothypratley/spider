(ns spider.main)

;; global data model
;; eavt
(defonce world (atom {}))

(defn assert! [db datom]
  (swap! db assoc-in datom true))
(defn retract! [db datom]
  ;; TODO: should remove empty entities as well
  (swap! db update-in (butlast datom) dissoc (last datom)))

(defprotocol Animal
  (init [this])
  (step [this result x])
  (complete [this result]))

(defrecord Pose [translate rotate scale])

(defrecord Spider [pose behavior]
  Animal
  (init [this])
  (step [this result x]
    (case behavior
      ;"spinning" _
      "walking" (do update-my-pose
                    update-feet)
      ;"running" _
      ))
  (complete [this result]))

(def spider-leg-sockets
  [[-1 -3] [1 -3]
   [-2 -2] [2 -2]
   [-2 2] [2 2]
   [-1 3] [1 3]])

(def spider-legs
  (map (fn [[x y]]
         {:socket      [x y]
          :hinges    [[] []]
          :appendage {:tr    [0 0]
                      :down? true}})))

(defn add-legs [s]
  (doseq [leg (into [] spider-legs spider-leg-sockets)]
    (.addChild s
               (doto
                 (js/document.createElement "path")
                 (.setAttribute "d" "")
                 (-> .-socket #js [])
                 ))))

(defn spi []
  (doto
    (js/document.createElement "g")
    (.setAttribute "transform")
    (-> .-translate (set! #js [0 0]))
    (-> .-rotate (set! #js [0]))
    (-> .-v (set! #js [0 1]))
    (.addChild)
    )
  {:id       (rand 10000)
   :tr       [0 0]
   :ro       [0]
   :v        [0 1]
   :limbs
   :behavior "walking"})

(defn dot [v s])
(defn inv [v]
  (mapv - v))
(defn add [u v]
  (mapv + u v))

(defn spider []
  (Spider. pose behavior)
  (fn modify [rf]
    (let [s (spi)]
      (fn step [result x]
        tr (add (dot (rot v) dt) tr)
        (doseq [{:keys [appendage]} limbs]
          (let [{:keys [down? tr]} appendage]
            (if down?
              (let [x (dot (inv v) dt)]
                {:tr    ()
                 :down? false})
              (do
                {:down? true})))
          )))))

(defn on-tick []
  (doseq [animal world]
    (step animal)))


(defn -main [])
