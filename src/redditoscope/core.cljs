(ns redditoscope.core
  (:require
   [reagent.dom :as d]
   [re-frame.core :as rf]
   [redditoscope.chart :as chart]
   [redditoscope.controllers]))

;; -------------------------
;; Views

(defn display-post [{:keys [permalink subreddit title score url]}]
  [:div.card.m-2
   [:div.card-block
    [:h4.card-title
     [:a {:href (str "http://reddit.com" permalink)} title ""]]
    [:div [:span.badge.badge-info {:color "info"} subreddit " score " score]]
    [:img {:width "300px" :src url}]]])

(defn display-posts [posts]
  (when-not (empty? posts)
    [:div
     (for [posts-row (partition-all 3 posts)]
       ^{:key posts-row}
       [:div.row
        (for [post posts-row]
          ^{:key post}
          [:div.col-4 [display-post post]])])]))

(defn sort-posts [title sort-key]
  [:button.btn.btn-secondary
   {:on-click #(rf/dispatch [:sort-posts sort-key])}
   (str "sort posts by " title)])

(defn navitem [title view id]
  [:li.nav-item
   {:class-name (when (= id view) "active")}
   [:a.nav-link
    {:href "#"
     :on-click #(rf/dispatch [:select-view id])}
    title]])

(defn navbar [view]
  [:nav.navbar.navbar-toggleable-md.navbar-light.bg-faded
   [:ul.navbar-nav.mr-auto.nav
    {:class-name "navbar-nav mr-auto"}
    [navitem "Posts" view :posts]
    [navitem "Chart" view :chart]]])

(defn home-page []
  (let [view @(rf/subscribe [:view])]
    [:div
     [navbar view]
     [:div.card>div.card-block
      [:div.btn-group
       [sort-posts "score" :score]
       [sort-posts "comments" :num_comments]]
      (case view
        :chart [chart/chart-posts-by-votes]
        :posts [display-posts @(rf/subscribe [:posts])])]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (rf/dispatch-sync [:initialize-db])
  (rf/dispatch [:load-posts "http://www.reddit.com/r/Catloaf.json?sort=new&limit=10"])
  (mount-root))
