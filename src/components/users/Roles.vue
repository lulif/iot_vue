<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>用户管理</el-breadcrumb-item>
			<el-breadcrumb-item>角色列表</el-breadcrumb-item>
		</el-breadcrumb>

		<el-card>
			<el-row>
				<el-col><el-button type="primary">添加角色</el-button></el-col>
			</el-row>

			<el-table :data="rolelist" border stripe>
				<el-table-column type="expand">
					<template v-slot="scope">
						<el-row :class="['bd-bottom', 'vcenter', index1 == 0 ? 'bd-top' : '']" v-for="(item1, index1) in scope.row.children" :key="item1.id">
							<!-- 一级权限 -->
							<el-col :span="5">
								<el-tag closable @close="removeRightById(scope.row, item1.id)">{{ item1.authName }}</el-tag>
								<i class="el-icon-caret-right"></i>
							</el-col>
							<!-- 二级权限/三级权限 -->
							<el-col :span="19">
								<el-row :class="[index2 == 0 ? '' : 'bd-top', 'vcenter']" v-for="(item2, index2) in item1.children" :key="item2.id">
									<el-col :span="6">
										<el-tag closable @close="removeRightById(scope.row, item2.id)" type="success">{{ item2.authName }}</el-tag>
										<i class="el-icon-caret-right"></i>
									</el-col>
									<el-col :span="18">
										<el-tag closable @close="removeRightById(scope.row, item3.id)" type="warning" v-for="item3 in item2.children" :key="item3.id">
											{{ item3.authName }}
										</el-tag>
									</el-col>
								</el-row>
							</el-col>
						</el-row>
					</template>
				</el-table-column>
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
						<el-button size="small" type="primary" icon="el-icon-edit" @click="showSetPermissionDialog(scope.row)">编辑</el-button>
						<el-button size="small" type="danger" icon="el-icon-delete" @click="removeRoleById(scope.row.id)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>

		<!-- 分配权限对话框 -->
		<el-dialog title="分配权限" :visible.sync="setRightDialogVisible" @close="setRightDialogClosed" width="50%">
			<el-tree :data="permissionlist" :default-checked-keys="defKeys" node-key="id" ref="treeRef" default-expand-all show-checkbox :props="treeProps"></el-tree>
			<div slot="footer">
				<el-button @click="setRightDialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="allotRights">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
export default {
	data() {
		return {
			rolelist: [],
			setRightDialogVisible: false,
			permissionlist: [],
			treeProps: {
				children: 'children',
				label: 'authName'
			},
			defKeys: [],
			roleId: ''
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

		removeRightById(role, rightId) {
			this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			})
				.then(async () => {
					const { data } = await this.$http.delete(`roles/${role.id}/rights/${rightId}`);
					if (data.meta.status !== 200) {
						return this.$message.error(data.meta.msg);
					}
					this.$message({
						type: 'success',
						message: '删除成功!'
					});
					role.children = data.data;
				})
				.catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					});
				});
		},
		async showSetPermissionDialog(role) {
			this.roleId = role.id;
			const { data } = await this.$http.get('rights/tree');
			if (data.meta.status !== 200) {
				return this.$message.error(data.meta.msg);
			}
			this.permissionlist = data.data;
			this.getLeafKeys(role, this.defKeys);
			this.setRightDialogVisible = true;
		},
		getLeafKeys(node, arr) {
			if (!node.children) {
				return arr.push(node.id);
			}
			node.children.forEach(item => this.getLeafKeys(item, arr));
		},
		setRightDialogClosed() {
			this.defKeys = [];
		},
		async allotRights() {
			const keys = [...this.$refs.treeRef.getCheckedKeys(), ...this.$refs.treeRef.getHalfCheckedKeys()];
			console.log(keys);
			const idStr = keys.join(',');
			const { data } = await this.$http.post(`roles/${this.roleId}/rights`, { rids: idStr });
			if (data.meta.status !== 200) {
				return this.$message.error(data.meta.msg);
			}
			this.$message.success('分配权限成功！');
			this.setRightDialogVisible = false;
			this.getRoleList();
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
