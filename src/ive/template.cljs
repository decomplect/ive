(ns ive.template                                            ;; Be sure to change this namespace.
  (:require
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard template-card
  (sab/html [:div
             [:h1 "This is a basic template"]]))
