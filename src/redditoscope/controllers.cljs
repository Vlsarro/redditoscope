(ns redditoscope.controllers
  (:require
   [ajax.core :as ajax]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:view :posts
    :sort-key :score
    :posts-fetch-num 10}))

(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

(rf/reg-event-db
 :set-posts-fetch-num
 (fn [db [_ posts-fetch-num]]
   (assoc db :posts-fetch-num (js/parseInt posts-fetch-num))))

(rf/reg-event-db
 :set-posts
 (fn [db [_ posts]] 
   (assoc db :posts
          (->> (get-in posts [:data :children])
               (map :data)
               (find-posts-with-preview)))))

(rf/reg-fx
 :ajax-get
 (fn [[url handler]]
   (ajax/GET url
             {:handler handler
              :response-format :json
              :keywords? true})))

(defn get-reddit-url [subreddit posts-num] 
  (str "http://www.reddit.com/r/" (or subreddit "Catloaf") ".json?sort=new&limit=" (or posts-num 10)))

(rf/reg-event-fx
 :load-posts
 (fn [{:keys [db]} _] 
   {:ajax-get [(get-reddit-url "Catloaf" (:posts-fetch-num db)) #(rf/dispatch [:set-posts %])]}))

(rf/reg-event-db
 :sort-posts
 (fn [db [_ sort-key]]
   (update db :posts (partial sort-by sort-key >))))

(rf/reg-event-db
 :select-view
 (fn [db [_ view]]
   (assoc db :view view)))

(rf/reg-sub
 :view
 (fn [db _]
   (:view db)))

(rf/reg-sub
 :posts
 (fn [db _]
   (:posts db)))

(rf/reg-sub
 :posts-fetch-num
 (fn [db _]
   (:posts-fetch-num db)))
