<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>用户管理</el-breadcrumb-item>
			<el-breadcrumb-item>角色列表</el-breadcrumb-item>
		</el-breadcrumb>

		<el-card>
			<el-row>
				<el-col><el-button type="primary" @click="showSetPermissionDialog(-1)">添加角色</el-button></el-col>
			</el-row>

			<el-table :data="rolelist" border stripe>
				<el-table-column type="index"></el-table-column>
				<el-table-column prop="roleName" label="角色名称"></el-table-column>
				<el-table-column prop="remark" label="备注">
					<template v-slot="scope">
						{{ scope.row.remark | isNull }}
					</template>
				</el-table-column>
				<el-table-column prop="rowCreateTime" label="创建时间">
					<template v-slot="scope">
						{{ scope.row.rowCreateTime | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="rowModifierTime" label="最后更新时间">
					<template v-slot="scope">
						{{ scope.row.rowModifierTime | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column label="操作">
					<template v-slot="scope">
						<el-button size="small" type="primary" icon="el-icon-edit" @click="showSetPermissionDialog(scope.row.id)">编辑</el-button>
						<el-button size="small" type="danger" icon="el-icon-delete" @click="removeRoleById(scope.row.id)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>

		<el-dialog :title="dialogTitle" :visible.sync="setPermissionDialogVisible" @close="setPermissionDialogClosed" width="40%">
			<el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
				<el-form-item label="角色名称：" prop="roleName"><el-input v-model="addForm.roleName"></el-input></el-form-item>
				<el-form-item label="备注：" prop="remark"><el-input v-model="addForm.remark"></el-input></el-form-item>
				<el-form-item label="权限：">
					<el-tree :data="permissionlist" ref="treeRef" node-key="id" default-expand-all show-checkbox :props="treeProps"></el-tree>
				</el-form-item>
			</el-form>
			<div slot="footer">
				<el-button @click="setPermissionDialogClosed">取 消</el-button>
				<el-button type="primary" @click="distributionPermission">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
export default {
	data() {
		return {
			rolelist: [],
			setPermissionDialogVisible: false,
			addForm: {
				id: '',
				roleName: '',
				remark: ''
			},
			permissionlist: [],
			treeProps: {
				children: 'childrens',
				label: 'permissionName'
			},
			dialogTitle: '添加角色',
			isUpdate: false,
			addFormRules: {
				roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
			}
		};
	},
	methods: {
		async getRoleList() {
			const { data } = await this.$http.post('/user/getAllRoleList');
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			this.rolelist = data.allRoleList;
		},
		async removeRoleById(id) {
			this.$confirm('此操作将永久删除该角色, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			})
				.then(async () => {
					const { data } = await this.$http.post('/user/removeRole', this.$qs.stringify({ id }));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					}
					this.getRoleList();
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
		async showSetPermissionDialog(id) {
			if (id !== -1) {
				this.isUpdate = true;
			}
			const { data } = await this.$http.post('/user/getPermissionList');
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			this.permissionlist = data.permissionList;
			this.setPermissionDialogVisible = true;
			if (this.isUpdate) {
				this.dialogTitle = '修改角色';
				await this.getPermissionsByRole(id);
			} else {
				const defaultCheckedKeys = [];
				this.$refs.treeRef.setCheckedKeys(defaultCheckedKeys);
			}
		},
		async getPermissionsByRole(id) {
			const perLevel = 3;
			const { data } = await this.$http.post('/user/getPermissionsByRole', this.$qs.stringify({ id, perLevel }));
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			this.addForm.roleName = data.role.roleName;
			this.addForm.id = data.role.id;
			this.addForm.remark = data.role.remark;
			let checkedKeys = [];
			data.rolePermissionList.forEach(p => {
				checkedKeys.push(p.id);
			});
			this.$refs.treeRef.setCheckedKeys(checkedKeys);
		},
		async distributionPermission() {
			this.$refs.addFormRef.validate(async valid => {
				if (!valid) return;
				const keys = [...this.$refs.treeRef.getCheckedKeys(), ...this.$refs.treeRef.getHalfCheckedKeys()];
				const ids = keys.join(',');
				if (!this.isUpdate) {
					const { data } = await this.$http.post('/user/addRole', this.$qs.stringify({ roleName: this.addForm.roleName, remark: this.addForm.remark, perIds: ids }));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('添加角色成功');
					}
				} else {
					const { data } = await this.$http.post(
						'/user/modifyRole',
						this.$qs.stringify({ roleName: this.addForm.roleName, remark: this.addForm.remark, id: this.addForm.id, perIds: ids })
					);
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('修改角色成功');
					}
				}
				this.setPermissionDialogClosed();
				this.getRoleList();
			});
		},
		setPermissionDialogClosed() {
			this.isUpdate = false;
			this.setPermissionDialogVisible = false;
			this.dialogTitle = '添加角色';
			this.addForm.id = '';
			this.addForm.roleName = '';
			this.addForm.remark = '';
			this.permissionlist = [];
		}
	},
	created() {
		this.getRoleList();
	}
};
</script>

<style lang="less" scoped>
.el-tag {
	margin: 7px;
}

.bd-top {
	border-top: 1px solid #eee;
}

.bd-bottom {
	border-bottom: 1px solid #eee;
}

.vcenter {
	display: flex;
	align-items: center;
}
</style>
