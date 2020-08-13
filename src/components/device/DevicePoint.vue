<template>
	<div>
		<el-breadcrumb separator-class="el-icon-arrow-right">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>设备管理</el-breadcrumb-item>
			<el-breadcrumb-item>设备点位</el-breadcrumb-item>
		</el-breadcrumb>

		<el-card>
			<el-row :gutter="20">
				<el-col :span="8">
					<el-input placeholder="请输入内容" clearable v-model="queryInfo.query" @clear="getGoodsList">
						<el-button
							slot="append"
							icon="el-icon-search"
							@click="
								handleCurrentChange(1);
								getGoodsList;
							"
						></el-button>
					</el-input>
				</el-col>
				<el-col :span="4"><el-button type="primary" @click="showAddDialog">添加设备点位</el-button></el-col>
			</el-row>

			<el-table :data="goodslist" stripe border style="width: 100%">
				<el-table-column type="index"></el-table-column>
				<el-table-column prop="goods_price" label="点位ID"></el-table-column>
				<el-table-column prop="goods_price" label="设备编号"></el-table-column>
				<el-table-column prop="goods_weight" label="点位英文名"></el-table-column>
				<el-table-column prop="goods_weight" label="点位中文名"></el-table-column>
				<el-table-column prop="goods_weight" label="备注"></el-table-column>
				<el-table-column prop="goods_weight" label="创建人"></el-table-column>
				<el-table-column prop="goods_weight" label="创建时间">
					<template v-slot="scope">
						{{ scope.row.add_time | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="goods_weight" label="最后更新人"></el-table-column>
				<el-table-column prop="goods_weight" label="最后更新时间">
					<template v-slot="scope">
						{{ scope.row.add_time | dateFormat }}
					</template>
				</el-table-column>
				<el-table-column prop="goods_weight" label="状态"></el-table-column>
				<el-table-column label="操作" width="130px">
					<template v-slot="scope">
						<el-button size="mini" type="primary" icon="el-icon-edit"></el-button>
						<el-button size="mini" type="warning" icon="el-icon-delete" @click="removeById(scope.row.goods_id)"></el-button>
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
		
		<el-dialog title="添加设备点位" :visible.sync="addDialogVisible" @close="addDialogClosed" width="50%">
			<el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
				<el-form-item label="点位ID：" prop="id"><el-input v-model="addForm.id"></el-input></el-form-item>
				<el-form-item label="设备编号：" prop="deviceId"><el-input v-model="addForm.deviceId"></el-input></el-form-item>
				<el-form-item label="点位英文名：" prop="pointNameEn"><el-input v-model="addForm.pointNameEn"></el-input></el-form-item>
				<el-form-item label="点位中文名：" prop="pointNameCn"><el-input v-model="addForm.pointNameCn"></el-input></el-form-item>
				<el-form-item label="备注：" prop="remark"><el-input v-model="addForm.remark"></el-input></el-form-item>
			</el-form>
			<div slot="footer">
				<el-button @click="addDialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="addCate">确 定</el-button>
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
				pagenum: 1,
				pagesize: 10
			},
			goodslist: [],
			total: 0,
			addDialogVisible: false,
			addForm: {
				id: '',
				deviceId: '',
				pointNameEn: '',
				pointNameCn: '',
				remark:''
			},
			addFormRules: {
				id: [{ required: true, message: '请输入设备ID', trigger: 'blur' }]
			}
		};
	},
	methods: {
		addCate() {
			this.$refs.addFormRef.validate(async valid => {
				if (!valid) return;
				const { data } = await this.$http.post('categories', this.addCateForm);
				if (data.meta.status !== 201) {
					return this.$message.error(data.meta.msg);
				}
				this.$message.success('添加分类成功！');
				this.getCateList();
				this.addCateDialogVisible = false;
			});
		},
		showAddDialog() {
			this.addDialogVisible = true;
		},
		addDialogClosed() {
			this.$refs.addFormRef.resetFields();
			this.addForm.id = "";
			this.addForm.deviceId = "";
			this.addForm.pointNameEn = "";
			this.addForm.pointNameCn = "";
			this.addForm.remark = "";
		},
		async getGoodsList() {
			const { data } = await this.$http.get('goods', {
				params: this.queryInfo
			});
			if (data.meta.status !== 200) {
				return this.$message.error(data.meta.msg);
			}
			this.goodslist = data.data.goods;
			this.total = data.data.total;
		},
		handleSizeChange(newSize) {
			this.queryInfo.pagesize = newSize;
			this.getGoodsList();
		},
		handleCurrentChange(newPage) {
			this.queryInfo.pagenum = newPage;
			this.getGoodsList();
		},
		removeById(id) {
			this.$confirm('此操作将永久删除该商品, 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			})
				.then(async () => {
					const { data } = await this.$http.delete(`goods/${id}`);
					if (data.meta.status !== 200) {
						return this.$message.error(data.meta.msg);
					}
					this.getGoodsList();
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
		goAddPage() {
			this.$router.push('goods/add');
		}
	},
	created() {
		this.getGoodsList();
	}
};
</script>

<style lang="less" scoped></style>
