<template>
	<div class="login_container">
		<div class="login_title"><span>IOT平台管理系统 V1.0</span></div>
		<div class="login_box">
			<!-- 头像 -->
			<div class="avatar_box"><img src="../assets/logo.png"/></div>
			<!-- 表单 -->
			<el-form ref="LoginFormRef" :model="loginForm" label-width="0" :rules="LoginFormRules" class="login_form">
				<el-form-item prop="userName"><el-input v-model="loginForm.userName" prefix-icon="el-icon-user"></el-input></el-form-item>
				<el-form-item prop="password"><el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password"></el-input></el-form-item>
				<el-form-item class="btns">
					<el-button type="primary" @click="login">登录</el-button>
					<el-button type="info" @click="resetLoginForm">重置</el-button>
				</el-form-item>
			</el-form>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			loginForm: {
				userName: 'admin',
				password: 'admin'
			},
			LoginFormRules: {
				userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
				password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
			}
		};
	},
	methods: {
		resetLoginForm() {
			this.$refs.LoginFormRef.resetFields();
			this.loginForm.userName = '';
			this.loginForm.password = '';
		},
		login() {
			this.$refs['LoginFormRef'].validate(async valid => {
				if (valid) {
					const { data } = await this.$http.post('/user/login', this.$qs.stringify(this.loginForm));
					if (data.code === 200) {
						this.$message({
							message: '登录成功',
							type: 'success'
						});
						window.sessionStorage.setItem('cw.iot.token.currentUser', this.loginForm.userName);
						window.sessionStorage.setItem('cw.iot.token.lastOperateTime', (new Date()).getTime());
						this.$router.push('/home');
					} else {
						this.$message({
							message: data.msg,
							type: 'error'
						});
					}
				} else {
					return false;
				}
			});
		}
	}
};
</script>

<style lang="less" scoped>
.login_container {
	background-color: #2b4b6b;
	height: 100%;
}

.login_title {
	width: 500px;
	height: 100px;
	font-size: 45px;
	font-weight: 600;
	color: whitesmoke;
	position: absolute;
	top: 8%;
	left: 31%;
}

.login_box {
	width: 450px;
	height: 300px;
	background-color: whitesmoke;
	border-radius: 3px;
	position: absolute;
	left: 48%;
	top: 62%;
	transform: translate(-50%, -50%);
}

.avatar_box {
	width: 130px;
	height: 130px;
	border: 1px solid #eee;
	border-radius: 50%;
	padding: 10px;
	box-shadow: 0 0 10px #ddd;
	position: absolute;
	left: 50%;
	transform: translate(-50%, -50%);
	background-color: #fff;
	img {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background-color: #eee;
	}
}

.login_form {
	position: absolute;
	bottom: 0;
	width: 100%;
	padding: 20px;
	box-sizing: border-box;
}

.btns {
	display: flex;
	justify-content: flex-end;
}
</style>
>
