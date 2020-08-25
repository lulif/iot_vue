<template>
	<div>
		<el-breadcrumb style="font-size:15px">
			<el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
			<el-breadcrumb-item>电能监控</el-breadcrumb-item>
			<el-breadcrumb-item>电表详情</el-breadcrumb-item>
		</el-breadcrumb>

		<div style="height:100%;width:100%">
			<el-row>
				<el-rol :span="12"><span style="font-size: 18px; font-weight: 600; margin-left: 20px; ">安科瑞PZ72L-DE/J型电表详情页</span></el-rol>
			</el-row>

			<el-row>
				<el-col :span="12">
					<el-card class="box-card common device-introduction">
						<div slot="header" class="clearfix"><span class="card-title">设备简介</span></div>
						<el-row>
							<el-col :span="12"><img src="../../assets/device.png" style="margin-left:35px;width: 150px;height:150px" /></el-col>
							<el-col :span="12">
								<ul style="list-style: none;font-size:13px; font-weight: 500; margin-left: -45px;">
									<li class="li">
										<i class="el-icon-user"></i>
										安科瑞PZ72L-DE/J型电表
									</li>
									<li class="li">
										<i class="el-icon-s-tools"></i>
										<span :style="deviceState == 1 ? 'color:green;' : 'color:black;'">已连接</span>
										/
										<span :style="deviceState == 0 ? 'color:red;' : 'color:black;'">已断开</span>
									</li>
									<li class="li">
										<i class="el-icon-location-outline"></i>
										192.168.0.221
									</li>
									<li class="li">
										<i class="el-icon-location-outline"></i>
										广东-东莞-拓斯达总部
									</li>
								</ul>
							</el-col>
						</el-row>
					</el-card>
				</el-col>
				<el-col :span="12">
					<el-card class="box-card common device-explain">
						<div slot="header" class="clearfix"><span class="card-title">设备说明</span></div>
						<div style="font-size:12px;">
							<span>工作环境</span>
							<br />
							<span>工作温度: LCD显示: -10°C~+45°C; LED显示: -10°C-+55°C</span>
							<br />
							<span>储存温度: -20C~+70°C等</span>
							<br />
							<span>相对湿度:≤93%RH,不结露，无腐蚀性气体场所 海拔高度:≤2500</span>
							<br />
							<span>直流电流 0~10A (直接输入，启动电流: 5mA)</span>
							<br />
							<span>0~9999A (外置分流器或霍尔元件，量程可编程设定)</span>
							<br />
							<span>分流器支持输出为75mV</span>
							<br />
							<span>霍尔件支持输出为0~20mA、4~20mA、0~5V、0~10V等</span>
							<br />
							<span>功耗≤1mW</span>
							<br />
						</div>
					</el-card>
				</el-col>
			</el-row>
			<br />
			<el-row>
				<el-col :span="24">
					<el-card class="box-card common live-time">
						<div slot="header" class="clearfix"><span class="card-title">实时数据</span></div>
						<el-row :gutter="20">
							<el-col :span="4" v-for="data in deviceData" :key="data" style="margin-bottom: 20px;">
								<el-card :body-style="{ padding: '0px' }">
									<div style="height: 90px;">
										<div class="data-type">{{ data.dataName }}</div>
										<div
											class="data-value"
											:style="data.maxValue < parseInt(data.dataSummaryValue.substring(0, data.dataSummaryValue.length - 1)) ? 'color:red' : 'color:black'"
										>
											{{ data.dataVale }}{{ data.dataUnit }}
										</div>
									</div>
									<hr style="color: gray; width: 78%;" />
									<div style="height: 40px; font-size:13px;font-weight: 600;">
										<span style="margin-left: 30px;">{{ data.dataSummary }}</span>
										<span
											style="margin-left: 10px;"
											:style="data.maxValue < parseInt(data.dataSummaryValue.substring(0, data.dataSummaryValue.length - 1)) ? 'color:red' : 'color:black'"
										>
											{{ data.dataSummaryValue }}
										</span>
									</div>
								</el-card>
							</el-col>
						</el-row>
					</el-card>
				</el-col>
				<el-col :span="24">
					<el-card class="box-card common live-trend-chart">
						<div slot="header" class="clearfix"><span class="card-title">A相电压 实时趋势图</span></div>
						<div id="main" style="width: 100%; height:500px;"></div>
					</el-card>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
