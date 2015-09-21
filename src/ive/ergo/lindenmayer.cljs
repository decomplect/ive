(ns ive.ergo.lindenmayer
  (:require
    [goog.dom]
    [ion.ergo.l-system :as ls :refer [basic-system]]
    [ion.poly.core :as poly]
    [monet.canvas :as canvas]
    [sablono.core :as sab :include-macros true]
    [turtle.core :as turtle]
    [turtle.renderer.canvas :as trc])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest dom-node]]))

(enable-console-print!)

(defcard
  "# Lindenmayer Systems

  The purpose of this page is to demonstrate the use of L-systems.")

(defn dragon-sequence
  "Returns a lazy sequence of vectors."
  []
  (let [axiom [:F :x]
        rules {:x [:x :+ :y :F :+]
               :y [:- :F :x :- :y]}]
    (basic-system axiom rules)))

(def dragon-render-rules {:F [:fwd 20]
                          :+ [:right 90]
                          :- [:left 90]
                          \[ [:save]
                          \] [:restore]
                          :x []
                          :y []})

#_(def dragon-render-rules {:F [:fwd 30]
                          :+ [:right 90 :fwd 5 :left 45 :fwd 15 :right 45 :fwd 5]
                          :- [:left 90 :fwd 15 :right 45 :fwd 5 :left 45 :fwd 15]
                          \[ [:save]
                          \] [:restore]
                          :x []
                          :y []})

(defn dragon-render-sequence
  []
  (map #(into [] (comp (replace dragon-render-rules) cat) %) (dragon-sequence)))

(defonce app-state
  (atom
    {:dragon (doall (take 15 (dragon-render-sequence)))
     :w 900
     :h 900}))

(defn show! []
  (let [canvas (goog.dom/getElement "canvas")
        context (.getContext canvas "2d")]
    (turtle/draw! (trc/->canvas context) (last (get-in @app-state [:dragon])) [900 900])))

(defn show-x4! []
  (let [canvas (goog.dom/getElement "canvas-x4")
        context (.getContext canvas "2d")
        dragon (concat [:save] (nth (get-in @app-state [:dragon]) 10) [:restore])
        x4 (concat [:color-index 0]
                   dragon
                   [:right 90 :color-index 1]
                   dragon
                   [:right 90 :color-index 2]
                   dragon
                   [:right 90 :color-index 5]
                   dragon
                   )]
    (turtle/draw! (trc/->canvas context) x4 [900 900])))

(defn show-gen! []
  (dorun
    (map (fn [n]
           (turtle/draw! (trc/->canvas (.getContext (goog.dom/getElement (str "canvas-" n)) "2d"))
                         (nth (get-in @app-state [:dragon]) n) [300 300]))
         (range (count (get-in @app-state [:dragon]))))))

(defn canvas-component [state]
  (sab/html
    [:div
     [:h3 (str "System Generation "
               (count (get-in @state [:dragon]))
               " of " (count (last (get-in @state [:dragon]))) " commands")]
     [:div [:button {:onClick show!} "Show"]]
     [:canvas {:id "canvas" :width (get-in @state [:w]) :height (get-in @state [:h])}]
     ]))

(defn canvas-x4-component [state]
  (sab/html
    [:div
     [:h3 "System Generation x4"]
     [:div [:button {:onClick show-x4!} "Show x4"]]
     [:canvas {:id "canvas-x4" :width (get-in @state [:w]) :height (get-in @state [:h])}]
     ]))

(defn canvas-component-generations [state]
  (sab/html
    [:div
     [:h3 "System Generations"]
     [:div [:button {:onClick show-gen!} (str "Show All " (count (get-in @state [:dragon])) " Generations")]]
     (doall (map (fn [n] [:canvas {:id (str "canvas-" n) :width 300 :height 300}])
                 (range (count (get-in @app-state [:dragon])))))
     ]))

(defcard dragon-system-generations
  (fn [state _] (canvas-component-generations state))
  app-state
  {:inspect-data false})

(defcard dragon-x4-system
  (fn [state _] (canvas-x4-component state))
  app-state
  {:inspect-data false})

(defcard dragon-system
  (fn [state _] (canvas-component state))
  app-state
  {:inspect-data false})
