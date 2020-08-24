import Vue from 'vue'
import App from './App.vue'
import axios from "axios";
import router from './router'
import 'lib-flexible';
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import qs from 'qs';
import TreeTable from 'vue-table-with-tree-grid'
import VueQuillEditor from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'
import './assets/css/global.css'
import './assets/fonts/iconfont.css'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

Vue.use(ElementUI)
Vue.component('tree-table', TreeTable)
Vue.use(VueQuillEditor)
Vue.prototype.$qs = qs

const request = axios.create({
	baseURL: `http://localhost:8040/`,
	withCredentials: true,
	timeout: 15000
})

request.interceptors.request.use(config => {
	NProgress.start()
	config.headers.Authorization = window.sessionStorage.getItem('cw.iot.token.currentUser')
	window.sessionStorage.setItem('cw.iot.token.lastOperateTime', (new Date()).getTime());
	return config
})

request.interceptors.response.use(config => {
	NProgress.done()
	return config
})

Vue.prototype.$http = request

Vue.filter('dateFormat', (originVal) => {
	if (!originVal || originVal == "" || originVal == null || originVal == undefined) {
		return ` ———`
	}
	const dt = new Date(originVal)
	const y = dt.getFullYear()
	const m = (dt.getMonth() + 1 + '').padStart(2, '0')
	const d = (dt.getDate() + 1 + '').padStart(2, '0')
	const hh = (dt.getHours() + 1 + '').padStart(2, '0')
	const mm = (dt.getMinutes() + 1 + '').padStart(2, '0')
	const ss = (dt.getSeconds() + 1 + '').padStart(2, '0')
	return `${y}-${m}-${d} ${hh}:${mm}:${ss}`
})

Vue.filter('isNull', (originVal) => {
	if (!originVal || originVal == "" || originVal == null || originVal == undefined) {
		return ` ———`
	}
	return originVal;
})

Vue.config.productionTip = false

new Vue({
	router,
	render: h => h(App)
}).$mount('#app')
