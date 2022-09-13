(ns redditoscope.core
  (:require
   [reagent.dom :as d]
   [re-frame.core :as rf] 
   [redditoscope.controllers]
   [redditoscope.views :as views]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [views/home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (rf/dispatch-sync [:initialize-db]) 
  (mount-root))
