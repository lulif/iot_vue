<template>
	<div>
		<el-breadcrumb style="font-size:15px">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>设备管理</el-breadcrumb-item>
			<el-breadcrumb-item>设备类型</el-breadcrumb-item>
		</el-breadcrumb>

		<el-card>
			<el-row :gutter="20">
				<el-col :span="3"><div class="search-item">搜索关键字：</div></el-col>
				<el-col :span="4" style="margin-left: -50px;"><el-input placeholder="按名称搜索" clearable v-model="queryInfo.query"></el-input></el-col>
				<el-col :span="3"><div class="search-item">是否启用：</div></el-col>
				<el-col :span="4" style="margin-left: -50px;">
					<el-select v-model="queryInfo.state" clearable placeholder="请选择">
						<el-option key="1" value="1" label="已启用"></el-option>
						<el-option key="2" value="0" label="已禁用"></el-option>
					</el-select>
				</el-col>
				<el-col :span="3">
					<el-button
						type="success"
						@click="
							handleCurrentChange(1);
							getDeviceModelList;
						"
					>
						查询
					</el-button>
				</el-col>
				<el-col :span="3" style="float: right;"><el-button type="primary" @click="showAddDialog">添加</el-button></el-col>
			</el-row>

			<el-table :data="deviceModelList" stripe border style="width: 100%">
				<el-table-column type="index"></el-table-column>
				<el-table-column prop="modelName" label="类型名称"></el-table-column>
				<!-- <el-table-column prop="identifyEn" label="英文标识"></el-table-column> -->
				<el-table-column prop="rowCreater" label="创建人"></el-table-column>
				<el-table-column prop="rowCreateTime" label="创建时间">
					<template v-slot="scope">
						{{ scope.row.rowCreateTime | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="rowModifier" label="最后更新人"></el-table-column>
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

			<el-pagination
				@size-change="handleSizeChange"
				@current-change="handleCurrentChange"
				:current-page="queryInfo.pagenum"
				:page-sizes="[10, 20, 50]"
				:page-size="queryInfo.pagesize"
				layout="total, sizes, prev, pager, next, jumper"
				:total="total"
				background
			></el-pagination>
		</el-card>

		<el-dialog :title="dialogTitle" :visible.sync="addDialogVisible" @close="addDialogClosed" width="50%">
			<el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
				<el-form-item label="类型名称：" prop="modelName"><el-input v-model="addForm.modelName"></el-input></el-form-item>
				<el-form-item label="英文标识：" prop="identifyEn"><el-input v-model="addForm.identifyEn"></el-input></el-form-item>
			</el-form>
			<div slot="footer">
				<el-button @click="addDialogClosed">取 消</el-button>
				<el-button type="primary" @click="addDeviceModel">确 定</el-button>
			</div>
		</el-dialog>
	</div>
</template>

<script>
export default {
	data() {
		return {
			queryInfo: {
				query: '',
				currentPage: 1,
				pageSize: 10,
				state: ''
			},
			deviceModelList: [],
			total: 0,
			addDialogVisible: false,
			addForm: {
				id: '',
				modelName: '',
				identifyEn: ''
			},
			isUpdate: false,
			dialogTitle: '添加设备类型',
			addFormRules: {
				modelName: [{ required: true, message: '请输入型号名称', trigger: 'blur' }],
				identifyEn: [{ required: true, message: '请输入英文标识', trigger: 'blur' }]
			}
		};
	},
	methods: {
		async userStateChanged(deviceModel) {
			let rowState = deviceModel.rowState == 1 ? 0 : 1;
			let id = deviceModel.id;
			console.log(deviceModel);
			const { data } = await this.$http.post('/device/modifyDeviceModel', this.$qs.stringify({ id, rowState }));
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			} else {
				this.$message.success('状态设置成功');
			}
			this.getDeviceModelList();
		},
		async addDeviceModel() {
			this.$refs.addFormRef.validate(async valid => {
				if (!valid) return;
				if (!this.isUpdate) {
					const { data } = await this.$http.post('/device/addDeviceModel', this.$qs.stringify(this.addForm));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('添加设备类型成功');
					}
				} else {
					const { data } = await this.$http.post('/device/modifyDeviceModel', this.$qs.stringify(this.addForm));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					} else {
						this.$message.success('修改设备类型成功');
					}
				}
				this.getDeviceModelList();
				this.addDialogClosed();
			});
		},
		async updateById(id) {
			const { data } = await this.$http.post('/device/getDeviceModelById', this.$qs.stringify({ id }));
			if (data.code == 200) {
				let form = this.addForm;
				let model = data.deviceModel;
				form.id = model.id;
				form.modelName = model.modelName;
				form.identifyEn = model.identifyEn;
			}
			this.dialogTitle = '修改设备类型';
			this.showAddDialog();
			this.isUpdate = true;
		},
		async getDeviceModelList() {
			const { data } = await this.$http.post('/device/getDeviceModelList', this.$qs.stringify(this.queryInfo));
			if (data.code !== 200) {
				return this.$message.error(data.msg);
			}
			this.deviceModelList = data.list;
			this.total = data.total;
		},
		async removeById(id) {
			this.$confirm('此操作将永久删除该设备类型, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			})
				.then(async () => {
					const { data } = await this.$http.post('/device/removeDeviceModel', this.$qs.stringify({ id }));
					if (data.code !== 200) {
						return this.$message.error(data.msg);
					}
					this.getDeviceModelList();
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
		handleSizeChange(newSize) {
			this.queryInfo.pageSize = newSize;
			this.getDeviceModelList();
		},
		handleCurrentChange(newPage) {
			this.queryInfo.currentPage = newPage;
			this.getDeviceModelList();
		},
		showAddDialog() {
			this.addDialogVisible = true;
		},
		addDialogClosed() {
			this.addDialogVisible = false;
			this.isUpdate = false;
			this.dialogTitle = '添加设备类型';
			this.$refs.addFormRef.resetFields();
			this.addForm.modelName = '';
			this.addForm.identifyEn = '';
		}
	},
	created() {
		this.getDeviceModelList();
	}
};
</script>

<style scoped>
.search-item {
	font-size: 21px;
	font-weight: 600;
	margin-top: 13px;
}
</style>
