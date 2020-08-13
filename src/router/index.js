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
		component: () =>
			import('../views/Home'),
		children: [{
				path: '/welcome',
				component: () =>
					import('../components/Welcome')
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
				path: '/rights',
				component: () =>
					import('../components/power/Rights')
			},
			{
				path: '/roles',
				component: () =>
					import('../components/power/Roles')
			},
			{
				path: '/categories',
				component: () =>
					import('../components/goods/Cate.vue')
			},
			{
				path: '/params',
				component: () =>
					import('../components/goods/Params.vue')
			},
			{
				path: '/goods',
				component: () =>
					import('../components/goods/List.vue')
			},
			{
				path: '/goods/add',
				component: () =>
					import('../components/goods/Add.vue')
			},
			{
				path: '/orders',
				component: () =>
					import('../components/order/Order.vue')
			},
			{
				path: '/reports',
				component: () =>
					import('../components/report/Report.vue')
			}
		],
	}
]

const router = new VueRouter({
	routes
})


//路由导航守卫
router.beforeEach((to, from, next) => {
	if (to.path === '/login') return next()
	const tokenStr = window.sessionStorage.getItem('token')
	if (!tokenStr) return next('/login')
	next()
})
export default router
