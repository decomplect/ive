(ns ive.canvas-bouncing-ball
  (:require
    [goog.async.AnimationDelay]
    [goog.dom]
    [ion.poly.core :as poly]
    [monet.canvas :as canvas]
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest dom-node]]))

(enable-console-print!)

(defcard
  "# Bouncing Ball

  The purpose of this page is to demonstrate the use of a canvas element.")

(defonce app-state
  (atom
    {:animation-frame-loop-alive? (atom false)
     :animation-frame-loop-active? (atom false)}))

(defn radians
  "Returns the angle deg in radians."
  [deg]
  (* (/ deg 180) Math/PI))

(defn circle-x
  "Returns the x location at angle t (in radians) on the circumference
  of the circle with radius r."
  [r t]
  (* r (Math/cos t)))

(defn circle-y
  "Returns the y location at angle t (in radians) on the circumference
  of the circle with radius r."
  [r t]
  (* r (Math/sin t)))

(defn ball-position [{:keys [delta-t-ms speed-pps x y w h direction]}]
  (let [direction (radians direction)
        pixels-per-ms (/ speed-pps 1000)
        delta-pixels (* delta-t-ms pixels-per-ms)
        dx (circle-x delta-pixels direction)
        dy (circle-y delta-pixels direction)
        clamped-x (-> x (+ dx) (max 0) (min w))
        clamped-y (-> y (+ dy) (max 0) (min h))]
    {:x clamped-x :y clamped-y}))

(defn ball-direction [{:keys [w h x y direction]}]
  (cond
    (not (< 0 x w)) (- 180 direction)
    (not (< 0 y h)) (- 360 direction)
    :else direction))

(defn canvas-update! [state timestamp]
  (if (:timestamp @state)
    (swap! state assoc :delta-t-ms (- timestamp (:timestamp @state))))
  (swap! state assoc :timestamp timestamp)
  (swap! state assoc :ball
         (merge (:ball @state)
                (ball-position (merge (select-keys @state [:delta-t-ms])
                                      (:ball @state)
                                      (:canvas @state)))))
  (swap! state assoc-in [:ball :direction]
         (ball-direction (merge (:ball @state)
                                (:canvas @state)))))

(defn draw-background
  [ctx {:keys [w h]}]
  (-> ctx
      (canvas/fill-style "rgba(42,42,42,0.75)")
      (canvas/fill-rect {:x 0 :y 0 :w w :h h})))

(defn draw-ball
  [ctx {:keys [color size x y]}]
  (-> ctx
      (canvas/fill-style color)
      (canvas/circle {:x x :y y :r size})
      canvas/fill))

(defn canvas-render! [data]
  (draw-background (:ctx data) (:canvas data))
  (draw-ball (:ctx data) (:ball data)))

(defonce canvas-state
  (atom
    {:ball {:color "red"
            :direction (rand-int 360)
            :size 10
            :speed-pps 50
            :x 10 :y 10}
     :canvas {:w 320 :h 320}
     :ctx nil
     :delta-t-ms 0
     :timestamp nil}))

(defn animate! [timestamp]
  (canvas-update! canvas-state timestamp)
  (canvas-render! @canvas-state))

(defn start-looping! []
  (let [activate? (:animation-frame-loop-active? @app-state)]
    (let [[alive? active?] (poly/animation-frame-loop! animate! @activate?)]
      (swap! app-state assoc
             :animation-frame-loop-alive? alive?
             :animation-frame-loop-active? active?))))

(defn stop-looping! []
  (reset! (:animation-frame-loop-alive? @app-state) false))

(defn toggle-animating! []
  (swap! (:animation-frame-loop-active? @app-state) not)
  (let [canvas (goog.dom/getElement "canvas-1")
        context (.getContext canvas "2d")]
    (swap! canvas-state assoc :ctx context)))

(defcard i-can-has-canvas?
  (fn [state _]
    (sab/html [:div [:canvas {:id "canvas-1"
                              :width (get-in @state [:canvas :w])
                              :height (get-in @state [:canvas :h])}]
               [:div [:button {:onClick toggle-animating!} "Start/Stop"]]]))
  canvas-state
  {:inspect-data true})

(stop-looping!)
(start-looping!)
