(ns ive.template  ;; Be sure to change this namespace.
  (:require
    [ion.poly.core :as poly]
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest dom-node]]))

(enable-console-print!)

(defcard
  "# Introduction

  This is a good place to introduce the purpose for this set of cards.")

(defcard template-card
  (sab/html [:div
             [:h1 "This is a basic template"]]))
