<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>用户管理</el-breadcrumb-item>
			<el-breadcrumb-item>用户管理</el-breadcrumb-item>
		</el-breadcrumb>

		<el-card>
			<el-row :gutter="20">
				<el-col :span="20"><el-button type="primary" @click="showAddDialog">添加用户</el-button></el-col>
			</el-row>

			<el-table :data="userList" stripe border style="width: 100%">
				<el-table-column type="index"></el-table-column>
				<el-table-column prop="userName" label="用户名"></el-table-column>
				<el-table-column prop="role" label="角色"></el-table-column>
				<el-table-column prop="remark" label="备注">
					<template v-slot="scope">
						{{ scope.row.remark | isNull }}
					</template>
				</el-table-column>
				<el-table-column prop="rowCreater" label="创建人">
					<template v-slot="scope">
						{{ scope.row.rowCreater | isNull }}
					</template>
				</el-table-column>
				<el-table-column prop="rowCreateTime" label="创建时间">
					<template v-slot="scope">
						{{ scope.row.rowCreateTime | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="rowModifier" label="最后更新人">
					<template v-slot="scope">
						{{ scope.row.rowModifier | isNull }}
					</template>
				</el-table-column>
				<el-table-column prop="rowModifierTime" label="最后更新时间">
					<template v-slot="scope">
						{{ scope.row.rowModifierTime | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="rowState" label="状态">
					<template v-slot="scope">
						<el-button
							style="margin: 0 auto"
							size="small"
							:type="scope.row.rowState == 1 ? 'primary' : 'danger'"
							v-model="scope.row.rowState"
							@click="userStateChanged(scope.row)"
						>
							{{ scope.row.rowState === 1 ? '已启用' : '已禁用' }}
						</el-button>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="130px">
					<template v-slot="scope">
						<el-button size="mini" type="primary" icon="el-icon-edit" @click="updateById(scope.row.id)"></el-button>
						<el-button size="mini" type="warning" icon="el-icon-delete" @click="removeById(scope.row.id)"></el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>

		<el-dialog :title="dialogTitle" :visible.sync="addDialogVisible" @close="addDialogClosed" width="50%">
			<el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
				<el-form-item label="用户名：" prop="userName"><el-input v-model="addForm.userName"></el-input></el-form-item>
				<el-form-item v-if="!isUpdate" label="密码：" prop="password"><el-input v-model="addForm.password" show-password></el-input></el-form-item>
				<el-form-item label="角色：" prop="role">
					<el-select v-model="addForm.role" placeholder="请选择">
						<el-option v-for="item in allRoleList" :key="item.id" :label="item.roleName" :value="item.id"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="备注：" prop="remark"><el-input v-model="addForm.remark"></el-input></el-form-item>
			</el-form>
			<div slot="footer">
				<el-button @click="addDialogClosed">取 消</el-button>
				<el-button type="primary" @click="addUser">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
export default {
	data() {
		return {
			userList: [],
			allRoleList: [],
			addDialogVisible: false,
			addForm: {
				id: '',
				userName: '',
				password: '',
				role: '',
				remark: ''
			},
			isUpdate: false,
			dialogTitle: '添加用户',
			addFormRules: {
				userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
				password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
				role: [{ required: true, message: '请选择角色', trigger: 'blur' }]
			}
		};
	},
	methods: {
		async userStateChanged(user) {
			let rowState = user.rowState === 1 ? 0 : 1;
			let id = user.id;
			const { data } = await this.$http.post('/user/modifyUser', this.$qs.stringify({ id, rowState }));
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			} else {
				this.$message.success('状态设置成功');
			}
			this.getUserList();
		},
		async addUser() {
			this.$refs.addFormRef.validate(async valid => {
				if (!valid) return;
				if (!this.isUpdate) {
					const { data } = await this.$http.post('/user/addUser', this.$qs.stringify(this.addForm));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('添加用户成功');
					}
				} else {
					const { data } = await this.$http.post('/user/modifyUser', this.$qs.stringify(this.addForm));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('修改用户成功');
					}
				}
				this.getUserList();
				this.addDialogClosed();
			});
		},
		async updateById(id) {
			const { data } = await this.$http.post('/user/getUserById', this.$qs.stringify({ id }));
			if (data.code == 200) {
				let form = this.addForm;
				let user = data.user;
				form.id = user.id;
				form.userName = user.userName;
				form.password = user.password;
				form.role = user.role;
				form.remark = user.remark;
			}
			this.dialogTitle = '修改用户';
			this.showAddDialog();
			this.isUpdate = true;
		},
		async getUserList() {
			const { data } = await this.$http.post('/user/getUserList');
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			this.userList = data.userList;
		},
		async getAllRoleList() {
			const { data } = await this.$http.post('/user/getAllRoleList');
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			data.allRoleList.forEach(role => {
				if (role.rowState > 0) {
					this.allRoleList.push(role);
				}
			});
		},
		async removeById(id) {
			this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			})
				.then(async () => {
					const { data } = await this.$http.post('/user/removeUser', this.$qs.stringify({ id }));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					}
					this.getUserList();
					this.$message({
						type: 'success',
						message: '删除成功!'
					});
				})
				.catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					});
				});
		},
		showAddDialog() {
			this.addDialogVisible = true;
		},
		addDialogClosed() {
			this.addDialogVisible = false;
			this.isUpdate = false;
			this.dialogTitle = '添加用户';
			this.$refs.addFormRef.resetFields();
			this.addForm.userName = '';
			this.addForm.password = '';
			this.addForm.role = '';
			this.addForm.remark = '';
		}
	},
	created() {
		this.getAllRoleList();
		this.getUserList();
	}
};
</script>

<style lang="less" scoped></style>
