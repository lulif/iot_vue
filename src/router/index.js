import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [{
		path: '/login',
		name: 'Login',
		component: () =>
			import('../components/Login.vue')
	},
	{
		path: '/home',
		name: 'Home',
		redirect: '/welcome',
		component: () => import('../views/Home'),
		children: [{
				path: '/welcome',
				component: () =>
					import('../components/Welcome')
			},
			{
				path: '/dataDisplay',
				component: () =>
					import('../components/display/DataDisplay')
			},
			{
				path: '/device',
				component: () =>
					import('../components/device/Device')
			},
			{
				path: '/deviceModel',
				component: () =>
					import('../components/device/DeviceModel')
			},
			{
				path: '/devicePoint',
				component: () =>
					import('../components/device/DevicePoint')
			},
			{
				path: '/users',
				component: () =>
					import('../components/users/Users')
			},
			{
				path: '/roles',
				component: () =>
					import('../components/users/Roles')
			}
		]
	}
]

const router = new VueRouter({
	routes
})


//路由导航守卫
router.beforeEach((to, from, next) => {
	if (to.path === '/login') return next()
	const tokenStr = window.sessionStorage.getItem('cw.iot.token.currentUser');
	const lastOperationTime = window.sessionStorage.getItem('cw.iot.token.lastOperateTime');
	let nowTime = (new Date()).getTime();
	if (!tokenStr || (nowTime - lastOperationTime > 1800000)) return next('/login')
	next()
})
export default router
