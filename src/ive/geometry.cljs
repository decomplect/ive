(ns ive.geometry
  (:require
    [ion.poly.core :as poly]
    [sablono.core :as sab :include-macros true]
    [thi.ng.geom.core :as g]
    [thi.ng.math.core :as m])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest dom-node]]
    [thi.ng.math.macros :as mm]))

(enable-console-print!)

(defcard
  "# Geometry

  This page will show how to work with geometry.")

(defn slider [state k value min max f]
  (sab/html
    [:input {:type "range" :value value :min min :max max
             :style {:width "100%"}
             :on-change (fn [e]
                          (swap! state assoc k (f (.-target.value e))))}]))

(defn convert-radians->degrees [{:keys [radians] :as data}]
  (->> radians
       m/degrees
       (assoc data :degrees)))

(defn radians->degrees-component [state]
  (let [{:keys [degrees radians]} (convert-radians->degrees @state)]
    (sab/html
      [:div
       [:h3 "Degrees convertor"]
       [:div
        [:span (str "Radians: " radians)]
        (slider state :radians radians 1 100 int)]
       [:div
        [:span (str "Degrees: " degrees)]]])))

(defcard radians->degrees
  "# Convert radians to degrees"
  (fn [state _] (radians->degrees-component state))
  {:radians 50}
  {:inspect-data true})
