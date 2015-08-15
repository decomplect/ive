(ns ive.canvas-bouncing-ball
  (:require
    [goog.async.AnimationDelay]
    [goog.dom]
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

(defn request-animation-frame!
  "A delayed callback that pegs to the next animation frame."
  [callback]
  (.start (goog.async.AnimationDelay. callback)))

(defn animation-frame-loop!
  ([callback]
   (animation-frame-loop! callback true))
  ([callback activate?]
   (let [alive? (atom true)
         active? (atom activate?)]
     (letfn [(step
               [timestamp]
               (when @alive?
                 (request-animation-frame! step)
                 (when @active?
                   (callback timestamp))))]
       (request-animation-frame! step))
     [alive? active?])))

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

(defn ball-position [{:keys [delta-t-ms ball-speed-pps ball-x ball-y canvas-w canvas-h ball-direction]}]
  (let [direction (radians ball-direction)
        pixels-per-ms (/ ball-speed-pps 1000)
        delta-pixels (* delta-t-ms pixels-per-ms)
        dx (circle-x delta-pixels direction)
        dy (circle-y delta-pixels direction)
        clamped-x (-> ball-x (+ dx) (max 0) (min canvas-w))
        clamped-y (-> ball-y (+ dy) (max 0) (min canvas-h))]
    {:ball-x clamped-x :ball-y clamped-y}))

(defn ball-direction [{:keys [canvas-w canvas-h ball-x ball-y ball-direction]}]
  (cond
    (not (< 0 ball-x canvas-w)) (- 180 ball-direction)
    (not (< 0 ball-y canvas-h)) (- 360 ball-direction)
    :else ball-direction))

(defn canvas-update! [state timestamp]
  (if (:timestamp @state)
    (swap! state assoc :delta-t-ms (- timestamp (:timestamp @state))))
  (swap! state assoc :timestamp timestamp)
  (swap! state merge (ball-position @state))
  (swap! state assoc :ball-direction (ball-direction @state)))

(defn draw-background
  [{:keys [ctx canvas-w canvas-h]}]
  (-> ctx
      (canvas/fill-style "rgba(42,42,42,0.75)")
      (canvas/fill-rect {:x 0 :y 0 :w canvas-w :h canvas-h})))

(defn draw-ball
  [{:keys [ctx ball-color ball-size ball-x ball-y]}]
  (-> ctx
      (canvas/fill-style ball-color)
      (canvas/circle {:x ball-x :y ball-y :r ball-size})
      canvas/fill))

(defn canvas-render! [data]
  (draw-background data)
  (draw-ball data))

(defonce canvas-state
  (atom
    {:canvas nil
     :canvas-w 320 :canvas-h 320
     :ctx nil
     :ball-color "red"
     :ball-direction (rand-int 360)
     :ball-size 10
     :ball-speed-pps 50
     :ball-x 10 :ball-y 10
     :delta-t-ms 0
     :timestamp nil}))

(defn animate! [timestamp]
  (canvas-update! canvas-state timestamp)
  (canvas-render! @canvas-state))

(defn start-looping! []
  (let [activate? (:animation-frame-loop-active? @app-state)]
    (let [[alive? active?] (animation-frame-loop! animate! @activate?)]
      (swap! app-state assoc
             :animation-frame-loop-alive? alive?
             :animation-frame-loop-active? active?))))

(defn stop-looping! []
  (reset! (:animation-frame-loop-alive? @app-state) false))

(defn toggle-animating! []
  (swap! (:animation-frame-loop-active? @app-state) not)
  (let [canvas (goog.dom/getElement "canvas-1")
        context (.getContext canvas "2d")]
    (swap! canvas-state assoc :canvas canvas)
    (swap! canvas-state assoc :ctx context)))

(defcard i-can-has-canvas?
  (fn [state _]
    (sab/html [:div [:canvas {:id "canvas-1"
                              :width (:canvas-w @state)
                              :height (:canvas-h @state)}]
               [:div [:button {:onClick toggle-animating!} "Start/Stop"]]]))
  canvas-state
  {:inspect-data true})

(stop-looping!)
(start-looping!)
