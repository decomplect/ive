(ns ive.ergo.development
  (:require
    [cljs.test :refer-macros [is testing]]
    [ion.ergo.core :as ergo]
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard
  "# Introduction

  I'm developing the Ergo DSL.

  I'm going to need:

  * react canvas component
  * render-agnostic library (`ion.ergo`)
  * unit tests for `ion.ergo`
  * rendering via `ion.ergo.rend` or `ion.rend`")

(defonce app-state
  (atom
    {:degrees 0
     :foo? true}))

(defn slider [state ks min max f]
  (sab/html
    [:input {:type "range" :value (get-in @state ks) :min min :max max
             :style {:width "100%"}
             :on-change (fn [e]
                          (swap! state assoc-in ks (f (.-target.value e))))}]))

(defn div-span-slider [label state ks min max f]
  (sab/html
    [:div
     [:span (str label ": " (get-in @state ks))]
     (slider state ks min max f)]))

(defn state-inspection-component [state]
  (sab/html
    [:div
     (div-span-slider "Degrees" state [:degrees] -360 360 int)
     [:div
      [:span (str "Radians: " (-> @state (get-in [:degrees]) (ergo/radians) (/ ergo/PI)) " PI")]]
     ]))

(defcard state-inspection
  "## Widgets for values and conversions."
  (fn [state _] (state-inspection-component state))
  app-state
  {:inspect-data true})

(deftest ergo-tests
  "## Tests of the Ergo DSL"
  (testing "Basic math and trigonometry functions."
    (is (= 1 (Math/abs -1)) "Use standard JS Math functions where they exist.")
    (is (= Math/PI ergo/PI) "Some useful constants are defined in ergo.")
    (is (= 180 (ergo/degrees ergo/PI)) "Convert from radians to degrees.")
    (is (= ergo/TWO-PI (ergo/radians 360)) "Convert from degrees to radians.")
    )
  "Top level strings are interpreted as markdown for inline documentation."
  (testing "testing context 2"
    (is false)))
