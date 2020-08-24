<template>
	<el-container style="height:100vh">
		<el-header>
			<div>
				<img src="../assets/logo.png"/>
				<span>IOT平台管理系统 V1.0</span>
			</div>
			<div style="float: right;"><span style="margin-right: 20px;">{{ userName }} 您好,欢迎登陆使用</span>
				<el-button type="info" @click="logout">退出</el-button>
			</div>
		</el-header>
		<el-container>
			<el-aside :width="isCollapse ? '64px' : '200px'">
				<div class="toggle-button" @click="toggleCollapse">|||</div>
				<el-menu
					router
					background-color="#333744"
					text-color="#fff"
					active-text-color="#409EFF"
					unique-opened
					:collapse="isCollapse"
					:collapse-transition="false"
					:default-active="$route.path"
				>
					<el-submenu :index="item.id + ''" v-for="item in menulist" :key="item.id">
						<template slot="title">
							<i :class="iconObj[item.id]"></i>
							<span>{{ item.authName }}</span>
						</template>
						<el-menu-item :index="'/' + subItem.path" v-for="subItem in item.children" :key="subItem.id">
							<template slot="title">
								<i class="el-icon-menu"></i>
								<span>{{ subItem.authName }}</span>
							</template>
						</el-menu-item>
					</el-submenu>
				</el-menu>
			</el-aside>
			<el-main><router-view></router-view></el-main>
		</el-container>
	</el-container>
</template>

<script>
import LeftNav from '../data/leftNav.js';
export default {
	data() {
		return {
			userName: '',
			menulist: LeftNav,
			iconObj: {
				'125': 'iconfont icon-icon_user',
				'103': 'iconfont icon-tijikongjian',
				'101': 'iconfont icon-shangpin',
				'102': 'iconfont icon-danju',
				'145': 'iconfont icon-baobiao',
				'104': 'iconfont icon-danju'
			},
			isCollapse: false
		};
	},
	methods: {
		logout() {
			window.sessionStorage.clear()
			this.$router.push('/login');
		},
		toggleCollapse() {
			this.isCollapse = !this.isCollapse;
		}
	},
	created() {
		this.userName = window.sessionStorage.getItem('cw.iot.token.currentUser');
	}
};
</script>

<style lang="less" scoped>
.el-header {
	background-color: #373d41;
	display: flex;
	justify-content: space-between;
	padding-left: 0;
	align-items: center;
	color: #fff;
	font-size: 20px;
	> div {
		display: flex;
		align-items: center;
		span {
			margin-left: 15px;
		}
	}
	img {
		width: 50px;
		height: 50px;
	}
}

.el-aside {
	background-color: #333744;
	.el-menu {
		border-right: none;
	}
}

.el-main {
	background-color: #eaedf1;
}

.iconfont {
	margin-right: 10px;
}

.toggle-button {
	background-color: #4a5064;
	font-size: 10px;
	line-height: 24px;
	color: #fff;
	text-align: center;
	letter-spacing: 0.2em;
	cursor: pointer;
}
</style>
