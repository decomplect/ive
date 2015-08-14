(ns ive.index
  (:require
    [devcards.core]
    [ive.template]))

(defn ^:export main []
  (devcards.core/start-devcard-ui!))
