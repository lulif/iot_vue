export default [{
		id: 104,
		authName: '数据统计',
		path: 'display',
		children: [{
			id: 1,
			authName: '数据展示',
			path: 'dataDisplay',
			children: [],
			order: null
		}],
		order: 1
	},
	{
		id: 101,
		authName: '设备管理',
		path: 'deviceManage',
		children: [{
				id: 1,
				authName: '设备类型',
				path: 'deviceModel',
				children: [],
				order: null
			},
			{
				id: 2,
				authName: '数据模型',
				path: 'devicePoint',
				children: [],
				order: null
			},
			{
				id: 3,
				authName: '设备信息',
				path: 'device',
				children: [],
				order: null
			}
		],
		order: 2
	},
	{
		id: 125,
		authName: '账号管理',
		path: 'userManage',
		children: [{
			id: 1,
			authName: '用户管理',
			path: 'users',
			children: [],
			order: null
		}, {
			id: 2,
			authName: '角色管理',
			path: 'roles',
			children: [],
			order: null
		}],
		order: 3
	}
]
