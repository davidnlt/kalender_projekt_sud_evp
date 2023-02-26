import Vue from "vue";
import UserLogin from "../views/UserLogin.vue";
import UserRegistration from "../views/UserRegistration.vue";
import VacationCalendar from "../views/VacationCalendar.vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "UserLogin",
    component: UserLogin,
  },
  {
    path: "/registration",
    name: "UserRegistration",
    component: UserRegistration,
  },
  {
    path: "/",
    name: "VacationCalendar",
    component: VacationCalendar,
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
