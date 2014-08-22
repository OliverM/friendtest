(ns friendtest.core
  (:require [ring.adapter.jetty]
            [ring.middleware.params :as params]
            [ring.util.response :as resp]
            [hiccup.page :as h]
            [hiccup.element :as e]
            [compojure.core :as http]
            [compojure.handler]
            [compojure.route :as route]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(def users {"root" {:username "root"
                    :password (creds/hash-bcrypt "admin_password")
                    :roles #{::admin}}
            "jane" {:username "jane"
                    :password (creds/hash-bcrypt "user_password")
                    :roles #{::user}}})

(derive ::admin ::user)

(def head
  [:head
   [:style {:type "text/css"} "ul { padding-left: 2em }"]
   ])

(defn body
  [& content]
  [:body
   (into [:div ] content)])

(def login-form
  [:div
   [:div
    [:h3 "Login"]
    [:div
     [:form {:method "POST" :action "login"}
      [:div "Username" [:input {:type "text" :name "username"}]]
      [:div "Password" [:input {:type "password" :name "password"}]]
      [:div [:input {:type "submit" :class "button" :value "Login"}]]]]]])

(compojure.core/defroutes user-routes
                          (http/GET "/account" req (resp/response (str "Account!..." req)))
                          (http/GET "/summary" req (resp/response (str "Summary!..." req))))

(compojure.core/defroutes ring-app
                          ;; user routes
                          (compojure.core/context "/user" req (friend/wrap-authorize user-routes #{::user}))

                          ;; admin routes
                          (http/GET "/admin" req (friend/authorize #{::admin}
                                                                   "This page is only visible to administrators..."
                                                                   (resp/response (str "Administrate!"))))

                          ;; open access routes
                          (http/GET "/login" req (h/html5 head (body login-form)))
                          (friend/logout (http/ANY "/logout" request (resp/redirect "/")))

                          ;; moving this route above the GET "/login" route clears the params to the login route
                          (http/GET "/" {params :params} (resp/response (str params)))
                          (route/not-found "Unpopulated link."))


(def secured-app
  (compojure.handler/site
    (friend/authenticate ring-app {:credential-fn #(do
                                                    (println (str "Testing user credentials..." %))
                                                    (creds/bcrypt-credential-fn users %))
                                   :workflows     [(workflows/interactive-form)]}))
  )