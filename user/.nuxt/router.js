import Vue from 'vue'
import Router from 'vue-router'
import { interopDefault } from './utils'
import scrollBehavior from './router.scrollBehavior.js'

const _221b7606 = () => interopDefault(import('..\\pages\\order\\index.vue' /* webpackChunkName: "pages/order/index" */))
const _03da476f = () => interopDefault(import('..\\pages\\patient\\index.vue' /* webpackChunkName: "pages/patient/index" */))
const _cbb94456 = () => interopDefault(import('..\\pages\\user\\index.vue' /* webpackChunkName: "pages/user/index" */))
const _16c4dbe6 = () => interopDefault(import('..\\pages\\hospital\\booking.vue' /* webpackChunkName: "pages/hospital/booking" */))
const _75dfe993 = () => interopDefault(import('..\\pages\\hospital\\schedule.vue' /* webpackChunkName: "pages/hospital/schedule" */))
const _7ac78ff9 = () => interopDefault(import('..\\pages\\order\\show.vue' /* webpackChunkName: "pages/order/show" */))
const _682594fe = () => interopDefault(import('..\\pages\\patient\\add.vue' /* webpackChunkName: "pages/patient/add" */))
const _56b72520 = () => interopDefault(import('..\\pages\\patient\\show.vue' /* webpackChunkName: "pages/patient/show" */))
const _4d51f0e9 = () => interopDefault(import('..\\pages\\weixin\\callback.vue' /* webpackChunkName: "pages/weixin/callback" */))
const _5cc6a2f2 = () => interopDefault(import('..\\pages\\hospital\\detail\\_hoscode.vue' /* webpackChunkName: "pages/hospital/detail/_hoscode" */))
const _a50a9664 = () => interopDefault(import('..\\pages\\hospital\\notice\\_hoscode.vue' /* webpackChunkName: "pages/hospital/notice/_hoscode" */))
const _496d5f14 = () => interopDefault(import('..\\pages\\hospital\\_hoscode.vue' /* webpackChunkName: "pages/hospital/_hoscode" */))
const _249d0a80 = () => interopDefault(import('..\\pages\\index.vue' /* webpackChunkName: "pages/index" */))

// TODO: remove in Nuxt 3
const emptyFn = () => {}
const originalPush = Router.prototype.push
Router.prototype.push = function push (location, onComplete = emptyFn, onAbort) {
  return originalPush.call(this, location, onComplete, onAbort)
}

Vue.use(Router)

export const routerOptions = {
  mode: 'history',
  base: decodeURI('/'),
  linkActiveClass: 'nuxt-link-active',
  linkExactActiveClass: 'nuxt-link-exact-active',
  scrollBehavior,

  routes: [{
    path: "/order",
    component: _221b7606,
    name: "order"
  }, {
    path: "/patient",
    component: _03da476f,
    name: "patient"
  }, {
    path: "/user",
    component: _cbb94456,
    name: "user"
  }, {
    path: "/hospital/booking",
    component: _16c4dbe6,
    name: "hospital-booking"
  }, {
    path: "/hospital/schedule",
    component: _75dfe993,
    name: "hospital-schedule"
  }, {
    path: "/order/show",
    component: _7ac78ff9,
    name: "order-show"
  }, {
    path: "/patient/add",
    component: _682594fe,
    name: "patient-add"
  }, {
    path: "/patient/show",
    component: _56b72520,
    name: "patient-show"
  }, {
    path: "/weixin/callback",
    component: _4d51f0e9,
    name: "weixin-callback"
  }, {
    path: "/hospital/detail/:hoscode?",
    component: _5cc6a2f2,
    name: "hospital-detail-hoscode"
  }, {
    path: "/hospital/notice/:hoscode?",
    component: _a50a9664,
    name: "hospital-notice-hoscode"
  }, {
    path: "/hospital/:hoscode?",
    component: _496d5f14,
    name: "hospital-hoscode"
  }, {
    path: "/",
    component: _249d0a80,
    name: "index"
  }],

  fallback: false
}

export function createRouter () {
  return new Router(routerOptions)
}
