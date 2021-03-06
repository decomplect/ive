(ns ive.template  ;; Be sure to change this namespace.
  (:require
    [cljs.test :refer-macros [is testing]]
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard
  "# Introduction

  This is a good place to introduce the purpose for this set of cards.")

(defonce app-state
  (atom
    {:bar 42
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
     (div-span-slider "Bar Size" state [:bar] 1 100 int)
     ]))

(defcard state-inspection
  "## Widgets for values and conversions."
  (fn [state _] (state-inspection-component state))
  app-state
  {:inspect-data true})

(defcard template-card
  (sab/html
    [:div
     [:h1 "This is a basic template"]]))

(deftest cljs-test-integration
  "## Here are some example tests"
  (testing "testing context 1"
    (is (= (+ 3 4 55555) 4) "This is the message arg to an 'is' test")
    (is (= (+ 1 0 0 0) 1) "This should work")
    (is (= 1 3))
    (is false)
    (is (throw "errors get an extra red line on the side")))
  "Top level strings are interpreted as markdown for inline documentation."
  (testing "testing context 2"
    (is (= (+ 1 0 0 0) 1))
    (is (= (+ 3 4 55555) 4))
    (is false)
    (testing "nested context"
      (is (= (+ 1 0 0 0) 1))
      (is (= (+ 3 4 55555) 4))
      (is false))))