import echarts from 'echarts';
export default {
	data() {
		return {
			deviceState: 0,
			deviceData: [
				{
					dataName: 'A相电压',
					dataVale: '380',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '+30%',
					maxValue: '100'
				},
				{
					dataName: 'B相电压',
					dataVale: '220',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '+20%',
					maxValue: '100'
				},
				{
					dataName: 'C相电压',
					dataVale: '36',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '0%',
					maxValue: '100'
				},
				{
					dataName: 'A-B线电压',
					dataVale: '380',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '-10%',
					maxValue: '100'
				},
				{
					dataName: 'C-B线电压',
					dataVale: '220',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '-20%',
					maxValue: '100'
				},
				{
					dataName: 'A-C线电压',
					dataVale: '36',
					dataUnit: 'V',
					dataSummary: '电压偏差',
					dataSummaryValue: '-30%',
					maxValue: '100'
				},
				{
					dataName: 'A相电流',
					dataVale: '1000',
					dataUnit: 'A',
					dataSummary: '使用负载',
					dataSummaryValue: '101%',
					maxValue: '100'
				},
				{
					dataName: 'B相电流',
					dataVale: '500',
					dataUnit: 'A',
					dataSummary: '使用负载',
					dataSummaryValue: '50%',
					maxValue: '100'
				},
				{
					dataName: 'C相电流',
					dataVale: '45',
					dataUnit: 'A',
					dataSummary: '使用负载',
					dataSummaryValue: '1%',
					maxValue: '100'
				},
				{
					dataName: '总有功功率',
					dataVale: '5000',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '80%',
					maxValue: '100'
				},
				{
					dataName: 'A相有功功率',
					dataVale: '4000',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '70%',
					maxValue: '100'
				},
				{
					dataName: 'B相有功功率',
					dataVale: '3000',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '70%',
					maxValue: '100'
				},
				{
					dataName: 'C相有功功率',
					dataVale: '4500',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '10%',
					maxValue: '100'
				},
				{
					dataName: '总无功功率',
					dataVale: '3800',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '10%',
					maxValue: '100'
				},
				{
					dataName: 'A相无功功率',
					dataVale: '3980',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '20%',
					maxValue: '100'
				},
				{
					dataName: 'B相无功功率',
					dataVale: '3260',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '90%',
					maxValue: '100'
				},
				{
					dataName: 'A相有功功率',
					dataVale: '2678',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '40%',
					maxValue: '100'
				},
				{
					dataName: 'B相有功功率',
					dataVale: '2354',
					dataUnit: 'KW',
					dataSummary: '使用负载',
					dataSummaryValue: '50%',
					maxValue: '100'
				}
			],
			option: {
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: ['10:00', '10:30', '11:00', '11:30', '12:00', '12:30', '13:00', '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00']
				},
				yAxis: {
					type: 'value'
				},
				series: [
					{
						data: [820, 700, 901, 200, 1290, 600, 1320, 850, 900, 200, 500, 1290, 1330, 1320],
						type: 'line'
					}
				],
				dataZoom: [
					{
						type: 'slider',
						show: true,
						xAxisIndex: [0],
						start: 0,
						end: 45
					}
				],
				grid: {
					left: 100,
					right: 100,
					top: '5%',
					bottom: 50
				},
				tooltip: {
					trigger: 'axis'
				},
				toolbox: {
					show: true,
					right: '2%',
					feature: {
						saveAsImage: { show: true }
					}
				}
			}
		};
	},
	async mounted() {
		const myChart = echarts.init(document.getElementById('main'));
		myChart.setOption(this.option);
	}
};
</script>
<style scoped>
.li {
	margin-top: 25px;
}
.data-type {
	width: 90%;
	height: 15%;
	font-size: 20px;
	color: gray;
	margin-left: 30px;
	margin-top: 30px;
}
.data-value {
	width: 90%;
	height: 33%;
	font-size: 40px;
	text-align: center;
	margin-top: 30px;
}
.common {
	background-color: white;
	border-radius: 4px;
	margin-left: 20px;
	margin-right: 20px;
}
.device-introduction {
	min-height: 350px;
}
.device-explain {
	min-height: 350px;
}
.live-time {
	min-height: 600px;
}
.live-trend-chart {
	min-height: 500px;
}
.card-title {
	font-weight: 600;
	font-size: 20px;
	color: #000000;
}
</style>
